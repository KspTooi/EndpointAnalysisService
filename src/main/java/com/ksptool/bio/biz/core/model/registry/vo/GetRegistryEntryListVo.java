package com.ksptool.bio.biz.core.model.registry.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class GetRegistryEntryListVo {

    @Schema(description = "ID")
    private Long id;

    @Schema(description = "父级项ID NULL顶级")
    private Long parentId;

    @Schema(description = "节点Key")
    private String nkey;

    @Schema(description = "数据类型 0:字串 1:整数 2:浮点 3:日期(LDT)")
    private Integer nvalueKind;

    @Schema(description = "节点Value")
    private String nvalue;

    @Schema(description = "节点标签")
    private String label;

    @Schema(description = "说明")
    private String remark;

    @Schema(description = "状态 0:正常 1:停用")
    private Integer status;

    @Schema(description = "排序")
    private Integer seq;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

}
