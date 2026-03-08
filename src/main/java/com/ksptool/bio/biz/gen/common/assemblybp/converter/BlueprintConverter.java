package com.ksptool.bio.biz.gen.common.assemblybp.converter;


import com.ksptool.bio.biz.gen.common.assemblybp.entity.blueprint.PolyBlueprint;
import com.ksptool.bio.biz.gen.common.assemblybp.entity.blueprint.RawBlueprint;
import com.ksptool.bio.biz.gen.common.assemblybp.entity.field.RawTable;

/**
 * 蓝图转换器接口
 */
public interface BlueprintConverter {


    /**
     * 将原始蓝图转换为多态蓝图
     *
     * @param rawBlueprint 原始蓝图
     * @return 多态蓝图
     */
    public PolyBlueprint toPolyBlueprint(RawBlueprint rawBlueprint, RawTable rawTable);

}
