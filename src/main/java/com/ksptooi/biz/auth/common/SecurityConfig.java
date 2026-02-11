package com.ksptooi.biz.auth.common;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

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


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                //关闭 CSRF（前后端分离项目通常关闭，使用 Session/Token 校验）
                .csrf(AbstractHttpConfigurer::disable)

                //配置接口权限规则
                .authorizeHttpRequests(auth -> auth
                        //白名单
                        .requestMatchers(whiteList.toArray(new String[0])).permitAll()

                        //兜底规则 其余所有请求都必须登录
                        .anyRequest().authenticated()
                );

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
