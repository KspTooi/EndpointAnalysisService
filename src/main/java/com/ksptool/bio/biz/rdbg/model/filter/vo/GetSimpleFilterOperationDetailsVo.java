package com.ksptool.bio.biz.rdbg.model.filter.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GetSimpleFilterOperationDetailsVo {

    @Schema(description = "操作ID")
    private Long id;

    @Schema(description = "操作名称")
    private String name;

    @Schema(description = "类型 0:持久化 1:缓存 2:注入缓存 3.注入持久化 4:覆写URL")
    private Integer kind;

    @Schema(description = "目标 0:标头 1:JSON载荷 2:URL(仅限kind=4)")
    private Integer target;

    @Schema(description = "原始键")
    private String f;

    @Schema(description = "目标键")
    private String t;

    @Schema(description = "排序")
    private Integer seq;

}

