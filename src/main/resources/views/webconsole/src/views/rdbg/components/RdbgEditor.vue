<template>
  <div class="container" v-loading="props.loading">
    <div class="header">
      <div class="header-title flex items-center">
        <span class="text-[#07998b]" v-show="hasUncommittedChanges"><ILineMdUploadingLoop /></span>
        <input class="name-input !text-[12px]" v-model="editor.name" />
        <div class="header-env-selector">
          <rdbg-editor-env-selector />
        </div>
      </div>

      <div class="header-input" style="margin-top: 8px">
        <rdbg-editor-url-input
          v-model:method="editor.reqMethod"
          v-model:url="editor.reqUrl"
          @on-request-send=""
          :loading="props.loading"
        />
      </div>
    </div>

    <!-- 选项卡 -->
    <div class="tab">
      <div class="tab-item" :class="{ active: rdbgStore.getEditorTab === 'params' }" @click="rdbgStore.setEditorTab('params')">
        参数
      </div>
      <div class="tab-item" :class="{ active: rdbgStore.getEditorTab === 'header' }" @click="rdbgStore.setEditorTab('header')">
        标头
      </div>
      <div class="tab-item" :class="{ active: rdbgStore.getEditorTab === 'body' }" @click="rdbgStore.setEditorTab('body')">
        载荷
      </div>
    </div>

    <div class="content">
      <!-- 参数内容 -->
      <rdbg-param-editor v-show="rdbgStore.getEditorTab === 'params'" v-model="editor.requestParams" title="URL查询参数" />

      <!-- 请求头内容 -->
      <rdbg-editor-tab-headers v-show="rdbgStore.getEditorTab === 'header'" v-model="editor.requestHeaders" />

      <!-- 载荷内容 -->
      <rdbg-editor-tab-body v-show="rdbgStore.getEditorTab === 'body'" v-model="editor.reqBody" />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, watch } from "vue";
import { PreferenceHolder } from "@/store/PreferenceHolder";
import RdbgEditorEnvSelector from "@/views/rdbg/components/RdbgEditorEnvSelector.vue";
import RdbgEditorUrlInput from "@/views/rdbg/components/RdbgEditorUrlInput.vue";
import type { GetCollectionDetailsVo } from "@/views/rdbg/api/CollectionApi";
import RdbgEditorService from "@/views/rdbg/service/RdbgEditorService";
import { useRdbgStore } from "@/views/rdbg/service/RdbgStore";
import RdbgParamEditor from "@/views/rdbg/components/RdbgParamEditor.vue";
import RdbgEditorTabHeaders from "@/views/rdbg/components/RdbgEditorTabHeaders.vue";
import RdbgEditorTabBody from "@/views/rdbg/components/RdbgEditorTabBody.vue";

const rdbgStore = useRdbgStore();

export interface RdbgEditorProps {
  details: GetCollectionDetailsVo | null;
  loading: boolean;
}

const props = defineProps<RdbgEditorProps>();

const emits = defineEmits<{
  (e: "on-details-change", details: GetCollectionDetailsVo): void;
}>();

const loading = ref(false);

const { editor, hasUncommittedChanges } = RdbgEditorService.useEditor(props, emits, 3000);
</script>

<style scoped>
.container {
  position: relative;
  display: flex;
  flex-direction: column;
  height: 100%;
  background: #fff;
  overflow: hidden;
  border-left: 1px solid #e4e7ed;
  /* box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1); */
}

.header {
  background: #f8f9fa;
  padding: 10px 12px;
  border-bottom: 1px solid #e9ecef;
}

.header-title {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  font-size: 14px;
  font-weight: 600;
  color: #495057;
  text-align: center;
}

.content {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.tab {
  display: flex;
  background: #fff;
  border-bottom: 1px solid #e9ecef;
}

.tab-item {
  padding: 5px 20px;
  cursor: pointer;
  border-bottom: 2px solid transparent;
  transition: all 0.3s;
  color: #6c757d;
  font-weight: 400;
  font-size: 14px;
}

.tab-item:hover {
  background: #f8f9fa;
  color: #495057;
}

.tab-item.active {
  color: #007bff;
  border-bottom-color: #007bff;
  background: #fff;
}

/* tab-panel滚动条样式 */
.tab-panel::-webkit-scrollbar {
  width: 8px;
}

.tab-panel::-webkit-scrollbar-track {
  background: #f1f1f1;
  border-radius: 4px;
}

.tab-panel::-webkit-scrollbar-thumb {
  background: #c1c1c1;
  border-radius: 4px;
}

.tab-panel::-webkit-scrollbar-thumb:hover {
  background: #a8a8a8;
}

.tab-panel {
  padding: 5px 5px 15px 15px;
  height: 100%;
  overflow: auto;
}

/* 请求头编辑器样式 */
.headers-editor {
  display: flex;
  flex-direction: column;
  height: 100%;
}

.headers-toolbar {
  padding: 5px 0;
  margin-bottom: 5px;
}

.name-input {
  border: none;
  outline: none;
  font-size: 14px;
  font-weight: 600;
  color: #495057;
  flex: 1;
  background: transparent;
  height: 20px;
  min-width: 0;
}

.header-env-selector {
  flex-shrink: 0;
}
</style>
