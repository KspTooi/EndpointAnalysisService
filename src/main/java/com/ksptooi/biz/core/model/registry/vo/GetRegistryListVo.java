package com.ksptooi.biz.core.model.registry.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class GetRegistryListVo {

    @Schema(description = "ID")
    private Long id;

    @Schema(description = "父级项ID NULL顶级")
    private Long parentId;

    @Schema(description = "节点Key的全路径")
    private String keyPath;

    @Schema(description = "节点Key")
    private String nkey;

    @Schema(description = "节点标签")
    private String label;

    @Schema(description = "排序")
    private Integer seq;

    @Schema(description = "说明")
    private String remark;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

}
