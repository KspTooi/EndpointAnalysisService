<script setup lang="ts">
import { computed } from "vue";
import { useRoute } from "vue-router";

const route = useRoute();

const mediaSrc = computed(() => {
  const sign = route.query.sign as string;
  if (!sign) return "";
  return `/api/drive/object/access/downloadEntry?sign=${sign}&preview=1`;
});
</script>

<template>
  <div class="preview-container">
    <video v-if="mediaSrc" controls autoplay class="video-player" controlslist="nodownload">
      <source :src="mediaSrc" />
      您的浏览器不支持 HTML5 视频播放。
    </video>
  </div>
</template>

<style scoped>
.preview-container {
  height: 100%;
  width: 100%;
  background-color: #000;
  display: flex;
  justify-content: center;
  align-items: center;
}

.video-player {
  max-width: 100%;
  max-height: 100%;
  outline: none;
}
</style>
