package com.ksptool.bio.biz.gen.common.assemblybp.projector;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.ksptool.bio.biz.gen.common.assemblybp.entity.blueprint.PolyBlueprint;
import com.ksptool.bio.biz.gen.common.assemblybp.entity.field.PolyTable;
import com.ksptool.text.PreparedPrompt;
import org.apache.commons.lang3.StringUtils;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.app.VelocityEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

/**
 * Velocity投影器实现
 * 使用Velocity模板引擎进行投影
 */
public class VelocityProjector implements Projector {

    //日志记录器
    private static final Logger logger = LoggerFactory.getLogger(VelocityProjector.class);
    //gson实例
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    //velocity引擎实例
    private VelocityEngine velocityEngine;
    //输出基准路径
    private String outputBasePath;
    //输出基准路径是否已被手动设置
    private boolean outputBasePathManuallySet = false;
    //projector.map.json 生成开关
    private boolean projectorMapEnabled = false;
    //文件覆盖开关
    private boolean overwriteEnabled = false;
    //全局变量 这些变量将在所有模板中可用 它们将会挂载在global下 例如putGlobalVar("name", "value") 将可在所有模板中使用${global.name}获取
    private Map<String, String> globalVars = new HashMap<>();

    public VelocityProjector() {
        initVelocityEngine();
    }

    private void initVelocityEngine() {
        velocityEngine = new VelocityEngine();
        velocityEngine.setProperty("runtime.log.logsystem.class", "org.apache.velocity.runtime.log.NullLogSystem");
        velocityEngine.init();

        Properties props = new Properties();
        props.setProperty("runtime.log.logsystem.class", "org.apache.velocity.runtime.log.NullLogSystem");
        Velocity.init(props);
    }

    /**
     * 设置全局变量
     * 全局变量将在所有模板中可用 它们将会挂载在global下 例如putGlobalVar("name", "value") 将可在所有模板中使用${global.name}获取
     *
     * @param key   变量名，不能为空
     * @param value 变量值，不能为空
     */
    @Override
    public void putGlobalVar(String key, String value) {
        if (StringUtils.isBlank(key)) {
            throw new IllegalArgumentException("全局变量名不能为空!");
        }
        if (StringUtils.isBlank(value)) {
            throw new IllegalArgumentException("全局变量值不能为空!");
        }
        globalVars.put(key, value);
        logger.info("设置全局变量：{} = {}", key, value);
    }

    /**
     * 设置输出基准路径(绝对路径)
     * 如果路径已被手动设置，则后续调用将被忽略
     *
     * @param absolutePath 绝对路径
     */
    @Override
    public void setOutputBasePath(String absolutePath) {
        if (outputBasePathManuallySet) {
            logger.warn("输出基准路径已被手动设置，忽略后续设置请求：{}", absolutePath);
            return;
        }
        if (StringUtils.isBlank(absolutePath)) {
            throw new IllegalArgumentException("输出基准路径不能为空!");
        }
        Path path = Paths.get(absolutePath).normalize();
        this.outputBasePath = path.toAbsolutePath().toString();
        this.outputBasePathManuallySet = true;
        logger.debug("设置输出基准路径(绝对路径)：{}", this.outputBasePath);
    }

    /**
     * 设置输出基准路径(相对路径，相对于当前工作目录)
     * 如果路径已被手动设置，则后续调用将被忽略
     *
     * @param relativePath 相对路径
     */
    @Override
    public void setOutputBasePathFromRelative(String relativePath) {
        if (outputBasePathManuallySet) {
            logger.warn("输出基准路径已被手动设置，忽略后续设置请求(相对路径：{})", relativePath);
            return;
        }
        if (StringUtils.isBlank(relativePath)) {
            throw new IllegalArgumentException("输出基准路径不能为空!");
        }
        Path currentDir = Paths.get(System.getProperty("user.dir"));
        Path path = currentDir.resolve(relativePath).normalize();
        this.outputBasePath = path.toAbsolutePath().toString();
        this.outputBasePathManuallySet = true;
        logger.debug("设置输出基准路径(相对路径：{})：{}", relativePath, this.outputBasePath);
    }

    /**
     * 启用或禁用 projector.map.json 文件生成
     *
     * @param enabled 是否启用
     */
    @Override
    public void enableProjectorMap(boolean enabled) {
        this.projectorMapEnabled = enabled;
        logger.debug("projector.map.json 生成开关：{}", enabled ? "启用" : "禁用");
    }

    /**
     * 设置文件覆盖开关
     * 当开启时，如果文件已存在则覆盖源文件
     * 当关闭时，如果文件已存在则跳过投影并记录日志
     *
     * @param enabled 是否启用覆盖
     */
    @Override
    public void setOverwriteEnabled(boolean enabled) {
        this.overwriteEnabled = enabled;
        logger.debug("文件覆盖开关：{}", enabled ? "启用" : "禁用");
    }

    /**
     * 投影多态表和蓝图为代码文件
     *
     * @param polyTables     多态表列表
     * @param polyBlueprints 多态蓝图列表
     */
    @Override
    public void project(List<PolyTable> polyTables, List<PolyBlueprint> polyBlueprints) {
        if (polyTables == null || polyTables.isEmpty()) {
            logger.warn("多态表列表为空，无法执行投影");
            return;
        }
        if (polyBlueprints == null || polyBlueprints.isEmpty()) {
            logger.warn("多态蓝图列表为空，无法执行投影");
            return;
        }
        if (StringUtils.isBlank(outputBasePath)) {
            throw new RuntimeException("输出基准路径未配置!");
        }

        Path outputBase = Paths.get(outputBasePath);
        if (!Files.exists(outputBase)) {
            try {
                Files.createDirectories(outputBase);
            } catch (IOException e) {
                throw new RuntimeException("无法创建输出目录：" + outputBasePath, e);
            }
        }

        logger.info("开始投影，表数量：{}，蓝图数量：{}", polyTables.size(), polyBlueprints.size());

        for (PolyTable polyTable : polyTables) {
            Map<String, String> promptParams = buildPromptParams(polyTable);

            for (PolyBlueprint polyBlueprint : polyBlueprints) {
                try {
                    processBlueprint(polyTable, polyBlueprint, promptParams, outputBase);
                } catch (Exception e) {
                    logger.error("处理蓝图失败，表：{}，蓝图：{}", polyTable.getPtstn(),
                            polyBlueprint.getRelativePathWithPlaceholder(), e);
                }
            }
        }

        logger.info("投影完成 所有产物已输出至基准目录:{}", outputBasePath);
    }

    /**
     * 构建与 projector.map.json 完全一致的变量映射
     * 确保模板中使用的变量与 projector.map.json 中的变量完全一致
     *
     * @param polyTable 多态表
     * @return 变量映射
     */
    private Map<String, String> buildPromptParams(PolyTable polyTable) {
        Map<String, String> params = new HashMap<>();
        params.put("PTSTN", polyTable.getPtstn());
        params.put("PTSCN", polyTable.getPtscn());
        params.put("PTBCN", polyTable.getPtbcn());
        params.put("PTULN", polyTable.getPtuln());
        params.put("PTALCN", polyTable.getPtalcn());
        params.put("PTAUCN", polyTable.getPtaucn());
        return params;
    }

    /**
     * 处理蓝图
     *
     * @param polyTable     多态表
     * @param polyBlueprint 多态蓝图
     * @param promptParams  变量映射
     * @param outputBase    输出基准路径
     * @throws IOException 如果处理过程中发生IO异常
     */
    private void processBlueprint(PolyTable polyTable, PolyBlueprint polyBlueprint,
                                  Map<String, String> promptParams, Path outputBase) throws IOException {
        String relativePathWithPlaceholder = polyBlueprint.getRelativePathWithPlaceholder();
        String fileNameWithPlaceholder = polyBlueprint.getFileNameWithPlaceholder();

        if (StringUtils.isBlank(relativePathWithPlaceholder)) {
            logger.warn("蓝图相对路径为空，跳过处理");
            return;
        }
        if (StringUtils.isBlank(fileNameWithPlaceholder)) {
            logger.warn("蓝图文件名为空，跳过处理");
            return;
        }

        PreparedPrompt pathPrompt = PreparedPrompt.prepare(relativePathWithPlaceholder);
        pathPrompt.setParameters(promptParams);
        String resolvedRelativePath = pathPrompt.execute(false);

        PreparedPrompt fileNamePrompt = PreparedPrompt.prepare(fileNameWithPlaceholder);
        fileNamePrompt.setParameters(promptParams);
        String resolvedFileName = fileNamePrompt.execute(false);

        if (resolvedFileName.endsWith(".vm")) {
            resolvedFileName = resolvedFileName.substring(0, resolvedFileName.length() - 3);
        }

        Path resolvedRelativePathObj = Paths.get(resolvedRelativePath);
        Path resolvedDir = resolvedRelativePathObj.getParent();
        if (resolvedDir == null) {
            resolvedDir = Paths.get("");
        }

        Path outputFilePath = outputBase.resolve(resolvedDir).resolve(resolvedFileName).normalize();

        if (!outputFilePath.startsWith(outputBase)) {
            throw new RuntimeException("输出路径超出基准目录范围：" + outputFilePath);
        }

        String templateContent = polyBlueprint.getTemplateContent();
        if (StringUtils.isBlank(templateContent)) {
            logger.warn("蓝图模板内容为空，跳过处理");
            return;
        }

        String renderedContent = renderTemplate(templateContent, polyTable);

        Files.createDirectories(outputFilePath.getParent());

        if (Files.exists(outputFilePath)) {
            if (overwriteEnabled) {
                logger.info("文件已存在，覆盖文件：{}", outputFilePath);
                Files.writeString(outputFilePath, renderedContent);
                logger.debug("已覆盖文件：{}", outputFilePath);
            } else {
                logger.warn("文件已存在，跳过投影（覆盖开关已关闭）：{}", outputFilePath);
                return;
            }
        } else {
            Files.writeString(outputFilePath, renderedContent);
            logger.debug("生成文件：{}", outputFilePath);
        }

        if (projectorMapEnabled) {
            generateProjectorMap(polyTable, outputBase);
        }
    }

    /**
     * 构建与 projector.map.json 完全一致的变量映射
     * 确保模板中使用的变量与 projector.map.json 中的变量完全一致
     *
     * @param polyTable 多态表
     * @return 变量映射
     */
    private Map<String, Object> buildTemplateVariables(PolyTable polyTable) {
        Map<String, Object> map = new HashMap<>();

        map.put("ptstn", polyTable.getPtstn());
        map.put("ptscn", polyTable.getPtscn());
        map.put("ptbcn", polyTable.getPtbcn());
        map.put("ptuln", polyTable.getPtuln());
        map.put("ptalcn", polyTable.getPtalcn());
        map.put("ptaucn", polyTable.getPtaucn());
        map.put("comment", polyTable.getComment());
        map.put("seq", polyTable.getSeq());

        if (polyTable.getFields() != null) {
            List<Map<String, Object>> fields = new ArrayList<>();
            for (var field : polyTable.getFields()) {
                Map<String, Object> fieldMap = new HashMap<>();
                fieldMap.put("stdName", field.getStdName());
                fieldMap.put("pfstn", field.getPfstn());
                fieldMap.put("pfscn", field.getPfscn());
                fieldMap.put("pfbcn", field.getPfbcn());
                fieldMap.put("pfuln", field.getPfuln());
                fieldMap.put("pfalcn", field.getPfalcn());
                fieldMap.put("pfaucn", field.getPfaucn());
                fieldMap.put("type", field.getType());
                fieldMap.put("comment", field.getComment());
                fieldMap.put("required", field.isRequired());
                fieldMap.put("primaryKey", field.isPrimaryKey());
                fieldMap.put("seq", field.getSeq());
                fields.add(fieldMap);
            }
            map.put("fields", fields);
        }

        if (polyTable.getImports() != null) {
            List<String> imports = new ArrayList<>();
            for (var imp : polyTable.getImports()) {
                imports.add(imp.getPath());
            }
            map.put("imports", imports);
        }

        return map;
    }

    /**
     * 生成 projector.map.json 文件
     *
     * @param polyTable  多态表
     * @param outputBase 输出基准路径
     */
    private void generateProjectorMap(PolyTable polyTable, Path outputBase) {
        try {
            Map<String, Object> map = buildTemplateVariables(polyTable);

            // 添加全局变量到映射中，确保与模板上下文一致
            if (globalVars != null && !globalVars.isEmpty()) {
                map.put("global", globalVars);
                logger.debug("已将 {} 个全局变量添加到 projector.map.json", globalVars.size());
            }

            String json = gson.toJson(map);

            Path mapFile = outputBase.resolve("projector.map.json");
            Files.writeString(mapFile, json);
            logger.info("已生成 projector.map.json 文件：{}", mapFile);
        } catch (IOException e) {
            logger.error("生成 projector.map.json 文件失败", e);
        }
    }

    /**
     * 渲染模板
     *
     * @param templateContent 模板内容
     * @param polyTable       多态表
     * @return 渲染后的内容
     */
    private String renderTemplate(String templateContent, PolyTable polyTable) {
        VelocityContext context = new VelocityContext();

        // 使用与 projector.map.json 完全一致的变量映射
        // 确保模板中使用的变量与 projector.map.json 中的变量完全一致
        Map<String, Object> variables = buildTemplateVariables(polyTable);
        for (Map.Entry<String, Object> entry : variables.entrySet()) {
            context.put(entry.getKey(), entry.getValue());
        }

        // 注入全局变量
        // 全局变量挂载在global对象下，例如：${global.author}
        if (globalVars != null && !globalVars.isEmpty()) {
            context.put("global", globalVars);
            logger.debug("已注入 {} 个全局变量到模板上下文", globalVars.size());
        }

        StringWriter writer = new StringWriter();
        boolean success = Velocity.evaluate(context, writer, "template", templateContent);

        if (!success) {
            throw new RuntimeException("无法渲染模板，表：" + polyTable.getPtstn());
        }

        return writer.toString();
    }

}

