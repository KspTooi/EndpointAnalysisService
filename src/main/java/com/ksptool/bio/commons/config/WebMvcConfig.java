package com.ksptool.bio.commons.config;

import com.ksptool.bio.biz.core.common.aop.AppInstallWizardInterceptor;
import com.ksptool.bio.commons.aop.SessionKeepMethodArgumentResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Autowired
    private AppInstallWizardInterceptor appInstallWizardInterceptor;


    /**
     * 配置资源处理器，将打包静态文件和网站图标映射到 classpath:/web-static/ 目录
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {

        //打包静态文件
        registry.addResourceHandler("/static/**").addResourceLocations("classpath:/web-static/");
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

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(appInstallWizardInterceptor);
    }

}