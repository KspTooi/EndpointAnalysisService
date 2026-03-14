package com.ksptool.bio.biz.assembly.common.assemblybp.collector;

import com.ksptool.bio.biz.assembly.common.assemblybp.entity.field.RawTable;

import java.util.List;

public interface RawCollector {

    /**
     * 选择要收集的表
     * 必须先调用此方法指定要处理的表，然后才能调用 collect 方法
     *
     * @param tableNames 表名列表
     */
    void select(String... tableNames);

    /**
     * 收集原始表
     * 必须先调用 select 方法指定要处理的表，否则将抛出异常
     *
     * @return 原始表列表
     */
    public List<RawTable> collect();

}
