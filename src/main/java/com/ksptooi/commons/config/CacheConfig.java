package com.ksptooi.commons.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Configuration
public class CacheConfig {

    @Bean
    public CacheManager cacheManager() {
        SimpleCacheManager cacheManager = new SimpleCacheManager();

        // 定义多种缓存策略
        List<CaffeineCache> caches = new ArrayList<>();

        //策略 用户Session 最多1000条 5分钟过期
        caches.add(new CaffeineCache("userSession",
                Caffeine.newBuilder()
                        .expireAfterWrite(5, TimeUnit.MINUTES)
                        .maximumSize(1000)
                        .build()));

        //策略 端点接口动态权限配置 最多2000条 30分钟过期
        caches.add(new CaffeineCache("endpoint",
                Caffeine.newBuilder()
                        .expireAfterWrite(30, TimeUnit.MINUTES)
                        .maximumSize(2000)
                        .build()));

        cacheManager.setCaches(caches);
        return cacheManager;
    }

}
