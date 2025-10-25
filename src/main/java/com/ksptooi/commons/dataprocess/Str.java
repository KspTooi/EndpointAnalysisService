package com.ksptooi.commons.dataprocess;

import org.apache.commons.lang3.StringUtils;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Str extends StringUtils {


    /**
     * 安全分割字符串
     *
     * @param str       字符串
     * @param separator 分隔符
     * @return 分割后的字符串
     */
    public static List<String> safeSplit(String str, String separator) {
        if (StringUtils.isBlank(str)) {
            return Collections.emptyList();
        }
        if (StringUtils.isBlank(separator)) {
            return Collections.singletonList(str);
        }
        return Arrays.asList(StringUtils.split(str, separator));
    }


    /**
     * 校验是否为整数
     *
     * @param str 整数字符串
     * @return 是否为整数
     */
    public static boolean isInteger(String str) {
        if (StringUtils.isBlank(str)) {
            return false;
        }
        //校验是否为整数
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * 校验是否为长整数
     *
     * @param str 长整数字符串
     * @return 是否为长整数
     */
    public static boolean isLong(String str) {
        if (StringUtils.isBlank(str)) {
            return false;
        }
        //校验是否为整数
        try {
            Long.parseLong(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * 校验是否为浮点数
     *
     * @param str 浮点数字符串
     * @return 是否为浮点数
     */
    public static boolean isDouble(String str) {
        if (StringUtils.isBlank(str)) {
            return false;
        }
        //校验是否为整数
        try {
            Double.parseDouble(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * 校验是否为日期
     *
     * @param str 日期字符串
     * @return 是否为日期
     */
    public static boolean isDate(String str) {
        if (StringUtils.isBlank(str)) {
            return false;
        }
        //校验是否为日期
        try {
            LocalDate.parse(str, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            return true;
        } catch (DateTimeException e) {
            return false;
        }
    }

    /**
     * 校验是否为日期时间
     *
     * @param str 日期时间字符串
     * @return 是否为日期时间
     */
    public static boolean isDateTime(String str) {
        if (StringUtils.isBlank(str)) {
            return false;
        }
        //校验是否为日期时间 LDT
        try {
            LocalDateTime.parse(str, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            return true;
        } catch (DateTimeException e) {
            return false;
        }
    }

    /**
     * 校验是否为时间
     *
     * @param str 时间字符串
     * @return 是否为时间
     */
    public static boolean isTime(String str) {
        if (StringUtils.isBlank(str)) {
            return false;
        }
        //校验是否为时间 使用LDT
        try {
            LocalTime.parse(str, DateTimeFormatter.ofPattern("HH:mm:ss"));
            return true;
        } catch (DateTimeException e) {
            return false;
        }
    }

    /**
     * 校验是否为指定值之一
     *
     * @param str 字符串
     * @param arg 指定值
     * @return 是否为指定值之一
     */
    public static boolean in(String str, String... arg) {
        if (StringUtils.isBlank(str)) {
            return false;
        }
        for (String s : arg) {
            if (str.equals(s)) {
                return true;
            }
        }
        return false;
    }


    /**
     * 校验是否不为整数
     *
     * @param str 整数字符串
     * @return 是否不为整数
     */
    public static boolean isNotInteger(String str) {
        return !isInteger(str);
    }

    /**
     * 校验是否不为长整数
     *
     * @param str 长整数字符串
     * @return 是否不为长整数
     */
    public static boolean isNotLong(String str) {
        return !isLong(str);
    }

    /**
     * 校验是否不为浮点数
     *
     * @param str 浮点数字符串
     * @return 是否不为浮点数
     */
    public static boolean isNotDouble(String str) {
        return !isDouble(str);
    }

    /**
     * 校验是否不为日期
     *
     * @param str 日期字符串
     * @return 是否不为日期
     */
    public static boolean isNotDate(String str) {
        return !isDate(str);
    }

    /**
     * 校验是否不为日期时间
     *
     * @param str 日期时间字符串
     * @return 是否不为日期时间
     */
    public static boolean isNotDateTime(String str) {
        return !isDateTime(str);
    }

    /**
     * 校验是否不为时间
     *
     * @param str 时间字符串
     * @return 是否不为时间
     */
    public static boolean isNotTime(String str) {
        return !isTime(str);
    }

    /**
     * 校验是否不为指定值之一
     *
     * @param str 字符串
     * @param arg 指定值
     * @return 是否不为指定值之一
     */
    public static boolean isNotIn(String str, String... arg) {
        return !in(str, arg);
    }

}
