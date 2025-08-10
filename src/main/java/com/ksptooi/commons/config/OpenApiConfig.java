package com.ksptooi.commons.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI easOpenAPI() {
        var cookieAuth = new SecurityScheme()
                .type(SecurityScheme.Type.APIKEY)
                .in(SecurityScheme.In.COOKIE)
                .name("token");

        var info = new Info()
                .title("Endpoint Analysis Service API")
                .description("EAS 接口文档")
                .version("1.0.0")
                .contact(new Contact().name("EAS").email("admin@example.com"))
                .license(new License().name("Apache-2.0"));

        return new OpenAPI()
                .components(new Components().addSecuritySchemes("cookieAuth", cookieAuth))
                .addSecurityItem(new SecurityRequirement().addList("cookieAuth"))
                .info(info);
    }

    @Bean
    public GroupedOpenApi coreApiGroup() {
        return GroupedOpenApi.builder()
                .group("core")
                .packagesToScan("com.ksptooi.biz")
                .build();
    }



}

