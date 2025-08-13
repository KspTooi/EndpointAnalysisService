package com.ksptooi;

import com.ksptooi.association.nhost.NuclearHost;
import com.ksptooi.commons.ArtifactJsonOutputProc;
import com.ksptooi.commons.GenericToolsProc;
import com.ksptooi.commons.VelocityProcessor;
import com.ksptooi.ng.MultiTaskLauncher;
import com.ksptooi.nuclear.datasource.DtdDataSource;
import com.ksptooi.nuclear.datasource.MysqlDataSource;
import com.ksptooi.nuclear.mybatis.MybatisGenProc;
import com.ksptooi.nuclear.mybatis.MybatisGenProcWithRemote;
import com.ksptooi.nuclear.mybatis.MysqlDdlProc;
import com.ksptooi.nuclear.vue.CrlfToLfProcessor;
import com.ksptooi.nuclear.vue.UnionFieldsProc;
import com.ksptooi.nuclear.vue.VueTemplateProc;

import java.io.IOException;

/**
 * 生成主机服务
 */
public class NuclearEndpoint {

    public static void main(String[] args) throws IOException {

        NuclearHost host = new NuclearHost();

        host.addLauncher("vue_launcher", vueLauncher());
        host.addLauncher("mybatis_launcher", mybatisLauncher());
        host.setPort(58000);
        host.start();
    }

    private static MultiTaskLauncher vueLauncher(){

        MultiTaskLauncher launcher = new MultiTaskLauncher();

        var ds = new DtdDataSource();
        var gen = new VueTemplateProc();
        var velocity = new VelocityProcessor();

        var genericTools = new GenericToolsProc();
        var unionFields = new UnionFieldsProc();
        var crlfToLf = new CrlfToLfProcessor();
        var artifactJsonOutput = new ArtifactJsonOutputProc();

        velocity.add("silence","true");

        launcher.add(ds);
        launcher.add(genericTools);
        launcher.add(unionFields);
        launcher.add(gen);
        launcher.add(velocity);
        launcher.add(crlfToLf);
        launcher.add(artifactJsonOutput);
        return launcher;
    }

    private static MultiTaskLauncher mybatisLauncher(){
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
        //ddlProc.set("tableName",dtd);

        //Mybatis生成器核心 用于构建生成工件
        var genProc = new MybatisGenProcWithRemote();
        //genProc.add("exclude.names","tb_");
        //genProc.add("exclude.names","sys_");
        //genProc.add("exclude.names","QRTZ_");
        genProc.add("logicRemoveField","del_status");
        genProc.add("logicExists","0");
        genProc.add("logicRemoved","1");

        //VM生成器 用于将工件+模板整合 并构建输出
        var vmProc = new VelocityProcessor();
        //vmProc.set("template.dir","velocity");
        //vmProc.set("template.namespace","ns_confidential");
        vmProc.set("silence","true");

        //构建流水线
        launcher.add(ds);
        launcher.add(ddlProc);
        launcher.add(genProc);
        launcher.add(vmProc);
        launcher.add(new ArtifactJsonOutputProc()); //用于输出调试工件数据
        return launcher;
    }



}
