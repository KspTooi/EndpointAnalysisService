package com.ksptooi.commons;

import com.ksptooi.ng.Artifact;
import com.ksptooi.ng.DataSource;
import com.ksptooi.ng.Option;
import com.ksptooi.ng.OptionRegister;
import com.ksptooi.utils.DirectoryTools;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class VelocityProcessor extends OptionRegister {

    private static final Logger log = LoggerFactory.getLogger(VelocityProcessor.class);
    private VelocityEngine ve = null;

    @Override
    public String getName() {
        return "模板构建生成器";
    }

    @Override
    public void process(DataSource ds, Artifact artifact) {

        //工件中存在ns 并且当前处理器没有配置ns
        if(artifact.containsKey("template.namespace") && !isExists("template.namespace")){
            set("template.namespace", (String)artifact.get("template.namespace"));
        }

        ifNotExists("template.path").add(System.getProperty("user.dir"));
        ifNotExists("template.dir").add("velocity");
        ifNotExists("template.namespace").add("");


        File template = DirectoryTools.findTemplate(val("template.path"), val("template.dir"));

        if(template == null){
            throw new RuntimeException("没有在"+val("template.path")+"下找到模板文件夹,请手动指定template.path");
        }

        log.info("当前选择的命名空间:{}",val("template.namespace"));

        final Properties properties = new Properties();
        properties.setProperty("resource.loader", "file");
        properties.setProperty("file.resource.loader.description", "Velocity File Resource Loader");
        properties.setProperty("file.resource.loader.path", template.getAbsolutePath());
        properties.setProperty("file.resource.loader.cache", "true");
        properties.setProperty("file.resource.loader.modificationCheckInterval", "30");
        properties.setProperty("directive.set.null.allowed", "true");
        final VelocityEngine velocityEngine = new VelocityEngine();
        velocityEngine.init(properties);
        this.ve = velocityEngine;

        //注入工件到VC中
        VelocityContext v = new VelocityContext();

        for(Map.Entry<String,Object> entry : artifact.entrySet()){
            v.put(entry.getKey(),entry.getValue());
        }

        List<Option> outputs = artifact.getOutputs();

        //根据output生成
        for(Option option : outputs){
            Template t = getTemplate(option.getKey());
            mergeAndOutput(t,v,new File(option.getVal()));
        }
        clear();
    }

    private Template getTemplate(String name){

        if(val("template.namespace").isBlank()){
            return ve.getTemplate(name);
        }

        return ve.getTemplate(val("template.namespace")+File.separator+name);
    }

    private void mergeAndOutput(Template t, VelocityContext vc, File output){

        if(output.exists()){
            if(!isExists("silence")){
                log.info("移除文件:{}",output.getAbsolutePath());
            }
        }

        File dir = output.getParentFile();

        if(!dir.exists()){
            if(!isExists("silence")){
                log.info("创建文件夹:{}",dir.getAbsolutePath());
            }
            dir.mkdirs();
        }

        StringWriter writer = new StringWriter();
        t.merge(vc, writer);


        Path outputPath = Paths.get(output.getPath());

        // create and write new file with contents from writer
        try (FileWriter fileWriter = new FileWriter(outputPath.toFile())) {
            fileWriter.write(writer.toString());
            if(!isExists("silence")){
                log.info("写出至:{}",output.getAbsolutePath());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }


}
