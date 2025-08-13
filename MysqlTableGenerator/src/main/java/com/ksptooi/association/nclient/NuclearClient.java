package com.ksptooi.association.nclient;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ksptooi.association.VK;
import com.ksptooi.association.nhost.NuclearHost;
import com.ksptooi.association.protocols.NetProtocols;
import com.ksptooi.ng.OptionDeclare;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.net.Socket;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class NuclearClient {

    private static final Logger log = LoggerFactory.getLogger(NuclearClient.class);
    private final ExecutorService threadPool = Executors.newCachedThreadPool();
    private Socket socket = null;
    private OutputStream os = null;

    private final Lock lockRcvLaunchers = new ReentrantLock();
    private final Lock lockRcvLauncherDeclare  = new ReentrantLock();
    private final Lock lockRcvLauncherSettings = new ReentrantLock();
    private final Lock lockRcvRequestCreateTemp = new ReentrantLock();
    private final Lock lockRcvPushSettings = new ReentrantLock();
    private final Lock lockRcvRequestLaunch = new ReentrantLock();
    private final Lock lockRcvPushFile = new ReentrantLock();
    private final Lock lockRcvKeepAlive = new ReentrantLock();

    private final Map<String,String> dataRcvLauncherSettings = new ConcurrentHashMap<>();
    private volatile String dataRcvLaunchers = "";
    private volatile String dataRcvRequestCreateTemp = null;
    private volatile String dataRcvRequestLaunch= null;
    private volatile String dataRcvLauncherDeclare= null;

    private final Gson gson = new Gson();

    public static void main(String[] args) throws IOException, InterruptedException {

        var client = new NuclearClient("127.0.0.1",58000);

        var set = new HashMap<String,String>();

        set.put("dtd.path","EmergencyGoods");
        set.put("view.title","模板管理");
        set.put("path.root","src.");
        set.put("path.api",".api.methods");
        set.put("path.model",".api.models");
        set.put("path.views",".views");
        set.put("path.catalog","special.");
        set.put("tableName",       "tb_spacial_record_fire");
        set.put("dir.root"  ,      "baodian-back");
        set.put("dir.classes",     "src.main.java");
        set.put("dir.xml" ,"src.main.resources.mapper");
        set.put("pkg.root" ,       "com.baodian.back");

        set.put("nc.launcher","mybatis_launcher");

        //同步启动器设置到远程
        client.pushSettings(set);

        //创建工作空间
        var workspace = client.requestCreateTempDir();
        System.out.println(workspace);

        //传输文件到远程工作空间
        client.pushFile(workspace,new File("C:\\InternalDev\\NanliProject\\industrysecureplatform\\MysqlTableGenerator\\src\\main\\resources\\velocity\\ns_confidential\\controller.vm"));
        client.pushFile(workspace,new File("C:\\InternalDev\\NanliProject\\industrysecureplatform\\MysqlTableGenerator\\src\\main\\resources\\velocity\\ns_confidential\\dto.vm"));
        client.pushFile(workspace,new File("C:\\InternalDev\\NanliProject\\industrysecureplatform\\MysqlTableGenerator\\src\\main\\resources\\velocity\\ns_confidential\\dto_insert.vm"));
        client.pushFile(workspace,new File("C:\\InternalDev\\NanliProject\\industrysecureplatform\\MysqlTableGenerator\\src\\main\\resources\\velocity\\ns_confidential\\dto_list.vm"));
        client.pushFile(workspace,new File("C:\\InternalDev\\NanliProject\\industrysecureplatform\\MysqlTableGenerator\\src\\main\\resources\\velocity\\ns_confidential\\dto_update.vm"));
        client.pushFile(workspace,new File("C:\\InternalDev\\NanliProject\\industrysecureplatform\\MysqlTableGenerator\\src\\main\\resources\\velocity\\ns_confidential\\mapper.vm"));
        client.pushFile(workspace,new File("C:\\InternalDev\\NanliProject\\industrysecureplatform\\MysqlTableGenerator\\src\\main\\resources\\velocity\\ns_confidential\\mapper_xml.vm"));
        client.pushFile(workspace,new File("C:\\InternalDev\\NanliProject\\industrysecureplatform\\MysqlTableGenerator\\src\\main\\resources\\velocity\\ns_confidential\\po.vm"));
        client.pushFile(workspace,new File("C:\\InternalDev\\NanliProject\\industrysecureplatform\\MysqlTableGenerator\\src\\main\\resources\\velocity\\ns_confidential\\service.vm"));
        client.pushFile(workspace,new File("C:\\InternalDev\\NanliProject\\industrysecureplatform\\MysqlTableGenerator\\src\\main\\resources\\velocity\\ns_confidential\\vo.vm"));
        client.pushFile(workspace,new File("C:\\InternalDev\\NanliProject\\industrysecureplatform\\MysqlTableGenerator\\src\\main\\resources\\velocity\\ns_confidential\\vo_get.vm"));
        client.pushFile(workspace,new File("C:\\InternalDev\\NanliProject\\industrysecureplatform\\MysqlTableGenerator\\src\\main\\resources\\velocity\\ns_confidential\\vo_list.vm"));
        client.pushFile(workspace,new File("C:\\InternalDev\\NanliProject\\industrysecureplatform\\MysqlTableGenerator\\src\\main\\resources\\velocity\\ns_confidential\\vo_remove.vm"));

        //发射并获取发射结果
        String result = client.requestLaunch();
        System.out.println(result);
    }

    public NuclearClient(String host, int port) throws IOException {
        socket = new Socket(host,port);
        log.info("Connect To:{}:{}",host,port);

        //打开通道
        threadPool.submit(()->{
            try {
                inThreadReceive();
            } catch (IOException e) {
                e.printStackTrace();
                log.info("关闭通道(RCV):{}","?");
                try {
                    socket.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });
        os = socket.getOutputStream();
    }

    public void inThreadReceive() throws IOException {

        var is = socket.getInputStream();

        log.info("Open Channel(RCV):{}","Remote");

        while(true){

            var buf = new byte[4096];
            var len = is.read(buf);

            if(len == -1){
                break;
            }

            var cmd = buf[0];


            if(cmd == VK.H_RCV_TXT){
                var ret = NetProtocols.rcvTxt(buf,len,is);
                System.out.println("[Remote] "+ret);
            }

            if(cmd == VK.H_RCV_PUSH || cmd == VK.H_RCV_PUSH_ABS){
                try{
                    if(cmd == VK.H_RCV_PUSH){
                        var f = NetProtocols.rcvPush(buf, len, is, NuclearHost.hostProperties.getWorkspace());
                        NetProtocols.pushCmd(VK.H_RESULT_PUSH,os);
                        NetProtocols.pushTxt("Receive Success FileName:"+f.getName(),os);
                    }
                    if(cmd == VK.H_RCV_PUSH_ABS){
                        var f = NetProtocols.rcvPush(buf, len, is, null);
                        NetProtocols.pushCmd(VK.H_RESULT_PUSH,os);
                        NetProtocols.pushTxt("Receive Success FileName:"+f.getName(),os);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                    NetProtocols.pushTxt("Receive Failed",os);
                    NetProtocols.pushTxt(e.getMessage(),os);
                    NetProtocols.pushCmd(VK.H_RESULT_PUSH,os);
                }
            }

            if(cmd == VK.H_RESULT_PUSH){
                NetProtocols.resolve(lockRcvPushFile);
            }

            //接收启动器列表数据
            if(cmd == VK.H_PUSH_LAUNCHERS){
                dataRcvLaunchers = NetProtocols.rcvTxt(buf,len,is);
                NetProtocols.resolve(lockRcvLaunchers);
            }

            //接收Launcher设定数据
            if(cmd == VK.H_GET_LAUNCH_SETTINGS){
                String gson = NetProtocols.rcvTxt(buf, len, is);
                Type type = new TypeToken<Map<String, String>>(){}.getType();
                Map<String,String> map = this.gson.fromJson(gson, type);
                dataRcvLauncherSettings.clear();
                dataRcvLauncherSettings.putAll(map);
                NetProtocols.resolve(lockRcvLauncherSettings);
            }

            //接受临时文件夹数据
            if(cmd == VK.H_RESPONSE_TEMP_DIR){
                dataRcvRequestCreateTemp = NetProtocols.rcvTxt(buf, len, is);;
                NetProtocols.resolve(lockRcvRequestCreateTemp);
            }

            //接受启动返回
            if(cmd == VK.H_RESPONSE_LAUNCH){
                dataRcvRequestLaunch = NetProtocols.rcvTxt(buf, len, is);
                NetProtocols.resolve(lockRcvRequestLaunch);
            }

            if(cmd == VK.H_RESPONSE_PUSH_SETTINGS){
                NetProtocols.resolve(lockRcvPushSettings);
            }

            if(cmd == VK.H_RESPONSE_KEEP_ALIVE){
                NetProtocols.resolve(lockRcvKeepAlive);
            }

            if(cmd == VK.H_RESPONSE_LAUNCHER_DECLARER){
                this.dataRcvLauncherDeclare = NetProtocols.rcvTxt(buf, len, is);
                NetProtocols.resolve(lockRcvLauncherDeclare);
            }

        }
    }

    public Map<String, List<OptionDeclare>> getLauncherDeclare(String name) throws IOException {

        NetProtocols.pushTxt(VK.H_GET_LAUNCHER_DECLARER,name,os);
        NetProtocols.await(lockRcvLauncherDeclare);

        var ret = new HashMap<String, List<OptionDeclare>>();

        if(dataRcvLauncherDeclare == null){
            return ret;
        }

        try{
            Type type = new TypeToken<Map<String, List<OptionDeclare>>>(){}.getType();
            return this.gson.fromJson(dataRcvLauncherDeclare, type);
        }catch (Exception e){
            return ret;
        }

    }

    public String getLaunchers() throws IOException {
        NetProtocols.pushCmd(VK.H_GET_LAUNCHERS,os);
        NetProtocols.await(lockRcvLaunchers);
        return dataRcvLaunchers;
    }

    public Map<String,String> getLauncherSettings() throws IOException {
        NetProtocols.pushCmd(VK.H_GET_LAUNCH_SETTINGS,os);
        NetProtocols.await(lockRcvLauncherSettings);
        return dataRcvLauncherSettings;
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
    }

    public void pushSettings(Map<String,String> settings) throws IOException {
        NetProtocols.pushTxt(VK.H_PUSH_LAUNCH_SETTINGS,gson.toJson(settings),os);
        NetProtocols.await(lockRcvPushSettings);
    }

    public String requestCreateTempDir() throws IOException {
        NetProtocols.pushCmd(VK.H_REQUEST_TEMP_DIR,os);
        NetProtocols.await(lockRcvRequestCreateTemp);
        return dataRcvRequestCreateTemp;
    }

    public String requestLaunch() throws IOException {
        NetProtocols.pushCmd(VK.H_REQUEST_LAUNCH,os);
        NetProtocols.await(lockRcvRequestLaunch);
        return dataRcvRequestLaunch;
    }

    public boolean isAlive() {
        try {
            NetProtocols.pushCmd(VK.H_REQUEST_KEEP_ALIVE,os);
            NetProtocols.await(lockRcvKeepAlive);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

}
