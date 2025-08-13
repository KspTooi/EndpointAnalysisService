package com.ksptooi.archives;

import com.ksptooi.commons.ArtifactJsonOutputProc;
import com.ksptooi.commons.VelocityProcessor;
import com.ksptooi.ng.MultiTaskLauncher;
import com.ksptooi.nuclear.datasource.MysqlDataSource;
import com.ksptooi.nuclear.mybatis.MybatisGenProcWithRemote;
import com.ksptooi.nuclear.mybatis.MysqlDdlProc;

public class TableToCrud {


    public static void main(String[] args) {

        generate("sys_menu_copy1");

    }

    public static void generate(String dtd){

        MultiTaskLauncher launcher = new MultiTaskLauncher();

        //使用Mysql数据源
        var ds = new MysqlDataSource();
        ds.set("driver","com.mysql.cj.jdbc.Driver");
        ds.set("host","192.168.10.200:3306");
        ds.set("username","root");
        ds.set("password","root");
        ds.set("params","?serverTimezone=Asia/Shanghai&useUnicode=true&characterEncoding=UTF-8");
        ds.set("dbName","industry_security_platform");

        //DDL解析器 用于将MYSQL表转换为Mybatis生成器需要的数据格式
        var ddlProc = new MysqlDdlProc();
        ddlProc.set("tableName",dtd);

        //Mybatis生成器核心 用于构建生成工件
        var genProc = new MybatisGenProcWithRemote();
        genProc.set("dir.root","baodian-back");
        genProc.set("dir.classes","src.main.java");
        genProc.set("dir.xml","src.main.resources.mapper");
        genProc.set("pkg.root","com.baodian.back");

        //VM生成器 用于将工件+模板整合 并构建输出
        var vmProc = new VelocityProcessor();
        vmProc.set("template.dir","velocity");
        vmProc.set("template.namespace","ns_confidential");
        vmProc.set("silence","true");

        //构建流水线
        launcher.add(ds);
        launcher.add(ddlProc);
        launcher.add(genProc);
        launcher.add(vmProc);
        launcher.add(new ArtifactJsonOutputProc()); //用于输出调试工件数据
        launcher.launch();
    }


}
