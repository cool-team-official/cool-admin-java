package com.cool.core.util;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;

import java.io.*;
import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.web.multipart.MultipartFile;

/**
 * 转换
 */
public class ConvertUtil {

    /**
     * 对象转数组
     *
     * @param obj
     * @return
     */
    public static byte[] toByteArray(Object obj) {
        byte[] bytes = null;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            ObjectOutputStream oos = new ObjectOutputStream(bos);
            oos.writeObject(obj);
            oos.flush();
            bytes = bos.toByteArray();
            oos.close();
            bos.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return bytes;
    }

    /**
     * 数组转对象
     *
     * @param bytes
     * @return
     */
    public static Object toObject(byte[] bytes) {
        Object obj = null;
        try {
            ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
            ObjectInputStream ois = new ObjectInputStream(bis);
            obj = ois.readObject();
            ois.close();
            bis.close();
        } catch (IOException | ClassNotFoundException ex) {
            ex.printStackTrace();
        }
        return obj;
    }

    public static MultipartFile convertToMultipartFile(File file) {
        FileInputStream inputStream = null;
        try {
            inputStream = new FileInputStream(file);
            return new SimpleMultipartFile(file.getName(), inputStream);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
            return null;
        }
    }

    // 简单的MultipartFile实现，用于模拟Spring中的MultipartFile对象
    static class SimpleMultipartFile implements MultipartFile {

        private String filename;
        private InputStream inputStream;

        public SimpleMultipartFile(String filename, InputStream inputStream) {
            this.filename = filename;
            this.inputStream = inputStream;
        }

        @Override
        public String getName() {
            return null;
        }

        @Override
        public String getOriginalFilename() {
            return filename;
        }

        @Override
        public String getContentType() {
            return "application/octet-stream";
        }

        @Override
        public boolean isEmpty() {
            return false;
        }

        @Override
        public long getSize() {
            try {
                return inputStream.available();
            } catch (IOException e) {
                e.printStackTrace();
                return 0;
            }
        }

        @Override
        public byte[] getBytes() throws IOException {
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int len;
            while ((len = inputStream.read(buffer)) != -1) {
                output.write(buffer, 0, len);
            }
            return output.toByteArray();
        }

        @Override
        public InputStream getInputStream() throws IOException {
            return inputStream;
        }

        @Override
        public void transferTo(File dest) throws IOException, IllegalStateException {
            try (FileOutputStream outputStream = new FileOutputStream(dest)) {
                byte[] buffer = new byte[1024];
                int len;
                while ((len = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, len);
                }
            } finally {
                if (inputStream != null) {
                    inputStream.close();
                }
            }
        }
    }



    /**
     * /admin/goods 转 AdminGoods
     */
    public static String pathToClassName(String path) {
        // 按斜杠分割字符串
        String[] parts = path.split("/");
        StringBuilder className = new StringBuilder();
        for (String part : parts) {
            // 将每个部分的首字母大写，并追加到 StringBuilder 中
            className.append(StrUtil.upperFirst(part));
        }
        return className.toString();
    }

    public static String extractController2Path(String prefix, String className) {
        Pattern pattern = Pattern.compile("([A-Za-z0-9]+)Controller$");
        Matcher matcher = pattern.matcher(className);

        if (matcher.find()) {
            String extracted = matcher.group(1);

            // 将前缀拆分为单词数组
            String[] prefixWords = splitCamelCase(prefix);
            String[] classWords = splitCamelCase(extracted);

            // 从前缀和类名中逐个匹配并去除匹配的部分
            int i = 0;
            for (int j = 0; i < prefixWords.length; j++) {
                if (j >= classWords.length) {
                    break;
                }
                for (String prefixWord : prefixWords) {
                    if (prefixWord.equalsIgnoreCase(classWords[i])) {
                        i++;
                        break;
                    }
                }
            }
            // 从当前位置开始，拼接剩余部分
            return String.join("/", java.util.Arrays.copyOfRange(classWords, i, classWords.length)).toLowerCase();
        }
        return "";
    }

    // 拆分驼峰命名的字符串为单词数组
    private static String[] splitCamelCase(String input) {
        return input.split("(?<=.)(?=[A-Z])");
    }

    /**
     * 将给定的字段值转换为可序列化的形式
     * 此方法旨在将一个对象的特定字段值转换为其相应的可序列化类型
     * 它在序列化和反序列化过程中特别有用，确保字段值可以被正确处理
     *
     * @param fieldName 字段名称，用于查找字段类型
     * @param fieldValue 待转换的字段值
     * @param clazz 包含该字段的类
     * @return 转换后的可序列化字段值，如果无法确定字段类型，则返回原始值
     */
    public static Object convertByClass(String fieldName, Object fieldValue, Class<?> clazz) {
        // 检查输入参数是否为空，如果字段名或字段值为空，则直接返回字段值
        if (fieldName == null || fieldValue == null) {
            return fieldValue;
        }

        // 获取字段类型
        Class<?> fieldType = getFieldType(clazz, fieldName);
        // 如果字段类型为空，则直接返回字段值
        if (fieldType == null) {
            return fieldValue;
        }

        // 使用Convert类的convert方法将字段值转换为字段类型
        return Convert.convert(fieldType, fieldValue);
    }

    public static List<Object> covertListByClass(String fieldName, List<Object> fieldValue, Class<?> clazz) {
        // 检查输入参数是否为空，如果字段名或字段值为空，则直接返回字段值
        if (fieldName == null || fieldValue == null) {
            return fieldValue;
        }

        // 获取字段类型
        Class<?> fieldType = getFieldType(clazz, fieldName);
        // 如果字段类型为空，则直接返回字段值
        if (fieldType == null) {
            return fieldValue;
        }

        return Collections.singletonList(Convert.toList(fieldType, fieldValue));
    }
    /**
     * 获取指定类中指定字段的类型
     *
     * @param clazz     目标类
     * @param fieldName 字段名称
     * @return 字段的类型 Class，如果字段不存在则返回 null
     */
    public static Class<?> getFieldType(Class<?> clazz, String fieldName) {
        Field field = ReflectUtil.getField(clazz, fieldName);
        return field != null ? field.getType() : null;
    }
}