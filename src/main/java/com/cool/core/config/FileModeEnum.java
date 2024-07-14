package com.cool.core.config;

/**
 * 文件模式
 */
public enum FileModeEnum {
    LOCAL("local", "local", "本地"), CLOUD("cloud", "oss", "云存储"), OTHER("other", "other", "其他");

    private String value;

    private String type;

    private String des;

    FileModeEnum(String value, String type, String des) {
        this.value = value;
        this.type = type;
        this.des = des;
    }

    public String value() {
        return this.value;
    }

    public String type() {
        return this.type;
    }
}
