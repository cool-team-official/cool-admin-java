package com.cool.modules.space.service.impl;

import com.cool.core.base.BaseServiceImpl;
import com.cool.modules.space.entity.SpaceInfoEntity;
import com.cool.modules.space.mapper.SpaceInfoMapper;
import com.cool.modules.space.service.SpaceInfoService;
import org.springframework.stereotype.Service;

/**
 * 文件空间信息
 */
@Service
public class SpaceInfoServiceImpl extends BaseServiceImpl<SpaceInfoMapper, SpaceInfoEntity>
        implements SpaceInfoService {
}