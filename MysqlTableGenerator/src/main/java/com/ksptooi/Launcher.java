package com.ksptooi;

import com.ksptooi.commons.*;
import com.ksptooi.ddl.DDLGeneratorLauncher;
import com.ksptooi.ddl.config.DDLConfigBuilder;
import com.ksptooi.ng.MultiTaskLauncher;
import com.ksptooi.nuclear.datasource.MysqlDataSource;
import com.ksptooi.nuclear.jpa.JpaGenProcWithRemote;
import com.ksptooi.nuclear.mybatis.MybatisGenProcWithRemote;
import com.ksptooi.nuclear.mybatis.MysqlDdlProc;
import com.ksptooi.nuclear.mybatis.VMNameSpaceAutoConfiguration;
import com.ksptooi.utils.DirectoryTools;

import java.io.File;

public class Launcher {

    public static void main(String[] args) {

        // 根据Dtd生成表结构
        // generateTable("EduExamProgress");

        // 根据表结构生成CRUD
        generateJpaCrud("core_excel_template", "DEAN");
//        generateJpaCrud("sys_tag_rel", "wangshuailong");
    }


    private static void generateJpaCrud(String tableName, String author) {

        MultiTaskLauncher launcher = new MultiTaskLauncher();

        // 使用Mysql数据源
        var ds = new MysqlDataSource();
        ds.set("driver", "com.mysql.cj.jdbc.Driver");
        ds.set("host", "127.0.0.1:3306");
        ds.set("username", "root");
        ds.set("password", "root");
        ds.set("params", "?serverTimezone=Asia/Shanghai&useUnicode=true&characterEncoding=UTF-8");
        ds.set("dbName", "endpoint_analysis_service");

        // DDL解析器 用于将MYSQL表转换为Mybatis生成器需要的数据格式
        var ddlProc = new MysqlDdlProc();
        ddlProc.set("tableName", tableName);

        // Mybatis生成器核心 用于构建生成工件
        var genProc = new JpaGenProcWithRemote();
        genProc.set("dir.root", "src");
        genProc.set("dir.classes", "main.java");
        genProc.set("dir.xml", "src.main.resources.mapper");
        genProc.set("pkg.root", "com.ksptooi.biz");
        genProc.set("exclude.names", "core_");

        genProc.set("gen.controller", "true");
        genProc.set("gen.service", "true");
        genProc.set("gen.repository", "true");
        genProc.set("gen.po", "true");
        genProc.set("gen.vo", "true");
        genProc.set("gen.dto", "true");

        genProc.set("logicRemoveField", "is_deleted");
        genProc.set("logicExists", "0");
        genProc.set("logicRemoved", "1");

        // VM生成器 用于将工件+模板整合 并构建输出
        var vmProc = new VelocityProcessor();
        vmProc.set("template.dir", "velocity_jpa");
        vmProc.set("template.namespace", "ns_main_no_logic_delete");
        vmProc.set("silence", "true");

        // 自动根据数据库字段判断当前需要使用哪个模板
        var vmNsAutoconfiguration = new VMNameSpaceAutoConfiguration();
        vmNsAutoconfiguration.set("vm.ns.main", "ns_main");
        vmNsAutoconfiguration.set("vm.ns.real.del", "ns_main");
        vmNsAutoconfiguration.set("logic.remove.field", "is_deleted");
        vmNsAutoconfiguration.set("author", author);
        vmNsAutoconfiguration.set("comments", "comments");

        // 构建流水线
        launcher.add(ds);
        launcher.add(ddlProc);
        launcher.add(vmNsAutoconfiguration);
        launcher.add(genProc);
        launcher.add(vmProc);
        launcher.add(new ArtifactJsonOutputProc()); // 用于输出调试工件数据
        launcher.launch();
    }

    private static void generateCrud(String tableName, String author) {

        MultiTaskLauncher launcher = new MultiTaskLauncher();

        // 使用Mysql数据源
        var ds = new MysqlDataSource();
        ds.set("driver", "com.mysql.cj.jdbc.Driver");
        ds.set("host", "192.168.10.200:3306");
        ds.set("username", "root");
        ds.set("password", "root");
        ds.set("params", "?serverTimezone=Asia/Shanghai&useUnicode=true&characterEncoding=UTF-8");
        ds.set("dbName", "databrain");

        // DDL解析器 用于将MYSQL表转换为Mybatis生成器需要的数据格式
        var ddlProc = new MysqlDdlProc();
        ddlProc.set("tableName", tableName);

        // Mybatis生成器核心 用于构建生成工件
        var genProc = new MybatisGenProcWithRemote();
        genProc.set("dir.root", "zsk-user-service");
        genProc.set("dir.classes", "src.main.java");
        genProc.set("dir.xml", "src.main.resources.mapper");
        genProc.set("pkg.root", "com.ksptooi.biz");
        genProc.set("exclude.names", "tb_");

        genProc.set("gen.controller", "true");
        genProc.set("gen.service", "true");
        genProc.set("gen.po", "true");
        genProc.set("gen.vo", "true");
        genProc.set("gen.dto", "true");


        genProc.set("logicRemoveField", "is_deleted");
        genProc.set("logicExists", "0");
        genProc.set("logicRemoved", "1");


        // VM生成器 用于将工件+模板整合 并构建输出
        var vmProc = new VelocityProcessor();
        vmProc.set("template.dir", "velocity");
        // vmProc.set("template.namespace","ns_main_no_logic_delete");
        vmProc.set("silence", "true");

        // 自动根据数据库字段判断当前需要使用哪个模板
        var vmNsAutoconfiguration = new VMNameSpaceAutoConfiguration();
        vmNsAutoconfiguration.set("vm.ns.main", "ns_main");
        vmNsAutoconfiguration.set("vm.ns.real.del", "ns_main_no_logic_delete");
        vmNsAutoconfiguration.set("logic.remove.field", "is_deleted");
        vmNsAutoconfiguration.set("author", author);
        vmNsAutoconfiguration.set("comments", "comments");

        // 构建流水线
        launcher.add(ds);
        launcher.add(ddlProc);
        launcher.add(vmNsAutoconfiguration);
        launcher.add(genProc);
        launcher.add(vmProc);
        launcher.add(new ArtifactJsonOutputProc()); // 用于输出调试工件数据
        launcher.launch();
    }

    private static void generateTable(String PoName) {
        String currentDir = System.getProperty("user.dir");
        File template = DirectoryTools.findTemplate(currentDir, "velocity_ddl");
        assert template != null;

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
                .projectName("MysqlTableGenerator") // 项目名
                .entityPacket("com.ksptooi.dtd") // 实体类包名
                .addTableNameExcludePattern("_po", "_vo", "_dto", "_dbtd") // 排除实体类命名的前缀与后缀
                .namespace("ns_mysql") // 模板命名空间
                .tablePrefix("tb_")  // 为所有表添加前缀
                .autoExecute(true)   // true开启自动执行
                .autoUpdate(true)    // true开启自动更新
                .autoUpdatePolicy(0) // 自动更新策略 0:不论如何都会删除重建 1:当表有数据时不进行动作
                .commentResolvePolicy(1); // 注释解析策略 0:注释位于成员变量前 1:注释位于成员变量之后

        // 添加实体类名称(如果不添加则默认生成该包下面全部实体类)
        opt.addEntity(PoName);

        // 添加候选主键 实体类中有该字段时将作为主键
        opt.addPrimary("id", "sid");

        GeneratorLauncher launcher = new DDLGeneratorLauncher(ds, opt.build());
        launcher.generate();
    }


}
