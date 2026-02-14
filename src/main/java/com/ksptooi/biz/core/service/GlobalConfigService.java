package com.ksptooi.biz.core.service;

import com.ksptooi.biz.core.model.config.ConfigPo;
import com.ksptooi.biz.core.repository.ConfigRepository;
import com.ksptooi.commons.enums.GlobalConfigEnum;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GlobalConfigService {
    
    private final ConfigRepository configRepository;

    public String getValue(String key) {
        ConfigPo config = configRepository.getGlobalConfig(key);
        if(config == null){
            return null;
        }
        return config.getConfigValue();
    }

    public void setValue(String key, String value) {
        ConfigPo config = configRepository.getGlobalConfig(key);
        if (config == null) {
            config = new ConfigPo();
            config.setConfigKey(key);
            config.setUser(null);
            config.setCreateTime(LocalDateTime.now());
        }
        config.setConfigValue(value);
        config.setUpdateTime(LocalDateTime.now());
        configRepository.save(config);
    }

    /**
     * 获取全局配置值
     */
    public String get(String key) {
        return getValue(key);
    }

    /**
     * 获取全局配置值，如果不存在返回默认值
     */
    public String get(String key, String defaultValue) {
        String value = get(key);
        return StringUtils.isNotBlank(value) ? value : defaultValue;
    }

    /**
     * 获取整数配置值
     */
    public int getInt(String key, int defaultValue) {
        String value = get(key);
        if (StringUtils.isBlank(value)) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    /**
     * 获取长整数配置值
     */
    public long getLong(String key, long defaultValue) {
        String value = get(key);
        if (StringUtils.isBlank(value)) {
            return defaultValue;
        }
        try {
            return Long.parseLong(value);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    /**
     * 获取双精度浮点数配置值
     */
    public double getDouble(String key, double defaultValue) {
        String value = get(key);
        if (StringUtils.isBlank(value)) {
            return defaultValue;
        }
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    /**
     * 获取布尔配置值
     */
    public boolean getBoolean(String key, boolean defaultValue) {
        String value = get(key);
        if (StringUtils.isBlank(value)) {
            return defaultValue;
        }
        return Boolean.parseBoolean(value);
    }


} 