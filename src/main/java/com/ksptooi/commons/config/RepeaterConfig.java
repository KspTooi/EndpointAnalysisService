package com.ksptooi.commons.config;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Bean;

import java.net.URI;
import java.net.http.HttpClient;
import java.time.Duration;

@Getter
@Configuration
public class RepeaterConfig {

    //@Value("${repeater.proxy_pass}")
    private String proxyPass;

    public URI buildTargetUri(jakarta.servlet.http.HttpServletRequest request) {
        String base = proxyPass;
        if (StringUtils.isBlank(base)) {
            throw new IllegalStateException("未配置repeater.proxy_pass");
        }
        if (StringUtils.startsWithIgnoreCase(base, "http://") || StringUtils.startsWithIgnoreCase(base, "https://")) {
            // do nothing
        }
        if (!StringUtils.startsWithIgnoreCase(base, "http://") && !StringUtils.startsWithIgnoreCase(base, "https://")) {
            base = "http://" + base;
        }

        StringBuilder url = new StringBuilder();
        url.append(base);
        url.append(request.getRequestURI());

        String query = request.getQueryString();
        if (StringUtils.isNotBlank(query)) {
            url.append('?').append(query);
        }
        return URI.create(url.toString());
    }

    @Bean
    public HttpClient httpClient() {
        return HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(15))
                .build();
    }
}
