package com.ksptooi.biz.requestdebug.model.collection;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class RequestHeaderJson {

    @Schema(description = "是否默认")
    private boolean d;

    @Schema(description = "是否启用")
    private boolean e;

    @Schema(description = "是否自动计算")
    private boolean a;

    @Schema(description = "请求头键")
    private String k;

    @Schema(description = "请求头值")
    private String v;

    @Schema(description = "排序")
    private Integer s;

    public static RequestHeaderJson of(String k, String v) {
        RequestHeaderJson requestHeaderJson = new RequestHeaderJson();
        requestHeaderJson.setD(false);
        requestHeaderJson.setE(false);
        requestHeaderJson.setA(false);
        requestHeaderJson.setK(k);
        requestHeaderJson.setV(v);
        requestHeaderJson.setS(0);
        return requestHeaderJson;
    }

    public static RequestHeaderJson ofDefault(String k, String v, Integer s) {
        RequestHeaderJson requestHeaderJson = new RequestHeaderJson();
        requestHeaderJson.setD(true);
        requestHeaderJson.setE(true);
        requestHeaderJson.setA(false);
        requestHeaderJson.setK(k);
        requestHeaderJson.setV(v);
        requestHeaderJson.setS(s);
        return requestHeaderJson;
    }

    public static RequestHeaderJson ofDefaultAuto(String k, String v, Integer s) {
        RequestHeaderJson requestHeaderJson = new RequestHeaderJson();
        requestHeaderJson.setD(true);
        requestHeaderJson.setE(true);
        requestHeaderJson.setA(true);
        requestHeaderJson.setK(k);
        requestHeaderJson.setV(v);
        requestHeaderJson.setS(s);
        return requestHeaderJson;
    }

    public static List<RequestHeaderJson> ofDefaultList() {
        List<RequestHeaderJson> requestHeaderJsonList = new ArrayList<>();
        requestHeaderJsonList.add(ofDefaultAuto("eas-token", "auto-calculated", -100));
        requestHeaderJsonList.add(ofDefaultAuto("Content-Type", "auto-calculated", -95));
        requestHeaderJsonList.add(ofDefaultAuto("Content-Length", "auto-calculated", -90));
        requestHeaderJsonList.add(ofDefaultAuto("Host", "auto-calculated", -85));
        requestHeaderJsonList.add(ofDefault("User-Agent", "EasServer/1.4M/CP23", -80));
        requestHeaderJsonList.add(ofDefault("Accept", "*/*", -75));
        requestHeaderJsonList.add(ofDefault("Accept-Encoding", "gzip, deflate, br", -70));
        requestHeaderJsonList.add(ofDefault("Connection", "keep-alive", -65));
        return requestHeaderJsonList;
    }


}
