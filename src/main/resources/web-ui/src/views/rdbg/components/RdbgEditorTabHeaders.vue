<template>
  <div class="flex flex-col h-full bg-white select-none">
    <div class="flex justify-between items-center px-4 py-1 border-b border-gray-200">
      <span class="text-sm font-semibold text-gray-700 text-[12px]">请求头 (Headers)</span>
      <button
        @click="addHeader"
        title="添加请求头"
        class="flex items-center justify-center w-6 h-6 rounded text-gray-500 hover:bg-gray-200 hover:text-gray-700 transition-all cursor-pointer"
      >
        <IIxAdd />
      </button>
    </div>

    <el-scrollbar>
      <div class="flex-1 overflow-y-auto border-t border-gray-200">
        <draggable v-model="headers" item-key="s" handle=".drag-handle" :filter="'.no-drag'" @move="onMove" @end="onDragEnd">
          <template #item="{ element, index }">
            <div
              class="flex items-center border-b border-gray-200 group hover:bg-gray-50/50 transition-colors"
              :class="{ 'opacity-50': !element.e }"
            >
              <!-- 拖动手柄 -->
              <div class="w-10 h-8 flex-shrink-0 flex items-center justify-center border-r border-gray-100">
                <span
                  v-if="!element.d"
                  class="drag-handle p-1 rounded cursor-grab active:cursor-grabbing text-gray-300 group-hover:text-gray-400 transition-colors"
                  title="拖动排序"
                >
                  <IIxDragGripper />
                </span>
                <span v-else class="no-drag p-1 rounded text-gray-200" title="默认项不可拖动">
                  <IIxDragGripper />
                </span>
              </div>

              <!-- Key 输入框 -->
              <div class="flex-1 h-8 min-w-0 border-r border-gray-100 relative focus-within:z-10">
                <input
                  v-model="element.k"
                  type="text"
                  placeholder="Key"
                  :disabled="element.d"
                  class="w-full h-full px-3 text-[13px] text-gray-700 bg-transparent border-0 rounded-none outline-none focus:ring-1 focus:ring-inset focus:ring-blue-500 transition-shadow placeholder:text-gray-300 disabled:bg-gray-50"
                />
              </div>

              <!-- Value 输入框 -->
              <div class="flex-1 h-8 min-w-0 border-r border-gray-100 relative focus-within:z-10">
                <input
                  v-if="!element.a"
                  v-model="element.v"
                  type="text"
                  placeholder="Value"
                  :disabled="element.d"
                  class="w-full h-full px-3 text-[13px] text-gray-700 bg-transparent border-0 rounded-none outline-none focus:ring-1 focus:ring-inset focus:ring-blue-500 transition-shadow placeholder:text-gray-300 disabled:bg-gray-50"
                />
                <span v-else class="w-full h-full px-3 flex items-center text-[13px] text-gray-400">
                  发送请求时将自动计算值
                </span>
              </div>

              <!-- 启用状态切换 -->
              <div class="w-10 h-8 flex-shrink-0 flex items-center justify-center border-r border-gray-100">
                <button
                  @click="toggleHeader(index)"
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
                  v-if="!element.d"
                  @click="deleteHeader(index)"
                  title="删除"
                  class="w-6 h-6 flex items-center justify-center rounded text-red-400 hover:text-red-600 hover:bg-red-50 transition-all opacity-90 group-hover:opacity-100 cursor-pointer"
                >
                  <IMaterialSymbolsDeleteOutline />
                </button>
                <span v-else class="w-6 h-6 flex items-center justify-center text-gray-300" title="默认项不可删除">
                  <IMaterialSymbolsLockOutline class="text-[14px]" />
                </span>
              </div>
            </div>
          </template>
        </draggable></div
    ></el-scrollbar>
  </div>
</template>

<script setup lang="ts">
import draggable from "vuedraggable";
import type { RequestHeaderJson } from "@/views/rdbg/api/CollectionApi";

const headers = defineModel<RequestHeaderJson[]>({ required: true });

const addHeader = () => {
  if (!headers.value) {
    return;
  }

  const maxSeq = headers.value.length > 0 ? Math.max(...headers.value.map((h) => h.s)) : 0;

  headers.value.push({
    d: false,
    e: true,
    a: false,
    k: "",
    v: "",
    s: maxSeq + 1,
  });
};

const deleteHeader = (index: number) => {
  if (!headers.value) {
    return;
  }

  if (headers.value[index].d) {
    return;
  }

  headers.value.splice(index, 1);
};

const toggleHeader = (index: number) => {
  if (!headers.value) {
    return;
  }

  headers.value[index].e = !headers.value[index].e;
};

const onMove = (evt: any) => {
  if (!headers.value) {
    return false;
  }

  const draggedElement = evt.draggedContext.element;

  if (draggedElement.d) {
    return false;
  }

  const defaultHeaderCount = headers.value.filter((h) => h.d).length;
  if (evt.relatedContext.index < defaultHeaderCount) {
    return false;
  }

  return true;
};

const onDragEnd = () => {
  if (!headers.value) {
    return;
  }

  const defaultHeaders: RequestHeaderJson[] = [];
  const customHeaders: RequestHeaderJson[] = [];

  headers.value.forEach((header) => {
    if (header.d) {
      defaultHeaders.push(header);
      return;
    }
    customHeaders.push(header);
  });

  headers.value = [...defaultHeaders, ...customHeaders];

  headers.value.forEach((header, index) => {
    header.s = index;
  });
};
</script>

<style scoped></style>
