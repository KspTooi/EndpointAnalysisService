package com.ksptooi.association;

import com.ksptooi.ng.Artifact;
import com.ksptooi.ng.DataSource;
import com.ksptooi.ng.OptionRegister;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 分支端点 使用纯TCP协议接收主机推送文件
 */
public class ForkEndpoint {

    private static final Logger log = LoggerFactory.getLogger(ForkEndpoint.class);
    private final int port;
    private ServerSocket server;
    private final ExecutorService executor = Executors.newFixedThreadPool(8);

    protected File workspace;

    public ForkEndpoint(int port){
        this.port = port;
    }

    public void start(){
        log.info("fork endpoint now running at:{}",port);
        try {
            server = new ServerSocket(port);
            while (!server.isClosed()){
                Socket socket = server.accept();
                executor.submit(new ForkSession(executor,socket));
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public void stop(){
        try {
            server.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    //设置工作空间
    public void setWorkspace(String path){

        File dir = new File(path);

        if(!dir.isDirectory() || !dir.exists()){
            throw new RuntimeException("工作空间不可用. " + dir.getAbsolutePath());
        }



    }


}
