package com.ksptooi.utils;

import com.ksptooi.ddl.model.DDLGenOptions;
import com.ksptooi.ddl.model.TableDescription;
import com.ksptooi.ddl.model.TableDescriptionField;
import com.ksptooi.mybatis.model.config.MtgGenOptions;
import com.ksptooi.mybatis.model.po.TableField;
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

public class DDLVelocityWrapper {

    private static final Logger log = LoggerFactory.getLogger(DDLVelocityWrapper.class);

    private static VelocityEngine ve = null;

    private static String namespace = null;

    private static DDLGenOptions opt = null;

    public static void init(String path, DDLGenOptions opt){
        final Properties properties = new Properties();
        properties.setProperty("resource.loader", "file");
        properties.setProperty("file.resource.loader.description", "Velocity File Resource Loader");
        properties.setProperty("file.resource.loader.path", path);
        properties.setProperty("file.resource.loader.cache", "true");
        properties.setProperty("file.resource.loader.modificationCheckInterval", "30");
        properties.setProperty("directive.set.null.allowed", "true");
        final VelocityEngine velocityEngine = new VelocityEngine();
        velocityEngine.init(properties);
        ve = velocityEngine;
        DDLVelocityWrapper.opt = opt;
    }

    public static void setNamespace(String ns){
        namespace = ns;
    }

    public static Template getTemplate(String name){

        if(namespace == null){
            throw new RuntimeException("模板获取失败 没有配置有效的namespace");
        }

        return ve.getTemplate(namespace+File.separator+name);
    }


    /**
     * 注入OPT到VC中
     * @return
     */
    public static VelocityContext injectContext(TableDescription table){

        VelocityContext v = new VelocityContext();

        v.put("tableName",table.getTableName());
        v.put("primary",table.getPrimary());
        v.put("fields",table.getFields());

        //自定义参数
        for(Map.Entry<String,Object> entry : opt.getOpt().entrySet()){
            v.put(entry.getKey(),entry.getValue());
        }

        return v;
    }


    public static void mergeAndOutput(Template t, VelocityContext vc, File output){

        if(output.exists()){
            if(!opt.getSilence()){
                log.info("移除文件:{}",output.getAbsolutePath());
            }
        }

        File dir = output.getParentFile();

        if(!dir.exists()){
            if(!opt.getSilence()){
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
            if(!opt.getSilence()){
                log.info("写出至:{}",output.getAbsolutePath());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }


}
