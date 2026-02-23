<template>
  <div class="app-status-container">
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
            <div class="net-info">
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
            <div class="io-info">
              <span class="read">R {{ formatNet(latestStatus?.ioRead) }}</span>
              <span class="write">W {{ formatNet(latestStatus?.ioWrite) }}</span>
            </div>
          </div>
        </template>
        <v-chart class="chart" :option="ioOption" autoresize />
      </el-card>
    </div>

    <!-- 详细信息表格 -->
    <el-card shadow="hover" class="info-card">
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
  </div>
</template>

<script setup lang="ts">
import { provide } from "vue";
import { use } from "echarts/core";
import { CanvasRenderer } from "echarts/renderers";
import { LineChart } from "echarts/charts";
import { GridComponent, TooltipComponent, LegendComponent, TitleComponent } from "echarts/components";
import VChart, { THEME_KEY } from "vue-echarts";
import AppStatusService from "@/views/core/service/AppStatusService.ts";

// 注册 ECharts 必须的组件
use([CanvasRenderer, LineChart, GridComponent, TooltipComponent, LegendComponent, TitleComponent]);

// 使用项目主题
provide(THEME_KEY, "light");

// 应用实时状态打包
const { latestStatus, cpuOption, memOption, netOption, ioOption, formatNet, getCpuTagType, getMemTagType } =
  AppStatusService.useAppRealTimeStatus();
</script>

<style scoped>
.app-status-container {
  padding: 20px;
  height: 100%;
  overflow-y: auto;
  background-color: var(--el-bg-color-page);
}

.monitor-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(450px, 1fr));
  gap: 20px;
  margin-bottom: 20px;
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
  height: 220px;
  width: 100%;
}

.info-card {
  margin-top: 20px;
}

.update-time {
  font-size: 12px;
  font-weight: normal;
  color: var(--el-text-color-secondary);
}

.net-info,
.io-info {
  font-size: 12px;
  display: flex;
  gap: 10px;
}

.rx,
.read {
  color: #e6a23c;
}

.tx,
.write {
  color: #f56c6c;
}
</style>
