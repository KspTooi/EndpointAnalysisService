package com.ksptooi.commons;

import org.springframework.beans.factory.annotation.Value;
import org.h2.tools.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.sql.SQLException;

@Component
public class H2Server {

    private final Logger log = LoggerFactory.getLogger(H2Server.class.getName());

    private int port = 1109;

    private Server tcpServer;
    private Server memServer;

    public H2Server(){
        this(1109);
    }

    public H2Server(int port) {
        this.port = port;
    }

    public void start() throws SQLException {
        tcpServer = Server.createTcpServer("-tcpPort", ""+port, "-tcpAllowOthers","-ifNotExists").start();
        log.info("H2数据库引擎已启动(TCP模式) 端口:"+port);
    }

    public void stop() {
        if(tcpServer != null) {
            tcpServer.stop();
            log.info("H2数据库引擎已停止(TCP模式) 端口:"+port);
        }
    }


    public static void main(String[] args) throws SQLException {
        H2Server h2Server = new H2Server(1109);
        h2Server.start();
    }
}
