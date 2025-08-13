package com.ksptooi.association.protocols;

import com.ksptooi.association.VK;
import com.ksptooi.utils.NetworkUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

public class NetProtocols {

    private static final Logger log = LoggerFactory.getLogger(NetProtocols.class);


    public static void pushCmd(byte cmd,OutputStream os) throws IOException {
        //组装数据报文
        ByteBuffer buffer = ByteBuffer.allocate(1);
        buffer.put(cmd);
        os.write(buffer.array());
        os.flush();
    }

    public static void pushTxt(String text, OutputStream os) throws IOException {
        pushTxt(VK.H_SND_TXT,text,os);
    }

    //自定义指令头的pushTxt
    public static void pushTxt(byte cmd,String text, OutputStream os) throws IOException {

        var txtBytes = text.getBytes(StandardCharsets.UTF_8);
        var txtLen = txtBytes.length;

        //Txt报文结构 1B:指令集 8B:文本长度 ?B:文本内容

        //组装数据报文
        ByteBuffer buffer = ByteBuffer.allocate(1+8 + txtLen);

        buffer.put(cmd);
        buffer.putLong(txtLen);
        buffer.put(txtBytes);

        os.write(buffer.array());
        os.flush();
    }

    public static void pushFile(OutputStream os,String rmtPath, File file, boolean abs) throws IOException {

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
            buffer.put(VK.H_SND_PUSH_ABS);
        }else {
            buffer.put(VK.H_SND_PUSH);
        }

        buffer.putLong(fnLen);
        buffer.putLong(rmtPathLen);
        buffer.putLong(fLen);
        buffer.put(fnBytes);
        buffer.put(rmtPathBytes);
        buffer.put(fBytes);
        os.write(buffer.array());
        os.flush();
    }



    public static String rcvTxt(byte[] buf,int len, InputStream is) throws IOException {

        //TXT报文结构 1B:指令集 8B:文本长度 ?B:文本
        var header = 1 + 8;

        var tLen = ByteBuffer.wrap(buf, 1, 8).getLong();

        var ret = new StringBuilder();

        ByteBuffer bb = ByteBuffer.allocate(4096);

        var tVol = 0;
        //首先写入已经读取到的部分
        for (var i = header; i < len; i++) {
            tVol = tVol + 1;
            bb.put(buf[i]);
        }

        //已经读够了
        if(tVol >= tLen){
            var txt = new String(bb.array(), 0, (int) tLen, StandardCharsets.UTF_8);
            //log.info("Rcv Txt:{}", txt);
            return txt;
        }

        //继续从TCP中获取数据
        while (tVol < len){

            var rLen = 4096;

            //剩余的数据不足4096字节 则按剩余数据读取
            if((len - tVol) < rLen){
                rLen = (int)(len - tVol);
            }

            byte[] b = new byte[rLen];
            var read = is.read(b);
            ret.append(new String(b,0,read,StandardCharsets.UTF_8));
            tVol = tVol + read;
        }
        //log.info("Rcv Txt:{}", ret);
        return ret.toString();
    }

    public static File rcvPush(byte[] buf,int len, InputStream is,File directory) throws IOException {

        //PUSH报文结构 1B:指令集 8B:文件名长度 8B:远程地址长度 8B:文件长度 ?B:文件名 ?B:远程地址 ?B:文件二进制数据
        var header = 1 + 8 + 8 + 8;

        var nLen = ByteBuffer.wrap(buf, 1, 8).getLong();
        var pLen = ByteBuffer.wrap(buf, 9, 8).getLong();
        var fLen = ByteBuffer.wrap(buf, 17, 8).getLong();

        //文件二进制数据开始
        var dataStart = header + nLen + pLen;
        var fileName = new String(buf, header, (int) nLen, StandardCharsets.UTF_8);
        var filePath = new String(buf, (int)(header + nLen), (int) pLen, StandardCharsets.UTF_8);
        var dir = new File(directory,filePath);

        if(dir == null){
            dir = new File(filePath);
        }

        var file = new File(dir,fileName);
        log.info("Rcv PSH FileName:{} Size:{} Path:{}",fileName,fLen,dir.getAbsolutePath());

        try{

            dir.mkdirs();

            if(file.exists()){
                //log.info("remove file:{}",file.getAbsolutePath());
                file.delete();
            }

            file.createNewFile();

            var fos = Files.newOutputStream(file.toPath());
            NetworkUtils.transfer(is,fos,buf,len, (int)dataStart,fLen);
            fos.flush();
            fos.close();
            //log.info("cmd success PSH:{}",fileName);

            return file;

        }catch (Exception e){
            NetworkUtils.transfer(is,new ByteArrayOutputStream(),buf,len, (int)dataStart,fLen);
            throw new RuntimeException(e);
        }

    }


    public static void await(Object obj){
        try {
            synchronized (obj){
                obj.wait(3600);
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }


    public static void resolve(Object obj){
        synchronized (obj){
            obj.notify();
        }
    }



}
