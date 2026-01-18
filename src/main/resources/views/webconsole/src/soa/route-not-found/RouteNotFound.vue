<script setup lang="ts">
import { useRoute, useRouter } from "vue-router";
import { computed } from "vue";

const route = useRoute();
const router = useRouter();

const requestedPath = computed(() => route.path);

const goBack = () => {
  router.back();
};

const goHome = () => {
  router.push("/");
};
</script>

<template>
  <div class="w-full h-full flex flex-col justify-center items-center bg-white text-gray-800 p-12">
    <div class="flex items-center gap-12 max-w-4xl border-l-8 border-blue-500 pl-12 py-4">
      <div class="text-9xl font-black text-gray-500 select-none leading-none">404</div>
      <div class="flex-1">
        <h1 class="text-4xl font-bold mb-6 text-gray-900 tracking-tight">ROUTE NOT DEFINED</h1>
        <p class="text-lg text-gray-500 mb-8 leading-relaxed max-w-lg">
          系统无法识别或定位您请求的资源路径。该功能模块可能尚未部署，或已被移动到新的入口。
        </p>

        <div class="bg-gray-50 border border-gray-200 p-5 mb-10">
          <div class="text-[10px] font-bold text-gray-400 uppercase tracking-[0.2em] mb-2">Request Endpoint</div>
          <div class="font-mono text-sm text-blue-700 break-all select-all">{{ requestedPath }}</div>
        </div>

        <div class="flex gap-4">
          <el-button type="primary" class="!rounded-none px-8 py-5 h-auto text-base" @click="goHome">
            回到控制台首页
          </el-button>
          <el-button class="!rounded-none px-8 py-5 h-auto text-base" @click="goBack"> 返回上一层 </el-button>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped></style>
