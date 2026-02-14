package com.ksptooi.biz.post.model.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

@Getter
@Setter
public class EditPostDto {


    @Schema(description="岗位ID")
    private Long id;

    @Schema(description="岗位名称")
    private String name;

    @Schema(description="岗位编码")
    private String code;

    @Schema(description="岗位排序")
    private Integer seq;

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

