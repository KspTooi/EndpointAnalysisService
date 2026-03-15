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
    private String qmpon;

    //用于简写表名称
    private String qmstn; //表标准化名称(qmstn) ex: LocalUserAccount
    private String qmscn; //表小驼峰名称(qmscn) ex: localUserAccount
    private String qmbcn; //表大驼峰名称(qmbcn) ex: LocalUserAccount
    private String qmuln; //表下划线名称(qmuln) ex: local_user_account
    private String qmalcn; //表全小写名称(qmalcn) ex: localuseraccount
    private String qmaucn; //表全大写名称(qmaucn) ex: LOCALUSERACCOUNT

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

    public void setPon(String pon) {

        if (StringUtils.isBlank(pon)) {
            throw new IllegalArgumentException("表原始名称不能为空!");
        }

        StdName stdNameObj = StdName.of(pon);
        this.qmpon = pon;
        this.qmstn = stdNameObj.getValue();
        this.qmbcn = stdNameObj.getValue();
        this.qmscn = stdNameObj.toSmallCamelCase();
        this.qmuln = stdNameObj.toUnderLineName();
        this.qmalcn = stdNameObj.toLowerCase();
        this.qmaucn = stdNameObj.toUpperCase();
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
