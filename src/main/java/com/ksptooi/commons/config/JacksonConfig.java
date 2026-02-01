package com.ksptooi.commons.config;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Jackson全局配置
 * 将Long类型转换为String类型返回给前端，避免精度丢失
 * LocalDateTime自动序列化/反序列化为yyyy-MM-dd HH:mm:ss格式
 */
@Configuration
public class JacksonConfig {

    private static final String DATE_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern(DATE_TIME_PATTERN);

    @Bean
    public Module jacksonModule() {
        SimpleModule module = new SimpleModule();
        // 注册Long类型转String序列化器
        module.addSerializer(Long.class, ToStringSerializer.instance);
        module.addSerializer(Long.TYPE, ToStringSerializer.instance);
        // 注册LocalDateTime序列化器与反序列化器
        module.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer());
        module.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer());
        return module;
    }

    /**
     * LocalDateTime序列化器
     * 将LocalDateTime对象序列化为yyyy-MM-dd HH:mm:ss格式字符串
     */
    public static class LocalDateTimeSerializer extends JsonSerializer<LocalDateTime> {
        @Override
        public void serialize(LocalDateTime value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
            if (value == null) {
                return;
            }
            gen.writeString(value.format(DATE_TIME_FORMATTER));
        }
    }

    /**
     * LocalDateTime反序列化器
     * 将yyyy-MM-dd HH:mm:ss格式字符串反序列化为LocalDateTime对象
     */
    public static class LocalDateTimeDeserializer extends JsonDeserializer<LocalDateTime> {
        @Override
        public LocalDateTime deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            String value = p.getValueAsString();
            if (StringUtils.isBlank(value)) {
                return null;
            }
            return LocalDateTime.parse(value, DATE_TIME_FORMATTER);
        }
    }
}