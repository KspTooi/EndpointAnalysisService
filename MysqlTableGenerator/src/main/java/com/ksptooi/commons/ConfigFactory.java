package com.ksptooi.commons;

import com.ksptooi.ddl.config.DDLConfigBuilder;
import com.ksptooi.mybatis.config.DsConfigBuilder;
import com.ksptooi.mybatis.config.GenConfigBuilder;

public class ConfigFactory {

    public static DsConfigBuilder datasource(){
        return new DsConfigBuilder();
    }

    public static GenConfigBuilder config(){
        return new GenConfigBuilder();
    }

    public static DDLConfigBuilder ddl(){
        return new DDLConfigBuilder();
    }

}
