package com.ksptool.bio.biz.auth.common;

import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.authorization.AuthorizationResult;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Stream;

@Slf4j
@Component
public class DynamicGlobalWhiteManager implements AuthorizationManager<RequestAuthorizationContext> {

    //使用 Spring Security 7.0 全新推荐的 PathPatternRequestMatcher
    private final List<PathPatternRequestMatcher> whiteListMatchers = new ArrayList<>();

    @Autowired
    private ResourceLoader resourceLoader;

    /**
     * 初始化白名单规则
     */
    @PostConstruct
    public void init() {
        // 1. 加载基础白名单
        Stream.of(
                "/maintain/**",
                "/auth/userLogin",
                "/v3/api-docs",
                "/auth/genCaptcha",
                "/auth/check"
        ).map(PathPatternRequestMatcher::pathPattern).forEach(whiteListMatchers::add);

        // 2. 动态判断并加载集成部署白名单
        if (isIntegratedDeploy()) {
            Stream.of(
                    "/api/maintain/**",
                    "/api/auth/userLogin",
                    "/api/v3/api-docs",
                    "/api/auth/genCaptcha",
                    "/api/auth/check",
                    "/js/**",
                    "/css/**",
                    "/assets/**",
                    "/index.html",
                    "/favicon.ico",
                    "/",
                    "/login"
            ).map(PathPatternRequestMatcher::pathPattern).forEach(whiteListMatchers::add);
            log.info("DGWM 白名单加载: 当前已启用集成部署模式，总条目: {}", whiteListMatchers.size());
        } else {
            log.info("DGWM 白名单加载: 当前处于标准部署模式，总条目: {}", whiteListMatchers.size());
        }
    }

    @Override
    public @Nullable AuthorizationResult authorize(@NonNull Supplier<? extends @Nullable Authentication> authentication, RequestAuthorizationContext rac) {

        if (rac == null) {
            return new AuthorizationDecision(false);
        }

        HttpServletRequest request = rac.getRequest();

        // 步骤一：检查是否匹配白名单
        boolean isWhiteListed = whiteListMatchers.stream()
                .anyMatch(matcher -> matcher.matches(request));

        if (isWhiteListed) {
            // 白名单直接放行
            return new AuthorizationDecision(true);
        }

        // 步骤二：非白名单请求，执行兜底认证检查 (必须已登录且非匿名)
        Authentication auth = authentication.get();
        boolean isAuth = auth != null && auth.isAuthenticated() && !(auth instanceof AnonymousAuthenticationToken);

        return new AuthorizationDecision(isAuth);
    }

    /**
     * 判断是否是集成部署
     */
    private boolean isIntegratedDeploy() {
        return resourceLoader.getResource("classpath:/web-static/index.html").exists();
    }
}