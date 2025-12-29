package com.ksptooi.commons.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

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


}