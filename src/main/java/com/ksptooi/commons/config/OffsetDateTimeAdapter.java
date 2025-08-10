package com.ksptooi.commons.config;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 用于 Google GSON 的 OffsetDateTime 类型适配器。
 * <p>
 * 将 OffsetDateTime 序列化为 ISO-8601 格式的字符串 (例如 "2025-08-11T01:51:24.123+09:00")。
 * 并能从同样格式的字符串反序列化回 OffsetDateTime 对象。
 * </p>
 */
public class OffsetDateTimeAdapter extends TypeAdapter<OffsetDateTime> {

    // 使用 ISO 标准格式，这是现代 API 和日志中最推荐的格式。
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_OFFSET_DATE_TIME;

    /**
     * 将 OffsetDateTime 对象写入 JSON。
     * @param out JsonWriter
     * @param value 要写入的 OffsetDateTime 对象，可以为 null。
     */
    @Override
    public void write(final JsonWriter out, final OffsetDateTime value) throws IOException {
        if (value == null) {
            // 如果对象是 null，则写入 JSON null
            out.nullValue();
        } else {
            // 否则，使用定义的格式化器将其转换为字符串并写入
            out.value(FORMATTER.format(value));
        }
    }

    /**
     * 从 JSON 读取并转换为 OffsetDateTime 对象。
     * @param in JsonReader
     * @return 解析后的 OffsetDateTime 对象，或者如果 JSON 值为 null 则返回 null。
     */
    @Override
    public OffsetDateTime read(final JsonReader in) throws IOException {
        // 首先检查 token 类型是否为 null
        if (in.peek() == JsonToken.NULL) {
            in.nextNull();
            return null;
        } else {
            // 如果不是 null，则读取字符串并使用格式化器进行解析
            String stringValue = in.nextString();
            return OffsetDateTime.parse(stringValue, FORMATTER);
        }
    }
}