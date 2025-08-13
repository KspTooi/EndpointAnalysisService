package com.ksptooi.mybatis.config;

import com.ksptooi.mybatis.generator.Generator;
import com.ksptooi.mybatis.model.config.MtgGenOptions;

import java.io.File;
import java.util.Arrays;

public class GenConfigBuilder {

    private MtgGenOptions target = null;


    public GenConfigBuilder(){
        target = new MtgGenOptions();
    }


    public GenConfigBuilder addNameExclude(String s){
        target.getExcludeNames().add(s);
        return this;
    }

    public GenConfigBuilder addNameExclude(String... s){
        target.getExcludeNames().addAll(Arrays.stream(s).toList());
        return this;
    }

    public GenConfigBuilder genController(boolean b){
        target.setGenController(b);
        return this;
    }

    public GenConfigBuilder genService(boolean b){
        target.setGenService(b);
        return this;
    }

    public GenConfigBuilder withImpl(boolean b){
        target.setWithImpl(b);
        return this;
    }

    public GenConfigBuilder genPo(boolean b){
        target.setGenPo(b);
        return this;
    }

    public GenConfigBuilder genVo(boolean in){
        target.setGenVo(in);
        return this;
    }

    public GenConfigBuilder genDto(boolean in){
        target.setGenDto(in);
        return this;
    }

    public GenConfigBuilder genMapper(boolean b){
        target.setGenMapper(b);
        return this;
    }


    /**
     * 配置基础包名
     * 所有的类都会生成在基础包下面
     */
    public GenConfigBuilder packetName(String in){
        target.setPacketName(in);
        return this;
    }

    //配置控制器类名称
    public GenConfigBuilder controllerName(String in){
        target.setControllerName(in);
        return this;
    }

    //配置服务类名称
    public GenConfigBuilder serviceName(String in){
        target.setServiceName(in);
        return this;
    }

    //配置服务实现类名称
    public GenConfigBuilder serviceImplName(String in){
        target.setServiceImplName(in);
        return this;
    }

    //配置PO类名
    public GenConfigBuilder poName(String in){
        target.setPoName(in);
        return this;
    }

    //配置VO类名
    public GenConfigBuilder voName(String in){
        target.setVoName(in);
        return this;
    }

    //配置DTO类名
    public GenConfigBuilder dtoName(String in){
        target.setDtoName(in);
        return this;
    }

    //配置Mapper类名
    public GenConfigBuilder mapperName(String in){
        target.setMapperName(in);
        return this;
    }

    //配置数据库表名
    public GenConfigBuilder tableName(String in) {
        target.setTableName(in);
        return this;
    }

    /**
     * 指定数据库表主键字段名
     * !如果不指定会进行自动配置
     */
    public GenConfigBuilder primaryField(String in){
        target.setPrimaryField(in);
        return this;
    }

    //配置控制器类所在包名
    public GenConfigBuilder packetNameController(String in){
        target.setPkgNameController(in);
        return this;
    }

    //配置服务类所在包名
    public GenConfigBuilder packetNameService(String in){
        target.setPkgNameService(in);
        return this;
    }

    //配置服务实现类所在包名
    public GenConfigBuilder packetNameServiceImpl(String in){
        target.setPkgNameServiceImpl(in);
        return this;
    }

    //配置PO类所在包名
    public GenConfigBuilder packetNamePo(String in){
        target.setPkgNamePo(in);
        return this;
    }

    //配置VO类所在包名
    public GenConfigBuilder packetNameVo(String in){
        target.setPkgNameVo(in);
        return this;
    }

    //配置DTO类所在包名
    public GenConfigBuilder packetNameDto(String in){
        target.setPkgNameDto(in);
        return this;
    }

    //配置Mapper接口类所在包名
    public GenConfigBuilder packetNameMapper(String in){
        target.setPkgNameMapper(in);
        return this;
    }

    //配置MapperXML文件所在包名
    public GenConfigBuilder outputXmlTo(String f){
        target.setOutputXmlPath(new File(f));
        return this;
    }
    public GenConfigBuilder packetMapperXml(String s){
        target.setPkgNameMapperXml(s);
        return this;
    }

    /**
     * 配置Maven项目名称
     * 代码生成器会将类输出到指定Maven子项目目录中
     */
    public GenConfigBuilder projectName(String in){
        target.setProjectName(in);
        return this;
    }

    /**
     * 配置模板的命名空间
     * 会以指定命名空间中的模板执行代码生成
     */
    public GenConfigBuilder namespace(String s){
        target.setTemplateNameSpace(s);
        return this;
    }

    /**
     * 向容器中添加自定义值 可以在生成模板中获取到添加的自定义值
     * @param k 自定义Key
     * @param v 自定义Val
     */
    public GenConfigBuilder put(String k,Object v){
        target.getOpt().put(k,v);
        return this;
    }

    /**
     * 插件配置
     */
    public GenConfigBuilder enableLombok(boolean b){
        target.setEnableLombok(b);
        return this;
    }

    public GenConfigBuilder enableMybatisPlus(boolean b){
        target.setEnableMybatisPlus(b);
        return this;
    }

    public GenConfigBuilder enableSwagger2(boolean b){
        target.setEnableSwagger2(b);
        return this;
    }

    public GenConfigBuilder outputTo(String f){
        target.setOutputPath(new File(f));
        return this;
    }

    public GenConfigBuilder silence(boolean b){
        target.setSilence(b);
        return this;
    }

    /**
     * 添加额外的生成器
     */
    public GenConfigBuilder addGenerator(Generator generator){
        target.getGenerators().add(generator);
        return this;
    }


    public MtgGenOptions build(){
        return target;
    }

}
