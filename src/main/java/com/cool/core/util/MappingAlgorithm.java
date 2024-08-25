package com.cool.core.util;

/**
 * 自定义映射算法
 * 将 ID 转换为一个混淆形式的数字，然后能够逆向转换回原始 ID。
 * 场景：混淆订单id
 */
public class MappingAlgorithm {

    private static final long ENCRYPTION_KEY = 123456789L; // 任意密钥

    // 将 ID 转换为混淆的数字
    public static long encrypt(long id) {
        return id ^ ENCRYPTION_KEY; // 使用异或操作进行混淆
    }

    // 将混淆的数字恢复为原始的 ID
    public static long decrypt(long encryptedId) {
        return encryptedId ^ ENCRYPTION_KEY; // 逆操作恢复原始 ID
    }
}
