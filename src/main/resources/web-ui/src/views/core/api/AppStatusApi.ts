import Http from "@/commons/Http.ts";
import type Result from "@/commons/entity/Result.ts";

/**
 * 实时性能监控数据 VO
 */
export interface GetRtStatusVo {
  load1: number | null; // 系统平均负载 1分钟，Windows 不可用时为 null
  processCount: number; // 进程数
  threadCount: number; // 线程数
  cpuUsage: number; // CPU使用率 0~100 (%)
  memoryTotal: number; // 物理内存总量 MB
  memoryUsage: number; // 内存使用率 0~100 (%)
  swapTotal: number; // 交换区总量 MB
  swapUsage: number; // 交换区使用率 0~100 (%)
  ioRead: number; // 磁盘读取速率 bytes/s（所有磁盘聚合）
  ioWrite: number; // 磁盘写入速率 bytes/s（所有磁盘聚合）
  networkRx: number; // 网络接收速率 bytes/s（所有网卡聚合）
  networkTx: number; // 网络发送速率 bytes/s（所有网卡聚合）
  networkIn: number; // 网络接收速率 包/s（所有网卡聚合）
  networkOut: number; // 网络发送速率 包/s（所有网卡聚合）
  createTime: string; // 本次采样时间
}

/**
 * 磁盘信息 VO
 */
export interface GetSystemInfoDiskVo {
  dirName: string; // 盘符或挂载点
  fileSystem: string; // 文件系统类型
  totalCapacity: number; // 总容量 bytes
  usedCapacity: number; // 已用容量 bytes
  availableCapacity: number; // 可用容量 bytes
  usage: number; // 使用率 0~100 (%)
}

/**
 * 网卡信息 VO
 */
export interface GetSystemInfoIFVo {
  name: string; // 网卡名称（系统层 eth0,ens33）
  displayName: string; // 网卡显示名称（网络适配器 1）
  mac: string; // MAC 地址
  ipv4Addrs: string[]; // IPv4 地址列表
}

/**
 * 系统信息 VO
 */
export interface GetSystemInfoVo {
  serverName: string; // 服务器名称
  osName: string; // 操作系统名称
  cpuArch: string; // CPU 架构
  cpuModel: string; // CPU 型号
  cpuCount: number; // CPU 逻辑核数
  memoryTotal: number; // 内存总量 bytes
  swapTotal: number; // 交换区总量 bytes
  jdkName: string; // JDK 厂商与虚拟机名称
  jdkVersion: string; // JDK 确切版本号
  jdkHome: string; // JDK/JRE 安装绝对路径
  jvmStartTime: string; // JVM 启动时间
  jvmRunTime: string; // JVM 连续运行时长 HH:mm:ss（小时可超 24）
  jvmInputArgs: string | null; // JVM 启动参数（默认不返回）
  diskInfo: GetSystemInfoDiskVo[]; // 磁盘信息
  ifInfo: GetSystemInfoIFVo[]; // 网卡信息
}

/**
 * 应用状态监控 API 接口
 */
export default {
  /**
   * 获取实时性能监控数据
   * @returns 实时监控快照
   */
  getRtStatus: async (): Promise<GetRtStatusVo> => {
    const result = await Http.postEntity<Result<GetRtStatusVo>>("/appStatus/getRtStatus", {});
    if (result.code === 0) {
      return result.data;
    }
    throw new Error(result.message);
  },

  /**
   * 获取系统信息（主机/OS/CPU/JDK/JVM/磁盘/网卡）
   * @returns 系统静态信息
   */
  getSystemInfo: async (): Promise<GetSystemInfoVo> => {
    const result = await Http.postEntity<Result<GetSystemInfoVo>>("/appStatus/getSystemInfo", {});
    if (result.code === 0) {
      return result.data;
    }
    throw new Error(result.message);
  },
};
