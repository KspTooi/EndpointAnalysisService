package com.ksptool.bio.biz.assembly.common.quickbuildengine;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.app.VelocityEngine;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

@Slf4j
public class QbeVelocityEngine {

    //gson实例
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    //velocity引擎实例
    private VelocityEngine velocityEngine;

    //输出基准路径
    private String outputBasePath;

    //文件覆盖开关
    private boolean overwriteEnabled = false;

    //全局变量 这些变量将在所有模板中可用 它们将会挂载在global下 例如putGlobalVar("name", "value") 将可在所有模板中使用${global.name}获取
    private Map<String, String> globalVars = new HashMap<>();


    public QbeVelocityEngine() {
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
    public void putGlobalVar(String key, String value) {
        if (StringUtils.isBlank(key)) {
            throw new IllegalArgumentException("全局变量名不能为空!");
        }
        if (StringUtils.isBlank(value)) {
            throw new IllegalArgumentException("全局变量值不能为空!");
        }
        globalVars.put(key, value);
        log.info("设置全局变量：{} = {}", key, value);
    }

    /**
     * 设置输出基准路径(绝对路径)
     * 如果路径已被手动设置，则后续调用将被忽略
     *
     * @param absolutePath 绝对路径
     */
    public void setOutputBasePath(String absolutePath) {

        if (StringUtils.isBlank(absolutePath)) {
            throw new IllegalArgumentException("输出基准路径不能为空!");
        }

        Path path = Paths.get(absolutePath).normalize();
        this.outputBasePath = path.toAbsolutePath().toString();
        log.info("设置输出基准路径(绝对路径)：{}", this.outputBasePath);
    }

    /**
     * 渲染蓝图
     *
     * @param model      QBE模型
     * @param blueprints 蓝图列表
     */
    public void render(QbeModel model, List<QbeBlueprint> blueprints) {

        if (model == null) {
            throw new IllegalArgumentException("模型不能为空!");
        }
        if (blueprints == null || blueprints.isEmpty()) {
            throw new IllegalArgumentException("蓝图列表不能为空!");
        }

        if (StringUtils.isBlank(outputBasePath)) {
            throw new IllegalArgumentException("输出基准路径不能为空!");
        }

        Map<String, Object> modelMap = new HashMap<>();

        //将模型和全局变量添加到模型映射中
        modelMap.put("model", model);
        modelMap.put("global", globalVars);

        //渲染蓝图
        for (QbeBlueprint blueprint : blueprints) {
            String templateContent = blueprint.getTemplateContent();
            String renderedContent = renderTemplate(templateContent, modelMap);
            blueprint.setRenderedContent(renderedContent);
        }
    }

}
