package com.ksptool.bio.biz.auth.common;

import com.ksptool.bio.biz.auth.common.aop.UserSessionAuthFilter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ResourceLoader;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.context.NullSecurityContextRepository;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@Slf4j
public class SecurityConfig {

    /**
     * 白名单,这里可以硬编码一些常见的接口,如登录、注册、静态资源等，这些接口不需要登录即可访问
     */
    private final Set<String> whiteList = Stream.of(
            "/maintain/**", // 维护中心
            "/auth/userLogin", // 用户登录
            "/v3/api-docs", // OpenApi 端点
            "/auth/genCaptcha", // 验证码端点
            "/auth/check" // 验证码端点
    ).collect(Collectors.toSet());

    /**
     * 集成部署白名单,这里可以硬编码一些常见的接口,如登录、注册、静态资源等，这些接口不需要登录即可访问
     */
    private final Set<String> integratedDeployWhiteList = Stream.of(
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
            "/index.html",
            "/login").collect(Collectors.toSet());

    @Autowired
    private UserSessionAuthFilter usaf;

    @Autowired
    private JsonAuthEntryPoint jaep;

    @Autowired
    private ResourceLoader resourceLoader;

    @Autowired
    private DynamicGlobalWhiteManager dgwm;

    /**
     * 配置Spring Security
     *
     * @param hs HttpSecurity
     * @return SecurityFilterChain 安全过滤链
     * @throws Exception 异常
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity hs) throws Exception {

        var whiteList = new HashSet<>(this.whiteList);

        if (isIntegratedDeploy()) {
            whiteList.addAll(integratedDeployWhiteList);
            log.info("Auth域安全配置: 当前已启用集成部署模式，加载集成部署白名单。条目:{}", whiteList.size());
        }

        if (!isIntegratedDeploy()) {
            log.info("Auth域安全配置: 当前处于标准部署模式，加载标准白名单。条目:{}", whiteList.size());
        }

        //关闭CSRF(现在前后端使用Authorization头进行鉴权,停用CSRF校验)
        hs.csrf(AbstractHttpConfigurer::disable);

        //关闭Session
        hs.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        //不使用 HttpSession 持久化 SecurityContext（每次请求由 token -> core_user_session 重建认证上下文）
        hs.securityContext(securityContext -> securityContext.securityContextRepository(new NullSecurityContextRepository()));

        //禁用 SavedRequest（默认会把原始请求缓存到 session）
        hs.requestCache(AbstractHttpConfigurer::disable);

        //禁用所有隐式依赖 session 的特性
        hs.formLogin(AbstractHttpConfigurer::disable);
        hs.httpBasic(AbstractHttpConfigurer::disable);
        hs.rememberMe(AbstractHttpConfigurer::disable);
        hs.logout(AbstractHttpConfigurer::disable);

        //一些默认 header 写入可能依赖会话/重定向语义，这里统一禁用；如需 HSTS/CSP/FrameOptions 再单独开启
        hs.headers(HeadersConfigurer::disable);

        //配置接口权限规则
        hs.authorizeHttpRequests(auth -> auth
                //白名单
                .requestMatchers(whiteList.toArray(String[]::new)).permitAll()
                //兜底规则 其余所有请求都必须登录
                .anyRequest().authenticated()
        );

        //配置认证失败和权限不足的处理(统一返回JSON格式)
        hs.exceptionHandling(exception -> exception
                .authenticationEntryPoint(jaep) // 认证失败
                .accessDeniedHandler(jaep) // 权限不足
        );

        //添加自定义过滤器
        //这个过滤器会解析并验证用户会话（依赖 sessionId -> core_user_session 重建认证上下文），确保每次请求都能获取到有效的用户会话
        hs.addFilterBefore(usaf, UsernamePasswordAuthenticationFilter.class);

        return hs.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * 判断是否是集成部署
     * <p>
     * 如果classpath:/web-static/index.html存在，则认为是集成部署
     */
    public boolean isIntegratedDeploy() {
        return resourceLoader.getResource("classpath:/web-static/index.html").exists();
    }

    @Bean
    public FilterRegistrationBean<UserSessionAuthFilter> registrationUserSessionAuthFilter(UserSessionAuthFilter filter) {
        FilterRegistrationBean<UserSessionAuthFilter> registration = new FilterRegistrationBean<>(filter);
        // 关键所在：禁止 Spring Boot 将其自动注册到全局 Servlet 过滤器链中
        registration.setEnabled(false);
        return registration;
    }
}
