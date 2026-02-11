package com.ksptooi.commons.aop;

import com.google.gson.Gson;
import com.ksptooi.biz.auth.model.session.UserSessionVo;
import com.ksptooi.biz.auth.service.AuthService;
import com.ksptooi.biz.core.service.SessionService;
import com.ksptool.assembly.entity.web.Result;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;
import java.util.Map;


/**
 * SessionKeepFilter 是一个全局 Servlet 过滤器，负责处理用户会话校验与 URL 访问权限控制。
 * 主要功能包括：
 * 1. 白名单放行：预定义静态资源、登录、注册等无需校验的路径。
 * 2. 会话校验：拦截非白名单请求，验证用户登录状态。若会话失效，根据请求类型（AJAX 或 普通请求）返回 401 状态码或重定向至登录页。
 * 3. 上下文存储：校验通过后，将会话信息 (UserSessionVo) 存入 RequestContextHolder，供后续业务逻辑使用。
 * 4. 权限控制：
 * - 黑名单校验：针对特定敏感路径（如 /actuator）进行强制权限检查。
 * - 通用权限校验：调用 AuthService 根据当前请求的 URI 校验用户是否拥有访问权限。
 * 5. 响应处理：针对 AJAX 请求返回标准化 JSON 结果 (Result)，针对普通请求执行页面重定向。
 */
@Component
@Order(1)
public class SessionKeepFilter implements Filter {

    private static final List<String> WHITE_LIST = Arrays.asList(
            "/login",
            "/css/**",
            "/js/**",
            "/img/**",
            "/commons/**",
            "/res/**",
            "/h2-console/**",
            "/error",
            "/register",
            "/userRegister",
            "/install-wizard/**",
            "/welcome",
            "/",
            "/favicon.ico",
            // springdoc / swagger
            "/v3/api-docs/**",
            "/swagger-ui.html",
            "/swagger-ui/**",
            "/noPermission",
            "/endpoint/**"
    );
    private static final Map<String, String> BLACK_LIST = Map.of(
            "/actuator/**", "admin:maintain:actuator",
            "/actuator", "admin:maintain:actuator"
    );
    private final AntPathMatcher pathMatcher = new AntPathMatcher();
    private final Gson gson = new Gson();
    @Autowired
    private AuthService authService;
    @Autowired
    private SessionService sessionService;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        String uri = req.getRequestURI();

        // 白名单路径直接放行
        for (String pattern : WHITE_LIST) {
            if (pathMatcher.match(pattern, uri)) {
                chain.doFilter(request, response);
                return;
            }
        }

        // 获取并验证会话
        UserSessionVo session = sessionService.getUserSessionByHSR(req);
        if (session == null) {
            // 检查是否为 AJAX 请求 (通过自定义请求头)
            String requestWithHeader = req.getHeader("AE-Request-With");
            if ("XHR".equalsIgnoreCase(requestWithHeader)) {
                // 如果是 AJAX 请求，返回 401 Unauthorized 和包含重定向URL的Result
                res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                res.setContentType("application/json;charset=UTF-8");
                // 添加 Location 响应头
                res.setHeader("Location", "/login");

                // 创建 Result 对象
                Result<String> result = Result.error(1, "会话已失效，请重新登录");

                try (PrintWriter writer = res.getWriter()) {
                    writer.write(gson.toJson(result));
                }
                return; // 处理完 AJAX 请求后返回
            }
            // 如果不是 AJAX 请求 (例如表单提交或直接访问URL)，执行重定向
            res.sendRedirect("/login");
            return;
        }

        // 将会话信息存储到请求上下文中
        RequestContextHolder.currentRequestAttributes().setAttribute(SessionService.SESSION_ATTRIBUTE, session, RequestAttributes.SCOPE_REQUEST);

        // 检查黑名单路径权限
        for (Map.Entry<String, String> entry : BLACK_LIST.entrySet()) {
            String pattern = entry.getKey();
            String requiredPermission = entry.getValue();

            if (pathMatcher.match(pattern, uri)) {
                // 检查用户是否有访问权限
                if (!authService.hasPermissionByUrlPath(uri)) {
                    // 检查是否为 AJAX 请求
                    String requestWithHeader = req.getHeader("AE-Request-With");
                    if ("XHR".equalsIgnoreCase(requestWithHeader)) {
                        // AJAX 请求返回403状态码和JSON错误信息
                        res.setStatus(HttpServletResponse.SC_FORBIDDEN);
                        res.setContentType("application/json;charset=UTF-8");

                        Result<String> result = Result.error(403, "权限不足，需要权限：" + requiredPermission);

                        try (PrintWriter writer = res.getWriter()) {
                            writer.write(gson.toJson(result));
                        }
                        return;
                    }
                    // 非AJAX请求重定向到无权限页面
                    res.sendRedirect("/noPermission");
                    return;
                }
                break; // 找到匹配的模式后退出循环
            }
        }

        // 检查用户是否有访问权限
        if (!authService.hasPermissionByUrlPath(uri)) {
            // 检查是否为 AJAX 请求
            String requestWithHeader = req.getHeader("AE-Request-With");
            if ("XHR".equalsIgnoreCase(requestWithHeader)) {
                // AJAX 请求返回403状态码和JSON错误信息
                res.setStatus(HttpServletResponse.SC_FORBIDDEN);
                res.setContentType("application/json;charset=UTF-8");

                Result<String> result = Result.error(403, "访问该端点所需的权限不足");

                try (PrintWriter writer = res.getWriter()) {
                    writer.write(gson.toJson(result));
                }
                return;
            }
            // 非AJAX请求重定向到无权限页面
            res.sendRedirect("/noPermission");
            return;
        }

        chain.doFilter(request, response);
    }

}