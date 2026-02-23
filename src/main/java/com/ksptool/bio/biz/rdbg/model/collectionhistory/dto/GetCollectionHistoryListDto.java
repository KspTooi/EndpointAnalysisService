package com.ksptool.bio.biz.rdbg.model.collectionhistory.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

import com.ksptool.assembly.entity.web.PageQuery;

@Getter
@Setter
public class GetCollectionHistoryListDto extends PageQuery {

    @Schema(description = "记录ID")
    private Long id;

    @Schema(description = "公司ID")
    private Long companyId;

    @Schema(description = "集合ID")
    private Long collectionId;

    @Schema(description = "请求URL")
    private String reqUrl;

    @Schema(description = "请求URL查询参数")
    private String reqUrlParamsJson;

    @Schema(description = "请求方法 0:GET 1:POST 2:PUT 3:PATCH 4:DELETE 5:HEAD 6:OPTIONS")
    private Integer reqMethod;

    @Schema(description = "请求头JSON")
    private String reqHeaderJson;

    @Schema(description = "请求体JSON")
    private String reqBodyJson;

    @Schema(description = "响应头JSON")
    private String retHeaderJson;

    @Schema(description = "响应体JSON")
    private String retBodyJson;

    @Schema(description = "HTTP状态码")
    private Integer retHttpStatus;

    @Schema(description = "业务状态 0:正常 1:HTTP失败 2:业务失败 3:正在处理")
    private Integer bizStatus;

    @Schema(description = "请求发起时间")
    private LocalDateTime reqTime;

    @Schema(description = "响应时间")
    private LocalDateTime retTime;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "创建人ID")
    private Long creatorId;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

    @Schema(description = "更新人ID")
    private Long updaterId;

    @Schema(description = "删除时间 NULL未删除")
    private LocalDateTime deleteTime;

}

