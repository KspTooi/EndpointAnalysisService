package com.ksptool.bio.biz.assembly.common.assemblybp.converter;


import com.ksptool.bio.biz.assembly.common.assemblybp.entity.blueprint.PolyBlueprint;
import com.ksptool.bio.biz.assembly.common.assemblybp.entity.blueprint.RawBlueprint;
import com.ksptool.bio.biz.assembly.common.assemblybp.entity.field.PolyField;
import com.ksptool.bio.biz.assembly.common.assemblybp.entity.field.PolyTable;
import com.ksptool.bio.biz.assembly.common.assemblybp.entity.field.RawField;
import com.ksptool.bio.biz.assembly.common.assemblybp.entity.field.RawTable;

public interface PolyConverter {

    /**
     * 将原始表转换为多态表
     *
     * @param rawTable 原始表
     * @return 多态表
     */
    public PolyTable toPolyTable(RawTable rawTable);


    /**
     * 将原始字段转换为多态字段
     *
     * @param rawField 原始字段
     * @return 多态字段
     */
    public PolyField toPolyField(RawField rawField);

    /**
     * 将原始蓝图转换为多态蓝图
     *
     * @param rawBlueprint 原始蓝图
     * @return 多态蓝图
     */
    public PolyBlueprint toPolyBlueprint(RawBlueprint rawBlueprint);

    /**
     * 设置表名前缀，转换时会自动去除这些前缀
     * 例如：如果设置前缀为 "sys_"，则表名 "sys_users" 会被转换为 "Users"
     *
     * @param prefixes 表名前缀列表
     */
    void removeTablePrefixes(String... prefixes);

    /**
     * 设置字段名前缀，转换时会自动去除这些前缀
     * 例如：如果设置前缀为 "fld_"，则字段名 "fld_user_id" 会被转换为 "userId"
     *
     * @param prefixes 字段名前缀列表
     */
    void removeFieldPrefixes(String... prefixes);

}
