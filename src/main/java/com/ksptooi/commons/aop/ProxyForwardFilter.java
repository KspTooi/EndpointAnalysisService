package com.ksptooi.commons.aop;

import com.google.gson.Gson;
import com.ksptooi.biz.relay.model.request.RequestPo;
import com.ksptooi.biz.relay.service.RequestService;
import com.ksptooi.commons.config.RepeaterConfig;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.*;

//@Component
//@Order(Ordered.HIGHEST_PRECEDENCE)
public class ProxyForwardFilter extends OncePerRequestFilter {

    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    @Autowired
    private RepeaterConfig repeaterConfig;

    @Autowired
    private HttpClient httpClient;

    //hop-by-hop请求头
    private static final Set<String> HOP_BY_HOP_HEADERS = new HashSet<>();

    //不需要发送请求体的请求方法
    private static final Set<String> METHODS_WITHOUT_BODY = new HashSet<>();

    private static final List<String> whitelistUrls = Arrays.asList(
            "/ras/**",
            "/assets/**",
            "/favicon.ico"
    );

    @Autowired
    private Gson gson;

    @Autowired
    private RequestService requestService;


    static {
        METHODS_WITHOUT_BODY.add("GET");
        METHODS_WITHOUT_BODY.add("DELETE");
        METHODS_WITHOUT_BODY.add("HEAD");
        METHODS_WITHOUT_BODY.add("OPTIONS");
        HOP_BY_HOP_HEADERS.add("connection");
        HOP_BY_HOP_HEADERS.add("keep-alive");
        HOP_BY_HOP_HEADERS.add("proxy-authenticate");
        HOP_BY_HOP_HEADERS.add("proxy-authorization");
        HOP_BY_HOP_HEADERS.add("te");
        HOP_BY_HOP_HEADERS.add("trailer");
        HOP_BY_HOP_HEADERS.add("transfer-encoding");
        HOP_BY_HOP_HEADERS.add("upgrade");
        HOP_BY_HOP_HEADERS.add("host");
        HOP_BY_HOP_HEADERS.add("content-length");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest hsr, HttpServletResponse hsrp, FilterChain filterChain) throws ServletException, IOException {

        //构建请求记录
        RequestPo request = new RequestPo();
        request.setRequestId(UUID.randomUUID().toString());
        request.setMethod(hsr.getMethod());
        request.setUrl(replaceHostAndScheme(hsr.getRequestURL().toString(), repeaterConfig.getProxyPass()));
        request.setSource(hsr.getRemoteAddr());

        //解析请求头
        Map<String, String> requestHeaders = new HashMap<>();
        Enumeration<String> headerNames = hsr.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String name = headerNames.nextElement();
            requestHeaders.put(name, hsr.getHeader(name));
        }

        request.setRequestHeaders(gson.toJson(requestHeaders));
        request.setRequestBodyLength(hsr.getContentLength());

        var contentType = hsr.getContentType();
        request.setRequestBodyType(contentType);
        request.setRequestBody("不支持分析");

        //如果是JSON请求，则需要解析并保存请求体
        byte[] requestBody = null;

        if (contentType != null && contentType.contains("application/json")) {
            requestBody = readRequestBody(hsr);
            request.setRequestBody(new String(requestBody));
        }

        request.setResponseHeaders("{}");
        request.setResponseBodyLength(0);
        request.setResponseBodyType("?");
        request.setResponseBody(null);
        request.setStatusCode(200);
        request.setRedirectUrl("未重定向");
        request.setStatus(3);
        request.setRequestTime(LocalDateTime.now());
        request.setResponseTime(null);

        //构建目标URI
        URI targetUri = repeaterConfig.buildTargetUri(hsr);

        if (targetUri == null) {
            hsrp.setStatus(HttpServletResponse.SC_BAD_GATEWAY);
            writeBytes(hsrp, "Bad Gateway".getBytes(StandardCharsets.UTF_8));

            //更新响应记录
            request.setResponseTime(LocalDateTime.now());
            request.setStatus(1);
            request.setResponseBody(null);
            request.setResponseBodyLength(request.getResponseBody().length());
            request.setResponseBodyType("文本");
            request.setStatusCode(HttpServletResponse.SC_BAD_GATEWAY);
            request.setRedirectUrl("未重定向");
            requestService.commitRequest(request);
            return;
        }

        //读取请求体
        if (requestBody == null) {
            requestBody = readRequestBody(hsr);
        }
        HttpRequest.Builder builder = HttpRequest.newBuilder(targetUri);

        //复制请求头到目标请求
        copyRequestHeaders(hsr, builder);

        String method = hsr.getMethod();
        // 如果是GET、DELETE、HEAD、OPTIONS方法，不需要发送请求体
        if (METHODS_WITHOUT_BODY.contains(method)) {
            builder.method(method, HttpRequest.BodyPublishers.noBody());
            request.setRequestBodyLength(0);
            request.setRequestBodyType("无");
            request.setRequestBody(null);
        }

        // 非GET、DELETE、HEAD、OPTIONS方法，需要发送请求体
        if (!METHODS_WITHOUT_BODY.contains(method)) {
            byte[] bodyBytes = requestBody;
            if (bodyBytes == null) {
                bodyBytes = new byte[0];
            }
            builder.method(method, HttpRequest.BodyPublishers.ofByteArray(bodyBytes));
        }

        //发送请求
        HttpResponse<byte[]> upstream;

        try {
            upstream = httpClient.send(builder.build(), HttpResponse.BodyHandlers.ofByteArray());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            hsrp.setStatus(HttpServletResponse.SC_BAD_GATEWAY);
            writeBytes(hsrp, "Interrupted".getBytes(StandardCharsets.UTF_8));
            return;
        } catch (Exception ex) {
            hsrp.setStatus(HttpServletResponse.SC_BAD_GATEWAY);
            writeBytes(hsrp, ("Proxy Error: " + ex.getMessage()).getBytes(StandardCharsets.UTF_8));
            return;
        }

        //更新响应记录

        //处理响应头
        Map<String, String> responseHeaders = new HashMap<>();
        for (var entry : upstream.headers().map().entrySet()) {
            String name = entry.getKey();
            if (StringUtils.isBlank(name)) {
                continue;
            }
            String lower = name.toLowerCase();
            if (HOP_BY_HOP_HEADERS.contains(lower)) {
                continue;
            }
            for (String value : entry.getValue()) {
                if (StringUtils.isBlank(value)) {
                    continue;
                }
                responseHeaders.put(name, value);
            }
        }
        request.setResponseHeaders(gson.toJson(responseHeaders));
        request.setResponseBodyLength(upstream.body().length);
        request.setResponseBodyType("二进制");

        byte[] body = upstream.body();

        //如果是JSON响应，则需要解析并保存响应体
        if (contentType != null && contentType.contains("application/json")) {
            request.setResponseBody(new String(body));
            request.setResponseBodyType("application/json");
        }

        request.setStatusCode(upstream.statusCode());

        //如果是重定向，则需要保存重定向URL
        String location = upstream.headers().firstValue("Location").orElse(null);
        if (location != null) {
            request.setRedirectUrl(location);
        }

        request.setResponseTime(LocalDateTime.now());
        request.setStatus(0);

        //更新响应记录
        requestService.commitRequest(request);

        hsrp.setStatus(upstream.statusCode());
        copyResponseHeaders(upstream, hsrp);
        if (body != null && body.length > 0) {
            writeBytes(hsrp, body);
            return;
        }
        hsrp.flushBuffer();
    }

    /**
     * 复制请求头到目标请求
     *
     * @param request 请求
     * @param builder 请求构建器
     */
    private void copyRequestHeaders(HttpServletRequest request, HttpRequest.Builder builder) {
        Enumeration<String> headerNames = request.getHeaderNames();
        if (headerNames == null) {
            return;
        }
        while (headerNames.hasMoreElements()) {
            String name = headerNames.nextElement();
            if (StringUtils.isBlank(name)) {
                continue;
            }
            String lower = name.toLowerCase();
            if (HOP_BY_HOP_HEADERS.contains(lower)) {
                continue;
            }
            Enumeration<String> values = request.getHeaders(name);
            while (values.hasMoreElements()) {
                String value = values.nextElement();
                if (StringUtils.isBlank(value)) {
                    continue;
                }
                builder.header(name, value);
            }
        }
    }

    /**
     * 复制响应头到目标响应
     *
     * @param upstream 上游响应
     * @param response 目标响应
     */
    private void copyResponseHeaders(HttpResponse<byte[]> upstream, HttpServletResponse response) {
        for (var entry : upstream.headers().map().entrySet()) {
            String name = entry.getKey();
            if (StringUtils.isBlank(name)) {
                continue;
            }
            String lower = name.toLowerCase();
            if (HOP_BY_HOP_HEADERS.contains(lower)) {
                continue;
            }
            for (String value : entry.getValue()) {
                if (StringUtils.isBlank(value)) {
                    continue;
                }
                response.addHeader(name, value);
            }
        }
    }

    /**
     * 读取请求体
     *
     * @param request 请求
     * @return 请求体
     * @throws IOException
     */
    private byte[] readRequestBody(HttpServletRequest request) throws IOException {
        int length = request.getContentLength();
        if (length == 0) {
            return new byte[0];
        }
        if (length < 0) {
            try (InputStream in = request.getInputStream()) {
                return in.readAllBytes();
            }
        }
        byte[] buffer = new byte[length];
        try (InputStream in = request.getInputStream()) {
            int readTotal = 0;
            while (readTotal < length) {
                int r = in.read(buffer, readTotal, length - readTotal);
                if (r < 0) {
                    break;
                }
                readTotal += r;
            }
        }
        return buffer;
    }

    /**
     * 写入响应体
     *
     * @param response 响应
     * @param bytes    响应体
     * @throws IOException
     */
    private void writeBytes(HttpServletResponse response, byte[] bytes) throws IOException {
        if (bytes == null || bytes.length == 0) {
            response.flushBuffer();
            return;
        }
        response.setContentLength(bytes.length);
        try (OutputStream os = response.getOutputStream()) {
            os.write(bytes);
            os.flush();
        }
    }

    /**
     * 替换URL中的主机名、协议和端口号 例如:http://www.baidu.com/index.html 替换为 http://127.0.0.1:8080/index.html
     *
     * @param url       原始URL
     * @param targetUrl 目标URL
     * @return 替换后的URL
     */
    private static String replaceHostAndScheme(String url, String targetUrl) {
        try {
            URI uri = new URI(url);
            URI targetUri = new URI(targetUrl);
            return new URI(targetUri.getScheme(), targetUri.getUserInfo(), targetUri.getHost(), targetUri.getPort(), uri.getPath(), uri.getQuery(), uri.getFragment()).toString();
        } catch (URISyntaxException e) {
            return url;
        }
    }


    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {

        // 使用AntPathMatcher检查是否在白名单中
        if (whitelistUrls.stream().anyMatch(pattern -> pathMatcher.match(pattern, request.getRequestURI()))) {
            return true;
        }

        return false;
    }

}


