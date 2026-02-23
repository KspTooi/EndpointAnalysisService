import { ref, onUnmounted, onMounted, computed } from "vue";
import type { GetRtStatusVo } from "../api/AppStatusApi";
import AppStatusApi from "../api/AppStatusApi";

//最大记录数
const maxRcdCount = 120;

//采样间隔时间
const sampleInterval = 1000;

export default {
  /**
   * 应用实时状态打包
   */
  useAppRealTimeStatus() {
    const data = ref<GetRtStatusVo[]>([]);
    const loading = ref(false);
    let timer: any = null;

    const fetchData = async () => {
      if (loading.value) return;
      try {
        const res = await AppStatusApi.getRtStatus();
        if (!res) return;

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
      fetchData();
      timer = setInterval(fetchData, sampleInterval);
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

    // 计算逻辑
    const latestStatus = computed(() => (data.value.length > 0 ? data.value[data.value.length - 1] : null));

    // 格式化网络/IO 速率 (B/s -> KB/s)
    const formatNet = (val: number | undefined) => {
      if (val === undefined) return "0";
      return (val / 1024).toFixed(1);
    };

    const getCpuTagType = (usage: number | undefined) => {
      if (!usage) return "info";
      if (usage > 80) return "danger";
      if (usage > 50) return "warning";
      return "success";
    };

    const getMemTagType = (usage: number | undefined) => {
      if (!usage) return "info";
      if (usage > 90) return "danger";
      if (usage > 70) return "warning";
      return "success";
    };

    // 通用图表配置基础
    const baseOption = {
      grid: {
        top: 30,
        right: 10,
        bottom: 20,
        left: 40,
      },
      tooltip: {
        trigger: "axis",
      },
      xAxis: {
        type: "category",
        data: computed(() => data.value.map((item) => item.createTime.split(" ")[1])),
        axisLabel: {
          show: false,
        },
      },
    };

    // CPU 图表配置
    const cpuOption = computed(() => ({
      ...baseOption,
      yAxis: {
        type: "value",
        min: 0,
        max: 100,
      },
      series: [
        {
          name: "CPU 使用率",
          type: "line",
          data: data.value.map((item) => item.cpuUsage),
          smooth: true,
          showSymbol: false,
          areaStyle: {
            opacity: 0.1,
          },
          itemStyle: {
            color: "#409eff",
          },
        },
      ],
    }));

    // 内存图表配置
    const memOption = computed(() => ({
      ...baseOption,
      yAxis: {
        type: "value",
        min: 0,
        max: 100,
      },
      series: [
        {
          name: "内存使用率",
          type: "line",
          data: data.value.map((item) => item.memoryUsage),
          smooth: true,
          showSymbol: false,
          areaStyle: {
            opacity: 0.1,
          },
          itemStyle: {
            color: "#67c23a",
          },
        },
      ],
    }));

    // 网络图表配置
    const netOption = computed(() => ({
      ...baseOption,
      yAxis: {
        type: "value",
      },
      series: [
        {
          name: "接收 (Rx)",
          type: "line",
          data: data.value.map((item) => (item.networkRx / 1024).toFixed(1)),
          smooth: true,
          showSymbol: false,
          itemStyle: {
            color: "#e6a23c",
          },
        },
        {
          name: "发送 (Tx)",
          type: "line",
          data: data.value.map((item) => (item.networkTx / 1024).toFixed(1)),
          smooth: true,
          showSymbol: false,
          itemStyle: {
            color: "#f56c6c",
          },
        },
      ],
    }));

    // 磁盘 IO 图表配置
    const ioOption = computed(() => ({
      ...baseOption,
      yAxis: {
        type: "value",
      },
      series: [
        {
          name: "读取",
          type: "line",
          data: data.value.map((item) => (item.ioRead / 1024).toFixed(1)),
          smooth: true,
          showSymbol: false,
          itemStyle: {
            color: "#909399",
          },
        },
        {
          name: "写入",
          type: "line",
          data: data.value.map((item) => (item.ioWrite / 1024).toFixed(1)),
          smooth: true,
          showSymbol: false,
          itemStyle: {
            color: "#409eff",
          },
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
};
