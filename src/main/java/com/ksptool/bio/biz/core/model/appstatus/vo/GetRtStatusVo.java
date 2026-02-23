package com.ksptool.bio.biz.core.model.appstatus.vo;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GetRtStatusVo {

    @Schema(description = "系统平均负载 1分钟，Windows 不可用时为 null")
    private Double load1;

    @Schema(description = "进程数")
    private Long processCount;

    @Schema(description = "线程数")
    private Long threadCount;

    @Schema(description = "CPU使用率 0~100 (%)，首次采样可能为 0")
    private Integer cpuUsage;

    @Schema(description = "物理内存总量 MB")
    private Long memoryTotal;

    @Schema(description = "内存使用率 0~100 (%)")
    private Integer memoryUsage;

    @Schema(description = "交换区总量 MB，未启用交换区时为 0")
    private Long swapTotal;

    @Schema(description = "交换区使用率 0~100 (%)，未启用交换区时为 0")
    private Integer swapUsage;

    @Schema(description = "磁盘读取速率 bytes/s（所有磁盘聚合），首次采样为 0")
    private Long ioRead;

    @Schema(description = "磁盘写入速率 bytes/s（所有磁盘聚合），首次采样为 0")
    private Long ioWrite;

    @Schema(description = "网络接收速率 bytes/s（所有网卡聚合），首次采样为 0")
    private Long networkRx;

    @Schema(description = "网络发送速率 bytes/s（所有网卡聚合），首次采样为 0")
    private Long networkTx;

    @Schema(description = "网络接收速率 包/s（所有网卡聚合），首次采样为 0")
    private Long networkIn;

    @Schema(description = "网络发送速率 包/s（所有网卡聚合），首次采样为 0")
    private Long networkOut;

    @Schema(description = "本次采样时间")
    private LocalDateTime createTime;

}
