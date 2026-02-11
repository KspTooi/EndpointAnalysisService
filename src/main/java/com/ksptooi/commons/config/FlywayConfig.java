package com.ksptooi.commons.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.flyway.autoconfigure.FlywayMigrationStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class FlywayConfig {

    @Bean
    public FlywayMigrationStrategy flywayMigrationStrategy() {
        return flyway -> {

            //获取当前需要执行的SQL脚本数量
            var pending = flyway.info().pending();

            if (pending.length < 1) {
                log.info("[Flyway] 当前数据库表结构已经是最新版本，无需执行迁移。");
                return;
            }

            log.warn("[Flyway] 当前数据库表结构已落后，请使用维护中心执行迁移与更新操作。");
        };
    }
}
