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

      <!-- 系统详细信息 -->
      <el-card shadow="hover">
        <template #header>
          <div class="card-header">
            <span>系统详细信息</span>
            <span class="update-time">最后更新: {{ latestStatus?.createTime ?? "-" }}</span>
          </div>
        </template>
        <el-descriptions :column="3" border>
          <el-descriptions-item label="进程数">{{ latestStatus?.processCount ?? 0 }}</el-descriptions-item>
          <el-descriptions-item label="线程数">{{ latestStatus?.threadCount ?? 0 }}</el-descriptions-item>
          <el-descriptions-item label="系统负载 (1m)">{{ latestStatus?.load1 ?? "N/A" }}</el-descriptions-item>
          <el-descriptions-item label="物理内存总量">{{ latestStatus?.memoryTotal ?? 0 }} MB</el-descriptions-item>
          <el-descriptions-item label="交换区总量">{{ latestStatus?.swapTotal ?? 0 }} MB</el-descriptions-item>
          <el-descriptions-item label="交换区使用率">{{ latestStatus?.swapUsage?.toFixed(1) ?? 0 }}%</el-descriptions-item>
        </el-descriptions>
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

// 应用实时状态打包
const { latestStatus, cpuOption, memOption, netOption, ioOption, formatNet, getCpuTagType, getMemTagType } =
  AppStatusService.useAppRealTimeStatus();
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
</style>
