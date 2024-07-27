package com.cool.core.util;

import cn.hutool.core.io.FileUtil;
import com.mybatisflex.processor.MybatisFlexProcessor;
import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.nio.charset.StandardCharsets;
import java.util.List;
import javax.annotation.processing.Processor;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.StandardLocation;
import javax.tools.ToolProvider;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CompilerUtils {

    public final static String META_INF_VERSIONS = "META-INF/versions/";

    // jdk版本
    private static String JVM_VERSION = null;

    /**
     * 获取jdk版本
     */
    public static String getJdkVersion() {
        if (JVM_VERSION == null) {
            RuntimeMXBean runtimeMXBean = ManagementFactory.getRuntimeMXBean();
            JVM_VERSION = runtimeMXBean.getSpecVersion();
        }
        return JVM_VERSION;
    }

    /**
     * 创建文件, 先删除在创建
     */
    public static void createFile(String content, String filePathStr) {
        FileUtil.del(filePathStr);
        File file = FileUtil.touch(filePathStr);
        FileUtil.appendString(content, file, StandardCharsets.UTF_8.name());
        compileAndSave(filePathStr);
    }

    public static String createMapper(String actModulePath, String fileName, String mapper) {
        String pathStr = actModulePath + File.separator + "mapper" + File.separator;
        String filePathStr = pathStr + fileName + "Mapper.java";
        createFile(mapper, filePathStr);
        return filePathStr;
    }

    public static String createServiceImpl(String actModulePath, String fileName,
        String serviceImpl) {
        String pathStr = actModulePath + File.separator + "service" + File.separator + "impl" + File.separator;
        String filePathStr = pathStr + fileName + "ServiceImpl.java";
        createFile(serviceImpl, filePathStr);
        return filePathStr;
    }

    public static String createService(String actModulePath, String fileName, String service) {
        String pathStr = actModulePath + File.separator + "service" + File.separator;
        String filePathStr = pathStr + fileName + "Service.java";
        createFile(service, filePathStr);
        return filePathStr;
    }

    public static String createEntity(String actModulePath, String fileName, String entity) {
        String pathStr = actModulePath + File.separator + "entity" + File.separator;
        String filePathStr = pathStr + fileName + "Entity.java";
        createFile(entity, filePathStr);
        return filePathStr;
    }

    public static String createController(String actModulePath, String fileName, String controller) {
        String pathStr = actModulePath + File.separator + "controller" + File.separator + "admin" + File.separator;
        String filePathStr = pathStr + "Admin" + fileName + "Controller.java";
        createFile(controller, filePathStr);
        return filePathStr;
    }

    public static String createModule(String modulesPath, String module) {
        String pathStr = modulesPath + File.separator + module;
        PathUtils.noExistsMk(pathStr);
        return pathStr;
    }

    public static boolean compileAndSave(String sourceFile) {
        // 获取系统 Java 编译器
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();

        // 获取标准文件管理器
        try (StandardJavaFileManager fileManager = compiler.getStandardFileManager(null, null, null)) {

            // 设置编译输出目录
            fileManager.setLocation(StandardLocation.CLASS_OUTPUT, List.of(new File("target" + File.separator + "classes")));

            // 获取源文件
            List<File> javaFiles = List.of(new File(sourceFile));
            Iterable<? extends JavaFileObject> compilationUnits = fileManager.getJavaFileObjectsFromFiles(javaFiles);

            // 创建编译任务
            JavaCompiler.CompilationTask task = compiler.getTask(null, fileManager, null, null, null, compilationUnits);
            // 执行编译任务
            return task.call();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void compilerEntityTableDef(String actModulePath, String fileName, String entityPath, List<String> javaPathList) {
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        try (StandardJavaFileManager fileManager = compiler.getStandardFileManager(null, null, null)) {
            Iterable<? extends JavaFileObject> compilationUnits = fileManager.getJavaFileObjects(
                entityPath);
            // 设置注解处理器
            Iterable<? extends Processor> processors = List.of(new MybatisFlexProcessor());
            // 添加 -proc:only 选项
            List<String> options = List.of("-proc:only");
            JavaCompiler.CompilationTask task = compiler.getTask(null, fileManager, null, options,
                null, compilationUnits);
            task.setProcessors(processors);
            task.call();
            compilationUnits = fileManager.getJavaFileObjects(
                javaPathList.toArray(new String[0]));
            // 设置编译输出目录
            fileManager.setLocation(StandardLocation.CLASS_OUTPUT, List.of(new File("target/classes")));

            task = compiler.getTask(null, fileManager, null, null, null, compilationUnits);
            String pathStr = actModulePath + File.separator + "entity" + File.separator + "table" + File.separator;
            String filePathStr = pathStr + fileName + "EntityTableDef.java";
            // 需在entity之后加载
            javaPathList.add(1, filePathStr);
            boolean success = task.call();
            if (success) {
                System.out.println("Compilation and annotation processing completed successfully.");
                // 指定源文件夹和目标文件夹
                File sourceDir = new File("com");
                File destinationDir = new File(PathUtils.getTargetGeneratedAnnotations());
                // 确保目标文件夹存在
                destinationDir.mkdirs();
                // 移动源文件夹内容到目标文件夹
                if (sourceDir.exists()) {
                    FileUtil.move(sourceDir, destinationDir, true);
                }
                if (countFiles(sourceDir) <= 1) {
                    FileUtil.clean(sourceDir);
                    FileUtil.del(sourceDir);
                }
            } else {
                System.out.println("Compilation and annotation processing failed.");
            }
        } catch (IOException e) {
            log.error("compilerEntityTableDefError", e);
        }
    }
    private static int countFiles(File directory) {
        File[] files = directory.listFiles();
        if (files == null) {
            return 0;
        }

        int count = 0;
        for (File file : files) {
            if (file.isFile()) {
                count++;
            } else if (file.isDirectory()) {
                count += countFiles(file);
            }
            // If more than one file is found, no need to continue counting
            if (count > 1) {
                break;
            }
        }
        return count;
    }
}
