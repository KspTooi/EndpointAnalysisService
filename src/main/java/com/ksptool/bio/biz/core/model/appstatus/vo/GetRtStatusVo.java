package com.ksptool.bio.biz.core.model.appstatus.vo;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GetRtStatusVo {

    @Schema(description = "系统平均负载 1分钟")
    private Double load1;

    @Schema(description = "进程数")
    private Long processCount;

    @Schema(description = "线程数")
    private Long threadCount;

    @Schema(description = "CPU使用率 %")
    private Integer cpuUsage;

    @Schema(description = "内存总量 MB")
    private Long memoryTotal;

    @Schema(description = "内存使用率 %")
    private Integer memoryUsage;

    @Schema(description = "交换区总量 MB")
    private Long swapTotal;

    @Schema(description = "交换区使用率 %")
    private Integer swapUsage;

    @Schema(description = "磁盘读取字节数 bytes/s")
    private Long ioRead;

    @Schema(description = "磁盘写入字节数 bytes/s")
    private Long ioWrite;

    @Schema(description = "网络接收字节数 bytes/s")
    private Long networkRx;

    @Schema(description = "网络发送字节数 bytes/s")
    private Long networkTx;

    @Schema(description = "网络接收包数 包/s")
    private Long networkIn;

    @Schema(description = "网络发送包数 包/s")
    private Long networkOut;

    @Schema(description = "采集时间")
    private LocalDateTime createTime;
    
}
