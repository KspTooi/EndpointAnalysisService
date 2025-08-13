package com.ksptooi.association.nhost;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ksptooi.association.VK;
import com.ksptooi.association.protocols.NetProtocols;
import com.ksptooi.ng.Artifact;
import com.ksptooi.ng.MultiTaskLauncher;
import com.ksptooi.ng.Option;
import com.ksptooi.utils.DirectoryTools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.lang.reflect.Type;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class NuclearSession {

    private static final Logger log = LoggerFactory.getLogger(NuclearSession.class);
    private String clientId = UUID.randomUUID().toString();
    private final Gson gson = new Gson();

    private NuclearHost server;

    private Socket socket;
    private OutputStream os;
    private ExecutorService pool;
    private Future<?> tRcv;

    private final Lock lockRcvPushFile = new ReentrantLock();

    private final Map<String,String> launcherSettings = new HashMap<>();

    private String tempDirectoryName;

    public NuclearSession(Socket socket, ExecutorService pool, NuclearHost server){
        this.socket = socket;
        this.pool = pool;
        this.server = server;

        tRcv = pool.submit(()->{
            try {
                inThreadReceive();
            } catch (IOException e) {
                e.printStackTrace();
                log.info("关闭通道(RCV):{}",clientId);
                try {
                    socket.close();
                    server.removeClient(clientId);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });
        try {
            os = socket.getOutputStream();
        } catch (IOException e) {
            log.info("关闭通道(SND):{}",clientId);
            try {
                socket.close();
                server.removeClient(clientId);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    public void inThreadReceive() throws IOException {

        var is = socket.getInputStream();

        log.info("打开通道(RCV):{}",clientId);

        while(true){

            var buf = new byte[4096];
            var len = is.read(buf);

            if(len == -1){
                break;
            }

            var cmd = buf[0];

            if(cmd == VK.H_RESULT_PUSH){
                NetProtocols.resolve(lockRcvPushFile);
                continue;
            }

            if(cmd == VK.H_RCV_TXT){
                var ret = NetProtocols.rcvTxt(buf,len,is);
                continue;
            }

            if(cmd == VK.H_RCV_PUSH || cmd == VK.H_RCV_PUSH_ABS){

                try{

                    if(cmd == VK.H_RCV_PUSH){
                        var f = NetProtocols.rcvPush(buf, len, is, NuclearHost.hostProperties.getWorkspace());
                        NetProtocols.pushTxt("Receive Success FileName:"+f.getName(),os);
                        NetProtocols.pushCmd(VK.H_RESULT_PUSH,os);
                    }
                    if(cmd == VK.H_RCV_PUSH_ABS){
                        var f = NetProtocols.rcvPush(buf, len, is, null);
                        NetProtocols.pushTxt("Receive Success FileName:"+f.getName(),os);
                        NetProtocols.pushCmd(VK.H_RESULT_PUSH,os);
                    }

                }catch (Exception e){
                    e.printStackTrace();
                    NetProtocols.pushTxt("Receive Failed",os);
                    NetProtocols.pushTxt(e.getMessage(),os);
                }
                continue;
            }

            if(cmd == VK.H_GET_LAUNCHERS){
                NetProtocols.pushTxt(VK.H_PUSH_LAUNCHERS,"Launchers:"+ NuclearHost.hostProperties.getLaunchers().keySet().toString(),os);
                continue;
            }

            if(cmd == VK.H_PUSH_LAUNCH_SETTINGS){
                String settingJson = NetProtocols.rcvTxt(buf, len, is);
                Type type = new TypeToken<Map<String, String>>(){}.getType();
                Map<String,String> map = gson.fromJson(settingJson, type);
                launcherSettings.putAll(map);
                NetProtocols.pushCmd(VK.H_RESPONSE_PUSH_SETTINGS,os);
                continue;
            }

            if(cmd == VK.H_GET_LAUNCH_SETTINGS){
                NetProtocols.pushTxt(VK.H_GET_LAUNCH_SETTINGS,gson.toJson(launcherSettings),os);
                continue;
            }

            if(cmd == VK.H_REQUEST_TEMP_DIR){
                String tempDirectory = createTempDirectory();
                this.tempDirectoryName = tempDirectory;
                log.info("创建临时目录:{}",tempDirectory);
                if(tempDirectory!=null){
                    NetProtocols.pushTxt(VK.H_RESPONSE_TEMP_DIR,tempDirectory,os);
                }
                continue;
            }

            if(cmd == VK.H_GET_LAUNCHER_DECLARER){

                var name = NetProtocols.rcvTxt(buf, len, is);
                var l = NuclearHost.hostProperties.getLaunchers().get(name);

                if(l == null){
                    NetProtocols.pushTxt(VK.H_RESPONSE_LAUNCHER_DECLARER,"[]",os);
                    continue;
                }

                NetProtocols.pushTxt(VK.H_RESPONSE_LAUNCHER_DECLARER,gson.toJson(l.getPipelineDeclare()),os);
                continue;
            }

            if(cmd == VK.H_REQUEST_KEEP_ALIVE){
                NetProtocols.pushCmd(VK.H_RESPONSE_KEEP_ALIVE,os);
                continue;
            }

            if(cmd == VK.H_REQUEST_LAUNCH){

                pool.submit(()->{

                    try{
                        //获取启动器名称
                        var launcherName = launcherSettings.get("nc.launcher");

                        if(launcherName == null){
                            NetProtocols.pushTxt(VK.H_RESPONSE_LAUNCH,"启动异常 未配置nc.launcher",os);
                            return;
                        }

                        if(tempDirectoryName == null){
                            NetProtocols.pushTxt(VK.H_RESPONSE_LAUNCH,"启动异常 未创建工作空间",os);
                            return;
                        }

                        //获取启动器实例
                        var launch = NuclearHost.hostProperties.getLaunchers().get(launcherName);

                        //设置全局工作文件夹为临时目录
                        launcherSettings.put("g.workspace",new File(DirectoryTools.getCurrentPath(),tempDirectoryName).getAbsolutePath());
                        launcherSettings.put("template.dir",tempDirectoryName);

                        if(launch == null){
                            NetProtocols.pushTxt(VK.H_RESPONSE_LAUNCH,"启动异常 找不到启动器:"+launcherName,os);
                            return;
                        }

                        //加载全局参数
                        launch.setGlobalOptions(launcherSettings);

                        //启动
                        try{
                            launch.launch();
                        }catch (Exception ex){
                            ex.printStackTrace();
                            //返回启动器错误内容
                            NetProtocols.pushTxt(VK.H_RESPONSE_LAUNCH,ex.getMessage(),os);
                            return;
                        }

                        launch.clear();

                        //获取构建产物
                        Artifact lastArtifact = launch.getLastArtifact();
                        List<Option> outputs = lastArtifact.getOutputs();
                        List<File> fl = new ArrayList<>();

                        for(var item : outputs){
                            var f = new File(item.getVal());
                            if(f.exists()){
                                fl.add(f);
                            }
                        }

                        NetProtocols.pushTxt(VK.H_RESPONSE_LAUNCH,"成功("+outputs.size()+" of " + fl.size()+")",os);

                        //推送文件回客户端
                        for(var artFile : fl){
                            var curDir = new File(DirectoryTools.getCurrentPath(),tempDirectoryName);
                            var rmtPath = artFile.getAbsolutePath().replace(curDir.getAbsolutePath(),"").replace(artFile.getName(),"");
                            log.info("推送产物:{} 远程路径:{}",artFile.getName(),rmtPath);
                            pushFile(rmtPath,artFile);
                        }

                    }catch (Exception e){
                        try {
                            NetProtocols.pushTxt(VK.H_RESPONSE_LAUNCH,e.getMessage(),os);
                        } catch (IOException ex) {
                            e.printStackTrace();
                        }
                        e.printStackTrace();
                    }

                });
                continue;
            }


            log.info("未知的指令码:{}",cmd);
        }
    }

    public void pushTxt(String txt) throws IOException {
        NetProtocols.pushTxt(txt,os);
    }

    public void pushFile(String rmtPath, File file) throws IOException {
        NetProtocols.pushFile(os,rmtPath,file,false);
        NetProtocols.await(lockRcvPushFile);
    }

    public void pushFileAbs(String rmtAbsPath,File file) throws IOException {
        NetProtocols.pushFile(os,rmtAbsPath,file,true);
        NetProtocols.await(lockRcvPushFile);
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    private String createTempDirectory(){

        // 获取当前时间
        LocalDateTime now = LocalDateTime.now();
        // 定义时间格式化器
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd_HH-mm-ss-SSS");
        // 格式化当前时间
        String formattedTime = now.format(formatter);

        File f = new File(DirectoryTools.getCurrentDirectory(),formattedTime);

        if(f.mkdir()){
            return f.getName();
        }
        return null;
    }
}
