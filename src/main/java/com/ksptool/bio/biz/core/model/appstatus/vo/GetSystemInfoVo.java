package com.ksptool.bio.biz.core.model.appstatus.vo;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GetSystemInfoVo {

    @Schema(description = "服务器名称")
    private String serverName;

    @Schema(description = "操作系统名称")
    private String osName;

    @Schema(description = "CPU架构")
    private String cpuArch;

    @Schema(description = "CPU型号")
    private String cpuModel;

    @Schema(description = "CPU数量")
    private Integer cpuCount;

    @Schema(description = "内存总量 后端返字节数 前端自动显示为MB/GB/TB")
    private Long memoryTotal;

    @Schema(description = "交换区总量 后端返字节数 前端自动显示为MB/GB/TB")
    private Long swapTotal;

    @Schema(description = "JDK厂商与虚拟机名称")
    private String jdkName;

    @Schema(description = "JDK确切版本号")
    private String jdkVersion;

    @Schema(description = "JDK/JRE 的安装绝对路径")
    private String jdkHome;

    @Schema(description = "JVM 启动时间")
    private LocalDateTime jvmStartTime;

    @Schema(description = "JVM 连续运行时长 后端返HH:mm:ss")
    private String jvmRunTime;

    @Schema(description = "JVM 启动参数")
    private String jvmInputArgs;

    @Schema(description = "磁盘信息")
    private List<GetSystemInfoDiskVo> diskInfo;

    @Schema(description = "网卡信息")
    private List<GetSystemInfoIFVo> ifInfo;

}
