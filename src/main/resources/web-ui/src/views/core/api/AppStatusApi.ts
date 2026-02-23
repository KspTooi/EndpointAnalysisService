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
};
