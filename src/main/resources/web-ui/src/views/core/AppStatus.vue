<template>
  <StdListContainer nopadding>
    <el-tabs v-model="activeTab" class="monitor-tabs">
      <!-- Tab 1: 实时数据 -->
      <el-tab-pane label="实时数据" name="realtime">
        <el-scrollbar class="monitor-scroll">
          <div class="monitor-content">
            <!-- 图表网格 -->
            <div class="monitor-grid">
              <!-- CPU 使用率 -->
              <el-card shadow="never" class="monitor-card">
                <template #header>
                  <div class="card-header">
                    <span class="title-with-icon">
                      <el-icon><Cpu /></el-icon>
                      CPU 使用率
                    </span>
                    <el-tag size="small" :type="getCpuTagType(latestStatus?.cpuUsage)" effect="plain">
                      {{ latestStatus?.cpuUsage?.toFixed(1) ?? 0 }}%
                    </el-tag>
                  </div>
                </template>
                <v-chart class="chart" :option="cpuOption" autoresize />
              </el-card>

              <!-- 内存使用率 -->
              <el-card shadow="never" class="monitor-card">
                <template #header>
                  <div class="card-header">
                    <span class="title-with-icon">
                      <el-icon><Odometer /></el-icon>
                      内存使用率
                    </span>
                    <el-tag size="small" :type="getMemTagType(latestStatus?.memoryUsage)" effect="plain">
                      {{ latestStatus?.memoryUsage?.toFixed(1) ?? 0 }}%
                    </el-tag>
                  </div>
                </template>
                <v-chart class="chart" :option="memOption" autoresize />
              </el-card>

              <!-- 网络流量 -->
              <el-card shadow="never" class="monitor-card">
                <template #header>
                  <div class="card-header">
                    <span class="title-with-icon">
                      <el-icon><Connection /></el-icon>
                      网络流量 (KB/s)
                    </span>
                    <div class="extra-info">
                      <span class="rx">↓ {{ formatNet(latestStatus?.networkRx) }}</span>
                      <span class="tx">↑ {{ formatNet(latestStatus?.networkTx) }}</span>
                    </div>
                  </div>
                </template>
                <v-chart class="chart" :option="netOption" autoresize />
              </el-card>

              <!-- 磁盘 IO -->
              <el-card shadow="never" class="monitor-card">
                <template #header>
                  <div class="card-header">
                    <span class="title-with-icon">
                      <el-icon><DataLine /></el-icon>
                      磁盘 IO (KB/s)
                    </span>
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
            <el-card shadow="never" class="summary-card">
              <template #header>
                <div class="card-header">
                  <span class="title-with-icon">
                    <el-icon><Timer /></el-icon>
                    实时摘要
                  </span>
                  <span class="update-time">最后更新: {{ latestStatus?.createTime ?? "-" }}</span>
                </div>
              </template>
              <el-descriptions :column="3" border size="small" class="custom-descriptions">
                <el-descriptions-item label="进程数">{{ latestStatus?.processCount ?? 0 }}</el-descriptions-item>
                <el-descriptions-item label="线程数">{{ latestStatus?.threadCount ?? 0 }}</el-descriptions-item>
                <el-descriptions-item label="系统负载 (1m)">{{ latestStatus?.load1 ?? "N/A" }}</el-descriptions-item>
                <el-descriptions-item label="物理内存总量">{{ formatBytes(latestStatus?.memoryTotal) }}</el-descriptions-item>
                <el-descriptions-item label="交换区总量">{{ formatBytes(latestStatus?.swapTotal) }}</el-descriptions-item>
                <el-descriptions-item label="交换区使用率"
                  >{{ latestStatus?.swapUsage?.toFixed(1) ?? 0 }}%</el-descriptions-item
                >
              </el-descriptions>
            </el-card>
          </div>
        </el-scrollbar>
      </el-tab-pane>

      <!-- Tab 2: 系统信息 -->
      <el-tab-pane label="系统信息" name="system">
        <el-scrollbar class="monitor-scroll">
          <div class="monitor-content">
            <el-card v-loading="sysLoading" shadow="never" class="system-info-card">
              <template #header>
                <div class="card-header">
                  <span class="title-with-icon">
                    <el-icon><Monitor /></el-icon>
                    系统环境
                  </span>
                  <el-button size="small" type="primary" plain @click="loadSysData">刷新</el-button>
                </div>
              </template>
              <template v-if="sysData">
                <el-descriptions :column="3" border size="small" class="custom-descriptions">
                  <el-descriptions-item label="服务器名称">{{ sysData.serverName }}</el-descriptions-item>
                  <el-descriptions-item label="操作系统">{{ sysData.osName }}</el-descriptions-item>
                  <el-descriptions-item label="操作系统架构">{{ sysData.cpuArch }}</el-descriptions-item>
                  <el-descriptions-item label="CPU 型号">{{ sysData.cpuModel }}</el-descriptions-item>
                  <el-descriptions-item label="CPU 核数">{{ sysData.cpuCount }}</el-descriptions-item>
                  <el-descriptions-item label="物理内存总量">{{ formatSysBytes(sysData.memoryTotal) }}</el-descriptions-item>
                </el-descriptions>
              </template>
            </el-card>

            <!-- Java 虚拟机信息 (从原 Tab 3 合并) -->
            <el-card v-loading="sysLoading" shadow="never" class="system-info-card" v-if="sysData">
              <template #header>
                <div class="card-header">
                  <span class="title-with-icon">
                    <el-icon><Coffee /></el-icon>
                    Java 虚拟机 (JVM)
                  </span>
                </div>
              </template>
              <el-descriptions :column="2" border size="small" class="custom-descriptions">
                <el-descriptions-item label="JDK 名称">{{ sysData.jdkName }}</el-descriptions-item>
                <el-descriptions-item label="JDK 版本">{{ sysData.jdkVersion }}</el-descriptions-item>
                <el-descriptions-item label="JVM 启动时间">{{ sysData.jvmStartTime }}</el-descriptions-item>
                <el-descriptions-item label="JVM 运行时长">{{ sysData.jvmRunTime }}</el-descriptions-item>
                <el-descriptions-item label="JDK 路径" :span="2">{{ sysData.jdkHome }}</el-descriptions-item>
              </el-descriptions>
            </el-card>

            <el-card v-loading="sysLoading" shadow="never" class="system-info-card" v-if="sysData">
              <template #header>
                <div class="card-header">
                  <span class="title-with-icon">
                    <el-icon><Files /></el-icon>
                    磁盘信息
                  </span>
                </div>
              </template>
              <el-table :data="sysData.diskInfo" size="small" border stripe class="custom-table">
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
                    <el-progress
                      :percentage="row.usage"
                      :stroke-width="12"
                      :status="row.usage > 90 ? 'exception' : row.usage > 70 ? 'warning' : 'success'"
                    />
                  </template>
                </el-table-column>
              </el-table>
            </el-card>

            <el-card v-loading="sysLoading" shadow="never" class="system-info-card" v-if="sysData">
              <template #header>
                <div class="card-header">
                  <span class="title-with-icon">
                    <el-icon><Share /></el-icon>
                    网卡信息
                  </span>
                </div>
              </template>
              <el-table :data="sysData.ifInfo" size="small" border stripe class="custom-table">
                <el-table-column prop="name" label="名称" min-width="80" />
                <el-table-column prop="displayName" label="显示名称" min-width="140" />
                <el-table-column prop="mac" label="MAC 地址" min-width="140" />
                <el-table-column label="IPv4 地址" min-width="160">
                  <template #default="{ row }">{{ row.ipv4Addrs.join(", ") }}</template>
                </el-table-column>
              </el-table>
            </el-card>
          </div>
        </el-scrollbar>
      </el-tab-pane>
    </el-tabs>
  </StdListContainer>
</template>

<script setup lang="ts">
import { ref, provide } from "vue";
import { use } from "echarts/core";
import { CanvasRenderer } from "echarts/renderers";
import { LineChart } from "echarts/charts";
import { GridComponent, TooltipComponent, LegendComponent, TitleComponent } from "echarts/components";
import VChart, { THEME_KEY } from "vue-echarts";
import { Cpu, Odometer, Connection, DataLine, Timer, Monitor, Files, Share, Coffee } from "@element-plus/icons-vue";
import StdListContainer from "@/soa/std-series/StdListContainer.vue";
import AppStatusService from "@/views/core/service/AppStatusService.ts";

// 注册 ECharts 必须的组件
use([CanvasRenderer, LineChart, GridComponent, TooltipComponent, LegendComponent, TitleComponent]);

provide(THEME_KEY, "light");

const activeTab = ref("realtime");

// 实时状态
const { latestStatus, cpuOption, memOption, netOption, ioOption, formatNet, getCpuTagType, getMemTagType } =
  AppStatusService.useAppRealTimeStatus();

// 系统信息
const {
  data: sysData,
  loading: sysLoading,
  loadData: loadSysData,
  formatBytes: formatSysBytes,
} = AppStatusService.useAppSystemInfo();

// GetRtStatusVo 的 memoryTotal/swapTotal 单位为 MB，直接套用 formatBytes 前需换算为 bytes
const formatBytes = (mb: number | undefined | null): string => {
  if (mb == null) return "0 B";
  return formatSysBytes(mb * 1024 * 1024);
};
</script>

<style scoped>
.monitor-tabs {
  height: 100%;
  display: flex;
  flex-direction: column;
}

:deep(.el-tabs__header) {
  margin: 0;
  background-color: #fff;
  padding: 0 16px;
  border-bottom: 1px solid var(--el-border-color-light);
}

:deep(.el-tabs__content) {
  flex: 1;
  min-height: 0;
}

:deep(.el-tab-pane) {
  height: 100%;
}

/* el-scrollbar 撑满 StdListContainer 剩余空间 */
.monitor-scroll {
  height: 100%;
  background-color: #fff;
}

.monitor-content {
  padding: 16px;
  display: flex;
  flex-direction: column;
  gap: 16px;
}

:deep(.el-scrollbar__view) {
  min-height: 100%;
}

.monitor-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 16px;
}

.monitor-card,
.summary-card,
.system-info-card {
  border-radius: 0 !important;
  border: 1px solid var(--el-border-color-lighter);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  height: 24px;
}

.title-with-icon {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 14px;
  font-weight: 600;
  color: var(--el-text-color-primary);
}

.title-with-icon .el-icon {
  font-size: 16px;
  color: var(--el-color-primary);
}

.chart {
  height: 200px;
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
  gap: 12px;
}

.rx {
  color: var(--el-color-warning);
  font-family: monospace;
}

.tx {
  color: var(--el-color-danger);
  font-family: monospace;
}

.section-title {
  margin: 20px 0 12px;
  font-weight: 600;
  font-size: 14px;
  color: var(--el-text-color-primary);
  display: flex;
  align-items: center;
  gap: 8px;
  padding-left: 4px;
  border-left: 3px solid var(--el-color-primary);
}

.section-title .el-icon {
  color: var(--el-color-primary);
}

/* 统一直角风格定制 */
:deep(.el-card__header) {
  padding: 8px 16px;
  background-color: #f8f9fb;
  border-bottom: 1px solid var(--el-border-color-lighter);
}

:deep(.el-card__body) {
  padding: 16px;
}

:deep(.custom-descriptions) {
  .el-descriptions__label {
    background-color: #f8f9fb !important;
    font-weight: 500;
    width: 120px;
  }
  .el-descriptions__content {
    color: var(--el-text-color-primary);
  }
  border-radius: 0 !important;
}

:deep(.custom-table) {
  border-radius: 0 !important;
  .el-table__header-wrapper th {
    background-color: #f8f9fb !important;
    font-weight: 600;
    color: var(--el-text-color-primary);
  }
}

:deep(.el-progress-bar__outer) {
  border-radius: 0 !important;
}

:deep(.el-progress-bar__inner) {
  border-radius: 0 !important;
}

/* Tabs 直角化 */
:deep(.el-tabs--top .el-tabs__item.is-top:nth-child(2)) {
  padding-left: 20px;
}
:deep(.el-tabs__active-bar) {
  border-radius: 0;
}
</style>
