package com.ksptooi.biz;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Enumeration;

@Controller
@Profile("dev")
public class ViteProxyController {

    // Vite 开发服务器的地址
    private static final String VITE_TARGET = "http://127.0.0.1:27501";

    // 实例化一个 RestTemplate 用于转发请求
    private final RestTemplate restTemplate = new RestTemplate();

    /**
     * 核心代理方法
     * 拦截 /src/**, /node_modules/**, /@vite/** 等前端开发资源
     */
    @RequestMapping(value = {
            "/src/**",              // 源码
            "/node_modules/**",     // 依赖
            "/@vite/**",            // Vite 内部
            "/@id/**",              // Vite 内部
            "/@fs/**",              // Vite 文件系统访问
            "/.well-known/**",      // [新增] 修复 Chrome DevTools 报错
            "/favicon.ico",
            "/*.json",
            "/*.png"
    })
    public void proxyRequest(HttpServletRequest request, HttpServletResponse response) {
        try {
            // 1. 构建目标 URL
            // 获取请求路径 (例如 /src/views/worker.ts)
            String path = request.getRequestURI();
            // 获取查询参数 (例如 ?worker_file&type=module)
            String query = request.getQueryString();

            // 组合成 Vite 的完整地址
            URI targetUri = UriComponentsBuilder
                    .fromHttpUrl(VITE_TARGET + path)
                    .query(query)
                    .build(true)
                    .toUri();

            // 2. 构造请求头 (把浏览器的 Header 转发给 Vite)
            // 这一步很重要，否则 Vite 可能不认识请求
            org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
            Enumeration<String> headerNames = request.getHeaderNames();
            while (headerNames.hasMoreElements()) {
                String headerName = headerNames.nextElement();
                // Host 头必须改掉，或者是忽略，否则 Vite 会以为是跨域攻击或错误的 Host
                if (!"host".equalsIgnoreCase(headerName)) {
                    headers.add(headerName, request.getHeader(headerName));
                }
            }

            // 3. 发送请求给 Vite
            ResponseEntity<byte[]> viteResponse = restTemplate.exchange(
                    targetUri,
                    HttpMethod.valueOf(request.getMethod()),
                    new org.springframework.http.HttpEntity<>(headers),
                    byte[].class
            );

            // 4. 将 Vite 的响应头写回给浏览器 (最关键的是 Content-Type)
            if (viteResponse.getHeaders().getContentType() != null) {
                response.setContentType(viteResponse.getHeaders().getContentType().toString());
            }

            // 5. 将 Vite 的响应体 (编译好的 JS) 写回给浏览器
            if (viteResponse.getBody() != null) {
                response.getOutputStream().write(viteResponse.getBody());
            }

        } catch (Exception e) {
            // 如果连接 Vite 失败 (比如 Vite 没启动)，返回 502 Bad Gateway
            response.setStatus(HttpServletResponse.SC_BAD_GATEWAY);
            try {
                response.getWriter().write("Vite Proxy Error: " + e.getMessage());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}