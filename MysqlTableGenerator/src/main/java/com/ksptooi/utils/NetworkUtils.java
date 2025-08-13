package com.ksptooi.utils;

import com.ksptooi.association.VK;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;

public class NetworkUtils {

    public static void sendFile(String fileName,byte[] fBytes,OutputStream os,boolean abs) throws IOException {

        var fnBytes = fileName.getBytes();
        var fnLen = fnBytes.length;
        var fLen = fBytes.length;

        //发送报文 1B:指令集 8B:文件名长度 8B:文件长度 ?B:文件名 ?B:文件二进制数据
        ByteBuffer buffer = ByteBuffer.allocate(1 + 8 + 8 + fnLen + fLen);

        if(abs){
            buffer.put(VK.F_ABS_PUSH);
        }else {
            buffer.put(VK.F_PUSH);
        }

        buffer.putLong(fnLen);
        buffer.putLong(fLen);
        buffer.put(fnBytes);
        buffer.put(fBytes);
        os.write(buffer.array());
        os.flush();
    }



    /**
     * 从TCP连接中读取一个文件并写入本地
     * @param nis TCP流
     * @param fos 文件流
     * @param init 已经读取到的部分
     * @param len 文件总长度
     */
    public static void transfer(InputStream nis, OutputStream fos,byte[] init,int initLen,int initOffset,long len){

        try{

            var tVol = 0;

            //首先写入已经读取到的部分
            for (int i = initOffset; i < initLen; i++) {
                tVol = tVol + 1;
                fos.write(init[i]);
                fos.flush();
            }

            //已经读够了
            if(tVol >= len){
                return;
            }

            //继续从TCP中获取数据
            while (tVol < len){

                var rLen = 4096;

                //剩余的数据不足4096字节 则按剩余数据读取
                if((len - tVol) < rLen){
                    rLen = (int)(len - tVol);
                }

                byte[] b = new byte[rLen];
                var read = nis.read(b);

                fos.write(b,0,read);
                tVol = tVol + read;
            }

        }catch (Exception ex){
            throw new RuntimeException(ex);
        }

    }




}
