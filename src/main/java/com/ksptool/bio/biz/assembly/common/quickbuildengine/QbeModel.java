package com.ksptool.bio.biz.assembly.common.quickbuildengine;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Getter
@Setter
public class QbeModel {

    //表原始名称(pon) ex: local_user_account
    private String stdName;

    //用于简写表名称
    private String stn; //表标准化名称(stn) ex: LocalUserAccount
    private String scn; //表小驼峰名称(scn) ex: localUserAccount
    private String bcn; //表大驼峰名称(bcn) ex: LocalUserAccount
    private String uln; //表下划线名称(uln) ex: local_user_account
    private String alcn; //表全小写名称(alcn) ex: localuseraccount
    private String aucn; //表全大写名称(aucn) ex: LOCALUSERACCOUNT

    //表注释
    @Setter
    private String comment;

    //字段列表
    @Setter
    private List<QbeField> fields;

    //附加字段
    private Map<String, Object> ext;

    //排序号
    @Setter
    private int seq;

    public void setStdName(String stdName) {

        if (StringUtils.isBlank(stdName)) {
            throw new IllegalArgumentException("模型标准化名称不能为空!");
        }

        StdName stdNameObj = StdName.of(stdName);
        this.stdName = stdName;
        this.stn = stdNameObj.getValue();
        this.bcn = stdNameObj.getValue();
        this.scn = stdNameObj.toSmallCamelCase();
        this.uln = stdNameObj.toUnderLineName();
        this.alcn = stdNameObj.toLowerCase();
        this.aucn = stdNameObj.toUpperCase();
    }

    /**
     * 向附加字段中添加字段
     *
     * @param key   字段名称
     * @param value 字段值
     */
    public void put(String key, Object value) {
        if (ext == null) {
            ext = new HashMap<>();
        }
        ext.put(key, value);
    }

}
