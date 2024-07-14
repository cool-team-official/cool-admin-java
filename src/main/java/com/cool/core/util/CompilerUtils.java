package com.cool.core.util;

import cn.hutool.core.io.FileUtil;
import com.mybatisflex.processor.MybatisFlexProcessor;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import javax.annotation.processing.Processor;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

public class CompilerUtils {

    /**
     * 创建文件, 先删除在创建
     */
    public static void createFile(String content, String filePathStr) {
        FileUtil.del(filePathStr);
        File file = FileUtil.touch(filePathStr);
        FileUtil.appendString(content, file, StandardCharsets.UTF_8.name());
    }

    public static void createMapper(String actModulePath, String fileName, String mapper) {
        String pathStr = actModulePath + "/mapper/";
        String filePathStr = pathStr + fileName + "Mapper.java";
        createFile(mapper, filePathStr);
    }

    public static void createServiceImpl(String actModulePath, String fileName,
        String serviceImpl) {
        String pathStr = actModulePath + "/service/impl/";
        String filePathStr = pathStr + fileName + "ServiceImpl.java";
        createFile(serviceImpl, filePathStr);
    }

    public static void createService(String actModulePath, String fileName, String service) {
        String pathStr = actModulePath + "/service/";
        String filePathStr = pathStr + fileName + "Service.java";
        createFile(service, filePathStr);
    }

    public static String createEntity(String actModulePath, String fileName, String entity) {
        String pathStr = actModulePath + "/entity/";
        String filePathStr = pathStr + fileName + "Entity.java";
        createFile(entity, filePathStr);
        return filePathStr;
    }

    public static void createController(String actModulePath, String fileName, String controller) {
        String pathStr = actModulePath + "/controller/admin/";
        String filePathStr = pathStr + "Admin" + fileName + "Controller.java";
        createFile(controller, filePathStr);
    }

    public static String createModule(String modulesPath, String module) {
        String pathStr = modulesPath + "/" + module;
        PathUtils.noExistsMk(pathStr);
        return pathStr;
    }

    public static void compilerEntityTableDef(String path) {
//        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
//        StandardJavaFileManager fileManager = compiler.getStandardFileManager(null, null, null);
//
//        Iterable<? extends JavaFileObject> compilationUnits = fileManager.getJavaFileObjects(
//            path);
//
//        // 设置注解处理器
//        Iterable<? extends Processor> processors = Arrays.asList(new MybatisFlexProcessor());
//        // 添加 -proc:only 选项
//        List<String> options = Arrays.asList("-proc:only");
//        JavaCompiler.CompilationTask task = compiler.getTask(null, fileManager, null, options,
//            null, compilationUnits);
//        task.setProcessors(processors);
//
//        boolean success = task.call();
//        if (success) {
//            System.out.println("Compilation and annotation processing completed successfully.");
//        } else {
//            System.out.println("Compilation and annotation processing failed.");
//        }
    }
}
