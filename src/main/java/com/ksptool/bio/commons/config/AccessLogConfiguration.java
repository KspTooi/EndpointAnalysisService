package com.ksptool.bio.commons.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;


@Configuration

public class AccessLogConfiguration {

    @Value("${accessLog.enable:true}")
    private boolean enabled;

    @Value("${accessLog.payloadMaxLength:512}")
    private int payloadMaxLength;

    @Value("${application.name:any}")
    private String applicationName;


    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public int getPayloadMaxLength() {
        return payloadMaxLength;
    }

    public void setPayloadMaxLength(int payloadMaxLength) {
        this.payloadMaxLength = payloadMaxLength;
    }

    public String getApplicationName() {
        return applicationName;
    }

    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
    }
}
