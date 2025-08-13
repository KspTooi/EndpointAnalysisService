package com.ksptooi.nuclear.mybatis;

import com.ksptooi.ng.Artifact;
import com.ksptooi.ng.DataSource;
import com.ksptooi.ng.OptionRegister;
import com.ksptooi.utils.TextConv;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;

public class MybatisGenProc extends OptionRegister {

    private static final Logger log = LoggerFactory.getLogger(MybatisGenProc.class);

    @Override
    public String getName() {
        return "Mybatis生成处理器";
    }

    @Override
    public void process(DataSource ds, Artifact a) {

        String tableName = (String) a.require("tableName");

        //用于排除某些名称前缀
        for(String exclude:vals("excludeNames")){
            tableName = tableName.replace(exclude,"");
            set("tableName",tableName);
        }

        String businessName = TextConv.toJavaFiled(tableName).toLowerCase();

        //构建大写的类名
        addIfNotPresent("controllerName",TextConv.toClassName(tableName,"Controller"));
        addIfNotPresent("serviceName",TextConv.toClassName(tableName,"Service"));
        addIfNotPresent("serviceImplName",TextConv.toClassName(tableName,"Service"));
        addIfNotPresent("voName",TextConv.toClassName(tableName,"Vo"));
        addIfNotPresent("poName",TextConv.toClassName(tableName,"Po"));
        addIfNotPresent("dtoName",TextConv.toClassName(tableName,"Dto"));
        addIfNotPresent("mapperName",TextConv.toClassName(tableName,"Mapper"));

        //构建顶级包名
        String pkgName = require("packetName");

        //pkgName以.结尾
        if(pkgName.endsWith(".")){
            pkgName = pkgName.substring(0,pkgName.length() - 1);
            set("packetName",pkgName);
        }

        //自动配置包名
        ifExists("pkgNameController").set(TextConv.unionPacket(pkgName,val("pkgNameController")));
        ifExists("pkgNameService").set(TextConv.unionPacket(pkgName,val("pkgNameService")));
        ifExists("pkgNameServiceImpl").set(TextConv.unionPacket(pkgName,val("pkgNameServiceImpl")));
        ifExists("pkgNameVo").set(TextConv.unionPacket(pkgName,val("pkgNameVo")));
        ifExists("pkgNamePo").set(TextConv.unionPacket(pkgName,val("pkgNamePo")));
        ifExists("pkgNameDto").set(TextConv.unionPacket(pkgName,val("pkgNameDto")));
        ifExists("pkgNameMapper").set(TextConv.unionPacket(pkgName,val("pkgNameMapper")));

        ifNotExists("pkgNameController").add(pkgName+".controller");
        ifNotExists("pkgNameService").add(pkgName+".services");
        ifNotExists("pkgNameServiceImpl").add(pkgName+".services");
        ifNotExists("pkgNameVo").add(pkgName+".models.mv."+businessName);
        ifNotExists("pkgNamePo").add(pkgName+".models.mv."+businessName);
        ifNotExists("pkgNameDto").add(pkgName+".models.mv."+businessName);
        ifNotExists("pkgNameMapper").add(pkgName+".mapper");

        //自动配置字段名
        ifNotExists("fieldControllerName").add(TextConv.toJavaFiled(val("controllerName")));
        ifNotExists("fieldServiceName").add(TextConv.toJavaFiled(val("serviceName")));
        ifNotExists("fieldServiceImplName").add(TextConv.toJavaFiled(val("serviceImplName")));
        ifNotExists("fieldVoName").add(TextConv.toJavaFiled(val("voName")));
        ifNotExists("fieldPoName").add(TextConv.toJavaFiled(val("poName")));
        ifNotExists("fieldDtoName").add(TextConv.toJavaFiled(val("dtoName")));
        ifNotExists("fieldMapperName").add(TextConv.toJavaFiled(val("mapperName")));

        //自动配置其他
        ifNotExists("classTableName").add(TextConv.toJavaClass(tableName));
        ifNotExists("requestMapping").add(TextConv.toJavaFiled(val("controllerName").replace("Controller","")));


        String require = require("projectName");

        String path = System.getProperty("user.dir");

        File mavenProject = findMavenProject(new File(path), require);

        if(mavenProject == null){
            log.error("自动配置失败,在所有路径中都无法查找到项目:{}", require);
            throw new RuntimeException("自动配置失败,在所有路径中都无法查找到项目:"+require);
        }

        File javaPath = new File(mavenProject,"\\src\\main\\java");
        ifNotExists("outputPath").add(javaPath.getAbsolutePath());
        ifNotExists("outputPathXml").add(new File(javaPath.getParentFile(), TextConv.pkgToPath(".resources.mapper.")).getAbsolutePath());

        String outParent = require("outputPath");

        String outController = TextConv.pkgToPath(val("pkgNameController")) + "\\"+ val("controllerName") + ".java";
        String outService = TextConv.pkgToPath(val("pkgNameService")) + "\\"+ val("serviceName") + ".java";
        String outMapper = TextConv.pkgToPath(val("pkgNameMapper")) + "\\"+ val("mapperName") + ".java";
        String outMapperXml = TextConv.pkgToPath(val("outputPathXml")) + "\\"+ val("mapperName") + ".xml";

        String outPo = TextConv.pkgToPath(val("pkgNamePo")) + "\\"+ val("poName") + ".java";

        String outListVo = TextConv.pkgToPath(val("pkgNameVo")) + "\\"+ "List"+ val("voName") + ".java";
        String outGetVo = TextConv.pkgToPath(val("pkgNameVo")) + "\\"+ "Get"+ val("voName") + ".java";

        String outListDto = TextConv.pkgToPath(val("pkgNameDto")) + "\\"+ "List" + val("dtoName") + ".java";
        String outInsertDto = TextConv.pkgToPath(val("pkgNameDto")) + "\\"+ "Insert" + val("dtoName") + ".java";
        String outUpdateDto = TextConv.pkgToPath(val("pkgNameDto")) + "\\"+ "Update" + val("dtoName") + ".java";

        ifNotExists("genController").add("true");
        ifNotExists("genService").add("true");
        ifNotExists("withImpl").add("true");
        ifNotExists("genPo").add("true");
        ifNotExists("genVo").add("true");
        ifNotExists("genDto").add("true");
        ifNotExists("genMapper").add("true");


        if(val("genController").equals("true")){
            a.addTemplateOutput("controller.vm",new File(outParent,outController).getAbsolutePath());
        }

        if(val("genService").equals("true")){
            a.addTemplateOutput("service.vm",new File(outParent,outService).getAbsolutePath());
        }

        if(val("genPo").equals("true")){
            a.addTemplateOutput("po.vm",new File(outParent,outPo).getAbsolutePath());
        }

        if(val("genVo").equals("true")){
            a.addTemplateOutput("vo_list.vm",new File(outParent,outListVo).getAbsolutePath());
            a.addTemplateOutput("vo_get.vm",new File(outParent,outGetVo).getAbsolutePath());
        }

        if(val("genDto").equals("true")){
            a.addTemplateOutput("dto_list.vm",new File(outParent,outListDto).getAbsolutePath());
            a.addTemplateOutput("dto_insert.vm",new File(outParent,outInsertDto).getAbsolutePath());
            a.addTemplateOutput("dto_update.vm",new File(outParent,outUpdateDto).getAbsolutePath());
        }

        if(val("genMapper").equals("true")){
            a.addTemplateOutput("mapper.vm",new File(outParent,outMapper).getAbsolutePath());
            a.addTemplateOutput("mapper_xml.vm",new File(outMapperXml).getAbsolutePath());
        }

        a.importOptions(this);
    }


    /**
     * 递归向上查找父级路径下是否存在Maven项目
     */
    public File findMavenProject(File path,String projectName){

        if(path == null){
            return null;
        }

        //当前路径是否有项目
        File f = new File(path,projectName);

        if(f.exists() && isMavenProject(f.getAbsolutePath())){
            return f;
        }

        //递归查找上级
        return findMavenProject(path.getParentFile(),projectName);
    }

    public boolean isMavenProject(String path){
        boolean hasSrc = Files.exists(Paths.get(path + "\\src"));
        boolean hasMain = Files.exists(Paths.get(path + "\\src\\main"));
        boolean hasJava = Files.exists(Paths.get(path + "\\src\\main\\java"));
        return hasSrc && hasMain && hasJava;
    }



}
