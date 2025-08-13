package com.ksptooi.association.nhost;

import com.ksptooi.commons.ArtifactJsonOutputProc;
import com.ksptooi.commons.ArtifactPushProcessor;
import com.ksptooi.commons.GenericToolsProc;
import com.ksptooi.commons.VelocityProcessor;
import com.ksptooi.ng.MultiTaskLauncher;
import com.ksptooi.nuclear.datasource.DtdDataSource;
import com.ksptooi.nuclear.datasource.MysqlDataSource;
import com.ksptooi.nuclear.mybatis.MybatisGenProc;
import com.ksptooi.nuclear.mybatis.MysqlDdlProc;
import com.ksptooi.nuclear.vue.CrlfToLfProcessor;
import com.ksptooi.nuclear.vue.UnionFieldsProc;
import com.ksptooi.nuclear.vue.VueTemplateProc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 生成主机
 */
public class NuclearHost {

    private static final Logger log = LoggerFactory.getLogger(NuclearHost.class);

    public static final HostProperties hostProperties = new HostProperties();

    private ServerSocket server;

    private ExecutorService threadPool = Executors.newCachedThreadPool();

    private Map<String, NuclearSession> clientMap = new HashMap<>();

    public void start() throws IOException {

        server = new ServerSocket(hostProperties.getPort());

        while (true){
            var accept = server.accept();
            var client = new NuclearSession(accept,threadPool,this);
            clientMap.put(client.getClientId(),client);
            client.pushTxt("Client ID:"+client.getClientId());
        }

    }

    public void setPort(int port){
        hostProperties.setPort(port);
    }

    public void addLauncher(String name, MultiTaskLauncher launcher){
        hostProperties.getLaunchers().put(name,launcher);
    }

    public void setWorkspace(String path){
        setWorkspace(new File(path));
    }
    public void setWorkspace(File f){
        if(!f.exists() || !f.isDirectory()){
            log.error("设置工作空间失败,路径:{}不存在或不是文件夹.",f.getAbsolutePath());
            return;
        }
        hostProperties.setWorkspace(f);
    }


    public void removeClient(String clientId){
        this.clientMap.remove(clientId);
        log.info("移除客户端:{}",clientId);
    }

}
