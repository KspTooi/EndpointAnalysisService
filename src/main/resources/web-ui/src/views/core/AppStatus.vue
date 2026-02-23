<template>
  <StdListContainer>
    <el-scrollbar class="monitor-scroll">
      <!-- 图表网格 -->
      <div class="monitor-grid">
        <!-- CPU 使用率 -->
        <el-card shadow="hover" class="monitor-card">
          <template #header>
            <div class="card-header">
              <span>CPU 使用率</span>
              <el-tag size="small" :type="getCpuTagType(latestStatus?.cpuUsage)">
                {{ latestStatus?.cpuUsage?.toFixed(1) ?? 0 }}%
              </el-tag>
            </div>
          </template>
          <v-chart class="chart" :option="cpuOption" autoresize />
        </el-card>

        <!-- 内存使用率 -->
        <el-card shadow="hover" class="monitor-card">
          <template #header>
            <div class="card-header">
              <span>内存使用率</span>
              <el-tag size="small" :type="getMemTagType(latestStatus?.memoryUsage)">
                {{ latestStatus?.memoryUsage?.toFixed(1) ?? 0 }}%
              </el-tag>
            </div>
          </template>
          <v-chart class="chart" :option="memOption" autoresize />
        </el-card>

        <!-- 网络流量 -->
        <el-card shadow="hover" class="monitor-card">
          <template #header>
            <div class="card-header">
              <span>网络流量 (KB/s)</span>
              <div class="extra-info">
                <span class="rx">↓ {{ formatNet(latestStatus?.networkRx) }}</span>
                <span class="tx">↑ {{ formatNet(latestStatus?.networkTx) }}</span>
              </div>
            </div>
          </template>
          <v-chart class="chart" :option="netOption" autoresize />
        </el-card>

        <!-- 磁盘 IO -->
        <el-card shadow="hover" class="monitor-card">
          <template #header>
            <div class="card-header">
              <span>磁盘 IO (KB/s)</span>
              <div class="extra-info">
                <span class="rx">R {{ formatNet(latestStatus?.ioRead) }}</span>
                <span class="tx">W {{ formatNet(latestStatus?.ioWrite) }}</span>
              </div>
            </div>
          </template>
          <v-chart class="chart" :option="ioOption" autoresize />
        </el-card>
      </div>

      <!-- 实时摘要 -->
      <el-card shadow="hover">
        <template #header>
          <div class="card-header">
            <span>实时摘要</span>
            <span class="update-time">最后更新: {{ latestStatus?.createTime ?? "-" }}</span>
          </div>
        </template>
        <el-descriptions :column="3" border>
          <el-descriptions-item label="进程数">{{ latestStatus?.processCount ?? 0 }}</el-descriptions-item>
          <el-descriptions-item label="线程数">{{ latestStatus?.threadCount ?? 0 }}</el-descriptions-item>
          <el-descriptions-item label="系统负载 (1m)">{{ latestStatus?.load1 ?? "N/A" }}</el-descriptions-item>
          <el-descriptions-item label="物理内存总量">{{ formatBytes(latestStatus?.memoryTotal) }}</el-descriptions-item>
          <el-descriptions-item label="交换区总量">{{ formatBytes(latestStatus?.swapTotal) }}</el-descriptions-item>
          <el-descriptions-item label="交换区使用率">{{ latestStatus?.swapUsage?.toFixed(1) ?? 0 }}%</el-descriptions-item>
        </el-descriptions>
      </el-card>

      <!-- 系统信息 -->
      <el-card v-loading="sysLoading" shadow="hover">
        <template #header>
          <div class="card-header">
            <span>系统信息</span>
            <el-button size="small" @click="loadSysData">刷新</el-button>
          </div>
        </template>
        <template v-if="sysData">
          <el-descriptions :column="3" border>
            <el-descriptions-item label="服务器名称">{{ sysData.serverName }}</el-descriptions-item>
            <el-descriptions-item label="操作系统">{{ sysData.osName }}</el-descriptions-item>
            <el-descriptions-item label="CPU 型号">{{ sysData.cpuModel }}</el-descriptions-item>
            <el-descriptions-item label="CPU 架构">{{ sysData.cpuArch }}</el-descriptions-item>
            <el-descriptions-item label="CPU 核数">{{ sysData.cpuCount }}</el-descriptions-item>
            <el-descriptions-item label="物理内存">{{ formatSysBytes(sysData.memoryTotal) }}</el-descriptions-item>
            <el-descriptions-item label="交换区大小">{{ formatSysBytes(sysData.swapTotal) }}</el-descriptions-item>
            <el-descriptions-item label="JDK">{{ sysData.jdkName }}</el-descriptions-item>
            <el-descriptions-item label="JDK 版本">{{ sysData.jdkVersion }}</el-descriptions-item>
            <el-descriptions-item label="JVM 启动时间">{{ sysData.jvmStartTime }}</el-descriptions-item>
            <el-descriptions-item label="JVM 运行时长">{{ sysData.jvmRunTime }}</el-descriptions-item>
            <el-descriptions-item label="JDK 路径">{{ sysData.jdkHome }}</el-descriptions-item>
          </el-descriptions>

          <!-- 磁盘信息 -->
          <div class="section-title">磁盘</div>
          <el-table :data="sysData.diskInfo" size="small" border>
            <el-table-column prop="dirName" label="挂载点" min-width="100" />
            <el-table-column prop="fileSystem" label="文件系统" min-width="80" />
            <el-table-column label="总容量" min-width="90">
              <template #default="{ row }">{{ formatSysBytes(row.totalCapacity) }}</template>
            </el-table-column>
            <el-table-column label="已用" min-width="90">
              <template #default="{ row }">{{ formatSysBytes(row.usedCapacity) }}</template>
            </el-table-column>
            <el-table-column label="可用" min-width="90">
              <template #default="{ row }">{{ formatSysBytes(row.availableCapacity) }}</template>
            </el-table-column>
            <el-table-column prop="usage" label="使用率" min-width="120">
              <template #default="{ row }">
                <el-progress :percentage="row.usage" :status="row.usage > 90 ? 'exception' : row.usage > 70 ? 'warning' : undefined" />
              </template>
            </el-table-column>
          </el-table>

          <!-- 网卡信息 -->
          <div class="section-title">网卡</div>
          <el-table :data="sysData.ifInfo" size="small" border>
            <el-table-column prop="name" label="名称" min-width="80" />
            <el-table-column prop="displayName" label="显示名称" min-width="140" />
            <el-table-column prop="mac" label="MAC 地址" min-width="140" />
            <el-table-column label="IPv4 地址" min-width="160">
              <template #default="{ row }">{{ row.ipv4Addrs.join(", ") }}</template>
            </el-table-column>
          </el-table>
        </template>
      </el-card>
    </el-scrollbar>
  </StdListContainer>
</template>

<script setup lang="ts">
import { provide } from "vue";
import { use } from "echarts/core";
import { CanvasRenderer } from "echarts/renderers";
import { LineChart } from "echarts/charts";
import { GridComponent, TooltipComponent, LegendComponent, TitleComponent } from "echarts/components";
import VChart, { THEME_KEY } from "vue-echarts";
import StdListContainer from "@/soa/std-series/StdListContainer.vue";
import AppStatusService from "@/views/core/service/AppStatusService.ts";

// 注册 ECharts 必须的组件
use([CanvasRenderer, LineChart, GridComponent, TooltipComponent, LegendComponent, TitleComponent]);

provide(THEME_KEY, "light");

// 实时状态
const { latestStatus, cpuOption, memOption, netOption, ioOption, formatNet, getCpuTagType, getMemTagType } =
  AppStatusService.useAppRealTimeStatus();

// 系统信息
const { data: sysData, loading: sysLoading, loadData: loadSysData, formatBytes: formatSysBytes } =
  AppStatusService.useAppSystemInfo();

// GetRtStatusVo 的 memoryTotal/swapTotal 单位为 MB，直接套用 formatBytes 前需换算为 bytes
const formatBytes = (mb: number | undefined | null): string => {
  if (mb == null) return "0 B";
  return formatSysBytes(mb * 1024 * 1024);
};
</script>

<style scoped>
/* el-scrollbar 撑满 StdListContainer 剩余空间 */
.monitor-scroll {
  flex: 1;
  min-height: 0;
}

:deep(.el-scrollbar__view) {
  display: flex;
  flex-direction: column;
  gap: 20px;
  padding-bottom: 4px;
}

.monitor-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 20px;
}

.monitor-card {
  height: 300px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-weight: bold;
}

.chart {
  height: 215px;
  width: 100%;
}

.update-time {
  font-size: 12px;
  font-weight: normal;
  color: var(--el-text-color-secondary);
}

.extra-info {
  font-size: 12px;
  display: flex;
  gap: 10px;
}

.rx {
  color: #e6a23c;
}

.tx {
  color: #f56c6c;
}

.section-title {
  margin: 16px 0 8px;
  font-weight: bold;
  font-size: 13px;
  color: var(--el-text-color-regular);
}
</style>
