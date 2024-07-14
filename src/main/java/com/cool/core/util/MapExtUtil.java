package com.cool.core.util;

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ObjectUtil;

import java.util.Map;

public class MapExtUtil extends MapUtil {

    /**
     * 比较两个map key 和 value 是否一致
     */
    public static boolean compareMaps(Map<String, Object> map1, Map<String, Object> map2) {
        if (ObjectUtil.isEmpty(map1) || ObjectUtil.isEmpty(map2)) {
            return true;
        }
        if (map1.size() != map2.size()) {
            return false;
        }
        for (Map.Entry<String, Object> entry : map1.entrySet()) {
            if (!map2.containsKey(entry.getKey())) {
                return false;
            }
            if (!ObjectUtil.equal(entry.getValue(), map2.get(entry.getKey()))) {
                return false;
            }
        }
        return true;
    }
}
