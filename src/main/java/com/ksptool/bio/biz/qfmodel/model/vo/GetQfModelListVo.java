package com.ksptool.bio.biz.qfmodel.model.vo;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import io.swagger.v3.oas.annotations.media.Schema;

@Getter
@Setter
public class GetQfModelListVo{

    @Schema(description="主键ID")
    private Long id;

    @Schema(description="模型组ID")
    private Long groupId;

    @Schema(description="模型名称")
    private String name;

    @Schema(description="模型编码")
    private String code;

    @Schema(description="模型版本号")
    private Integer dataVersion;

    @Schema(description="模型状态 0:草稿 1:已部署 2:历史")
    private Integer status;

    @Schema(description="排序")
    private Integer seq;

    @Schema(description="创建时间")
    private LocalDateTime createTime;
}

