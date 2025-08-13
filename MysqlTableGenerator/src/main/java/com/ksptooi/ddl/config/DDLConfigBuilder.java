package com.ksptooi.ddl.config;

import com.ksptooi.ddl.model.DDLGenOptions;
import com.ksptooi.mybatis.model.config.MtgGenOptions;

import java.util.Arrays;

public class DDLConfigBuilder {

    private DDLGenOptions target = null;

    public DDLConfigBuilder(){
        target = new DDLGenOptions();
    }

    public DDLConfigBuilder tableName(String s){
        target.setTableName(s);
        return this;
    }

    public DDLConfigBuilder projectName(String s){
        target.setProjectDir(s);
        return this;
    }

    public DDLConfigBuilder absoluteProjectPath(String s){
        target.setAbsoluteProjectDir(s);
        return this;
    }

    public DDLConfigBuilder entityPacket(String s){
        target.setEntitiesDir(s);
        return this;
    }

    public DDLConfigBuilder addEntity(String s){
        target.getSpecifyEntities().add(s);
        return this;
    }

    public DDLConfigBuilder addEntity(String... s){
        target.getSpecifyEntities().addAll(Arrays.stream(s).toList());
        return this;
    }

    public DDLConfigBuilder namespace(String s){
        target.setTemplateNameSpace(s);
        return this;
    }

    public DDLConfigBuilder addTableNameExcludePattern(String s){
        target.getExcludeTableName().add(s);
        return this;
    }

    public DDLConfigBuilder addTableNameExcludePattern(String... s){
        target.getExcludeTableName().addAll(Arrays.stream(s).toList());
        return this;
    }

    public DDLConfigBuilder addPrimary(String field){
        target.getPrimaryFields().add(field);
        return this;
    }

    public DDLConfigBuilder addPrimary(String... field){
        target.getPrimaryFields().addAll(Arrays.stream(field).toList());
        return this;
    }

    public DDLConfigBuilder tablePrefix(String s){
        target.setPrefix(s);
        return this;
    }

    public DDLConfigBuilder autoExecute(boolean b){
        target.setAutoExecute(b);
        return this;
    }

    public DDLConfigBuilder autoUpdate(boolean b){
        target.setAutoUpdate(b);
        return this;
    }

    public DDLConfigBuilder autoUpdatePolicy(int b){
        target.setAutoUpdatePolicy(b);
        return this;
    }

    public DDLConfigBuilder commentResolvePolicy(int b){
        target.setEntityCommentResolverPolicy(b);
        return this;
    }

    public DDLGenOptions build(){
        return target;
    }
}
