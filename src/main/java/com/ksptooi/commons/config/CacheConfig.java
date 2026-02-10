package com.ksptooi.commons.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
public class CacheConfig {

    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();
        cacheManager.setCaffeine(Caffeine.newBuilder()
                //设置最后一次写入后经过多久过期
                .expireAfterWrite(10, TimeUnit.MINUTES)
                //初始缓存空间大小
                .initialCapacity(100)
                //缓存最大条数
                .maximumSize(1000));
        return cacheManager;
    }

}
