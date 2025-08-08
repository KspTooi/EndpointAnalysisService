package com.ksptooi.commons.config;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LocalDateTimeAdapter extends TypeAdapter<LocalDateTime> {

    // 定义我们希望在 JSON 中使用的日期时间格式
    // ISO_LOCAL_DATE_TIME 是一个标准格式，例如: "2025-08-05T22:09:14"
    private static final DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    /**
     * 序列化时调用：将 LocalDateTime 对象转换为 JSON 字符串
     */
    @Override
    public void write(JsonWriter out, LocalDateTime value) throws IOException {
        if (value == null) {
            out.nullValue();
            return;
        }
        // 使用指定的格式将 LocalDateTime 转换为字符串并写入
        out.value(formatter.format(value));
    }

    /**
     * 反序列化时调用：从 JSON 字符串读取并转换为 LocalDateTime 对象
     */
    @Override
    public LocalDateTime read(JsonReader in) throws IOException {
        if (in.peek() == com.google.gson.stream.JsonToken.NULL) {
            in.nextNull();
            return null;
        }
        // 读取字符串并使用指定的格式将其解析为 LocalDateTime
        String json = in.nextString();
        return LocalDateTime.parse(json, formatter);
    }
}