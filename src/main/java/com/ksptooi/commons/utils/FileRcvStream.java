package com.ksptooi.commons.utils;


import jakarta.validation.constraints.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * 字节流包装类 - 文件接收流
 * 该类非线程安全 不可多线程使用
 */
public class FileRcvStream extends InputStream {

    private final InputStream in;

    //最大可接收的Byte数
    private long maxRcvLimit = -1;

    //当前已接收的Byte数
    private long rcv = 0;

    public FileRcvStream(InputStream in) {
        this.in = in;
    }

    public FileRcvStream(InputStream in, long limitSize){
        this.in = in;
        maxRcvLimit = limitSize;
    }

    public int next(int len) throws IOException{

        //仅桥接 不对最大传输的字节数做限制
        if(maxRcvLimit == -1){
            return len;
        }

        rcv = rcv + len;

        if(rcv > maxRcvLimit){
            close();
            throw new RuntimeException("已经达到字节数读取上限! 限制为:"+maxRcvLimit);
        }

        return len;
    }

    public long getRcvTotal(){
        return rcv + 1;
    }

    @Override
    public int read() throws IOException {
        int len = in.read();

        if(len < 1){
            return len;
        }

        if(maxRcvLimit == -1){
            return len;
        }

        rcv++;

        if(rcv > maxRcvLimit){
            close();
            throw new RuntimeException("已经达到字节数读取上限! 限制为:"+maxRcvLimit);
        }

        return len;
    }

    @Override
    public int read(@NotNull byte[] b, int off, int len) throws IOException {
        return next(in.read(b, off, len));
    }

    @Override
    public int read(@NotNull byte[] b) throws IOException {
        return next(in.read(b));
    }


    @Override
    public byte[] readAllBytes() throws IOException {
        throw new IOException("FileReceiveStream不支持NBytes操作");
    }

    @Override
    public byte[] readNBytes(int len) throws IOException {
        throw new IOException("FileReceiveStream不支持NBytes操作");
        //return in.readNBytes(len);
    }

    @Override
    public int readNBytes(byte[] b, int off, int len) throws IOException {
        throw new IOException("FileReceiveStream不支持NBytes操作");
        //return in.readNBytes(b, off, len);
    }

    @Override
    public long transferTo(OutputStream out) throws IOException {
        throw new IOException("FileReceiveStream不支持连接操作");
        //return in.transferTo(out);
    }

    @Override
    public boolean markSupported() {
        return in.markSupported();
    }

    @Override
    public void reset() throws IOException {
        in.reset();
    }

    @Override
    public void mark(int readlimit) {
        in.mark(readlimit);
    }

    @Override
    public void close() throws IOException {
        in.close();
    }

    @Override
    public int available() throws IOException {
        return in.available();
    }

    @Override
    public void skipNBytes(long n) throws IOException {
        in.skipNBytes(n);
    }

    @Override
    public long skip(long n) throws IOException {
        return in.skip(n);
    }
}
