import { ref, onUnmounted, onMounted, computed, nextTick } from "vue";
import type { GetRtStatusVo, GetSystemInfoVo } from "../api/AppStatusApi";
import AppStatusApi from "../api/AppStatusApi";

//最大记录数
const maxRcdCount = 120;

//采样间隔时间
const sampleInterval = 1000;

/**
 * 公用图表结构配置（纯静态对象，不含任何响应式引用，避免 vue-echarts 深克隆时产生循环引用）
 */
const baseChartLayout = {
  grid: { top: 30, right: 10, bottom: 20, left: 40 },
  tooltip: { trigger: "axis" },
  xAxis: {
    type: "category",
    axisLabel: { show: false },
    data: [] as string[],
  },
};

export default {
  /**
   * 应用实时状态打包
   */
  useAppRealTimeStatus() {
    const data = ref<GetRtStatusVo[]>([]);
    const loading = ref(false);
    let timer: any = null;

    const loadData = async () => {
      if (loading.value) {
        return;
      }
      try {
        const res = await AppStatusApi.getRtStatus();
        if (!res) {
          return;
        }

        // 使用 nextTick 延迟数据写入，避免在 ECharts 主进程渲染期间触发 setOption
        await nextTick();
        data.value.push(res);
        if (data.value.length > maxRcdCount) {
          data.value.shift();
        }
      } catch (error) {
        console.error("获取实时状态失败:", error);
      }
    };

    const start = () => {
      stop();
      loadData();
      timer = setInterval(loadData, sampleInterval);
    };

    const stop = () => {
      if (timer) {
        clearInterval(timer);
        timer = null;
      }
    };

    onMounted(() => {
      start();
    });

    onUnmounted(() => {
      stop();
    });

    const latestStatus = computed(() => {
      if (data.value.length > 0) {
        return data.value[data.value.length - 1];
      }
      return null;
    });

    const formatNet = (val: number | undefined) => {
      if (val === undefined) {
        return "0";
      }
      return (val / 1024).toFixed(1);
    };

    const getCpuTagType = (usage: number | undefined) => {
      if (!usage) {
        return "info";
      }
      if (usage > 80) {
        return "danger";
      }
      if (usage > 50) {
        return "warning";
      }
      return "success";
    };

    const getMemTagType = (usage: number | undefined) => {
      if (!usage) {
        return "info";
      }
      if (usage > 90) {
        return "danger";
      }
      if (usage > 70) {
        return "warning";
      }
      return "success";
    };

    // xAxis.data 在每个 computed 内部单独计算，不嵌套 computed 对象到 option 中
    const cpuOption = computed(() => ({
      ...baseChartLayout,
      xAxis: { ...baseChartLayout.xAxis, data: data.value.map((item) => item.createTime.split(" ")[1]) },
      yAxis: { type: "value", min: 0, max: 100 },
      series: [
        {
          name: "CPU 使用率",
          type: "line",
          data: data.value.map((item) => item.cpuUsage),
          smooth: true,
          showSymbol: false,
          areaStyle: { opacity: 0.1 },
          itemStyle: { color: "#409eff" },
        },
      ],
    }));

    const memOption = computed(() => ({
      ...baseChartLayout,
      xAxis: { ...baseChartLayout.xAxis, data: data.value.map((item) => item.createTime.split(" ")[1]) },
      yAxis: { type: "value", min: 0, max: 100 },
      series: [
        {
          name: "内存使用率",
          type: "line",
          data: data.value.map((item) => item.memoryUsage),
          smooth: true,
          showSymbol: false,
          areaStyle: { opacity: 0.1 },
          itemStyle: { color: "#67c23a" },
        },
      ],
    }));

    const netOption = computed(() => ({
      ...baseChartLayout,
      xAxis: { ...baseChartLayout.xAxis, data: data.value.map((item) => item.createTime.split(" ")[1]) },
      yAxis: { type: "value" },
      series: [
        {
          name: "接收 (Rx)",
          type: "line",
          data: data.value.map((item) => (item.networkRx / 1024).toFixed(1)),
          smooth: true,
          showSymbol: false,
          itemStyle: { color: "#e6a23c" },
        },
        {
          name: "发送 (Tx)",
          type: "line",
          data: data.value.map((item) => (item.networkTx / 1024).toFixed(1)),
          smooth: true,
          showSymbol: false,
          itemStyle: { color: "#f56c6c" },
        },
      ],
    }));

    const ioOption = computed(() => ({
      ...baseChartLayout,
      xAxis: { ...baseChartLayout.xAxis, data: data.value.map((item) => item.createTime.split(" ")[1]) },
      yAxis: { type: "value" },
      series: [
        {
          name: "读取",
          type: "line",
          data: data.value.map((item) => (item.ioRead / 1024).toFixed(1)),
          smooth: true,
          showSymbol: false,
          itemStyle: { color: "#909399" },
        },
        {
          name: "写入",
          type: "line",
          data: data.value.map((item) => (item.ioWrite / 1024).toFixed(1)),
          smooth: true,
          showSymbol: false,
          itemStyle: { color: "#409eff" },
        },
      ],
    }));

    return {
      data,
      loading,
      latestStatus,
      cpuOption,
      memOption,
      netOption,
      ioOption,
      formatNet,
      getCpuTagType,
      getMemTagType,
      start,
      stop,
    };
  },

  /**
   * 应用系统信息打包（仅加载一次，供静态展示）
   */
  useAppSystemInfo() {
    const data = ref<GetSystemInfoVo | null>(null);
    const loading = ref(false);

    const loadData = async () => {
      if (loading.value) {
        return;
      }
      loading.value = true;
      try {
        data.value = await AppStatusApi.getSystemInfo();
      } catch (error) {
        console.error("获取系统信息失败:", error);
      } finally {
        loading.value = false;
      }
    };

    // 格式化字节数为带单位的可读字符串
    const formatBytes = (bytes: number | undefined | null): string => {
      if (bytes == null || bytes <= 0) {
        return "0 B";
      }
      if (bytes < 1024) {
        return `${bytes} B`;
      }
      if (bytes < 1024 * 1024) {
        return `${(bytes / 1024).toFixed(1)} KB`;
      }
      if (bytes < 1024 * 1024 * 1024) {
        return `${(bytes / 1024 / 1024).toFixed(1)} MB`;
      }
      if (bytes < 1024 * 1024 * 1024 * 1024) {
        return `${(bytes / 1024 / 1024 / 1024).toFixed(2)} GB`;
      }
      return `${(bytes / 1024 / 1024 / 1024 / 1024).toFixed(2)} TB`;
    };

    onMounted(() => {
      loadData();
    });

    return {
      data,
      loading,
      loadData,
      formatBytes,
    };
  },
};
