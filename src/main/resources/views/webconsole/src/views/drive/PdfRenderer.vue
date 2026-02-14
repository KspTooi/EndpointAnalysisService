<script setup lang="ts">
import { computed } from "vue";
import { useRoute } from "vue-router";
import Http from "@/commons/Http";
const route = useRoute();

const pdfSrc = computed(() => {
  const sign = route.query.sign as string;
  if (!sign) return "";
  // 许多浏览器使用 iframe 打开 PDF 时表现良好
  return Http.resolve(`/drive/object/access/downloadEntry?sign=${sign}&preview=1`);
});
</script>

<template>
  <div class="preview-container">
    <iframe v-if="pdfSrc" :src="pdfSrc" class="pdf-iframe" frameborder="0"></iframe>
  </div>
</template>

<style scoped>
.preview-container {
  height: 100%;
  width: 100%;
  background-color: #525659; /* 常用PDF阅读器背景色 */
  overflow: hidden;
}

.pdf-iframe {
  width: 100%;
  height: 100%;
  display: block;
}
</style>
