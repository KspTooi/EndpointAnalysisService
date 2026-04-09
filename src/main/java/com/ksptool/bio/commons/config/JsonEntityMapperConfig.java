package com.ksptool.bio.commons.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.ksptool.entities.Entities;
import com.ksptool.entities.mapper.JsonEntityMapper;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;

@Configuration
public class JsonEntityMapperConfig implements JsonEntityMapper {

    static {
        //将全局JSON的默认转换器实现变更为当前的转换器
        Entities.setJsonEntityMapper(new JsonEntityMapperConfig());
    }

    private final Gson gson = (new GsonBuilder())
            .setDateFormat("yyyy-MM-dd HH:mm:ss")
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeTypeAdapter())
            .create();

    @Override
    public String toJson(Object obj) {
        return obj == null ? null : this.gson.toJson(obj);
    }

    @Override
    public <T> T fromJson(String json, Class<T> clazz) {
        if (this.isBlank(json)) {
            return null;
        } else {
            try {
                return this.gson.fromJson(json, clazz);
            } catch (JsonSyntaxException var4) {
                throw new RuntimeException(var4);
            }
        }
    }

    @Override
    public <T> List<T> fromJsonArray(String json, Class<T> clazz) {
        if (this.isBlank(json)) {
            return Collections.emptyList();
        } else {
            try {
                return (List) this.gson.fromJson(json, TypeToken.getParameterized(List.class, new Type[]{clazz}).getType());
            } catch (JsonSyntaxException var4) {
                return Collections.emptyList();
            }
        }
    }

    protected boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }

    private static class LocalDateTimeTypeAdapter extends TypeAdapter<LocalDateTime> {
        private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        @Override
        public void write(JsonWriter out, LocalDateTime value) throws IOException {
            if (value == null) {
                out.nullValue();
                return;
            }
            out.value(formatter.format(value));
        }

        @Override
        public LocalDateTime read(JsonReader in) throws IOException {
            if (in.peek() == com.google.gson.stream.JsonToken.NULL) {
                in.nextNull();
                return null;
            }
            String json = in.nextString();
            if (json == null || json.trim().isEmpty()) {
                return null;
            }
            return LocalDateTime.parse(json, formatter);
        }
    }
}
