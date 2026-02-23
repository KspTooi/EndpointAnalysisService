package com.ksptool.bio.biz.rdbg.model.collectionhistory.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class GetCollectionHistoryListVo {

    @Schema(description = "记录ID")
    private Long id;

    @Schema(description = "集合ID")
    private Long collectionId;

    @Schema(description = "请求URL")
    private String reqUrl;

    @Schema(description = "请求方法 0:GET 1:POST 2:PUT 3:PATCH 4:DELETE 5:HEAD 6:OPTIONS")
    private Integer reqMethod;

    @Schema(description = "HTTP状态码")
    private Integer retHttpStatus;

    @Schema(description = "业务状态 0:正常 1:HTTP失败 2:业务失败 3:正在处理 4:EAS内部错误")
    private Integer bizStatus;

    @Schema(description = "请求发起时间")
    private LocalDateTime reqTime;

    @Schema(description = "响应时间")
    private LocalDateTime retTime;

}

