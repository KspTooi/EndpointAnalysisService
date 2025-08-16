package com.ksptooi.nuclear.datasource;

import com.ksptooi.ng.Artifact;
import com.ksptooi.ng.DataSource;
import com.ksptooi.ng.OptionRegister;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;

public class H2DataSource extends OptionRegister implements DataSource {

    private static final Logger log = LoggerFactory.getLogger(H2DataSource.class);

    @Override
    public String getName() {
        return "H2DB数据源";
    }

    private Connection connection;

    @Override
    public void init() {

        try {

            require("driver", "host", "dbName");
            declare("username", "用户名(可选)");
            declare("password", "密码(可选)");
            declare("params", "密码(可选)");

            Class.forName(val("driver"));

            //jdbc:h2:tcp//127.0.0.1:59000/./EndpointAnalysisService
            //jdbc:h2:tcp://127.0.0.1:59000/./EndpointAnalysisService
            String url = "jdbc:h2:tcp://#{host}/./#{database}#{params}";
            url = url.replace("#{host}", val("host"));
            url = url.replace("#{database}", val("dbName"));
            url = url.replace("#{params}", val("params"));

            String u = null;
            String p = null;

            if (getOption("username") == null) {
                u = val("username");
            }
            if (getOption("password") == null) {
                p = val("password");
            }

            if (StringUtils.isNotBlank(u) && StringUtils.isNotBlank(p)) {
                connection = DriverManager.getConnection(url, u, p);
            }

            if (StringUtils.isBlank(u) && StringUtils.isBlank(p)) {
                connection = DriverManager.getConnection(url);
            }

            log.info("数据源初始化成功:{}", url);

        } catch (Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException("数据源:" + getName() + "初始化时遇到错误");
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
