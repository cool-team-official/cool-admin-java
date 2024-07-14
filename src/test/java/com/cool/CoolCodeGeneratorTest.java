package com.cool;

import com.cool.core.code.CodeGenerator;
import com.cool.core.code.CodeModel;
import com.cool.core.code.CodeTypeEnum;

public class CoolCodeGeneratorTest {
    public static void main(String[] args) {
        CodeGenerator codeGenerator = new CodeGenerator();
        codeGenerator.init();

        CodeModel codeModel = new CodeModel();
        codeModel.setType(CodeTypeEnum.ADMIN);
        codeModel.setName("测试CURD");
        codeModel.setModule("demo");
//        codeModel.setEntity(DemoEntity.class);

        codeGenerator.controller(codeModel);
        codeGenerator.mapper(codeModel);
        codeGenerator.service(codeModel);
    }
}
