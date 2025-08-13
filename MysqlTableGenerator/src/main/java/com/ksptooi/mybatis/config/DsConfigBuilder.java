package com.ksptooi.mybatis.config;

import com.ksptooi.commons.GeneratorDataSource;

public class DsConfigBuilder {

    private GeneratorDataSource target = null;

    public DsConfigBuilder(){
        target = new GeneratorDataSource();
    }

    public DsConfigBuilder driver(String in){
        target.setDriverName(in);
        return this;
    }

    public DsConfigBuilder host(String in){
        target.setDbHost(in);
        return this;
    }

    public DsConfigBuilder dbName(String in){
        target.setDbName(in);
        return this;
    }

    public DsConfigBuilder username(String in){
        target.setDbUserName(in);
        return this;
    }

    public DsConfigBuilder password(String in){
        target.setDbPassword(in);
        return this;
    }

    public DsConfigBuilder params(String in){
        target.setParams(in);
        return this;
    }

    public DsConfigBuilder templatePath(String in){
        target.setTemplatePath(in);
        return this;
    }

    public GeneratorDataSource build(){
        return target;
    }


}
