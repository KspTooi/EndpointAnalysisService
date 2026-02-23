package com.ksptool.bio.commons.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnResource;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.method.HandlerTypePredicate;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import lombok.extern.slf4j.Slf4j;

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

    @Override
    public void configurePathMatch(PathMatchConfigurer configurer) {
        // 只给 @RestController 加 /api 前缀，@Controller（SPA转发）不受影响
        configurer.addPathPrefix("/api", HandlerTypePredicate.forAnnotation(RestController.class));
        log.info("集成部署配置已生效，现在所有请求路径前都会添加 /api 前缀。");
    }
}
