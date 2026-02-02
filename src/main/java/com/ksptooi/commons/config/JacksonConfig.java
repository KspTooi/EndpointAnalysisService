package com.ksptooi.commons.config;

import tools.jackson.core.JsonGenerator;
import tools.jackson.core.JsonParser;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.ValueDeserializer;
import tools.jackson.databind.ValueSerializer;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.module.SimpleModule;
import tools.jackson.databind.ser.std.ToStringSerializer;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.jackson.autoconfigure.JsonMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.TimeZone;

/**
 * Jackson全局配置 (Spring Boot 4.0 / Jackson 3 适配版)
 * Long/BigInteger -> String (解决前端精度丢失)
 * LocalDateTime -> yyyy-MM-dd HH:mm:ss
 */
@Configuration
public class JacksonConfig {

    private static final String DATE_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern(DATE_TIME_PATTERN);

    @Bean
    public JsonMapperBuilderCustomizer jsonMapperBuilderCustomizer() {
        return builder -> {
            //全局基础配置
            builder.defaultTimeZone(TimeZone.getTimeZone("GMT+8"));
            builder.defaultDateFormat(new SimpleDateFormat(DATE_TIME_PATTERN));
            
            //禁用 "遇到未知属性报错"
            builder.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

            //创建自定义模块
            SimpleModule simpleModule = new SimpleModule();

            // 处理精度丢失：Long/BigInteger -> String
            simpleModule.addSerializer(Long.class, ToStringSerializer.instance);
            simpleModule.addSerializer(Long.TYPE, ToStringSerializer.instance);
            simpleModule.addSerializer(BigInteger.class, ToStringSerializer.instance);

            // 处理时间格式
            simpleModule.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer());
            simpleModule.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer());

            //将模块注册到 Builder
            builder.addModule(simpleModule);
        };
    }

    /**
     * LocalDateTime序列化器
     */
    public static class LocalDateTimeSerializer extends ValueSerializer<LocalDateTime> {
        @Override
        public void serialize(LocalDateTime value, JsonGenerator gen, SerializationContext serializers) throws JacksonException {
            if (value != null) {
                gen.writeString(value.format(DATE_TIME_FORMATTER));
            }
        }
    }

    /**
     * LocalDateTime反序列化器
     */
    public static class LocalDateTimeDeserializer extends ValueDeserializer<LocalDateTime> {
        @Override
        public LocalDateTime deserialize(JsonParser p, DeserializationContext ctxt) throws JacksonException {
            String value = p.getValueAsString();
            if (StringUtils.isBlank(value)) {
                return null;
            }
            return LocalDateTime.parse(value, DATE_TIME_FORMATTER);
        }
    }
}
