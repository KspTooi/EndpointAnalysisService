package com.ksptool.bio.biz.assembly.common.quickbuildengine;

import com.ksptool.bio.biz.assembly.common.assemblybp.entity.field.PolyField;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class QbeModel {

    //表标准化名称(ptstn) ex: LocalUserAccount
    private String stdName;

    //用于简写表名称
    private String ptstn; //表标准化名称(ptstn) ex: LocalUserAccount
    private String ptscn; //表小驼峰名称(ptscn) ex: localUserAccount
    private String ptbcn; //表大驼峰名称(ptbcn) ex: LocalUserAccount
    private String ptuln; //表下划线名称(ptuln) ex: local_user_account
    private String ptalcn; //表全小写名称(ptalcn) ex: localuseraccount
    private String ptaucn; //表全大写名称(ptaucn) ex: LOCALUSERACCOUNT

    //表注释
    @Setter
    private String comment;

    //字段列表
    @Setter
    private List<PolyField> fields;

    //附加字段
    private Map<String, Object> appendFields;

    //排序号
    @Setter
    private int seq;

    public void setStdName(String stdName) {
        if (StringUtils.isBlank(stdName)) {
            this.stdName = stdName;
            this.ptstn = stdName;
            this.ptscn = "";
            this.ptbcn = "";
            this.ptuln = "";
            this.ptalcn = "";
            this.ptaucn = "";
            return;
        }

        StdName stdNameObj = StdName.of(stdName);
        this.stdName = stdNameObj.getValue();
        this.ptstn = stdNameObj.getValue();
        this.ptbcn = stdNameObj.getValue();
        this.ptscn = stdNameObj.toSmallCamelCase();
        this.ptuln = stdNameObj.toUnderLineName();
        this.ptalcn = stdNameObj.toLowerCase();
        this.ptaucn = stdNameObj.toUpperCase();
    }

    /**
     * 向附加字段中添加字段
     *
     * @param key   字段名称
     * @param value 字段值
     */
    public void put(String key, Object value) {
        if (appendFields == null) {
            appendFields = new HashMap<>();
        }
        appendFields.put(key, value);
    }

}
