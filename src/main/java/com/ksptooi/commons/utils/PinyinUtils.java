package com.ksptooi.commons.utils;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;
import org.apache.commons.lang3.StringUtils;

public class PinyinUtils {

    /**
     * 将汉字转为全拼
     */
    public static String convertToPinyin(String chinese) {

        if (StringUtils.isBlank(chinese)) {
            return null;
        }

        StringBuilder pinyinStr = new StringBuilder();
        char[] newChar = chinese.toCharArray();

        HanyuPinyinOutputFormat format = new HanyuPinyinOutputFormat();
        format.setCaseType(HanyuPinyinCaseType.LOWERCASE); // 小写
        format.setToneType(HanyuPinyinToneType.WITHOUT_TONE); // 无声调

        for (char c : newChar) {
            if (c > 128) { // 如果是中文字符
                try {
                    // pinyin4j 返回的是数组，因为存在多音字，这里默认取第一个
                    String[] pinyinArray = PinyinHelper.toHanyuPinyinStringArray(c, format);
                    if (pinyinArray != null && pinyinArray.length > 0) {
                        pinyinStr.append(pinyinArray[0]);
                    }
                } catch (BadHanyuPinyinOutputFormatCombination e) {
                    e.printStackTrace();
                }
            } else {
                pinyinStr.append(c);
            }
        }
        return pinyinStr.toString();
    }

    /**
     * 获取汉字首字母
     */
    public static String getFirstLetters(String chinese) {

        if (StringUtils.isBlank(chinese)) {
            return null;
        }

        StringBuilder firstLetters = new StringBuilder();
        char[] newChar = chinese.toCharArray();

        HanyuPinyinOutputFormat format = new HanyuPinyinOutputFormat();
        format.setCaseType(HanyuPinyinCaseType.UPPERCASE); // 大写
        format.setToneType(HanyuPinyinToneType.WITHOUT_TONE);

        for (char c : newChar) {
            if (c > 128) {
                try {
                    String[] pinyinArray = PinyinHelper.toHanyuPinyinStringArray(c, format);
                    if (pinyinArray != null && pinyinArray.length > 0 && StringUtils.isNotBlank(pinyinArray[0])) {
                        // 取拼音字符串的第一个字母
                        firstLetters.append(pinyinArray[0].charAt(0));
                    }
                } catch (BadHanyuPinyinOutputFormatCombination e) {
                    e.printStackTrace();
                }
            } else {
                firstLetters.append(c);
            }
        }
        return firstLetters.toString();
    }

    public static void main(String[] args) {
        String input = null;
        System.out.println("全拼: " + convertToPinyin(input));    // nihao Java
        System.out.println("首字母: " + getFirstLetters(input)); // NH Java
    }
}