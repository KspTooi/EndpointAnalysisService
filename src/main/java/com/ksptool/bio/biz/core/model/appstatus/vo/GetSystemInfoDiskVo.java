package com.ksptool.bio.biz.core.model.appstatus.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GetSystemInfoDiskVo {

    @Schema(description = "盘符或挂载点")
    private String dirName;

    @Schema(description = "文件系统类型")
    private String fileSystem;

    @Schema(description = "总容量 后端返字节数 前端自动显示为MB/GB/TB")
    private Long totalCapacity;

    @Schema(description = "已用容量 后端返字节数 前端自动显示为MB/GB/TB")
    private Long usedCapacity;

    @Schema(description = "可用容量 后端返字节数 前端自动显示为MB/GB/TB")
    private Long availableCapacity;

    @Schema(description = "使用率 0~100 (%)")
    private Integer usage;

}
