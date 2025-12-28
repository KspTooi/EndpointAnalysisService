<script setup lang="ts">
import { computed, ref, watch, nextTick } from "vue";
import { useRoute } from "vue-router";
import { RefreshLeft } from "@element-plus/icons-vue";
import DriveRendererService from "./service/DriveRendererService";

const route = useRoute();
const imgRef = ref<HTMLImageElement | null>(null);
const containerRef = ref<HTMLElement | null>(null);

// 计算图片源地址
const imgSrc = computed(() => {
  const sign = route.query.sign as string;
  if (!sign) return "";
  return `/drive/object/access/downloadEntry?sign=${sign}&preview=1`;
});

// 注入拖拽服务
const { x, y, isDragging, onMouseDown, resetPosition } = DriveRendererService.useRendererDrag();

// 注入缩放服务
const { scale, showIndicator, handleWheel, resetScale } = DriveRendererService.useRendererScale(
  {
    maxScale: 20.0, // 图片可以放得更大一点
    minScale: 0.1,
    step: 0.1,
  },
  { x, y }
);

/**
 * 将图片移动到容器中心
 */
const moveToCenter = () => {
  if (!containerRef.value || !imgRef.value) return;

  const containerRect = containerRef.value.getBoundingClientRect();
  const imgRect = imgRef.value.getBoundingClientRect();

  // 注意：这里要用图片的原始尺寸（未缩放时的尺寸）来计算居中
  // 但 imgRect 是受 scale 影响的。
  // 更简单的方法是利用 offsetWidth (它通常是不受 transform 影响的布局尺寸，或者我们需要先重置 scale)

  // 获取容器宽高
  const cw = containerRef.value.clientWidth;
  const ch = containerRef.value.clientHeight;

  // 获取图片自然宽高或当前渲染宽高（假设 scale 为 1 时）
  const iw = imgRef.value.offsetWidth;
  const ih = imgRef.value.offsetHeight;

  // 计算居中坐标
  x.value = (cw - iw) / 2;
  y.value = (ch - ih) / 2;
};

/**
 * 图片加载完成回调
 */
const onImgLoad = () => {
  // 图片加载后，重置缩放并居中
  resetScale();
  // 必须等待 DOM 更新，确保 offsetWidth 正确
  nextTick(() => {
    moveToCenter();
  });
};

/**
 * 手动复位按钮
 */
const handleReset = () => {
  resetScale();
  moveToCenter();
};

// 监听窗口大小变化，重新居中（可选）
// window.addEventListener('resize', moveToCenter);
</script>

<template>
  <div class="preview-container" @wheel="handleWheel" ref="containerRef">
    <div class="image-wrapper" v-if="imgSrc">
      <img
        ref="imgRef"
        :src="imgSrc"
        :alt="route.query.name as string"
        class="preview-img"
        :class="{ 'is-dragging': isDragging }"
        :style="{
          transform: `translate(${x}px, ${y}px) scale(${scale})`,
          cursor: isDragging ? 'grabbing' : 'grab',
          transformOrigin: '0 0',
        }"
        @load="onImgLoad"
        @mousedown="onMouseDown"
        @dragstart.prevent
      />
    </div>

    <div v-else class="empty-tip">无效的预览链接</div>

    <div v-show="showIndicator" class="zoom-indicator">{{ Math.round(scale * 100) }}%</div>

    <div class="control-bar">
      <div class="control-btn" @click="handleReset" title="复位">
        <el-icon><RefreshLeft /></el-icon>
      </div>
    </div>
  </div>
</template>

<style scoped>
.preview-container {
  height: 100%;
  width: 100%;
  /* 基础背景色（白色） */
  background-color: #ffffff;
  /* 利用线性渐变绘制灰白相间的格子 */
  background-image:
    linear-gradient(45deg, #eee 25%, transparent 25%), linear-gradient(-45deg, #eee 25%, transparent 25%),
    linear-gradient(45deg, transparent 75%, #eee 75%), linear-gradient(-45deg, transparent 75%, #eee 75%);
  /* 格子的大小 */
  background-size: 20px 20px;
  /* 格子的位置偏移，使其错位形成棋盘效果 */
  background-position:
    0 0,
    0 10px,
    10px -10px,
    -10px 0px;

  /* 保持之前的布局样式 */
  display: block;
  position: relative;
  overflow: hidden;
}
.image-wrapper {
  /* 关键修改：改为绝对定位，从 (0,0) 开始 */
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  /* pointer-events: none; 让事件穿透wrapper直接到达img (可选) */
}

.preview-img {
  /* 图片变为绝对定位，初始位置 0,0 */
  position: absolute;
  top: 0;
  left: 0;

  /* 限制最大尺寸，防止图片过大溢出太远，保持 contain 效果 */
  max-width: 100%;
  max-height: 100%;

  /* 保持原有的过渡和交互样式 */
  /* transition: transform 0.1s linear; */
  user-select: none;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.5);
}

.preview-img.is-dragging {
  transition: none;
}

.empty-tip {
  height: 100%;
  display: flex;
  justify-content: center;
  align-items: center;
  color: #909399;
}

/* 样式优化：指示器 */
.zoom-indicator {
  position: absolute;
  bottom: 40px;
  left: 50%;
  transform: translateX(-50%);
  background: rgba(0, 0, 0, 0.6);
  color: white;
  padding: 5px 12px;
  border-radius: 4px;
  font-size: 14px;
  pointer-events: none;
  z-index: 10;
}

/* 样式优化：控制栏 */
.control-bar {
  position: absolute;
  bottom: 40px;
  right: 40px;
  z-index: 10;
}

.control-btn {
  background: rgba(255, 255, 255, 0.2);
  width: 36px;
  height: 36px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  color: white;
  transition: background 0.2s;
}

.control-btn:hover {
  background: rgba(255, 255, 255, 0.4);
}
</style>
