package com.ksptooi.association;

import com.ksptooi.utils.DirectoryTools;
import com.ksptooi.utils.NetworkUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

public class ForkClient {

    private static final Logger log = LoggerFactory.getLogger(ForkClient.class);
    private String host;
    private int port;

    private Socket socket;
    private OutputStream os;
    private InputStream is;

    public ForkClient(String host, int port){
        this.host = host;
        this.port = port;
    }

    public void connect() throws IOException {
        this.socket = new Socket(host,port);
        this.os = socket.getOutputStream();
        this.is = socket.getInputStream();
    }

    public void pushFile(String rmtPath,File file) throws IOException {
        pushFile(rmtPath,file,false);
    }

    public void pushFileAbs(String rmtAbsPath,File file) throws IOException {
        pushFile(rmtAbsPath,file,true);
    }

    public void pushFile(String rmtPath,File file,boolean abs) throws IOException {

        var fnBytes = file.getName().getBytes(StandardCharsets.UTF_8);
        var fnLen = fnBytes.length;

        var fBytes = Files.readAllBytes(file.toPath());
        var fLen = fBytes.length;

        var rmtPathBytes = rmtPath.getBytes(StandardCharsets.UTF_8);
        var rmtPathLen = rmtPathBytes.length;

        //PUSH报文结构 1B:指令集 8B:文件名长度 8B:远程地址长度 8B:文件长度  ?B:文件名 ?B:远程地址 ?B:文件二进制数据

        //组装数据报文
        ByteBuffer buffer = ByteBuffer.allocate(1+8+8+8 + fnLen + rmtPathLen + fLen);

        if(abs){
            buffer.put(VK.F_ABS_PUSH);
        }else {
            buffer.put(VK.F_PUSH);
        }

        buffer.putLong(fnLen);
        buffer.putLong(rmtPathLen);
        buffer.putLong(fLen);
        buffer.put(fnBytes);
        buffer.put(rmtPathBytes);
        buffer.put(fBytes);
        os.write(buffer.array());
        os.flush();

        var buf = new byte[2048];
        var len = is.read(buf);

        if(len < 1){
            throw new IOException("操作失败");
        }

        if(buf[0] == VK.OPT_SUCCESS){
            log.info("已成功推送文件:{}",file.getName());
            return;
        }

        if(buf[0] == VK.OPT_FAILED){
            var strLen = ByteBuffer.wrap(buf,1,8).getLong();
            throw new IOException(new String(buf, 1+8, (int) strLen, StandardCharsets.UTF_8));
        }

    }



    public void pullFile(String rPath, String directory){

        //组装数据报文
        byte[] rPathBytes = rPath.getBytes(StandardCharsets.UTF_8);
        ByteBuffer buffer = ByteBuffer.allocate(1 + 8 + rPathBytes.length);
        buffer.put(VK.F_PULL);
        buffer.putLong(rPathBytes.length);
        buffer.put(rPathBytes);

        //发送数据报文
        try {

            os.write(buffer.array());
            os.flush();

            byte[] b = new byte[4096];
            var read = is.read(b);

            if(read < 1){
                return;
            }

            //获取指令位
            var cmd = b[0];

            if(cmd == VK.F_PUSH || cmd == VK.F_ABS_PUSH){

                var nLen = ByteBuffer.wrap(b,1,8).getLong();
                var dLen = ByteBuffer.wrap(b,9,8).getLong();

                var name = new String(b, 1+8+8, (int) nLen, StandardCharsets.UTF_8);
                log.info("接收指令:F_RCV Name:{} Len:{}",name,dLen);

                File dir = null;

                if(cmd == VK.F_PUSH){
                    dir = new File(DirectoryTools.getCurrentDirectory(),directory);
                }

                if(cmd == VK.F_ABS_PUSH){
                    dir = new File(name);
                }

                dir.mkdirs();
                File f = new File(dir,name);

                if(f.exists()){
                    f.delete();
                }

                f.createNewFile();
                var fos = Files.newOutputStream(f.toPath());
                NetworkUtils.transfer(is,fos,b,read, (int) (1+8+8+nLen),dLen);
            }


        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }


}
