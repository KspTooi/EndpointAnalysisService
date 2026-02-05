package com.ksptooi.commons.config;

import java.util.List;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.ksptooi.commons.aop.SessionKeepMethodArgumentResolver;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/src/assets/**").addResourceLocations("classpath:/views/webconsole/src/assets/");
        registry.addResourceHandler("/src/views/**").addResourceLocations("classpath:/views/webconsole/src/views/");
        registry.addResourceHandler("/commons/**").addResourceLocations("classpath:/views/commons/");
        registry.addResourceHandler("/res/**").addResourceLocations("file:userdata/res/");
        registry.addResourceHandler("/node_modules/**").addResourceLocations("file:src/main/resources/views/aether/node_modules/");
        registry.addResourceHandler("/**").addResourceLocations("classpath:/views/static/");
    }

    /**
     * 添加方法参数解析器
     * @param resolvers 方法参数解析器列表
     */
    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(new SessionKeepMethodArgumentResolver());
    }

}