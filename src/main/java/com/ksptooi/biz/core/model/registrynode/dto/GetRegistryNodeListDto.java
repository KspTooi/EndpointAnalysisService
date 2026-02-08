package com.ksptooi.biz.core.model.registrynode.dto;

import com.ksptool.assembly.entity.web.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class GetRegistryNodeListDto extends PageQuery {


    @Schema(description = "ID")
    private Long id;

    @Schema(description = "父级ID NULL顶级")
    private Long parentId;

    @Schema(description = "KEY的全路径")
    private String keyPath;

    @Schema(description = "键")
    private String nkey;

    @Schema(description = "数据类型 0:字串 1:整数 2:浮点 3:日期(LDT)")
    private Integer nvalueKind;

    @Schema(description = "值")
    private String nvalue;

    @Schema(description = "标签")
    private String label;

    @Schema(description = "状态 0:正常 1:停用")
    private Integer status;

    @Schema(description = "排序")
    private Integer seq;

    @Schema(description = "说明")
    private String remark;

    @Schema(description = "元数据JSON")
    private String metadata;

    @Schema(description = "内置项 0:否 1:是")
    private Integer isSystem;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "创建人ID")
    private Long creatorId;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

    @Schema(description = "更新人ID")
    private Long updaterId;

    @Schema(description = "删除时间 NULL未删")
    private LocalDateTime deleteTime;
}

