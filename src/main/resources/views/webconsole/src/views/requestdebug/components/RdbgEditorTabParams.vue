<template>
  <div class="flex flex-col h-full bg-white">
    <div class="flex justify-between items-center px-4 py-3 border-b border-gray-200">
      <span class="text-sm font-semibold text-gray-700">URL查询参数</span>
      <button
        @click="addParam"
        title="添加参数"
        class="flex items-center justify-center w-7 h-7 rounded bg-gray-100 text-gray-600 hover:bg-gray-200 hover:text-gray-700 transition-all cursor-pointer"
      >
        <IIxAdd />
      </button>
    </div>

    <div class="flex-1 overflow-y-auto">
      <div class="flex items-center px-4 py-2 bg-gray-50 border-b border-gray-200 text-xs font-semibold text-gray-600">
        <div class="w-8 flex-shrink-0"></div>
        <div class="flex-1 min-w-0 pr-2">Key</div>
        <div class="flex-1 min-w-0 pr-2">Value</div>
        <div class="w-20 flex-shrink-0"></div>
      </div>

      <draggable v-model="params" item-key="s" handle=".drag-handle" @end="onDragEnd">
        <template #item="{ element, index }">
          <div
            class="flex items-center px-4 py-2 border-b border-gray-100 hover:bg-gray-50 transition-colors"
            :class="{ 'opacity-50': !element.e }"
          >
            <div class="w-8 flex-shrink-0">
              <span
                class="drag-handle flex items-center justify-center cursor-grab active:cursor-grabbing text-gray-400 hover:text-gray-600 transition-colors"
                title="拖动排序"
              >
                <IIxDragGripper />
              </span>
            </div>
            <div class="flex-1 min-w-0 pr-2">
              <input
                v-model="element.k"
                type="text"
                placeholder="Key"
                class="w-full px-2 py-1.5 border border-gray-200 rounded text-sm text-gray-700 bg-white focus:outline-none focus:border-blue-500 focus:ring-3 focus:ring-blue-500/10 transition-all placeholder:text-gray-400"
              />
            </div>
            <div class="flex-1 min-w-0 pr-2">
              <input
                v-model="element.v"
                type="text"
                placeholder="Value"
                class="w-full px-2 py-1.5 border border-gray-200 rounded text-sm text-gray-700 bg-white focus:outline-none focus:border-blue-500 focus:ring-3 focus:ring-blue-500/10 transition-all placeholder:text-gray-400"
              />
            </div>
            <div class="w-20 flex-shrink-0 flex gap-1 justify-end">
              <button
                @click="toggleParam(index)"
                title="启用/禁用"
                class="flex items-center justify-center w-8 h-8 rounded bg-transparent hover:bg-gray-100 transition-all"
                :class="
                  element.e ? 'text-green-500 hover:bg-green-50 hover:text-green-600' : 'text-gray-400 hover:text-gray-600'
                "
              >
                <svg v-if="element.e" width="16" height="16" viewBox="0 0 16 16" fill="currentColor">
                  <path
                    d="M10.97 4.97a.75.75 0 0 1 1.07 1.05l-3.99 4.99a.75.75 0 0 1-1.08.02L4.324 8.384a.75.75 0 1 1 1.06-1.06l2.094 2.093 3.473-4.425a.267.267 0 0 1 .02-.022z"
                  />
                </svg>
              </button>
              <button
                @click="deleteParam(index)"
                title="删除"
                class="flex items-center justify-center w-8 h-8 rounded bg-transparent text-gray-400 hover:bg-red-50 hover:text-red-500 transition-all"
              >
                <svg width="16" height="16" viewBox="0 0 16 16" fill="currentColor">
                  <path
                    d="M5.5 5.5A.5.5 0 0 1 6 6v6a.5.5 0 0 1-1 0V6a.5.5 0 0 1 .5-.5zm2.5 0a.5.5 0 0 1 .5.5v6a.5.5 0 0 1-1 0V6a.5.5 0 0 1 .5-.5zm3 .5a.5.5 0 0 0-1 0v6a.5.5 0 0 0 1 0V6z"
                  />
                  <path
                    fill-rule="evenodd"
                    d="M14.5 3a1 1 0 0 1-1 1H13v9a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2V4h-.5a1 1 0 0 1-1-1V2a1 1 0 0 1 1-1H6a1 1 0 0 1 1-1h2a1 1 0 0 1 1 1h3.5a1 1 0 0 1 1 1v1zM4.118 4 4 4.059V13a1 1 0 0 0 1 1h6a1 1 0 0 0 1-1V4.059L11.882 4H4.118zM2.5 3V2h11v1h-11z"
                  />
                </svg>
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
