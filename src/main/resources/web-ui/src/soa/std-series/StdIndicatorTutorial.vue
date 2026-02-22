<template>
  <!-- 说明文档指示器 -->
  <div class="std-indicator" @click="onClick">
    <el-tooltip content="说明文档指示器: 此页面有可用的说明文档供查阅，点击可查看详细说明" placement="left">
      <IRivetIconsQuestionMark :class="active ? 'active-color' : 'color'" />
    </el-tooltip>
  </div>
</template>

<script setup lang="ts">
withDefaults(
  defineProps<{
    active?: boolean;
  }>(),
  {
    active: false,
  }
);

const emit = defineEmits(["on-click"]);

const onClick = () => {
  emit("on-click");
};
</script>

<style scoped>
.std-indicator {
  cursor: pointer;
  display: flex;
  align-items: center;
}

.color {
  color: #0d9488;
}

.active-color {
  color: #00cfbe;
}
</style>
