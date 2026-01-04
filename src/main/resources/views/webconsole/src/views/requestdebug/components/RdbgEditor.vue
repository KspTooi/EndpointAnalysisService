<template>
  <div class="container">
    <div class="editor-wrapper">
      <div class="header">
        <div class="header-title">
          <input class="name-input" v-model="editor.name" />
          <div class="header-env-selector">
            <rdbg-editor-env-selector />
          </div>
        </div>

        <div class="header-input" style="margin-top: 12px">
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
        <div
          class="tab-item"
          :class="{ active: PreferenceHolder().getRequestEditorTab === 'header' }"
          @click="PreferenceHolder().setRequestEditorTab('header')"
        >
          标头
        </div>
        <div
          class="tab-item"
          :class="{ active: PreferenceHolder().getRequestEditorTab === 'body' }"
          @click="PreferenceHolder().setRequestEditorTab('body')"
        >
          载荷
        </div>
      </div>

      <div class="content">
        <!-- 请求头内容 -->
        <div v-show="PreferenceHolder().getRequestEditorTab === 'header'" class="tab-panel">
          <!--           <div class="headers-editor">
            <div class="headers-toolbar">
              <el-button @click="addHeader" type="primary" size="small">添加请求头</el-button>
            </div>
            <el-table
              :data="editableHeaders"
              style="width: 100%"
              height="100%"
              empty-text="点击'添加请求头'开始编辑"
              size="small"
              border
            >
              <el-table-column prop="k" label="键" width="200">
                <template #default="scope">
                  <el-input v-model="scope.row.k" @blur="onHeaderChange" placeholder="请求头名称" size="small" />
                </template>
              </el-table-column>
              <el-table-column prop="v" label="值">
                <template #default="scope">
                  <el-input v-model="scope.row.v" @blur="onHeaderChange" placeholder="请求头值" size="small" />
                </template>
              </el-table-column>
              <el-table-column label="操作" width="80" align="center">
                <template #default="scope">
                  <el-button @click="removeHeader(scope.$index)" type="danger" size="small" plain>删除</el-button>
                </template>
              </el-table-column>
            </el-table>
          </div> -->
        </div>

        <!-- 载荷内容 -->
        <div v-show="PreferenceHolder().getRequestEditorTab === 'body'" class="tab-panel">
          <!--          <RequestPayload :requestDetails="requestDetail" @onRequestBodyChange="onRequestBodyChange" />-->
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, watch } from "vue";
import { PreferenceHolder } from "@/store/PreferenceHolder";
import RdbgEditorEnvSelector from "@/views/requestdebug/components/RdbgEditorEnvSelector.vue";
import RdbgEditorUrlInput from "@/views/requestdebug/components/RdbgEditorUrlInput.vue";
import type { GetCollectionDetailsVo } from "../api/CollectionApi";
import RdbgEditorService from "../service/RdbgEditorService";

export interface RdbgEditorProps {
  details: GetCollectionDetailsVo | null;
  loading: boolean;
}

const props = defineProps<RdbgEditorProps>();

const emits = defineEmits<{
  (e: "on-details-change", details: GetCollectionDetailsVo): void;
}>();

const loading = ref(false);

const { editor } = RdbgEditorService.useEditor(props, emits);
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
  padding: 10px 20px;
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
