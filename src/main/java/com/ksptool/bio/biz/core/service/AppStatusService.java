package com.ksptool.bio.biz.core.service;

import com.ksptool.bio.biz.core.model.appstatus.vo.GetRtStatusVo;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;
import oshi.hardware.GlobalMemory;
import oshi.hardware.HWDiskStore;
import oshi.hardware.HardwareAbstractionLayer;
import oshi.hardware.NetworkIF;
import oshi.software.os.OperatingSystem;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

@Service
@Slf4j
public class AppStatusService {

    @Value("${module-app-status.sample-delay-ms:1000}")
    private long sampleDelayMs;

    // OSHI 核心对象，只初始化一次
    private SystemInfo si;
    private HardwareAbstractionLayer hal;
    private CentralProcessor cpu;
    private OperatingSystem os;

    // 上一帧 CPU ticks（用于差值计算使用率）
    private long[] prevCpuTicks;

    // 上一帧网卡累计字节/包数
    private long prevNetRxBytes;
    private long prevNetTxBytes;
    private long prevNetRxPackets;
    private long prevNetTxPackets;

    // 上一帧磁盘累计字节数
    private long prevDiskReadBytes;
    private long prevDiskWriteBytes;

    // 上一帧采样时间戳（毫秒），用于计算实际间隔
    private long prevSampleMs;

    //最新快照，用于缓存最新采样数据
    private final AtomicReference<GetRtStatusVo> snapshot = new AtomicReference<>();

    @PostConstruct
    public void init() {
        si = new SystemInfo();
        hal = si.getHardware();
        cpu = hal.getProcessor();
        os = si.getOperatingSystem();

        // 初始化"上一帧"，避免第一次采样差值为负
        prevCpuTicks = cpu.getSystemCpuLoadTicks();
        prevSampleMs = System.currentTimeMillis();

        // 初始化网卡累计基线
        long[] netBase = sumNetworkCounters(hal.getNetworkIFs(true));
        prevNetRxBytes = netBase[0];
        prevNetTxBytes = netBase[1];
        prevNetRxPackets = netBase[2];
        prevNetTxPackets = netBase[3];

        // 初始化磁盘累计基线
        long[] diskBase = sumDiskCounters(hal.getDiskStores());
        prevDiskReadBytes = diskBase[0];
        prevDiskWriteBytes = diskBase[1];

        log.info("AppStatusService 初始化完成，采样间隔: {}ms", sampleDelayMs);
    }

    /**
     * 定时采样任务，写入最新快照
     * fixedDelay 保证两次采样之间至少间隔 sampleDelayMs，不会出现并发采样
     */
    @Scheduled(fixedDelayString = "${module-app-status.sample-delay-ms:1000}")
    public void sample() {
        try {
            long nowMs = System.currentTimeMillis();
            long intervalMs = nowMs - prevSampleMs;

            GetRtStatusVo vo = new GetRtStatusVo();
            vo.setCreateTime(LocalDateTime.now());

            // ---- CPU ----
            double cpuLoad = cpu.getSystemCpuLoadBetweenTicks(prevCpuTicks);
            prevCpuTicks = cpu.getSystemCpuLoadTicks();
            vo.setCpuUsage(clampPercent((int) Math.round(cpuLoad * 100)));

            // ---- 系统负载 ----
            double[] loads = cpu.getSystemLoadAverage(1);
            Double load1 = null;
            if (loads != null && loads.length > 0 && loads[0] >= 0) {
                load1 = loads[0];
            }
            vo.setLoad1(load1);

            // ---- 进程/线程 ----
            vo.setProcessCount((long) os.getProcessCount());
            vo.setThreadCount((long) os.getThreadCount());

            // ---- 内存 ----
            GlobalMemory mem = hal.getMemory();
            long memTotal = mem.getTotal();
            long memAvailable = mem.getAvailable();
            vo.setMemoryTotal(toMb(memTotal));
            vo.setMemoryUsage(calcUsagePercent(memTotal, memTotal - memAvailable));

            // ---- 交换区 ----
            long swapTotal = mem.getVirtualMemory().getSwapTotal();
            long swapUsed = mem.getVirtualMemory().getSwapUsed();
            vo.setSwapTotal(toMb(swapTotal));
            vo.setSwapUsage(calcUsagePercent(swapTotal, swapUsed));

            // ---- 网卡（所有网卡聚合） ----
            long[] netNow = sumNetworkCounters(hal.getNetworkIFs(true));
            long netRxBytes = calcRate(netNow[0], prevNetRxBytes, intervalMs);
            long netTxBytes = calcRate(netNow[1], prevNetTxBytes, intervalMs);
            long netRxPkts = calcRate(netNow[2], prevNetRxPackets, intervalMs);
            long netTxPkts = calcRate(netNow[3], prevNetTxPackets, intervalMs);
            prevNetRxBytes = netNow[0];
            prevNetTxBytes = netNow[1];
            prevNetRxPackets = netNow[2];
            prevNetTxPackets = netNow[3];
            vo.setNetworkRx(netRxBytes);
            vo.setNetworkTx(netTxBytes);
            vo.setNetworkIn(netRxPkts);
            vo.setNetworkOut(netTxPkts);

            // ---- 磁盘 IO（所有磁盘聚合） ----
            long[] diskNow = sumDiskCounters(hal.getDiskStores());
            long ioRead = calcRate(diskNow[0], prevDiskReadBytes, intervalMs);
            long ioWrite = calcRate(diskNow[1], prevDiskWriteBytes, intervalMs);
            prevDiskReadBytes = diskNow[0];
            prevDiskWriteBytes = diskNow[1];
            vo.setIoRead(ioRead);
            vo.setIoWrite(ioWrite);

            prevSampleMs = nowMs;
            snapshot.set(vo);

        } catch (Exception e) {
            log.error("AppStatus 采样失败", e);
        }
    }

    /**
     * 获取最新快照，供 Controller 直接返回，不做任何 OSHI 调用
     */
    public GetRtStatusVo getRtStatus() {
        return snapshot.get();
    }

    // ---- 私有工具方法 ----

    /**
     * 聚合所有网卡的累计收发字节数和包数
     * 返回 [rxBytes, txBytes, rxPackets, txPackets]
     */
    private long[] sumNetworkCounters(List<NetworkIF> nics) {
        long rxBytes = 0, txBytes = 0, rxPkts = 0, txPkts = 0;
        for (NetworkIF nic : nics) {
            rxBytes += nic.getBytesRecv();
            txBytes += nic.getBytesSent();
            rxPkts += nic.getPacketsRecv();
            txPkts += nic.getPacketsSent();
        }
        return new long[]{rxBytes, txBytes, rxPkts, txPkts};
    }

    /**
     * 聚合所有磁盘的累计读写字节数
     * 返回 [readBytes, writeBytes]
     */
    private long[] sumDiskCounters(List<HWDiskStore> disks) {
        long read = 0, write = 0;
        for (HWDiskStore disk : disks) {
            disk.updateAttributes();
            read += disk.getReadBytes();
            write += disk.getWriteBytes();
        }
        return new long[]{read, write};
    }

    /**
     * 计算 bytes/s 速率；间隔为 0 或差值为负时返回 0
     */
    private long calcRate(long current, long previous, long intervalMs) {
        if (intervalMs <= 0) {
            return 0L;
        }
        long delta = current - previous;
        if (delta < 0) {
            return 0L;
        }
        return delta * 1000L / intervalMs;
    }

    /**
     * bytes 转 MB（整数向下取整）
     */
    private long toMb(long bytes) {
        return bytes / (1024L * 1024L);
    }

    /**
     * 计算使用率百分比，total 为 0 时返回 0
     */
    private int calcUsagePercent(long total, long used) {
        if (total <= 0) {
            return 0;
        }
        return clampPercent((int) Math.round((double) used / total * 100));
    }

    /**
     * 将百分比夹取在 [0, 100]
     */
    private int clampPercent(int value) {
        return Math.min(100, Math.max(0, value));
    }

}
