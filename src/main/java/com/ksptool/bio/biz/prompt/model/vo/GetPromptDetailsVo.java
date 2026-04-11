package com.ksptool.bio.biz.prompt.model.vo;

import java.time.LocalDateTime;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import io.swagger.v3.oas.annotations.media.Schema;

@Getter
@Setter
public class GetPromptDetailsVo {

    @Schema(description="主键ID")
    private Long id;

    @Schema(description="名称")
    private String name;

    @Schema(description="标签(CTJ)")
    private String tags;

    @Schema(description="内容")
    private String content;

}
