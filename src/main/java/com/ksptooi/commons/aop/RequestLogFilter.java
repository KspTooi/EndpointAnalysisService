package com.ksptooi.commons.aop;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Getter;
import lombok.Setter;

import org.apache.commons.lang3.StringUtils;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * 增强型请求日志过滤器，用于打印那些无法被AOP切面捕获的请求日志 这包括：
 * 1. 参数验证失败
 * 2. 异常处理
 * 3. 其他无法被AOP切面捕获的请求日志
 */
@Component
@Order(Integer.MIN_VALUE)
public class RequestLogFilter implements Filter {


    //请求信息线程本地变量
    private static final ThreadLocal<RequestInfo> requestInfoThreadLocal = new ThreadLocal<>();

    public static RequestInfo getRequestInfo() {
        return requestInfoThreadLocal.get();
    }

    @Getter
    @Setter
    public static class RequestInfo {
        private String gatewayRequestId;
        private String uri;
        private String method;
        private String ip;
        private Map<String, String> headers;
        private Supplier<String> bodySupplier;
        private long cost;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        if (!(request instanceof HttpServletRequest)) {
            chain.doFilter(request, response);
            return;
        }

        //包装请求使其可以缓存请求体
        ContentCachingRequestWrapper requestWrapper = new ContentCachingRequestWrapper((HttpServletRequest) request);

        try {
            //获取请求信息
            RequestInfo info = new RequestInfo();
            info.setMethod(requestWrapper.getMethod());
            info.setUri(requestWrapper.getRequestURI());
            info.setIp(requestWrapper.getRemoteAddr());
            info.setHeaders(extractHeaders(requestWrapper));

            //获取网关请求ID
            var gatewayRequestId = requestWrapper.getHeader("gateway-request-id");
            info.setGatewayRequestId("UNKNOWN");

            //如果网关请求ID不为空，则设置到请求信息中
            if(StringUtils.isNotBlank(gatewayRequestId)){
                info.setGatewayRequestId(gatewayRequestId);
            }

            //设置请求体供应商
            info.setBodySupplier(() -> new String(requestWrapper.getContentAsByteArray(), StandardCharsets.UTF_8));

            //设置请求信息到线程本地变量
            requestInfoThreadLocal.set(info);

            //耗时计算开始
            var start = System.currentTimeMillis();

            //执行过滤器
            chain.doFilter(requestWrapper, response);

            //耗时计算结束
            var end = System.currentTimeMillis();
            var cost = end - start;
            info.setCost(cost);



        } finally {
            //移除请求信息线程本地变量
            requestInfoThreadLocal.remove();
        }
    }

    /**
     * 提取请求头
     * @param request 请求
     * @return 请求头
     */
    private Map<String, String> extractHeaders(HttpServletRequest request) {
        Map<String, String> headers = new HashMap<>();
        Enumeration<String> headerNames = request.getHeaderNames();
        if (headerNames != null) {
            while (headerNames.hasMoreElements()) {
                String name = headerNames.nextElement();
                headers.put(name, request.getHeader(name));
            }
        }
        return headers;
    }

}
