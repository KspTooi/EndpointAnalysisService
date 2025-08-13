package com.ksptooi.utils;

import org.apache.commons.lang3.StringUtils;

import java.io.File;

public class TextConv {


    /**
     * 组合两个包
     */
    public static String unionPacket(String p1,String p2){
        String source = removeTailSymbol(p1,'.');
        String target = removeHeadSymbol(p2,'.');
        return source + "." + target;
    }
    public static String unionPkg(String p1,String p2){
        return unionPacket(p1,p2);
    }
    public static String unionPkg(String... p) {
        if (p == null || p.length == 0) {
            return "";
        }

        String result = p[0];
        for (int i = 1; i < p.length; i++) {
            result = unionPacket(result, p[i]);
        }
        return result;
    }

    /**
     * 移除字符串头部的一个或多个多余的符号
     */
    public static String removeHeadSymbol(String s,char c){
        if(StringUtils.isBlank(s)){
            return s;
        }

        char[] buf = s.toCharArray();

        for(int i = 0; i < buf.length; i++){
            if(buf[i] != c){
                return String.valueOf(buf,i,buf.length - i);
            }
        }

        return s;
    }

    /**
     * 移除字符串尾部的一个或多个多余的符号
     */
    public static String removeTailSymbol(String s,char c){

        if(StringUtils.isBlank(s)){
            return s;
        }

        char[] buf = s.toCharArray();

        for(int i = buf.length - 1; i > 0;i--){
            if(buf[i] != c){
                return String.valueOf(buf,0,i + 1);
            }
        }

        return s;
    }

    public static String toJavaGetterName(String fieldName){
        char[] chars = fieldName.toCharArray();
        chars[0] = Character.toUpperCase(chars[0]);
        return new String(chars);
    }

    public static String toJavaFiled(String fieldName){

        if(!fieldName.contains("_")){
            char firstChar = fieldName.charAt(0);
            firstChar = Character.toLowerCase(firstChar);
            return firstChar + fieldName.substring(1);
        }

        String[] words = fieldName.split("_");
        StringBuilder javaName = new StringBuilder(words[0].toLowerCase());
        for (int i = 1; i < words.length; i++) {
            javaName.append(Character.toUpperCase(words[i].charAt(0)))
                    .append(words[i].substring(1).toLowerCase());
        }
        return javaName.toString();
    }

    public static String toJavaClass(String name){
        String[] parts = name.split("_");
        StringBuilder camelCaseName = new StringBuilder();

        for (String part : parts) {
            camelCaseName.append(Character.toUpperCase(part.charAt(0)))
                    .append(part.substring(1).toLowerCase());
        }

        return camelCaseName.toString();
    }

    public static String toClassName(String name,String suffix){
        return toJavaClass(name) + suffix;
    }


    public static String toSnake(String s){

        if (s == null || s.isEmpty()) {
            return "";
        }

        // Insert underscores before each uppercase letter and convert the string to lowercase
        return s.replaceAll("([a-z])([A-Z]+)", "$1_$2").toLowerCase();
    }

    public static String toSnakeMiddle(String s){

        if (s == null || s.isEmpty()) {
            return "";
        }

        // Insert underscores before each uppercase letter and convert the string to lowercase
        return s.replaceAll("([a-z])([A-Z]+)", "$1-$2").toLowerCase();
    }

    public static String pkgToPath(String pkg){
        return pkg.replace(".", File.separator);
    }

    public static String pkgToPath(String pkg,String fileSuffix){
        return pkg.replace(".", File.separator) + fileSuffix;
    }

    public static String pkgToCamel(String input) {

        // 如果输入为空或者只包含空格，直接返回空字符串
        if (input == null || input.trim().isEmpty()) {
            return "";
        }

        if(!input.contains(".")){
            return input;
        }

        // 使用点分隔输入字符串，并去除空白部分
        String[] parts = input.split("\\.");

        // 构建StringBuilder用于拼接结果
        StringBuilder camelCaseString = new StringBuilder();

        for (String part : parts) {
            // 跳过空的部分
            if (part.isEmpty()) {
                continue;
            }
            if (camelCaseString.length() == 0) {
                // 第一个部分保持小写
                camelCaseString.append(part.toLowerCase());
            } else {
                // 其余部分首字母大写，其它小写
                camelCaseString.append(Character.toUpperCase(part.charAt(0)));
                camelCaseString.append(part.substring(1).toLowerCase());
            }
        }
        return camelCaseString.toString();
    }



    public static String unionFile(String... paths){

        var path = new String[paths.length - 1];
        var file = paths[paths.length - 1];

        for (int i = 0; i < paths.length - 1; i++) {
            path[i] = paths[i];
        }

        file = file.replace("/","");
        file = file.replace("\\","");

        var filePath = unionPath(path);
        filePath = filePath + File.separator + file;
        return filePath;
    }


    public static String unionPath(String... paths) {

        if (paths == null || paths.length == 0) {
            return "";
        }

        StringBuilder result = new StringBuilder();

        for (String path : paths) {

            if(path.equals("-")){
                continue;
            }

            if (path == null || path.isEmpty()) {
                continue;
            }

            path = pkgToPath(path);

            // 使用正则表达式将反斜杠替换为正斜杠，并去除重复的斜杠
            path = path.replaceAll("[/\\\\]+", "/");

            if (result.length() > 0) {
                if (result.charAt(result.length() - 1) != '/' && !path.startsWith("/")) {
                    result.append('/');
                } else if (result.charAt(result.length() - 1) == '/' && path.startsWith("/")) {
                    path = path.substring(1);
                }
            }

            result.append(path);
        }

        return result.toString();
    }


    public static void main(String[] args) {


    }
}
