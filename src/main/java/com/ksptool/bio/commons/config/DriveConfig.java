package com.ksptool.bio.commons.config;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
@Getter
public class DriveConfig {

    //签名SecretKey
    @Value("${module-drive.sign-secret-key}")
    private String signSecretKey;

    //签名有效时间(秒)
    @Value("${module-drive.ttl}")
    private Long ttl;

}
