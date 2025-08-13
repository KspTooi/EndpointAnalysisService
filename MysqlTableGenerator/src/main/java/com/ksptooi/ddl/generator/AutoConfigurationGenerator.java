package com.ksptooi.ddl.generator;

import com.ksptooi.ddl.model.DDLGenOptions;
import com.ksptooi.utils.DirectoryTools;
import com.ksptooi.utils.TextConv;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Text;

import java.io.File;
import java.io.FilenameFilter;
import java.nio.file.Files;
import java.nio.file.Paths;

public class AutoConfigurationGenerator implements DDLGenerator{

    private static final Logger log = LoggerFactory.getLogger(AutoConfigurationGenerator.class);

    @Override
    public void generate(DDLGenOptions options) {

        String absoluteProjectDir = options.getAbsoluteProjectDir();
        String projectDir = options.getProjectDir();

        if(StringUtils.isBlank(options.getEntitiesDir())){
            throw new RuntimeException("自动配置出现错误,没有配置entitiesDir.");
        }

        if(StringUtils.isBlank(absoluteProjectDir) && StringUtils.isBlank(projectDir)){
            throw new RuntimeException("自动配置出现错误,没有配置absoluteProjectDir或者projectDir.");
        }

        if(StringUtils.isBlank(absoluteProjectDir)){
            String path = System.getProperty("user.dir");
            File mavenProject = DirectoryTools.findMavenProject(new File(path), projectDir);

            if(mavenProject == null){
                throw new RuntimeException("无法进行自动配置 因为路径下不存在Maven项目. 路径:"+path + " :: "+ projectDir);
            }

            log.info("已查找到Maven项目:{}",mavenProject.getAbsolutePath());

            String root = new File(mavenProject,"\\src\\main\\java").getAbsolutePath();

            log.info("[自动配置]项目根目录为:{}",root);
            options.setAbsoluteProjectDir(root);
        }

        //解析实体类绝对路径
        File aed = new File(options.getAbsoluteProjectDir(), TextConv.pkgToPath(options.getEntitiesDir()));

        if(!aed.exists()){
            throw new RuntimeException("自动配置出现错误,无法找到路径:"+aed.getAbsolutePath());
        }

        if(!aed.isDirectory()){
            throw new RuntimeException("自动配置出现错误,路径不合法:"+aed.getAbsolutePath());
        }

        log.info("[自动配置]实体类绝对路径为:{}",aed.getAbsolutePath());
        options.setAbsoluteEntitiesDir(aed.getAbsolutePath());

        //如果指定了解析实体 则检查实体文件完整性
        if(!options.getSpecifyEntities().isEmpty()){

            File[] files = aed.listFiles((dir, name) -> name.endsWith(".java"));

            if(files == null){
                throw new RuntimeException("自动配置出现错误,路径:"+aed.getAbsolutePath() +"下找不到指定的实体类.");
            }

            for(String specify : options.getSpecifyEntities()){

                boolean found = false;

                for(File f : files){

                    String entityName = f.getName().replace(".java","");

                    if(entityName.equals(specify)){
                        found = true;
                    }

                }

                if(!found){
                    throw new RuntimeException("\r\n自动配置出现错误\r\n路径:"+aed.getAbsolutePath() +"\r\n找不到指定的实体类:"+specify);
                }

            }

        }

        if(options.getAutoExecute() == null){
            options.setAutoExecute(true);
        }
        if(options.getAutoUpdate() == null){
            options.setAutoUpdate(true);
        }
        if(options.getAutoUpdatePolicy() == null){
            options.setAutoUpdatePolicy(1);
        }
        if(options.getEntityCommentResolverPolicy() == null){
            options.setEntityCommentResolverPolicy(0);
        }

        if(options.getAutoExecute()){
            log.info("自动创建表:是");
        }else {
            log.info("自动创建表:否");
        }

        if(options.getAutoUpdate()){
            log.info("自动更新表:是");
        }else {
            log.info("自动更新表:否");
        }

        log.info("自动更新策略配置为:"+options.getAutoUpdatePolicy());
        log.info("表注释解析策略配置为:"+options.getEntityCommentResolverPolicy());

        if(options.getPrefix() == null){
            log.info("添加表前缀:无");
        }else {
            log.info("添加表前缀:"+options.getPrefix());
        }

        if(options.getSuffix() == null){
            log.info("添加表后缀:无");
        }else {
            log.info("添加表后缀:"+options.getSuffix());
        }

        if(StringUtils.isBlank(options.getTemplateNameSpace())){
            throw new RuntimeException("\r\n自动配置失败\r\n没有配置模板命名空间");
        }

        File template = new File(new File(options.getTemplatePath(),options.getTemplateNameSpace()),"table.vm");

        if(!template.exists()){
            throw new RuntimeException("\r\n自动配置失败\r\n路径:"+template.getAbsolutePath()+"\r\n命名空间下模板不存在");
        }

        log.info("模板文件:"+template.getAbsolutePath());
        options.setAbsoluteTemplateFile(template.getAbsolutePath());
    }

    @Override
    public String getName() {
        return "自动配置生成器";
    }

}
