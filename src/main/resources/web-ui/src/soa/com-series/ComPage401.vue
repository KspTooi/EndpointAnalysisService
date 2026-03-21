<template>
  <div class="w-full h-full flex flex-col justify-center items-center bg-white text-gray-800 p-12">
    <div class="flex items-center gap-12 max-w-4xl border-l-8 border-red-500 pl-12 py-4">
      <div class="text-9xl font-black text-gray-500 select-none leading-none">403</div>
      <div class="flex-1">
        <h1 class="text-4xl font-bold mb-6 text-gray-900 tracking-tight">ACCESS DENIED</h1>
        <p class="text-lg text-gray-500 mb-8 leading-relaxed max-w-lg">
          {{ permissionError }}。请联系系统管理员申请相关功能模块的访问权限。
        </p>

        <div class="bg-gray-50 border border-gray-200 p-5 mb-10">
          <div class="text-[10px] font-bold text-gray-400 uppercase tracking-[0.2em] mb-2">Required Permission</div>
          <div class="font-mono text-sm text-red-700 break-all select-all">{{ permissionCode }}</div>
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

<script setup lang="ts">
import { ref, onMounted, computed } from "vue";
import { useRouter, useRoute } from "vue-router";

const router = useRouter();
const route = useRoute();

const permissionError = ref<string>("您没有访问此页面的权限");
const permissionCode = computed(() => (route.query.permissionCode as string) || "N/A");

onMounted(() => {
  if (route.query.message) {
    permissionError.value = route.query.message as string;
  }

  if (route.query.permissionCode) {
    permissionError.value = `权限不足：${route.query.permissionCode}`;
  }
});

const goBack = () => {
  router.back();
};

const goHome = () => {
  router.push("/index");
};
</script>

<style scoped></style>
