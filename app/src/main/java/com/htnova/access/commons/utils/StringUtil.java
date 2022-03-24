package com.htnova.access.commons.utils;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** 字符串处理工具类，与字符串相关的通用方法。 */
public class StringUtil {
    private static Logger log = LoggerFactory.getLogger(StringUtil.class);

    private static final Pattern doubleOrNumericPattern = Pattern.compile("[0-9]+[.]{0,1}[0-9]*[dD]{0,1}");
    private static final String charsetNameUtf8 = StandardCharsets.UTF_8.name();

    public static String toStr(Object o) {
        if (o == null) {
            return "";
        } else {
            return o.toString();
        }
    }

    /**
     * 字节数组转换为字符串。
     *
     * @param bytes
     *            字节数组。
     * @return 字符串。
     */
    public static String toString(byte[] bytes) {
        try {
            return new String(bytes, charsetNameUtf8);
        } catch (UnsupportedEncodingException e) {
            return StringUtils.EMPTY;
        }
    }

    /** 转换为String类型 */
    public static String toString(Object val, String defaultValue) {
        if (val == null) {
            return defaultValue;
        }
        try {
            return val.toString();
        } catch (Exception e) {
            log.error("字符串{}转Double异常，返回默认值{}", val, defaultValue, e);
            return defaultValue;
        }
    }

    /** 转换为Double类型 */
    public static Double toDouble(Object val, Double defaultValue) {
        if (val == null) {
            return defaultValue;
        }
        try {
            return Double.parseDouble(StringUtils.trim(val.toString()));
        } catch (Exception e) {
            log.error("字符串{}转Double异常，返回默认值{}", val, defaultValue, e);
            return defaultValue;
        }
    }

    /** 转换为Float类型 */
    public static Float toFloat(Object val, Float defaultValue) {
        if (val == null) {
            return defaultValue;
        }
        try {
            return Float.parseFloat(StringUtils.trim(val.toString()));
        } catch (Exception e) {
            log.error("字符串{}转Float异常，返回默认值{}", val, defaultValue, e);
            return defaultValue;
        }
    }

    /** 转换为Integer类型 */
    public static Integer toInteger(Object val, Integer defaultValue) {
        if (val == null) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(StringUtils.trim(val.toString()));
        } catch (Exception e) {
            log.error("字符串{}转Integer异常，返回默认值{}", val, defaultValue, e);
            return defaultValue;
        }
    }

    /** 获取UUID字符串。 */
    public static String getUUID() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    /**
     * 通过两级拆分，将拆分的字符串存储到map中。
     *
     * @param str
     *            原始字符串。
     * @param separator1
     *            第一级拆分符。
     * @param separator2
     *            第二季拆分符。
     * @return 拆分后的map。
     */
    public static Map<String, String> getMap(String str, String separator1, String separator2) {
        Map<String, String> map = new HashMap<String, String>();
        if (StringUtils.isBlank(str)) {
            return map;
        }

        String newStr1 = StringUtils.trim(str);
        String[] arr1 = StringUtils.split(newStr1, separator1);
        if (arr1 == null || arr1.length == 0) {
            return map;
        }

        for (String str1 : arr1) {
            String newStr2 = StringUtils.trim(str1);
            String[] arr2 = StringUtils.split(newStr2, separator2);
            if (arr2 != null && arr2.length == 2) {
                map.put(StringUtils.trim(arr2[0]), StringUtils.trim(arr2[1]));
            }
        }
        return map;
    }

    public static boolean isDoubleOrNumeric(String str) {
        Matcher isNum = doubleOrNumericPattern.matcher(str);
        if (!isNum.matches()) {
            return false;
        }
        return true;
    }

    // 工具类不需要外部构造。
    private StringUtil() {}
}
