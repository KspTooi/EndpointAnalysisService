package com.ksptooi.commons.httprelay.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class RelayBody {

    @Schema(description = """
                请求体类型
                0:空
                1:form-data
                2:raw-text
                3:raw-javascription
                4:raw-json
                5:raw-html
                6:raw-xml
                7:binary
                8:x-www-form-urlencoded
            """)
    private Integer kind;

    @Schema(description = "请求体数据(FORM-DATA)")
    private List<RelayParam> formData;

    @Schema(description = "请求体数据(X-WWW-FORM-URL-ENCODED)")
    private List<RelayParam> formDataUrlEncoded;

    @Schema(description = "请求体数据(RAW)")
    private String rawData;

    @Schema(description = "请求体数据(BINARY)")
    private String binaryData;

    /**
     * 默认请求体
     *
     * @return 默认请求体
     */
    public static RelayBody ofDefault() {
        RelayBody relayBody = new RelayBody();
        relayBody.setKind(0);
        relayBody.setFormData(new ArrayList<>());
        relayBody.setFormDataUrlEncoded(new ArrayList<>());
        relayBody.setRawData("");
        relayBody.setBinaryData("");
        return relayBody;
    }

}
