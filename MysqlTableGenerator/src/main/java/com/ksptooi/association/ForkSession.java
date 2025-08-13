package com.ksptooi.association;

import com.ksptooi.utils.DirectoryTools;
import com.ksptooi.utils.NetworkUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

public class ForkSession extends Thread {

    private String sessionId = UUID.randomUUID().toString();

    private static final Logger log = LoggerFactory.getLogger(ForkSession.class);
    private Socket socket;


    public ForkSession(ExecutorService tp, Socket socket){
        this.socket = socket;
        log.info("new session:{}",sessionId);
    }

    /**
     * 协议交换数据报格式
     * -------------------------------
     * 1B  8B     ?
     * CMD LENGTH DATA
     */
    @Override
    public void run() {

        try {

            var is = socket.getInputStream();
            var os = socket.getOutputStream();

            byte[] b = new byte[4096];

            while (true){

                var read = is.read(b);

                if(read < 1){
                    socket.close();
                    log.info("session:{} has closed.",sessionId);
                    return;
                }

                //获取第一位指令集
                var cmd = b[0];
                var len = ByteBuffer.wrap(b,1,8).getLong();

                if(cmd == VK.F_PULL){
                    //读取 9~len的字符串长度
                    String path = new String(b, 9, (int) len, StandardCharsets.UTF_8);

                    File file = new File(path);

                    if(!file.exists()){
                        //组装数据报文
                        byte[] snd = "文件不存在".getBytes(StandardCharsets.UTF_8);
                        ByteBuffer buffer = ByteBuffer.allocate(1 + 8 + snd.length);
                        buffer.put(VK.OPT_FAILED);
                        buffer.putLong(snd.length);
                        buffer.put(snd);
                        os.write(buffer.array());
                        continue;
                    }

                    //发送报文 1B:指令集 8B:文件名长度 8B:文件长度 ?B:文件名 ?B:文件二进制数据
                    var fBytes = Files.readAllBytes(file.toPath());
                    NetworkUtils.sendFile(file.getName(),fBytes,os,true);
                }

                if(cmd == VK.D_LIST){

                }

                //接收文件推送
                if(cmd == VK.F_PUSH || cmd == VK.F_ABS_PUSH){

                    //PUSH报文结构 1B:指令集 8B:文件名长度 8B:远程地址长度 8B:文件长度 ?B:文件名 ?B:远程地址 ?B:文件二进制数据
                    var header = 1 + 8 + 8 + 8;

                    var nLen = ByteBuffer.wrap(b, 1, 8).getLong();
                    var pLen = ByteBuffer.wrap(b, 9, 8).getLong();
                    var fLen = ByteBuffer.wrap(b, 17, 8).getLong();

                    //文件二进制数据开始
                    var dataStart = header + nLen + pLen;

                    var fileName = new String(b, header, (int) nLen, StandardCharsets.UTF_8);
                    var filePath = new String(b, (int)(header + nLen), (int) pLen, StandardCharsets.UTF_8);

                    var dir = new File(DirectoryTools.getCurrentDirectory(),filePath);

                    //绝对路径推送
                    if(cmd == VK.F_ABS_PUSH){
                        dir = new File(filePath);
                    }

                    var file = new File(dir,fileName);

                    log.info("Rcv PSH FileName:{} Size:{} Path:{}",fileName,fLen,dir.getAbsolutePath());

                    try{

                        dir.mkdirs();

                        if(file.exists()){
                            log.info("remove file:{}",file.getAbsolutePath());
                            file.delete();
                        }

                        file.createNewFile();

                    }catch (Exception ex){

                        //需要消耗掉发送方的流 否则单线程的发送方会阻塞在发送阶段
                        NetworkUtils.transfer(is,new ByteArrayOutputStream(),b,read, (int)dataStart,fLen);
                        
                        byte[] snd = ex.getMessage().getBytes(StandardCharsets.UTF_8);
                        ByteBuffer buffer = ByteBuffer.allocate(1 + 8 + snd.length);
                        buffer.put(VK.OPT_FAILED);
                        buffer.putLong(snd.length);
                        buffer.put(snd);
                        os.write(buffer.array());
                        ex.printStackTrace();
                        log.info("cmd failed PSH:{}",fileName);
                        continue;
                    }

                    var fos = Files.newOutputStream(file.toPath());
                    NetworkUtils.transfer(is,fos,b,read, (int)dataStart,fLen);
                    fos.flush();
                    fos.close();

                    log.info("cmd success PSH:{}",fileName);

                    os.write(VK.OPT_SUCCESS);
                    os.flush();
                }

            }

        } catch (IOException e) {
            //e.printStackTrace();
            log.info("session closed:{}",sessionId);
            throw new RuntimeException(e);
        }

    }

}
