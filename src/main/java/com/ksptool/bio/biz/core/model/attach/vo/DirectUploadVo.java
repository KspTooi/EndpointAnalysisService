package com.ksptool.bio.biz.core.model.attach.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
public class DirectUploadVo {
    
    @Schema(description = "服务端附件ID")
    private Long id;

    @Schema(description = "服务端文件名")
    private String name;

    @Schema(description = "服务端文件总大小")
    private Long totalSize;

    @Schema(description = "业务代码")
    private String kind;

    @Schema(description = "服务端文件路径")
    private String path;

    @Schema(description = "状态 0:预检文件 1:区块不完整 2:校验中 3:正常")
    private Integer status;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

}
