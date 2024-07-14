package com.cool.core.code;

import cn.hutool.core.io.file.FileWriter;
import cn.hutool.core.lang.Dict;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.template.Template;
import cn.hutool.extra.template.TemplateConfig;
import cn.hutool.extra.template.TemplateEngine;
import cn.hutool.extra.template.TemplateUtil;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

/**
 * 代码生成器
 */
@Component
public class CodeGenerator {

    private TemplateEngine templateEngine;

    private String baseSrcPath;

    private String baseResPath;

    @PostConstruct
    public void init() {
        templateEngine = coolTemplateEngine();
        baseSrcPath = System.getProperty("user.dir") + "/src/main/java/com/cool/modules/";
        baseResPath = System.getProperty("user.dir") + "/src/main/resources/";
    }

    public TemplateEngine coolTemplateEngine() {
        return TemplateUtil.createEngine(
            new TemplateConfig("cool/code", TemplateConfig.ResourceMode.CLASSPATH));
    }

    private String filePath(CodeModel codeModel, String type) {
        if (type.equals("controller")) {
            return StrUtil.isEmpty(codeModel.getSubModule())
                ? baseSrcPath + codeModel.getModule() + "/" + type + "/" + codeModel.getType()
                .value()
                : baseSrcPath + codeModel.getModule() + "/" + type + "/" + codeModel.getType()
                    .value() + "/"
                    + codeModel.getSubModule();
        }
        if (type.equals("xmlMapper")) {
            return StrUtil.isEmpty(codeModel.getSubModule()) ? baseResPath + "mapper/"
                + codeModel.getModule()
                : baseResPath + "mapper/" + codeModel.getModule() + "/" + codeModel.getSubModule();
        }
        return StrUtil.isEmpty(codeModel.getSubModule()) ? baseSrcPath + codeModel.getModule() + "/"
            + type
            : baseSrcPath + codeModel.getModule() + "/" + type + "/" + codeModel.getSubModule();
    }

    /**
     * 生成Mapper
     *
     * @param codeModel 代码模型
     */
    public void mapper(CodeModel codeModel) {
        Template template = templateEngine.getTemplate("/mapper/interface.th");
        String result = template.render(Dict.parse(codeModel));
        FileWriter writer = new FileWriter(
            filePath(codeModel, "mapper") + "/" + codeModel.getEntity() + "Mapper.java");
        writer.write(result);
    }

    /**
     * 生成Service
     *
     * @param codeModel 代码模型
     */
    public void service(CodeModel codeModel) {
        Template interfaceTemplate = templateEngine.getTemplate("/service/interface.th");
        String interfaceResult = interfaceTemplate.render(Dict.parse(codeModel));
        FileWriter interfaceWriter = new FileWriter(
            filePath(codeModel, "service") + "/" + codeModel.getEntity() + "Service.java");
        interfaceWriter.write(interfaceResult);

        Template template = templateEngine.getTemplate("/service/impl.th");
        String result = template.render(Dict.parse(codeModel));
        FileWriter writer = new FileWriter(
            filePath(codeModel, "service") + "/impl/" + codeModel.getEntity() + "ServiceImpl.java");
        writer.write(result);
    }

    /**
     * 生成Controller
     *
     * @param codeModel 代码模型
     */
    public void controller(CodeModel codeModel) {
        Template template = templateEngine.getTemplate("controller.th");
        System.out.println(codeModel.getType().value());
        Dict data = Dict.create().set("upperType", StrUtil.upperFirst(codeModel.getType().value()))
            .set("url",
                "/" + codeModel.getType() + "/" + StrUtil.toUnderlineCase(codeModel.getEntity())
                    .replace("_", "/"));
        data.putAll(Dict.parse(codeModel));
        data.set("type", codeModel.getType().value());
        String result = template.render(data);
        FileWriter writer = new FileWriter(filePath(codeModel, "controller") + "/"
            + StrUtil.upperFirst(codeModel.getType().value()) + codeModel.getEntity()
            + "Controller.java");
        writer.write(result);
    }
}
