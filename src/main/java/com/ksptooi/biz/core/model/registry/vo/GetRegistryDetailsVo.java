package com.ksptooi.biz.core.model.registry.vo;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GetRegistryDetailsVo {

    @Schema(description = "ID")
    private Long id;

    @Schema(description = "父级项ID NULL顶级")
    private Long parentId;

    @Schema(description = "节点Key的全路径")
    private String keyPath;

    @Schema(description = "类型 0:节点 1:条目")
    private Integer kind;

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

    @Schema(description = "元数据JSON")
    private String metadata;

    @Schema(description = "内置值 0:否 1:是")
    private Integer isSystem;

    @Schema(description = "状态 0:正常 1:停用")
    private Integer status;

    @Schema(description = "排序")
    private Integer seq;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

}
