<script setup lang="ts">
import { computed } from "vue";
import { useRoute } from "vue-router";

const route = useRoute();

// 计算图片源地址
const imgSrc = computed(() => {
  const sign = route.query.sign as string;
  if (!sign) return "";
  // 直接指向后端流式下载接口
  return `/drive/object/access/downloadEntry?sign=${sign}&preview=1`;
});
</script>

<template>
  <div class="preview-container">
    <div class="image-wrapper" v-if="imgSrc">
      <img :src="imgSrc" :alt="route.query.name as string" />
    </div>
    <div v-else class="empty-tip">无效的预览链接</div>
  </div>
</template>

<style scoped>
.preview-container {
  height: 100%;
  width: 100%;
  background-color: #f0f2f5;
  display: flex;
  justify-content: center;
  align-items: center;
  overflow: hidden;
  position: relative;
}

.image-wrapper {
  width: 100%;
  height: 100%;
  padding: 20px;
  box-sizing: border-box;
  display: flex;
  justify-content: center;
  align-items: center;
}

img {
  max-width: 100%;
  max-height: 100%;
  object-fit: contain;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
  border-radius: 4px;
}

.empty-tip {
  color: #909399;
}
</style>
