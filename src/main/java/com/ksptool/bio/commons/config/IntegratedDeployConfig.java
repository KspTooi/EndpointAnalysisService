package com.ksptool.bio.commons.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnResource;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.method.HandlerTypePredicate;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 集成部署配置
 * <p>
 * 这个配置利用MavenProfile - with-web-ui打包时会将前端文件放入web-static目录这一特征来实现。
 * <p>
 * 在生产环境下 如果选择了集成部署(with-web-ui)那么这个配置会自动生效，以在所有请求路径前添加 /api 前缀。
 */
@Slf4j
@Configuration
@ConditionalOnResource(resources = "classpath:web-static/index.html")
public class IntegratedDeployConfig implements WebMvcConfigurer {

    public IntegratedDeployConfig() {
        log.info(""" 
                \r
                --------------------------------
                当前应用开启了集成部署模式。
                影响效果:
                1. 所有后端请求路径前都会添加 /api 前缀。
                2. /js/** 和 /css/** 路径将分别映射到 classpath:/web-static/js 和 classpath:/web-static/css 目录。
                3. 根路径 / 将转发到 classpath:/web-static/index.html 页面。
                4. 前端项目将会被构建到Jar包 classpath:/web-static/ 目录下。
                --------------------------------
                """);
    }

    /**
     * 配置资源处理器，将打包静态文件和网站图标映射到 classpath:/web-static/ 目录
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {

        //打包静态文件
        registry.addResourceHandler("/js/**").addResourceLocations("classpath:/web-static/css");
        registry.addResourceHandler("/css/**").addResourceLocations("classpath:/web-static/js");

        //网站图标
        registry.addResourceHandler("/favicon.ico").addResourceLocations("classpath:/web-static/");
    }

    /**
     * 配置路径匹配，为所有 @RestController 的请求路径前添加 /api 前缀
     */
    @Override
    public void configurePathMatch(PathMatchConfigurer configurer) {
        configurer.addPathPrefix("/api", HandlerTypePredicate.forAnnotation(RestController.class));
    }

    /**
     * 将根路径转发到 SPA 入口，Hash 路由由客户端接管，无需通配转发
     */
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/").setViewName("forward:/static/index.html");
    }
}
