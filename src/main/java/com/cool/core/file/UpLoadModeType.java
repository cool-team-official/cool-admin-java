package com.cool.core.file;

import com.cool.core.config.FileModeEnum;
import lombok.Data;

/**
 * 上传模式类型
 */
@Data
public class UpLoadModeType {

    /**
     * 模式
     */
    private FileModeEnum mode;

    /**
     * 类型
     */
    private String type;

    public UpLoadModeType(FileModeEnum mode) {
        this.mode = mode;
        this.type = mode.type();
    }
}
