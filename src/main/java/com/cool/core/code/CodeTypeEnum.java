package com.cool.core.code;

/**
 * 代码类型
 */
public enum CodeTypeEnum {
    ADMIN("admin", "后端接口"), APP("app", "对外接口");

    private String value;

    private String des;

    CodeTypeEnum(String value, String des) {
        this.value = value;
        this.des = des;
    }

    public String value() {
        return this.value;
    }
}
