package com.cool.core.util;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.NumberUtil;

import java.io.Serializable;

public class AutoTypeConverter {
    /**
     * 将字符串自动转换为数字或保留为字符串
     *
     * @param input 输入字符串
     * @return Integer / Long / String
     */
    public static Serializable autoConvert(Object input) {
        if (input == null) {
            return null;
        }
        if (NumberUtil.isInteger(input.toString())) {
            return Convert.convert(Integer.class, input);
        } else if (NumberUtil.isLong(input.toString())) {
            return Convert.convert(Long.class, input);
        } else {
            return (Serializable) input;
        }
    }
}
