<template>
  <div class="oper-time-edit">
    <div class="oper-time-edit__item">
      <el-date-picker
        class="w-full! min-w-0!"
        v-model="rangeStartValue"
        type="datetime"
        :placeholder="startPlaceholder"
        :value-format="valueFormat"
        :format="valueFormat"
        :disabled="disableds?.[0]"
        :disabled-date="disableStartDate"
      />
      <div v-if="startValidateMessage" class="oper-time-edit__message">{{ startValidateMessage }}</div>
    </div>
    <span class="oper-time-edit__separator">{{ rangeSeparator }}</span>
    <div class="oper-time-edit__item">
      <el-date-picker
        class="w-full! min-w-0!"
        v-model="rangeEndValue"
        type="datetime"
        :placeholder="endPlaceholder"
        :value-format="valueFormat"
        :format="valueFormat"
        clearable
        :disabled="disableds?.[1]"
        :disabled-date="disableEndDate"
      />
      <div v-if="endValidateMessage" class="oper-time-edit__message">{{ endValidateMessage }}</div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, watch, ref } from "vue";

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
    disableds?: boolean[];
  }>(),
  {
    type: "datetimerange",
    startPlaceholder: "开始日期",
    endPlaceholder: "结束日期",
    valueFormat: "YYYY-MM-DD HH:mm:ss",
    clearable: true,
    rangeSeparator: "至",
    disableds: () => [false, false],
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

const innerRangeStart = ref(rangeStart.value); //内部开始日期
const innerRangeEnd = ref(rangeEnd.value); //内部结束日期
let isSyncingInner = false; //是否正在同步到父组件

/**
 * 监听开始日期和结束日期变化
 */
watch([() => rangeStart.value, () => rangeEnd.value], ([start, end]) => {
  if (isSyncingInner) return;
  innerRangeStart.value = start;
  innerRangeEnd.value = end;
});

/**
 * 同步开始日期和结束日期到父组件
 */
function syncRangeToParent() {
  //将内部开始日期和结束日期同步到父组件
  isSyncingInner = true;
  if (!innerRangeStart.value || !innerRangeEnd.value) {
    rangeStart.value = "";
    rangeEnd.value = "";
    queueMicrotask(() => {
      isSyncingInner = false;
    });
    return;
  }
  rangeStart.value = innerRangeStart.value;
  rangeEnd.value = innerRangeEnd.value;
  queueMicrotask(() => {
    isSyncingInner = false;
  });
}

/**
 * 开始时间代理，拦截非法输入
 */
const rangeStartValue = computed({
  get: () => innerRangeStart.value,
  set: (val: string) => {
    if (!val) {
      innerRangeStart.value = "";
      syncRangeToParent();
      return;
    }
    if (innerRangeEnd.value && new Date(val).getTime() > new Date(innerRangeEnd.value).getTime()) return;
    innerRangeStart.value = val;
    if (!innerRangeEnd.value) {
      syncRangeToParent();
      return;
    }
    syncRangeToParent();
  },
});

/**
 * 结束时间代理，拦截非法输入
 */
const rangeEndValue = computed({
  get: () => innerRangeEnd.value,
  set: (val: string) => {
    if (!val) {
      innerRangeEnd.value = "";
      syncRangeToParent();
      return;
    }
    if (innerRangeStart.value && new Date(val).getTime() < new Date(innerRangeStart.value).getTime()) return;
    innerRangeEnd.value = val;
    if (!innerRangeStart.value) {
      syncRangeToParent();
      return;
    }
    syncRangeToParent();
  },
});

const startValidateMessage = computed(() => {
  if (!innerRangeEnd.value) return "";
  if (innerRangeStart.value) return "";
  return "请选择开始时间";
});

const endValidateMessage = computed(() => {
  if (!innerRangeStart.value) return "";
  if (innerRangeEnd.value) return "";
  return "请选择结束时间";
});

/**
 * 开始时间不能晚于结束时间
 */
function disableStartDate(date: Date) {
  if (!innerRangeEnd.value) return false;
  return date.getTime() > new Date(innerRangeEnd.value).getTime();
}

/**
 * 结束时间不能早于开始时间
 */
function disableEndDate(date: Date) {
  if (!innerRangeStart.value) return false;
  return date.getTime() < new Date(innerRangeStart.value).getTime();
}
</script>

<style scoped>
.oper-time-edit {
  display: flex;
  align-items: flex-start !important;
  gap: 8px;
  width: 100%;
}

.oper-time-edit__item {
  flex: 1;
  min-width: 0;
}

.oper-time-edit__message {
  margin-top: 4px;
  color: var(--el-color-danger);
  font-size: 12px;
  line-height: 1.2;
}

.oper-time-edit :deep(.el-date-editor) {
  width: 100% !important;
  min-width: 0;
}

.oper-time-edit__separator {
  color: var(--el-text-color-regular);
  white-space: nowrap;
}
</style>
