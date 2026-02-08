package com.ksptooi.biz.registryentry.model.vo;

import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

@Getter
@Setter
public class GetRegistryEntryListVo{

    @Schema(description="值ID")
    private Long id;

    @Schema(description="节点ID")
    private Long nodeId;

    @Schema(description="项全键")
    private String nodeKeyPath;

    @Schema(description="条目Key")
    private String ekey;

    @Schema(description="节点Key的全路径")
    private String valueKeyPath;

    @Schema(description="数据类型 0:字串 1:整数 2:浮点 3:日期(LDT)")
    private Integer valueKind;

    @Schema(description="条目值")
    private String value;

    @Schema(description="条目标签")
    private String label;

    @Schema(description="元数据JSON")
    private String metadata;

    @Schema(description="排序")
    private Integer seq;

    @Schema(description="状态 0:正常 1:停用")
    private Integer status;

    @Schema(description="内置值 0:否 1:是")
    private Integer isSystem;

    @Schema(description="创建时间")
    private LocalDateTime createTime;

    @Schema(description="创建人ID")
    private Long creatorId;

    @Schema(description="更新时间")
    private LocalDateTime updateTime;

    @Schema(description="更新人ID")
    private Long updaterId;

    @Schema(description="删除时间 NULL未删")
    private LocalDateTime deleteTime;

}

