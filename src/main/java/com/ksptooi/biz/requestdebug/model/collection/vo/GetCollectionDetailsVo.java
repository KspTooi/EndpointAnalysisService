package com.ksptooi.biz.requestdebug.model.collection.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class GetCollectionDetailsVo {

    @Schema(description = "主键ID")
    private Long id;

    @Schema(description = "父级ID NULL顶级")
    private Long parentId;

    @Schema(description = "集合名称")
    private String name;

    @Schema(description = "集合类型 0:请求 1:组")
    private Integer kind;

    @Schema(description = "请求URL")
    private String reqUrl;

    @Schema(description = "请求URL参数JSON")
    private String reqUrlParamsJson;

    @Schema(description = "请求方法 0:GET 1:POST 2:PUT 3:PATCH 4:DELETE 5:HEAD 6:OPTIONS")
    private Integer reqMethod;

    @Schema(description = "请求头JSON")
    private String reqHeaderJson;

    @Schema(description = "请求体类型 0:空 1:form-data 2:raw-text 3:raw-javascription 4:raw-json 5:raw-html 6:raw-xml 7:binary 8:x-www-form-urlencoded")
    private Integer reqBodyKind;

    @Schema(description = "请求体JSON")
    private String reqBodyJson;

    @Schema(description = "排序")
    private Integer seq;

}

