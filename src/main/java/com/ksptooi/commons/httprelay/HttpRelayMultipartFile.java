package com.ksptooi.commons.httprelay;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.SequenceInputStream;
import java.net.http.HttpRequest;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.ksptooi.commons.utils.IdWorker;

public class HttpRelayMultipartFile {

    private final String boundary;
    private final List<InputStream> inputStreams = new ArrayList<>();

    private long contentLength = 0;

    private HttpRequest.BodyPublisher bodyPublisher = null;

    public HttpRelayMultipartFile() {
        this.boundary = "EasSystem14PCP2FormBoundary" + IdWorker.nextId();
    }

    public String getContentType() {
        if(bodyPublisher == null) {
            throw new RuntimeException("当前对象还未构建，无法获取完整的Content-Type");
        }
        return "multipart/form-data; boundary=" + boundary;
    }

    /**
     * 获取内容长度
     * @return 内容长度
     * @throws IOException 流读取失败
     */
    public long getContentLength() throws IOException {
        return contentLength;
    }

    // 添加普通文本
    public void addText(String name, String value) {
        String header = "--" + boundary + "\r\n" +
                "Content-Disposition: form-data; name=\"" + name + "\"\r\n" +
                "Content-Type: text/plain; charset=UTF-8\r\n\r\n";

        var hIs = new ByteArrayInputStream(header.getBytes(StandardCharsets.UTF_8));
        var vIs = new ByteArrayInputStream(value.getBytes(StandardCharsets.UTF_8));
        var rIs = new ByteArrayInputStream("\r\n".getBytes(StandardCharsets.UTF_8));

        inputStreams.add(hIs);
        inputStreams.add(vIs);
        inputStreams.add(rIs);
        contentLength += hIs.available() + vIs.available() + rIs.available();
    }

    /**
     * 添加文件
     * @param name 文件名
     * @param file 文件
     * @throws IOException 文件读取失败
     */
    public void addFile(String name, File file) throws IOException {
        String fileName = file.getName();
        String header = "--" + boundary + "\r\n" +
                "Content-Disposition: form-data; name=\"" + name + "\"; filename=\"" + fileName + "\"\r\n" +
                "Content-Type: application/octet-stream\r\n\r\n";

        var hIs = new ByteArrayInputStream(header.getBytes(StandardCharsets.UTF_8));
        var fIs = new FileInputStream(file);
        var rIs = new ByteArrayInputStream("\r\n".getBytes(StandardCharsets.UTF_8));

        inputStreams.add(hIs);
        inputStreams.add(fIs);
        inputStreams.add(rIs);
        contentLength += hIs.available() + file.length() + rIs.available();
    }

    /**
     * 转换为BodyPublisher
     * @return BodyPublisher
     */
    public HttpRequest.BodyPublisher toBodyPublisher() {

        if(bodyPublisher != null) {
            return bodyPublisher;
        }

        // 添加结束符
        String footer = "--" + boundary + "--\r\n";

        var fIs = new ByteArrayInputStream(footer.getBytes(StandardCharsets.UTF_8));
        inputStreams.add(fIs);
        contentLength += fIs.available();
        InputStream seqStream = new SequenceInputStream(Collections.enumeration(inputStreams));
        return HttpRequest.BodyPublishers.ofInputStream(() -> seqStream);
    }
    

}
