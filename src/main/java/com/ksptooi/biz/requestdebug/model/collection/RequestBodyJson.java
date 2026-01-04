package com.ksptooi.biz.requestdebug.model.collection;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RequestBodyJson {

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
    private List<RequestParamJson> formData;

    @Schema(description = "请求体数据(X-WWW-FORM-URL-ENCODED)")
    private List<RequestParamJson> formDataUrlEncoded;

    @Schema(description = "请求体数据(RAW)")
    private String rawData;

    @Schema(description = "请求体数据(BINARY)")
    private String binaryData;

}
