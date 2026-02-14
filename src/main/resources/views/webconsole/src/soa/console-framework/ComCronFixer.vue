<template>
  <el-popover
    placement="bottom"
    trigger="hover"
    :width="300"
    :hide-after="50"
    popper-class="com-cron-fixer-popover"
  >
    <template #reference>
      <div class="cron-trigger">
        <el-icon class="cron-icon"><Clock /></el-icon>
        <span class="cron-display-text">{{ displayValue || cron || '-' }}</span>
      </div>
    </template>

    <div class="cron-info-panel">
      <div class="info-section">
        <div class="section-title">Cron 表达式</div>
        <div class="section-content">
          <code class="cron-code">{{ cron || '未设置' }}</code>
        </div>
      </div>

      <div class="info-section">
        <div class="section-title">语义化解析</div>
        <div class="section-content semantic-desc">
          {{ cronDescription }}
        </div>
      </div>

      <div class="info-section">
        <div class="section-title">近期执行计划</div>
        <div class="section-content execution-preview">
          <div v-if="nextRunTimes.length === 0" class="no-runs">无法计算执行时间</div>
          <div v-for="(time, index) in nextRunTimes" :key="index" class="run-item">
            <span class="run-index">{{ index + 1 }}</span>
            <span class="run-time">{{ time }}</span>
          </div>
        </div>
      </div>
    </div>
  </el-popover>
</template>

<script setup lang="ts">
import { computed } from "vue";
import { Clock } from "@element-plus/icons-vue";
import cronstrue from "cronstrue/i18n";
import { CronExpressionParser } from "cron-parser";

const props = defineProps<{
  cron: string;
  displayValue?: string;
}>();

// 语义化描述
const cronDescription = computed(() => {
  if (!props.cron) {
    return "未提供 Cron 表达式";
  }
  
  try {
    // 使用 cronstrue 进行中文语义化转换
    return cronstrue.toString(props.cron, { locale: "zh_CN" });
  } catch (e) {
    return "解析失败：非法的 Cron 格式";
  }
});

// 计算未来 5 次执行时间
const nextRunTimes = computed(() => {
  if (!props.cron) {
    return [];
  }

  try {
    // 使用 cron-parser 解析表达式，指定上海时区
    const interval = CronExpressionParser.parse(props.cron, { tz: "Asia/Shanghai" });
    const results: string[] = [];
    
    for (let i = 0; i < 5; i++) {
      const next = interval.next();
      
      // 这里的 normalization 逻辑确保获取到标准的 Date 对象
      let date: Date | null = null;
      if (next instanceof Date) {
        date = next;
      }
      
      // 避免使用 else，通过判断 date 是否已赋值来继续尝试
      if (!date && (next as any)?.toDate) {
        date = (next as any).toDate();
      }
      
      if (!date && (next as any)?.getTime) {
        date = new Date((next as any).getTime());
      }
      
      // 如果最终还是没拿到日期则中断
      if (!date) {
        break;
      }
      
      results.push(formatDate(date));
    }
    
    return results;
  } catch (e) {
    // 捕获解析错误，返回空列表
    return [];
  }
});

/**
 * 格式化日期为 YYYY-MM-DD HH:mm:ss
 */
const formatDate = (date: Date) => {
  const pad = (n: number) => String(n).padStart(2, "0");
  const y = date.getFullYear();
  const m = pad(date.getMonth() + 1);
  const d = pad(date.getDate());
  const hh = pad(date.getHours());
  const mm = pad(date.getMinutes());
  const ss = pad(date.getSeconds());
  return `${y}-${m}-${d} ${hh}:${mm}:${ss}`;
};
</script>

<style scoped>
.cron-trigger {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  cursor: pointer;
  color: var(--el-color-primary);
  font-family: var(--el-font-family-mono);
  font-size: 13px;
  transition: all 0.2s ease;
  padding: 2px 4px;
  border-radius: 4px;
}

.cron-trigger:hover {
  background-color: var(--el-color-primary-light-9);
  text-decoration: underline;
}

.cron-icon {
  font-size: 14px;
}

.cron-display-text {
  max-width: 200px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.cron-info-panel {
  padding: 8px 4px;
}

.info-section {
  margin-bottom: 12px;
}

.info-section:last-child {
  margin-bottom: 0;
}

.section-title {
  font-size: 12px;
  font-weight: 600;
  color: var(--el-text-color-secondary);
  margin-bottom: 6px;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.cron-code {
  display: block;
  background-color: var(--el-fill-color-darker);
  color: #fff;
  padding: 4px 8px;
  border-radius: 4px;
  font-size: 11px;
  font-family: var(--el-font-family-mono);
  word-break: break-all;
}

.semantic-desc {
  background-color: var(--el-color-primary-light-9);
  color: var(--el-color-primary-darker);
  padding: 8px;
  border-radius: 6px;
  font-size: 13px;
  line-height: 1.5;
  border-left: 3px solid var(--el-color-primary);
}

.execution-preview {
  background-color: var(--el-fill-color-light);
  border-radius: 6px;
  padding: 8px;
}

.run-item {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 12px;
  margin-bottom: 4px;
  font-family: var(--el-font-family-mono);
}

.run-item:last-child {
  margin-bottom: 0;
}

.run-index {
  color: var(--el-text-color-disabled);
  font-size: 10px;
  width: 14px;
}

.run-time {
  color: var(--el-text-color-primary);
}

.no-runs {
  font-size: 12px;
  color: var(--el-text-color-placeholder);
  text-align: center;
  padding: 8px 0;
}
</style>
