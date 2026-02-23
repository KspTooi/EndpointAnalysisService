package com.ksptool.bio.commons.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
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
                .title("Bio API")
                .description("Bio 接口文档")
                .version("1.0.0")
                .contact(new Contact().name("Bio").email("ksptooi@outlook.com"))
                .license(new License().name("Apache-2.0"));

        return new OpenAPI()
                .components(new Components().addSecuritySchemes("cookieAuth", cookieAuth))
                .addSecurityItem(new SecurityRequirement().addList("cookieAuth"))
                .info(info);
    }

//    @Bean
//    public GroupedOpenApi coreApiGroup() {
//        return GroupedOpenApi.builder()
//                .group("core")
//                .packagesToScan("com.ksptool.bio.biz")
//                .build();
//    }


}

