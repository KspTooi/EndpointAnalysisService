package com.ksptooi.nuclear.datasource;

import com.ksptooi.ng.Artifact;
import com.ksptooi.ng.DataSource;
import com.ksptooi.ng.OptionRegister;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;

public class MysqlDataSource extends OptionRegister implements DataSource {

    private static final Logger log = LoggerFactory.getLogger(MysqlDataSource.class);

    @Override
    public String getName() {
        return "MYSQL数据源";
    }

    private Connection connection;

    @Override
    public void init() {

        try{

            require("driver","host","username","password","params","dbName");

            Class.forName(val("driver"));

            String url = "jdbc:mysql://#{host}/#{database}#{params}";
            url = url.replace("#{host}", val("host"));
            url = url.replace("#{database}", val("dbName"));
            url = url.replace("#{params}", val("params"));
            connection = DriverManager.getConnection(url, val("username"), val("password"));

            log.info("数据源初始化成功:{}",url);

        }catch (Exception ex){
            ex.printStackTrace();
            throw new RuntimeException("数据源:"+getName()+"初始化时遇到错误");
        }

    }

    @Override
    public Object getRaw() {
        return connection;
    }


    @Override
    public void process(DataSource ds, Artifact artifact) {
        artifact.importOptions(this);
    }

}
