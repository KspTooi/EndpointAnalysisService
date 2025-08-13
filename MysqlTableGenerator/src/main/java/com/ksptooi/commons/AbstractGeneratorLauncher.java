package com.ksptooi.commons;

import com.ksptooi.mybatis.MtGenerator;
import com.ksptooi.utils.DatabaseTools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;

public abstract class AbstractGeneratorLauncher implements GeneratorLauncher {

    protected final static Logger log = LoggerFactory.getLogger(MtGenerator.class);
    protected final GeneratorDataSource dataSource;
    protected Connection connection;
    protected DatabaseTools dbTool;

    public AbstractGeneratorLauncher(GeneratorDataSource dataSource){
        this.dataSource = dataSource;
    }

    public void initDataSource(){

        try{

            Class.forName(dataSource.getDriverName());
            String url = "jdbc:mysql://#{host}/#{database}#{params}";
            url = url.replace("#{host}", dataSource.getDbHost());
            url = url.replace("#{database}", dataSource.getDbName());
            url = url.replace("#{params}", dataSource.getParams());
            connection = DriverManager.getConnection(url, dataSource.getDbUserName(), this.dataSource.getDbPassword());

            log.info("数据源初始化成功:{}",url);
            dbTool = new DatabaseTools(connection);

        }catch(Exception e){
            // Handle errors for Class.forName
            e.printStackTrace();
            throw new RuntimeException(e);
        }

    }




}
