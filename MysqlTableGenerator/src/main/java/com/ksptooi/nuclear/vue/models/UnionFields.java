package com.ksptooi.nuclear.vue.models;

import java.util.HashMap;

public class UnionFields {

    String name; //字段名
    String label; //中文名
    String tsType; //字段TS类型
    String javaType; //Java类型
    String comment;//注释
    String mOptions; //model附加选项
    String require; //是否必填
    String hint; //校验失败提示
    String placeholder; //提示
    String dateFormat; //时间格式 默认yyyy-MM-dd hh:mm:ss
    String vType; //视图类型 input select radio date

    String initVal; //字段初始值 规则:string number为'' 日期为 null

    boolean listParam; //是否为 列表查询条件
    boolean listRet;   //是否为 列表返回字段

    HashMap<String,String> inlineDict; //内联字典格式 [{"标签1","值"},{"标签2","值"}]
    String inlineDictJson; //内联字典JSON
    String dictName; //当前字段的外部字典值

    public String getInlineDictJson() {
        return inlineDictJson;
    }

    public void setInlineDictJson(String inlineDictJson) {
        this.inlineDictJson = inlineDictJson;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getTsType() {
        return tsType;
    }

    public void setTsType(String tsType) {
        this.tsType = tsType;
    }

    public String getJavaType() {
        return javaType;
    }

    public void setJavaType(String javaType) {
        this.javaType = javaType;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getmOptions() {
        return mOptions;
    }

    public void setmOptions(String mOptions) {
        this.mOptions = mOptions;
    }

    public String getRequire() {
        return require;
    }

    public void setRequire(String require) {
        this.require = require;
    }

    public String getHint() {
        return hint;
    }

    public void setHint(String hint) {
        this.hint = hint;
    }

    public String getPlaceholder() {
        return placeholder;
    }

    public void setPlaceholder(String placeholder) {
        this.placeholder = placeholder;
    }


    public boolean isListParam() {
        return listParam;
    }

    public void setListParam(boolean listParam) {
        this.listParam = listParam;
    }

    public boolean isListRet() {
        return listRet;
    }

    public void setListRet(boolean listRet) {
        this.listRet = listRet;
    }

    public HashMap<String, String> getInlineDict() {
        return inlineDict;
    }

    public void setInlineDict(HashMap<String, String> inlineDict) {
        this.inlineDict = inlineDict;
    }

    public String getDictName() {
        return dictName;
    }

    public void setDictName(String dictName) {
        this.dictName = dictName;
    }

    public String getvType() {
        return vType;
    }

    public void setvType(String vType) {
        this.vType = vType;
    }

    public String getDateFormat() {
        return dateFormat;
    }

    public void setDateFormat(String dateFormat) {
        this.dateFormat = dateFormat;
    }

    public String getInitVal() {
        return initVal;
    }

    public void setInitVal(String initVal) {
        this.initVal = initVal;
    }
}
