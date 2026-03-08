package com.ksptool.bio.biz.gen.common.assemblybp.projector;


import com.ksptool.bio.biz.gen.common.assemblybp.entity.blueprint.PolyBlueprint;
import com.ksptool.bio.biz.gen.common.assemblybp.entity.field.PolyTable;

import java.util.List;

/*
 * 投影器接口，用于将多态表投影为代码文件
 */
public interface Projector {

    /**
     * 设置全局变量
     * 全局变量将在所有模板中可用 它们将会挂载在global下 例如putGlobalVar("name", "value") 将可在所有模板中使用${global.name}获取
     *
     * @param key   变量名，不能为空
     * @param value 变量值，不能为空
     */
    void putGlobalVar(String key, String value);

    /**
     * 设置输出基准路径(绝对路径)
     * 如果路径已被手动设置，则后续调用将被忽略
     *
     * @param absolutePath 绝对路径
     */
    void setOutputBasePath(String absolutePath);

    /**
     * 设置输出基准路径(相对路径，相对于当前工作目录)
     * 如果路径已被手动设置，则后续调用将被忽略
     *
     * @param relativePath 相对路径
     */
    void setOutputBasePathFromRelative(String relativePath);

    /**
     * 启用或禁用 projector.map.json 文件生成
     *
     * @param enabled 是否启用
     */
    void enableProjectorMap(boolean enabled);

    /**
     * 设置文件覆盖开关
     * 当开启时，如果文件已存在则覆盖源文件
     * 当关闭时，如果文件已存在则跳过投影并记录日志
     *
     * @param enabled 是否启用覆盖
     */
    void setOverwriteEnabled(boolean enabled);

    /**
     * 投影多态表和蓝图为代码文件
     *
     * @param polyTables     多态表列表
     * @param polyBlueprints 多态蓝图列表
     */
    void project(List<PolyTable> polyTables, List<PolyBlueprint> polyBlueprints);

}
