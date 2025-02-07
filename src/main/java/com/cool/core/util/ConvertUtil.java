package com.cool.core.util;

import cn.hutool.core.util.StrUtil;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
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
        // 1. 使用正则匹配 `Controller` 之前的部分
        Pattern pattern = Pattern.compile("([A-Za-z0-9]+)Controller$");
        Matcher matcher = pattern.matcher(className);

        if (matcher.find()) {
            String extracted = matcher.group(1); // 提取 "DemoInfo" 或 "Demo2UserInfo"

            // 2. 计算前缀后缀匹配部分
            String prefixSuffix = findPrefixSuffixMatch(prefix, extracted);
            if (!prefixSuffix.isEmpty()) {
                extracted = extracted.replaceFirst(prefixSuffix, ""); // 去掉匹配部分
            }

            // 3. 处理驼峰命名转换 `/` 分隔符
            return formatExtractedString(extracted);
        }
        return "";
    }

    private static String findPrefixSuffixMatch(String prefix, String extracted) {
        if (extracted.startsWith(prefix)) {
            return prefix;
        }
        // 从 prefix 中找出类名的前缀部分，例如 AdminDemo -> Demo，AdminDemo2 -> Demo2
        Pattern pattern = Pattern.compile("[A-Z][a-z0-9]*$");
        Matcher matcher = pattern.matcher(prefix);
        return matcher.find() ? matcher.group() : "";
    }

    private static String formatExtractedString(String extracted) {
        // 处理驼峰命名转换，DemoDataInfo3 → data/info3
        extracted = extracted.replaceAll("([a-z])([A-Z])", "$1/$2").toLowerCase();
        return extracted;
    }
}