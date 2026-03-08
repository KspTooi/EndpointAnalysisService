package com.ksptool.bio.biz.gen.common.assemblybp.core;


import com.ksptool.bio.biz.gen.common.assemblybp.collector.BlueprintCollector;
import com.ksptool.bio.biz.gen.common.assemblybp.collector.RawCollector;
import com.ksptool.bio.biz.gen.common.assemblybp.converter.PolyConverter;
import com.ksptool.bio.biz.gen.common.assemblybp.entity.blueprint.PolyBlueprint;
import com.ksptool.bio.biz.gen.common.assemblybp.entity.blueprint.RawBlueprint;
import com.ksptool.bio.biz.gen.common.assemblybp.entity.field.PolyTable;
import com.ksptool.bio.biz.gen.common.assemblybp.entity.field.RawTable;
import com.ksptool.bio.biz.gen.common.assemblybp.projector.Projector;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Getter
public class AssemblyFactory {

    /**
     * 数据采集器
     */
    private RawCollector rawCollector;

    /**
     * 蓝图采集器
     */
    private BlueprintCollector blueprintCollector;

    /**
     * 数据转换器
     */
    @Setter
    private PolyConverter converter;

    /**
     * 投影器
     */
    @Setter
    private Projector projector;


    /**
     * 输入基准路径(蓝图文件所在路径)
     */
    private String inputBasePath;

    /**
     * 输出基准路径(生成文件的目标路径)
     */
    private String outputBasePath;


    /**
     * 执行蓝图生成
     */
    public void execute() {

        // 确保配置正确
        ensureConfiguration();

        // 收集数据
        List<RawTable> rawTables = rawCollector.collect();

        if (rawTables == null || rawTables.isEmpty()) {
            throw new RuntimeException("未能从数据源中收集到任何数据!");
        }

        // 收集蓝图
        List<RawBlueprint> blueprints = null;

        try {
            blueprints = blueprintCollector.collect(inputBasePath);
        } catch (IOException e) {
            throw new RuntimeException("无法执行蓝图生成，因为无法收集蓝图：" + e.getMessage(), e);
        }

        if (blueprints == null || blueprints.isEmpty()) {
            throw new RuntimeException("未能从基准路径" + inputBasePath + "中收集到任何蓝图!");
        }

        // 转换数据
        List<PolyTable> polyTables = new ArrayList<>();

        for (RawTable rawTable : rawTables) {
            PolyTable polyTable = converter.toPolyTable(rawTable);
            if (polyTable == null) {
                throw new RuntimeException("未能将数据 " + rawTable.getName() + " 转换为多态表!");
            }
            polyTables.add(polyTable);
        }

        // 转换蓝图
        List<PolyBlueprint> polyBlueprints = new ArrayList<>();
        for (RawBlueprint blueprint : blueprints) {
            PolyBlueprint polyBlueprint = converter.toPolyBlueprint(blueprint);
            if (polyBlueprint == null) {
                throw new RuntimeException("未能将蓝图 " + blueprint.getFileName() + " 转换为多态蓝图!");
            }
            polyBlueprints.add(polyBlueprint);
        }

        // 投影
        projector.setOutputBasePath(outputBasePath);
        projector.project(polyTables, polyBlueprints);
    }

    /**
     * 设置全局变量
     * 全局变量将在所有模板中可用，它们将会挂载在global下
     * 例如putGlobalVar("author", "张三") 将可在所有模板中使用${global.author}获取
     * 此方法会代理调用 projector.putGlobalVar(key, value)
     *
     * @param key   变量名，不能为空
     * @param value 变量值，不能为空
     */
    public void putGlobalVar(String key, String value) {
        if (projector == null) {
            throw new RuntimeException("投影器未配置，无法设置全局变量!");
        }
        projector.putGlobalVar(key, value);
    }

    /**
     * 启用或禁用 projector.map.json 文件生成
     *
     * @param enabled 是否启用
     */
    public void enableProjectorMap(boolean enabled) {
        if (projector == null) {
            throw new RuntimeException("投影器未配置，无法设置 projector.map 开关!");
        }
        projector.enableProjectorMap(enabled);
    }

    /**
     * 设置文件覆盖开关
     * 当开启时，如果文件已存在则覆盖源文件
     * 当关闭时，如果文件已存在则跳过投影并记录日志
     *
     * @param enabled 是否启用覆盖
     */
    public void setOverwriteEnabled(boolean enabled) {
        if (projector == null) {
            throw new RuntimeException("投影器未配置，无法设置覆盖开关!");
        }
        projector.setOverwriteEnabled(enabled);
    }

    /**
     * 选择要收集的表
     * 此方法会代理调用 rawCollector.select(tableNames)
     *
     * @param tableNames 表名列表
     */
    public void selectTables(String... tableNames) {
        if (rawCollector == null) {
            throw new RuntimeException("数据采集器未配置，无法选择表!");
        }
        rawCollector.select(tableNames);
    }

    /**
     * 设置表名前缀，转换时会自动去除这些前缀
     * 此方法会代理调用 converter.removeTablePrefixes(prefixes)
     *
     * @param prefixes 表名前缀列表
     */
    public void removeTablePrefixes(String... prefixes) {
        if (converter == null) {
            throw new RuntimeException("数据转换器未配置，无法设置表名前缀!");
        }
        converter.removeTablePrefixes(prefixes);
    }

    /**
     * 设置字段名前缀，转换时会自动去除这些前缀
     * 此方法会代理调用 converter.removeFieldPrefixes(prefixes)
     *
     * @param prefixes 字段名前缀列表
     */
    public void removeFieldPrefixes(String... prefixes) {
        if (converter == null) {
            throw new RuntimeException("数据转换器未配置，无法设置字段名前缀!");
        }
        converter.removeFieldPrefixes(prefixes);
    }


    /**
     * 设置数据采集器
     *
     * @param rawCollector 数据采集器
     */
    public void setCollector(RawCollector rawCollector) {
        this.rawCollector = rawCollector;
    }

    /**
     * 设置蓝图采集器
     *
     * @param blueprintCollector 蓝图采集器
     */
    public void setCollector(BlueprintCollector blueprintCollector) {
        this.blueprintCollector = blueprintCollector;
    }


    /**
     * 确保配置正确
     */
    public void ensureConfiguration() {
        if (rawCollector == null) {
            throw new RuntimeException("无法执行蓝图生成，因为数据采集器未配置!");
        }
        if (blueprintCollector == null) {
            throw new RuntimeException("无法执行蓝图生成，因为蓝图采集器未配置!");
        }
        if (converter == null) {
            throw new RuntimeException("无法执行蓝图生成，因为数据转换器未配置!");
        }
        if (projector == null) {
            throw new RuntimeException("无法执行蓝图生成，因为投影器未配置!");
        }
        validateBasePath();
    }

    /**
     * 设置输入基准路径(绝对路径)
     *
     * @param absolutePath 绝对路径
     */
    public void setInputBasePath(String absolutePath) {
        if (StringUtils.isBlank(absolutePath)) {
            throw new IllegalArgumentException("输入基准路径不能为空!");
        }
        Path path = Paths.get(absolutePath).normalize();
        this.inputBasePath = path.toAbsolutePath().toString();
        log.debug("设置输入基准路径(绝对路径)：{}", this.inputBasePath);
    }

    /**
     * 设置输出基准路径(绝对路径)
     *
     * @param absolutePath 绝对路径
     */
    public void setOutputBasePath(String absolutePath) {
        if (StringUtils.isBlank(absolutePath)) {
            throw new IllegalArgumentException("输出基准路径不能为空!");
        }
        Path path = Paths.get(absolutePath).normalize();
        this.outputBasePath = path.toAbsolutePath().toString();
        log.debug("设置输出基准路径(绝对路径)：{}", this.outputBasePath);
    }

    /**
     * 设置输入基准路径(相对路径，相对于当前工作目录)
     *
     * @param relativePath 相对路径
     */
    public void setInputBasePathFromRelative(String relativePath) {
        if (StringUtils.isBlank(relativePath)) {
            throw new IllegalArgumentException("输入基准路径不能为空!");
        }
        Path currentDir = Paths.get(System.getProperty("user.dir"));
        Path path = currentDir.resolve(relativePath).normalize();
        this.inputBasePath = path.toAbsolutePath().toString();
        log.debug("设置输入基准路径(相对路径：{})：{}", relativePath, this.inputBasePath);
    }

    /**
     * 设置输出基准路径(相对路径，相对于当前工作目录)
     *
     * @param relativePath 相对路径
     */
    public void setOutputBasePathFromRelative(String relativePath) {
        if (StringUtils.isBlank(relativePath)) {
            throw new IllegalArgumentException("输出基准路径不能为空!");
        }
        Path currentDir = Paths.get(System.getProperty("user.dir"));
        Path path = currentDir.resolve(relativePath).normalize();
        this.outputBasePath = path.toAbsolutePath().toString();
        log.debug("设置输出基准路径(相对路径：{})：{}", relativePath, this.outputBasePath);
    }

    /**
     * 校验基准路径
     */
    private void validateBasePath() {
        if (StringUtils.isBlank(inputBasePath)) {
            throw new RuntimeException("无法执行蓝图生成，因为输入基准路径未配置!");
        }
        if (StringUtils.isBlank(outputBasePath)) {
            throw new RuntimeException("无法执行蓝图生成，因为输出基准路径未配置!");
        }

        Path path = Paths.get(inputBasePath);
        if (!Files.exists(path)) {
            throw new RuntimeException("无法执行蓝图生成，因为输入基准路径不存在：" + inputBasePath);
        }

        if (!Files.isDirectory(path)) {
            throw new RuntimeException("无法执行蓝图生成，因为输入基准路径不是目录：" + inputBasePath);
        }

        try {
            boolean hasContent;
            try (var stream = Files.list(path)) {
                hasContent = stream.findAny().isPresent();
            }
            if (!hasContent) {
                throw new RuntimeException("无法执行蓝图生成，因为输入基准路径目录为空：" + inputBasePath);
            }
        } catch (IOException e) {
            throw new RuntimeException("无法执行蓝图生成，因为无法读取输入基准路径目录：" + inputBasePath, e);
        }
    }

}
