package com.ksptooi.biz.auth.common;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
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

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * 白名单,这里可以硬编码一些常见的接口,如登录、注册、静态资源等，这些接口不需要登录即可访问
     */
    private final List<String> whiteList = Arrays.asList(
            "/maintain/**", //维护中心
            "/auth/userLogin" //用户登录(新)
    );

    @Autowired
    private UserSessionAuthFilter userSessionAuthFilter;

    @Autowired
    private JsonAuthEntryPoint jsonAuthEntryPoint;

    /**
     * 配置Spring Security
     *
     * @param http HttpSecurity
     * @return SecurityFilterChain 安全过滤链
     * @throws Exception 异常
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                //关闭 CSRF（前后端分离项目通常关闭，使用 Session/Token 校验）
                .csrf(AbstractHttpConfigurer::disable)

                //禁用Session
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                //不使用 HttpSession 持久化 SecurityContext（每次请求由 token -> core_user_session 重建认证上下文）
                .securityContext(securityContext -> securityContext.securityContextRepository(new NullSecurityContextRepository()))

                //禁用 SavedRequest（默认会把原始请求缓存到 session）
                .requestCache(AbstractHttpConfigurer::disable)

                //禁用所有隐式依赖 session 的特性
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .rememberMe(AbstractHttpConfigurer::disable)
                .logout(AbstractHttpConfigurer::disable)

                //一些默认 header 写入可能依赖会话/重定向语义，这里统一禁用；如需 HSTS/CSP/FrameOptions 再单独开启
                .headers(HeadersConfigurer::disable)

                //配置接口权限规则
                .authorizeHttpRequests(auth -> auth
                        //白名单
                        .requestMatchers(whiteList.toArray(new String[0])).permitAll()

                        //兜底规则 其余所有请求都必须登录
                        .anyRequest().authenticated()
                )
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(jsonAuthEntryPoint)
                        .accessDeniedHandler(jsonAuthEntryPoint)
                );

        //添加自定义过滤器
        //这个过滤器会解析并验证用户会话（依赖 sessionId -> core_user_session 重建认证上下文），确保每次请求都能获取到有效的用户会话
        http.addFilterBefore(userSessionAuthFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
