<template>
  <div class="flex flex-col h-full bg-white select-none">
    <div class="flex justify-between items-center px-4 py-1 border-b border-gray-200">
      <span class="text-sm font-semibold text-gray-700 text-[12px]">URL查询参数</span>
      <button
        @click="addParam"
        title="添加参数"
        class="flex items-center justify-center w-6 h-6 rounded text-gray-500 hover:bg-gray-200 hover:text-gray-700 transition-all cursor-pointer"
      >
        <IIxAdd />
      </button>
    </div>

    <div class="flex-1 overflow-y-auto border-t border-gray-200">
      <div
        class="flex items-center bg-gray-50 border-b border-gray-200 text-[11px] font-bold text-gray-400 uppercase tracking-tight"
      >
        <div class="w-10 h-8 flex-shrink-0 border-r border-gray-200"></div>
        <div class="flex-1 h-8 flex items-center px-3 border-r border-gray-200">参数名 (Key)</div>
        <div class="flex-1 h-8 flex items-center px-3 border-r border-gray-200">参数值 (Value)</div>
        <div class="w-10 h-8 flex-shrink-0 border-r border-gray-200"></div>
        <div class="w-10 h-8 flex-shrink-0"></div>
      </div>

      <draggable v-model="params" item-key="s" handle=".drag-handle" @end="onDragEnd">
        <template #item="{ element, index }">
          <div
            class="flex items-center border-b border-gray-200 group hover:bg-gray-50/50 transition-colors"
            :class="{ 'opacity-50': !element.e }"
          >
            <!-- 拖动手柄 -->
            <div class="w-10 h-8 flex-shrink-0 flex items-center justify-center border-r border-gray-100">
              <span
                class="drag-handle p-1 rounded cursor-grab active:cursor-grabbing text-gray-300 group-hover:text-gray-400 transition-colors"
                title="拖动排序"
              >
                <IIxDragGripper />
              </span>
            </div>

            <!-- Key 输入框 -->
            <div class="flex-1 h-8 min-w-0 border-r border-gray-100 relative focus-within:z-10">
              <input
                v-model="element.k"
                type="text"
                placeholder="Key"
                class="w-full h-full px-3 text-[13px] text-gray-700 bg-transparent border-0 rounded-none outline-none focus:ring-1 focus:ring-inset focus:ring-blue-500 transition-shadow placeholder:text-gray-300"
              />
            </div>

            <!-- Value 输入框 -->
            <div class="flex-1 h-8 min-w-0 border-r border-gray-100 relative focus-within:z-10">
              <input
                v-model="element.v"
                type="text"
                placeholder="Value"
                class="w-full h-full px-3 text-[13px] text-gray-700 bg-transparent border-0 rounded-none outline-none focus:ring-1 focus:ring-inset focus:ring-blue-500 transition-shadow placeholder:text-gray-300"
              />
            </div>

            <!-- 启用状态切换 -->
            <div class="w-10 h-8 flex-shrink-0 flex items-center justify-center border-r border-gray-100">
              <button
                @click="toggleParam(index)"
                title="启用/禁用"
                class="w-6 h-6 flex items-center justify-center rounded transition-all cursor-pointer"
                :class="element.e ? 'text-green-500 hover:bg-green-50' : 'text-gray-300 hover:bg-gray-100'"
              >
                <IQlementineIconsSuccess12 />
              </button>
            </div>

            <!-- 删除按钮 -->
            <div class="w-10 h-8 flex-shrink-0 flex items-center justify-center">
              <button
                @click="deleteParam(index)"
                title="删除"
                class="w-6 h-6 flex items-center justify-center rounded text-red-400 hover:text-red-600 hover:bg-red-50 transition-all opacity-90 group-hover:opacity-100 cursor-pointer"
              >
                <IMaterialSymbolsDeleteOutline />
              </button>
            </div>
          </div>
        </template>
      </draggable>
    </div>
  </div>
</template>

<script setup lang="ts">
import draggable from "vuedraggable";
import type { RequestUrlParam } from "../api/CollectionApi";

const params = defineModel<RequestUrlParam[]>({ required: true });

const addParam = () => {
  if (!params.value) {
    return;
  }

  const maxSeq = params.value.length > 0 ? Math.max(...params.value.map((p) => p.s)) : 0;

  params.value.push({
    e: true,
    k: "",
    v: "",
    s: maxSeq + 1,
  });
};

const deleteParam = (index: number) => {
  if (!params.value) {
    return;
  }

  params.value.splice(index, 1);
};

const toggleParam = (index: number) => {
  if (!params.value) {
    return;
  }

  params.value[index].e = !params.value[index].e;
};

const onDragEnd = () => {
  if (!params.value) {
    return;
  }

  params.value.forEach((param, index) => {
    param.s = index;
  });
};
</script>

<style scoped></style>
