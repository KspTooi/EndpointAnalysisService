<template>
  <el-date-picker
    :model-value="dateRange"
    :type="type"
    :start-placeholder="startPlaceholder"
    :end-placeholder="endPlaceholder"
    :value-format="valueFormat"
    :clearable="clearable"
    :range-separator="rangeSeparator"
    v-bind="$attrs"
    @update:model-value="onChange"
  />
</template>

<script setup lang="ts">
import { computed } from "vue";

/**
 * 默认属性
 * type: 日期范围类型
 * startPlaceholder: 开始日期占位符
 * endPlaceholder: 结束日期占位符
 * valueFormat: 日期格式
 * clearable: 是否可清除
 * rangeSeparator: 范围分隔符
 */
withDefaults(
  defineProps<{
    type?: "daterange" | "datetimerange";
    startPlaceholder?: string;
    endPlaceholder?: string;
    valueFormat?: string;
    clearable?: boolean;
    rangeSeparator?: string;
  }>(),
  {
    type: "datetimerange",
    startPlaceholder: "开始日期",
    endPlaceholder: "结束日期",
    valueFormat: "YYYY-MM-DD HH:mm:ss",
    clearable: true,
    rangeSeparator: "至",
  }
);

/**
 * 开始日期
 */
const rangeStart = defineModel<string>("rangeStart", { default: "" });
/**
 * 结束日期
 */
const rangeEnd = defineModel<string>("rangeEnd", { default: "" });

/**
 * 日期范围
 */
const dateRange = computed(() => {
  if (!rangeStart.value && !rangeEnd.value) return null;
  return [rangeStart.value, rangeEnd.value];
});

/**
 * 日期范围变化时触发
 * @param val 日期范围
 */
function onChange(val: [string, string] | null) {
  rangeStart.value = val?.[0] ?? "";
  rangeEnd.value = val?.[1] ?? "";
}
</script>

<style scoped></style>
