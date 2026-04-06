package com.ksptool.bio.biz.assembly.common.quickbuildengine;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
public class QbeField {

    //字段原始名称(qfpon) ex: user_id
    private String stdName;

    //用于简写字段名称
    private String fstn; //字段标准化名称(fstn) ex: UserId
    private String fscn; //字段小驼峰名称(fscn) ex: userId
    private String fbcn; //字段大驼峰名称(fbcn) ex: UserId
    private String fuln; //字段下划线名称(fuln) ex: user_id
    private String falcn; //字段全小写名称(falcn) ex: userid
    private String faucn; //字段全大写名称(faucn) ex: USERID

    //字段类型
    @Setter
    private String type;

    //字段长度
    @Setter
    private Integer length;

    //字段注释
    @Setter
    private String comment;

    //必填
    @Setter
    private boolean required;

    //是否主键
    @Setter
    private boolean primaryKey;

    //排序号
    @Setter
    private int seq;

    //字段支持的操作
    private boolean listQuery = true;
    private boolean listView = true;
    private boolean add = true;
    private boolean edit = true;
    private boolean details = true;

    //附加字段
    private Map<String, Object> ext;


    public void setStdName(String stdName) {
        if (StringUtils.isBlank(stdName)) {
            throw new IllegalArgumentException("字段原始名称不能为空!");
        }

        StdName stdNameObj = StdName.of(stdName);
        this.stdName = stdName;
        this.fstn = stdNameObj.getValue();
        this.fbcn = stdNameObj.getValue();
        this.fscn = stdNameObj.toSmallCamelCase();
        this.fuln = stdNameObj.toUnderLineName();
        this.falcn = stdNameObj.toLowerCase();
        this.faucn = stdNameObj.toUpperCase();
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
