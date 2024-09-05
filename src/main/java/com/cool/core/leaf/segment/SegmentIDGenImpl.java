package com.cool.core.leaf.segment;

import static com.cool.core.leaf.segment.entity.table.LeafAllocEntityTableDef.LEAF_ALLOC_ENTITY;

import com.cool.core.exception.CoolPreconditions;
import com.cool.core.leaf.IDGenService;
import com.cool.core.leaf.common.Result;
import com.cool.core.leaf.common.Status;
import com.cool.core.leaf.segment.entity.LeafAllocEntity;
import com.cool.core.leaf.segment.mapper.LeafAllocMapper;
import com.cool.core.leaf.segment.model.Segment;
import com.cool.core.leaf.segment.model.SegmentBuffer;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.update.UpdateChain;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;
import lombok.RequiredArgsConstructor;
import org.perf4j.StopWatch;
import org.perf4j.slf4j.Slf4JStopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SegmentIDGenImpl implements IDGenService, DisposableBean {

    private static final Logger logger = LoggerFactory.getLogger(SegmentIDGenImpl.class);

    @Value("${leaf.segment.enable:false}")
    private boolean enable;

    /**
     * IDCache未初始化成功时的异常码
     */
    private static final long EXCEPTION_ID_IDCACHE_INIT_FALSE = -1;
    /**
     * key不存在时的异常码
     */
    private static final long EXCEPTION_ID_KEY_NOT_EXISTS = -2;
    /**
     * SegmentBuffer中的两个Segment均未从DB中装载时的异常码
     */
    private static final long EXCEPTION_ID_TWO_SEGMENTS_ARE_NULL = -3;
    /**
     * 最大步长不超过100,0000
     */
    private static final int MAX_STEP = 1000000;
    /**
     * 一个Segment维持时间为15分钟
     */
    private static final long SEGMENT_DURATION = 15 * 60 * 1000L;
    private final ExecutorService executorService = new ThreadPoolExecutor(5, Integer.MAX_VALUE, 60L, TimeUnit.SECONDS, new SynchronousQueue<>(), new UpdateThreadFactory());
    private final ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor(r -> {
        Thread t = new Thread(r);
        t.setName("check-idCache-thread");
        t.setDaemon(true);
        return t;
    });
    private volatile boolean initOK = false;
    private final Map<String, SegmentBuffer> cache = new ConcurrentHashMap<>();
    private final LeafAllocMapper leafAllocMapper;

    public static class UpdateThreadFactory implements ThreadFactory {

        private static int threadInitNumber = 0;

        private static synchronized int nextThreadNum() {
            return threadInitNumber++;
        }

        @Override
        public Thread newThread(Runnable r) {
            return new Thread(r, "Thread-Segment-Update-" + nextThreadNum());
        }
    }

    @Override
    public long next(String key) {
        Result result = get(key);
        CoolPreconditions.check(result.getId() < 0, "获取失败，code值: {}", result.getId());
        return result.getId();
    }

    @Override
    public void init() {
        if (enable) {
            // 确保加载到kv后才初始化成功
            updateCacheFromDb();
            initOK = true;
            updateCacheFromDbAtEveryMinute();
            logger.info("唯一ID组件初始化成功 ...");
        }
    }

    private void updateCacheFromDbAtEveryMinute() {
        scheduledExecutorService.scheduleWithFixedDelay(this::updateCacheFromDb, 60, 60, TimeUnit.SECONDS);
    }

    private void updateCacheFromDb() {
        logger.info("update cache from db");
        StopWatch sw = new Slf4JStopWatch();
        try {
            List<String> dbTags = leafAllocMapper.selectListByQuery(QueryWrapper.create().select(
                LeafAllocEntity::getKey)).stream().map(LeafAllocEntity::getKey).toList();
            if (dbTags.isEmpty()) {
                return;
            }
            List<String> cacheTags = new ArrayList<String>(cache.keySet());
            Set<String> insertTagsSet = new HashSet<>(dbTags);
            Set<String> removeTagsSet = new HashSet<>(cacheTags);
            //db中新加的tags灌进cache
            for (String tmp : cacheTags) {
                insertTagsSet.remove(tmp);
            }
            for (String tag : insertTagsSet) {
                SegmentBuffer buffer = new SegmentBuffer();
                buffer.setKey(tag);
                Segment segment = buffer.getCurrent();
                segment.setValue(new AtomicLong(0));
                segment.setMax(0);
                segment.setStep(0);
                cache.put(tag, buffer);
                logger.info("Add tag {} from db to IdCache, SegmentBuffer {}", tag, buffer);
            }
            //cache中已失效的tags从cache删除
            for (String tmp : dbTags) {
                removeTagsSet.remove(tmp);
            }
            for (String tag : removeTagsSet) {
                cache.remove(tag);
                logger.info("Remove tag {} from IdCache", tag);
            }
        } catch (Exception e) {
            logger.warn("update cache from db exception", e);
        } finally {
            sw.stop("updateCacheFromDb");
        }
    }

    private Result get(final String key) {
        if (!initOK) {
            return new Result(EXCEPTION_ID_IDCACHE_INIT_FALSE, Status.EXCEPTION);
        }
        CoolPreconditions.check(!initOK, "IDCache未初始化成功");
        if (cache.containsKey(key)) {
            SegmentBuffer buffer = cache.get(key);
            if (!buffer.isInitOk()) {
                synchronized (buffer) {
                    if (!buffer.isInitOk()) {
                        try {
                            updateSegmentFromDb(key, buffer.getCurrent());
                            logger.info("Init buffer. Update leafkey {} {} from db", key, buffer.getCurrent());
                            buffer.setInitOk(true);
                        } catch (Exception e) {
                            logger.warn("Init buffer {} exception", buffer.getCurrent(), e);
                        }
                    }
                }
            }
            return getIdFromSegmentBuffer(cache.get(key));
        }
        return new Result(EXCEPTION_ID_KEY_NOT_EXISTS, Status.EXCEPTION);
    }

    public void updateSegmentFromDb(String key, Segment segment) {
        StopWatch sw = new Slf4JStopWatch();
        SegmentBuffer buffer = segment.getBuffer();
        LeafAllocEntity leafAllocEntity;
        if (!buffer.isInitOk()) {
            leafAllocEntity = updateMaxIdAndGetLeafAlloc(key);
            buffer.setStep(leafAllocEntity.getStep());
            buffer.setMinStep(leafAllocEntity.getStep());//leafAlloc中的step为DB中的step
        } else if (buffer.getUpdateTimestamp() == 0) {
            leafAllocEntity = updateMaxIdAndGetLeafAlloc(key);
            buffer.setUpdateTimestamp(System.currentTimeMillis());
            buffer.setStep(leafAllocEntity.getStep());
            buffer.setMinStep(leafAllocEntity.getStep());//leafAlloc中的step为DB中的step
        } else {
            long duration = System.currentTimeMillis() - buffer.getUpdateTimestamp();
            int nextStep = buffer.getStep();
            if (duration < SEGMENT_DURATION) {
                if (nextStep * 2 > MAX_STEP) {
                    //do nothing
                } else {
                    nextStep = nextStep * 2;
                }
            } else if (duration < SEGMENT_DURATION * 2) {
                //do nothing with nextStep
            } else {
                nextStep = nextStep / 2 >= buffer.getMinStep() ? nextStep / 2 : nextStep;
            }
            logger.info("leafKey[{}], step[{}], duration[{}mins], nextStep[{}]", key, buffer.getStep(), String.format("%.2f",((double)duration / (1000 * 60))), nextStep);
            LeafAllocEntity temp = new LeafAllocEntity();
            temp.setKey(key);
            temp.setStep(nextStep);
            leafAllocEntity = updateMaxIdByCustomStepAndGetLeafAlloc(temp);
            buffer.setUpdateTimestamp(System.currentTimeMillis());
            buffer.setStep(nextStep);
            buffer.setMinStep(leafAllocEntity.getStep());//leafAlloc的step为DB中的step
        }
        // must set value before set max
        long value = leafAllocEntity.getMaxId() - buffer.getStep();
        segment.getValue().set(value);
        segment.setMax(leafAllocEntity.getMaxId());
        segment.setStep(buffer.getStep());
        sw.stop("updateSegmentFromDb", key + " " + segment);
    }

    private LeafAllocEntity updateMaxIdByCustomStepAndGetLeafAlloc(LeafAllocEntity temp) {
        UpdateChain.of(LeafAllocEntity.class)
            .setRaw(LeafAllocEntity::getMaxId, LEAF_ALLOC_ENTITY.MAX_ID.getName() + " + " + temp.getStep())
            .where(LeafAllocEntity::getKey).eq(temp.getKey())
            .update();
        return leafAllocMapper.selectOneByQuery(QueryWrapper.create().select(
            LEAF_ALLOC_ENTITY.KEY, LEAF_ALLOC_ENTITY.MAX_ID, LEAF_ALLOC_ENTITY.STEP).eq(LeafAllocEntity::getKey, temp.getKey()));
    }

    private LeafAllocEntity updateMaxIdAndGetLeafAlloc(String key) {
        UpdateChain.of(LeafAllocEntity.class)
            .setRaw(LeafAllocEntity::getMaxId, LEAF_ALLOC_ENTITY.MAX_ID.getName() + " + " + LEAF_ALLOC_ENTITY.STEP.getName())
            .where(LeafAllocEntity::getKey).eq(key)
            .update();
        return leafAllocMapper.selectOneByQuery(QueryWrapper.create().select(
            LEAF_ALLOC_ENTITY.KEY, LEAF_ALLOC_ENTITY.MAX_ID, LEAF_ALLOC_ENTITY.STEP).eq(LeafAllocEntity::getKey, key));
    }

    public Result getIdFromSegmentBuffer(final SegmentBuffer buffer) {
        while (true) {
            buffer.rLock().lock();
            try {
                final Segment segment = buffer.getCurrent();
                if (!buffer.isNextReady() && (segment.getIdle() < 0.9 * segment.getStep()) && buffer.getThreadRunning().compareAndSet(false, true)) {
                    executorService.execute(() -> {
                        Segment next = buffer.getSegments()[buffer.nextPos()];
                        boolean updateOk = false;
                        try {
                            updateSegmentFromDb(buffer.getKey(), next);
                            updateOk = true;
                            logger.info("update segment {} from db {}", buffer.getKey(), next);
                        } catch (Exception e) {
                            logger.warn(buffer.getKey() + " updateSegmentFromDb exception", e);
                        } finally {
                            if (updateOk) {
                                buffer.wLock().lock();
                                buffer.setNextReady(true);
                                buffer.getThreadRunning().set(false);
                                buffer.wLock().unlock();
                            } else {
                                buffer.getThreadRunning().set(false);
                            }
                        }
                    });
                }
                long value = segment.getValue().getAndIncrement();
                if (value < segment.getMax()) {
                    return new Result(value, Status.SUCCESS);
                }
            } finally {
                buffer.rLock().unlock();
            }
            waitAndSleep(buffer);
            buffer.wLock().lock();
            try {
                final Segment segment = buffer.getCurrent();
                long value = segment.getValue().getAndIncrement();
                if (value < segment.getMax()) {
                    return new Result(value, Status.SUCCESS);
                }
                if (buffer.isNextReady()) {
                    buffer.switchPos();
                    buffer.setNextReady(false);
                } else {
                    logger.error("Both two segments in {} are not ready!", buffer);
                    return new Result(EXCEPTION_ID_TWO_SEGMENTS_ARE_NULL, Status.EXCEPTION);
                }
            } finally {
                buffer.wLock().unlock();
            }
        }
    }

    private void waitAndSleep(SegmentBuffer buffer) {
        int roll = 0;
        while (buffer.getThreadRunning().get()) {
            roll += 1;
            if(roll > 10000) {
                try {
                    TimeUnit.MILLISECONDS.sleep(10);
                    break;
                } catch (InterruptedException e) {
                    logger.warn("Thread {} Interrupted",Thread.currentThread().getName());
                    break;
                }
            }
        }
    }

    @Override
    public void destroy() throws Exception {
        executorService.shutdown();
        executorService.awaitTermination(10, TimeUnit.SECONDS);

        scheduledExecutorService.shutdown();
        scheduledExecutorService.awaitTermination(10, TimeUnit.SECONDS);
    }
}
