package com.ksptool.bio.core.common;

import com.ksptool.bio.biz.core.common.AppVersion;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

@Slf4j
public class TestAppVersion {

    @Test
    public void testAppVersion() {
        AppVersion appVersion = AppVersion.of("1.0A1024");
        System.out.println(appVersion);
    }

    @Test
    public void testMultiPartVersion() {
        AppVersion appVersion = AppVersion.of("1.0M1");
        System.out.println("主版本号: " + appVersion.getMv());
        System.out.println("次版本号: " + appVersion.getSv());
        System.out.println("修订号: " + appVersion.getRv());
        System.out.println("构建版本号: " + appVersion.getBv());
    }

    @Test
    public void testNumericVersion() {
        AppVersion appVersion = AppVersion.of("1.0M26");
        System.out.println("数字版本号: " + appVersion.toNumericVersion());
    }

}
