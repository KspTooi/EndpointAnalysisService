package com.ksptool.bio.commons.config;

import com.ksptool.bio.commons.aop.SessionKeepMethodArgumentResolver;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 打包静态文件
        registry.addResourceHandler("/static/**").addResourceLocations("classpath:/web-static/");
        // 网站图标
        registry.addResourceHandler("/favicon.ico").addResourceLocations("classpath:/web-static/");
    }

    /**
     * 将根路径转发到 SPA 入口，Hash 路由由客户端接管，无需通配转发
     */
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/").setViewName("forward:/static/index.html");
    }

    /**
     * 添加方法参数解析器
     *
     * @param resolvers 方法参数解析器列表
     */
    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(new SessionKeepMethodArgumentResolver());
    }

}