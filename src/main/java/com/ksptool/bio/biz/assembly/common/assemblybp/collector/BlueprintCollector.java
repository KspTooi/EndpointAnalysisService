package com.ksptool.bio.biz.assembly.common.assemblybp.collector;


import com.ksptool.bio.biz.assembly.common.assemblybp.entity.blueprint.RawBlueprint;

import java.io.IOException;
import java.util.List;

/**
 * 蓝图收集器接口，用于收集原始蓝图
 */
public interface BlueprintCollector {

    /**
     * 收集蓝图
     *
     * @param basePath 收集路径
     * @return 原始蓝图列表
     * @throws IOException 如果收集蓝图失败,则抛出IOException
     */
    List<RawBlueprint> collect(String basePath) throws IOException;

}
