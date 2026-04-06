<template>
  <div class="preview-page">
    <!-- 顶部工具栏 -->
    <div class="top-bar">
      <el-button :icon="ArrowLeftIcon" @click="cdrcReturn">{{ cdrcReturnName }}</el-button>
      <span class="schema-title">{{ schemaName }} — 蓝图预览</span>
    </div>

    <div class="main-content">
      <!-- 左侧蓝图列表 -->
      <div class="left-panel">
        <div class="panel-header">蓝图文件</div>
        <div v-loading="listLoading" class="blueprint-list">
          <div
            v-for="item in blueprintList"
            :key="item.sha256Hex"
            class="blueprint-item"
            :class="{ active: selectedSha256 === item.sha256Hex }"
            @click="selectBlueprint(item)"
          >
            <el-icon class="item-icon"><Document /></el-icon>
            <div class="item-info">
              <div class="item-name" :title="item.fileName">{{ item.fileName }}</div>
              <div class="item-path" :title="item.filePath">{{ item.filePath }}</div>
            </div>
          </div>
          <div v-if="!listLoading && blueprintList.length === 0" class="empty-list">暂无蓝图文件</div>
        </div>
      </div>

      <!-- 右侧代码预览 -->
      <div class="right-panel">
        <div class="code-preview-container">
          <div class="toolbar">
            <div class="file-info">
              <span class="file-name">{{ selectedFileName || "请选择蓝图文件" }}</span>
              <span v-if="detectedLanguage" class="file-lang">({{ detectedLanguage }})</span>
            </div>
            <div class="actions">
              <el-button link type="primary" :disabled="!codeContent" @click="onCopy">
                <el-icon><DocumentCopy /></el-icon> 复制
              </el-button>
            </div>
          </div>

          <div v-loading="previewLoading" class="code-wrapper">
            <pre v-if="rawHtml" class="hljs code-block"><code v-html="rawHtml"></code></pre>
            <div v-else-if="!previewLoading" class="empty-tip">
              {{ blueprintList.length > 0 ? "请从左侧选择一个蓝图文件" : "暂无内容" }}
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from "vue";
import { ElMessage } from "element-plus";
import { DocumentCopy, Document, ArrowLeft } from "@element-plus/icons-vue";
import hljs from "highlight.js";
import "highlight.js/styles/atom-one-dark.css";
import ComDirectRouteContext from "@/soa/com-series/service/ComDirectRouteContext.ts";
import OpSchemaApi from "@/views/assembly/api/OpSchemaApi";
import type { GetOpBluePrintListVo } from "@/views/assembly/api/OpSchemaApi";
import ComIconService from "@/soa/com-series/service/ComIconService";

const { resolveIcon } = ComIconService.useIconService();
const ArrowLeftIcon = resolveIcon("arrow-left");

const { getCdrcQuery, cdrcReturn, cdrcReturnName } = ComDirectRouteContext.useDirectRouteContext();

const cdrcRow = getCdrcQuery();

const schemaName = ref<string>("");
const opSchemaId = ref<string>("");

const listLoading = ref(false);
const previewLoading = ref(false);
const blueprintList = ref<GetOpBluePrintListVo[]>([]);
const selectedSha256 = ref<string>("");
const selectedFileName = ref<string>("");
const codeContent = ref<string>("");
const rawHtml = ref<string>("");
const detectedLanguage = ref<string>("");

/**
 * 根据文件名后缀获取高亮语言
 */
const getLanguageByExt = (filename: string): string => {
  const ext = filename.split(".").pop()?.toLowerCase();
  const map: Record<string, string> = {
    js: "javascript",
    ts: "typescript",
    vue: "xml",
    html: "xml",
    css: "css",
    scss: "scss",
    json: "json",
    java: "java",
    py: "python",
    go: "go",
    c: "c",
    cpp: "cpp",
    cs: "csharp",
    php: "php",
    sql: "sql",
    md: "markdown",
    sh: "bash",
    yml: "yaml",
    xml: "xml",
  };
  return ext ? map[ext] || "" : "";
};

/**
 * 为代码添加行号结构
 */
const generateLineNumbers = (html: string): string => {
  const lines = html.split(/\r\n|\r|\n/);
  if (lines[lines.length - 1] === "") {
    lines.pop();
  }
  return lines.map((line) => `<div class="code-line">${line || " "}</div>`).join("");
};

/**
 * 渲染并高亮代码
 */
const renderCode = (code: string, fileName: string): void => {
  const lang = getLanguageByExt(fileName);
  let result;

  if (lang && hljs.getLanguage(lang)) {
    result = hljs.highlight(code, { language: lang });
    detectedLanguage.value = lang;
  } else {
    result = hljs.highlightAuto(code);
    detectedLanguage.value = result.language || "text";
  }

  rawHtml.value = generateLineNumbers(result.value);
};

/**
 * 选择蓝图，调用预览接口
 */
const selectBlueprint = async (item: GetOpBluePrintListVo): Promise<void> => {
  if (selectedSha256.value === item.sha256Hex) {
    return;
  }

  selectedSha256.value = item.sha256Hex;
  selectedFileName.value = item.fileName;
  codeContent.value = "";
  rawHtml.value = "";
  detectedLanguage.value = "";
  previewLoading.value = true;

  try {
    const code = await OpSchemaApi.previewOpBluePrint({
      opSchemaId: opSchemaId.value,
      sha256Hex: item.sha256Hex,
    });
    codeContent.value = code;
    renderCode(code, item.fileName);
  } catch (error: any) {
    ElMessage.error(error.message || "预览失败");
  } finally {
    previewLoading.value = false;
  }
};

/**
 * 加载蓝图列表
 */
const loadBlueprintList = async (): Promise<void> => {
  listLoading.value = true;
  try {
    blueprintList.value = await OpSchemaApi.getOpBluePrintList({ id: opSchemaId.value });
  } catch (error: any) {
    ElMessage.error(error.message || "加载蓝图列表失败");
  } finally {
    listLoading.value = false;
  }
};

/**
 * 复制代码到剪贴板
 */
const onCopy = async (): Promise<void> => {
  if (!codeContent.value) {
    return;
  }
  try {
    await navigator.clipboard.writeText(codeContent.value);
    ElMessage.success("代码已复制到剪贴板");
  } catch {
    ElMessage.error("复制失败");
  }
};

onMounted(async () => {
  if (!cdrcRow) {
    return;
  }
  opSchemaId.value = cdrcRow.id;
  schemaName.value = cdrcRow.name || "";
  await loadBlueprintList();
});
</script>

<style scoped>
.preview-page {
  height: 100%;
  width: 100%;
  display: flex;
  flex-direction: column;
  overflow: hidden;
  background-color: #f5f7fa;
}

.top-bar {
  height: 48px;
  flex-shrink: 0;
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 0 16px;
  background-color: #ffffff;
  border-bottom: 1px solid #e4e7ed;
}

.schema-title {
  font-size: 14px;
  font-weight: 600;
  color: #303133;
}

.main-content {
  flex: 1;
  display: flex;
  overflow: hidden;
  padding: 12px;
  gap: 12px;
}

/* 左侧面板 */
.left-panel {
  width: 260px;
  flex-shrink: 0;
  display: flex;
  flex-direction: column;
  background-color: #ffffff;
  border: 1px solid #e4e7ed;
  border-radius: 4px;
  overflow: hidden;
}

.panel-header {
  height: 40px;
  flex-shrink: 0;
  display: flex;
  align-items: center;
  padding: 0 12px;
  font-size: 12px;
  font-weight: 600;
  color: #909399;
  text-transform: uppercase;
  letter-spacing: 0.5px;
  border-bottom: 1px solid #e4e7ed;
  background-color: #fafafa;
}

.blueprint-list {
  flex: 1;
  overflow-y: auto;
  scrollbar-width: thin;
  scrollbar-color: #c0c4cc #f5f7fa;
}

.blueprint-list::-webkit-scrollbar {
  width: 6px;
}

.blueprint-list::-webkit-scrollbar-track {
  background: #f5f7fa;
}

.blueprint-list::-webkit-scrollbar-thumb {
  background: #c0c4cc;
  border-radius: 3px;
}

.blueprint-item {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px 12px;
  cursor: pointer;
  border-bottom: 1px solid #f0f0f0;
  transition: background-color 0.15s;
}

.blueprint-item:hover {
  background-color: #f5f7fa;
}

.blueprint-item.active {
  background-color: #ecf5ff;
  border-left: 3px solid #409eff;
  padding-left: 9px;
}

.item-icon {
  font-size: 16px;
  color: #909399;
  flex-shrink: 0;
}

.blueprint-item.active .item-icon {
  color: #409eff;
}

.item-info {
  flex: 1;
  min-width: 0;
}

.item-name {
  font-size: 13px;
  color: #303133;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.blueprint-item.active .item-name {
  color: #409eff;
  font-weight: 600;
}

.item-path {
  font-size: 11px;
  color: #909399;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  margin-top: 2px;
}

.empty-list {
  padding: 24px 12px;
  text-align: center;
  color: #909399;
  font-size: 13px;
}

/* 右侧代码预览 */
.right-panel {
  flex: 1;
  overflow: hidden;
  border-radius: 4px;
  border: 1px solid #e4e7ed;
}

.code-preview-container {
  height: 100%;
  width: 100%;
  display: flex;
  flex-direction: column;
  background-color: #282c34;
  overflow: hidden;
}

.toolbar {
  height: 40px;
  flex-shrink: 0;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 16px;
  background-color: #21252b;
  border-bottom: 1px solid #181a1f;
  color: #abb2bf;
}

.file-name {
  font-weight: 600;
  font-size: 14px;
  margin-right: 8px;
  color: #abb2bf;
}

.file-lang {
  font-size: 12px;
  color: #5c6370;
}

.code-wrapper {
  flex: 1;
  overflow: auto;
  position: relative;
  scrollbar-width: thin;
  scrollbar-color: #4b5263 #282c34;
}

.code-wrapper::-webkit-scrollbar {
  width: 10px;
  height: 10px;
}

.code-wrapper::-webkit-scrollbar-track {
  background: #282c34;
}

.code-wrapper::-webkit-scrollbar-thumb {
  background: #4b5263;
  border-radius: 5px;
}

.code-block {
  margin: 0;
  padding: 16px;
  font-family: "Fira Code", "Consolas", "Monaco", "Courier New", monospace;
  font-size: 14px;
  line-height: 1.5;
  background: transparent !important;
  counter-reset: line-number;
}

:deep(.code-line) {
  position: relative;
  padding-left: 3.5em;
  min-height: 1.5em;
}

:deep(.code-line::before) {
  counter-increment: line-number;
  content: counter(line-number);
  position: absolute;
  left: 0;
  top: 0;
  width: 3em;
  text-align: right;
  color: #5c6370;
  user-select: none;
  border-right: 1px solid #3e4451;
  padding-right: 0.5em;
}

.empty-tip {
  height: 100%;
  display: flex;
  justify-content: center;
  align-items: center;
  color: #5c6370;
  font-size: 14px;
}
</style>
