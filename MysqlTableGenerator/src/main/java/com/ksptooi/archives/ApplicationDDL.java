package com.ksptooi.archives;

import com.ksptooi.commons.GeneratorDataSource;
import com.ksptooi.commons.GeneratorLauncher;
import com.ksptooi.ddl.DDLGeneratorLauncher;
import com.ksptooi.commons.ConfigFactory;
import com.ksptooi.ddl.config.DDLConfigBuilder;
import com.ksptooi.utils.DirectoryTools;

import java.io.File;

public class ApplicationDDL {

    public static void main(String[] args) {
        generateTbByPoName("SpacialTickets");
    }

    private static void generateTbByPoName(String PoName){
        String currentDir = System.getProperty("user.dir");
        File template = DirectoryTools.findTemplate(currentDir, "velocity_ddl");
        assert template!=null;

        GeneratorDataSource ds = ConfigFactory.datasource()
                .driver("com.mysql.cj.jdbc.Driver")
                .host("192.168.10.200:3306")
                .username("root")
                .password("root")
                .params("?serverTimezone=Asia/Shanghai&useUnicode=true&characterEncoding=UTF-8")
                .dbName("industry_security_platform")
                .templatePath(template.getAbsolutePath())
                .build();

        DDLConfigBuilder opt = ConfigFactory.ddl()
                .projectName("MysqlTableGenerator") //项目名
                .entityPacket("com.ksptooi.dtd") //实体类包名
                .addTableNameExcludePattern("_po","_vo","_dto","_dbtd") //排除实体类命名的前缀与后缀
                .namespace("ns_mysql") //模板命名空间
                .tablePrefix("tb_")  //为所有表添加前缀
                .autoExecute(true)   //true开启自动执行
                .autoUpdate(true)    //true开启自动更新
                .autoUpdatePolicy(0) //自动更新策略 0:不论如何都会删除重建 1:当表有数据时不进行动作
                .commentResolvePolicy(1); //注释解析策略 0:注释位于成员变量前 1:注释位于成员变量之后

        //添加实体类名称(如果不添加则默认生成该包下面全部实体类)
        opt.addEntity(PoName);

        //添加候选主键 实体类中有该字段时将作为主键
        opt.addPrimary("id","sid");

        GeneratorLauncher launcher = new DDLGeneratorLauncher(ds,opt.build());
        launcher.generate();
    }


}
