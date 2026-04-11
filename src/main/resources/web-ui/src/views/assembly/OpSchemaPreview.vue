<template>
  <div v-loading="listLoading" element-loading-text="正在从SCM下载蓝图列表" class="preview-page">
    <!-- 输出方案信息区域 -->
    <div class="schema-info-bar">
      <div class="schema-info-item">
        <span class="schema-info-label">输出方案</span>
        <span class="schema-info-value">{{ cdrcRow?.name }}</span>
      </div>
      <div class="schema-info-divider" />
      <div class="schema-info-item">
        <span class="schema-info-label">模型名称</span>
        <span class="schema-info-value">{{ cdrcRow?.modelName }}</span>
      </div>
      <div class="schema-info-divider" />
      <div class="schema-info-item">
        <span class="schema-info-label">数据源表名</span>
        <span class="schema-info-value">{{ cdrcRow?.tableName ?? "未配置" }}</span>
      </div>
      <div class="schema-info-divider" />
      <div class="schema-info-item">
        <span class="schema-info-label">显示未解析的文件名</span>
        <el-switch v-model="showRawName" size="small" />
      </div>
    </div>

    <!-- 操作按钮区域 -->
    <div class="action-bar">
      <el-button type="primary" class="ml-0!" :icon="CloseIcon" link @click="goToList"> 回退 </el-button>
      <el-button type="success" class="ml-0!" :icon="RefreshIcon" link @click="refreshBlueprint">刷新蓝图</el-button>
      <el-button type="success" class="ml-0!" style="color: #a53200" :icon="CheckAllIcon" link @click="checkedAll">
        全选
      </el-button>
      <el-button type="success" class="ml-0!" style="color: #a53200" :icon="ClearCheckedIcon" link @click="clearSelected">
        清空已选
      </el-button>
      <el-button type="success" class="ml-0!" :icon="ExecuteIcon" link @click="onExecute">执行已选蓝图</el-button>
      <el-button type="primary" class="ml-0!" :icon="DesignIcon" link @click="goToDesign">转到设计</el-button>
    </div>

    <div class="main-content">
      <splitpanes class="custom-theme">
        <!-- 左侧蓝图列表 -->
        <pane size="20" min-size="10" max-size="50">
          <div class="left-panel">
            <div class="panel-header">蓝图文件</div>
            <div class="blueprint-list">
              <!-- 固定条目：QBE模型 -->
              <div
                class="blueprint-item"
                :class="{ active: selectedKey === '__qbe_model__' }"
                @click="onQbeModelSelect(cdrcRow.id)"
              >
                <el-icon class="item-icon"><DataAnalysis /></el-icon>
                <div class="item-info">
                  <div class="item-name">QBE引擎模型参数</div>
                  <div class="item-path">查看提供给QBE引擎的模型数据JSON</div>
                </div>
              </div>

              <div class="list-divider" />

              <div
                v-for="item in blueprintList"
                :key="item.sha256Hex"
                class="blueprint-item"
                :class="{ active: selectedKey === item.sha256Hex }"
                @click="onBlueprintSelect(item)"
              >
                <el-checkbox
                  :model-value="checkedBlueprints.some((c) => c.sha256Hex === item.sha256Hex)"
                  class="item-checkbox"
                  @change="toggleCheckedBlueprint(item)"
                  @click.stop
                />
                <el-icon class="item-icon"><Document /></el-icon>
                <div class="item-info">
                  <div class="item-name" :title="showRawName ? item.fileName : item.parsedName">
                    {{ showRawName ? item.fileName : item.parsedName }}
                  </div>
                  <div class="item-path" :title="showRawName ? item.filePath : item.parsedPath">
                    {{ showRawName ? item.filePath : item.parsedPath }}
                  </div>
                </div>
              </div>
              <div v-if="!listLoading && blueprintList.length === 0" class="empty-list">暂无蓝图文件</div>
            </div>
          </div>
        </pane>

        <!-- 右侧代码预览 -->
        <pane>
          <div class="right-panel">
            <div class="code-preview-container">
              <div class="toolbar">
                <div class="file-info">
                  <span class="file-name">{{ previewFileName || "请选择蓝图文件" }}</span>
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

                <div v-if="!rawHtml && !previewLoading && !previewBlueprintDeleted" class="empty-tip">
                  {{ blueprintList.length > 0 ? "请从左侧选择一个蓝图文件" : "暂无内容" }}
                </div>

                <div v-if="previewBlueprintDeleted" class="empty-tip">
                  <close-icon class="text-red-500 mr-2 size-8" />该蓝图已被删除，请重新选择
                </div>
              </div>
            </div>
          </div>
        </pane>
      </splitpanes>
    </div>

    <!-- 执行进度模态框 -->
    <el-dialog
      v-model="executionVisible"
      title="执行输出方案"
      width="500px"
      :close-on-click-modal="false"
      :close-on-press-escape="false"
      :show-close="!executionLoading"
    >
      <div class="execution-content">
        <div v-if="executionLoading" class="execution-warning">
          <el-icon class="is-loading"><Loading /></el-icon>
          <span>正在执行中，请勿关闭窗口...</span>
        </div>

        <div v-if="!executionFinished" class="execution-steps">
          <div v-for="(step, index) in executionSteps" :key="index" class="step-item">
            <div class="step-icon">
              <el-icon><Document /></el-icon>
            </div>
            <div class="step-title">{{ step }}</div>
          </div>
        </div>

        <div v-if="executionError" class="execution-error-msg">
          <el-alert :title="executionError" type="error" :closable="false" show-icon />
        </div>

        <div v-if="executionFinished" class="execution-success-msg">
          <el-result icon="success" title="执行成功" sub-title="所有蓝图已成功处理并推送至SCM"> </el-result>
        </div>
      </div>
      <template #footer>
        <span class="dialog-footer">
          <el-button v-if="!executionLoading" @click="executionVisible = false">{{
            executionFinished ? "完成" : "取消"
          }}</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { DocumentCopy, Document, DataAnalysis, Loading } from "@element-plus/icons-vue";
import { ElMessageBox } from "element-plus";
import ComIconService from "@/soa/com-series/service/ComIconService";
import OpSchemaPreviewService from "@/views/assembly/service/OpSchemaPreviewService";
import { ref } from "vue";
import type { GetOpBluePrintListVo, GetOpSchemaListVo } from "@/views/assembly/api/OpSchemaApi";
import ComDirectRouteContext from "@/soa/com-series/service/ComDirectRouteContext";
import { Splitpanes, Pane } from "splitpanes";
import "splitpanes/dist/splitpanes.css";

//图标服务
const { resolveIcon } = ComIconService.useIconService();
const CloseIcon = resolveIcon("fontisto:close");
const RefreshIcon = resolveIcon("el:download");
const ExecuteIcon = resolveIcon("game-icons:nuclear-bomb");
const CheckAllIcon = resolveIcon("ep:check");
const ClearCheckedIcon = resolveIcon("ep:circle-close");
const DesignIcon = resolveIcon("edit");

//CDRC上下文服务
const { getCdrcQuery, cdrcReturn, cdrcCanReturn, cdrcRedirect } = ComDirectRouteContext.useDirectRouteContext();
const cdrcRow = getCdrcQuery() as GetOpSchemaListVo;

//当前选中蓝图的key 如果是QBE模型，则为__qbe_model__ 如果是蓝图，则为蓝图的sha256Hex
const selectedKey = ref<string>("");

//当前选中的蓝图
const selectedBlueprint = ref<GetOpBluePrintListVo | null>(null);

/**
 * 选中蓝图
 * @param item 蓝图VO
 */
const onBlueprintSelect = (item: GetOpBluePrintListVo): void => {
  selectedKey.value = item.sha256Hex;
  selectedBlueprint.value = item;
  previewBlueprint(item, cdrcRow.id);
};

/**
 * 选中QBE模型
 * @param opSchemaId 输出方案ID
 */
const onQbeModelSelect = (opSchemaId: string): void => {
  selectedKey.value = "__qbe_model__";
  selectedBlueprint.value = null;
  previewQbeModel(opSchemaId);
};

//蓝图列表服务打包
const {
  listLoading,
  blueprintList,
  showRawName,
  checkedBlueprints,
  loadBlueprintList,
  toggleCheckedBlueprint,
  checkedAll,
  clearSelected,
} = OpSchemaPreviewService.useBlueprintList(cdrcRow, cdrcReturn);

/**
 * 蓝图预览打包
 */
const {
  codeContent,
  rawHtml,
  detectedLanguage,
  previewLoading,
  previewFileName,
  previewBlueprintDeleted,
  previewBlueprint,
  previewQbeModel,
  onCopy,
} = OpSchemaPreviewService.useBlueprintPreview();

//操作栏打包
const {
  refreshBlueprint,
  executeSelectedBlueprint,
  executionVisible,
  executionLoading,
  executionFinished,
  executionError,
  executionSteps,
} = OpSchemaPreviewService.useActionBar(
  cdrcRow,
  selectedKey,
  selectedBlueprint,
  loadBlueprintList,
  previewBlueprint,
  previewQbeModel
);

const goToDesign = (): void => {
  cdrcRedirect("op-schema-design", cdrcRow);
};

const goToList = (): void => {
  cdrcRedirect("op-schema-manager", cdrcRow);
};

/**
 * 执行已选蓝图 胶水代码
 */
const onExecute = (): void => {
  if (checkedBlueprints.value.length === 0) {
    ElMessageBox.alert("请先勾选至少一个蓝图文件", "提示", { confirmButtonText: "确定" });
    return;
  }
  ElMessageBox.confirm(`确认执行已选的 ${checkedBlueprints.value.length} 个蓝图？此操作将推送至SCM，请谨慎操作。`, "执行确认", {
    confirmButtonText: "确认执行",
    cancelButtonText: "取消",
    type: "warning",
  }).then(() => {
    executeSelectedBlueprint(checkedBlueprints.value);
  });
};
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

.schema-info-bar {
  display: flex;
  align-items: center;
  padding: 10px 16px;
  background: #fff;
  border-radius: 0;
  border-bottom: 1px solid #e4e7ed;
  border-top: 2px solid var(--el-color-primary);
  flex-shrink: 0;
}

.schema-info-item {
  display: flex;
  flex-direction: column;
  gap: 2px;
  padding: 0 20px;
}

.schema-info-label {
  font-size: 11px;
  color: #909399;
  letter-spacing: 0.5px;
  height: 25px;
}

.schema-info-value {
  font-size: 15px;
  font-weight: 600;
  color: #303133;
}

.schema-info-divider {
  width: 1px;
  height: 36px;
  background: #e4e7ed;
  flex-shrink: 0;
}

.action-bar {
  display: flex;
  margin-bottom: 0;
  border-bottom: 1px solid #e4e7ed;
  flex-shrink: 0;
}

.action-bar :deep(.el-button.is-link) {
  padding: 8px 16px;
  border-radius: 0;
  transition:
    background-color 0.2s,
    color 0.2s;
}

.action-bar :deep(.el-button.is-link:hover) {
  background-color: var(--el-color-primary-light-9);
  color: var(--el-color-primary);
}

.main-content {
  flex: 1;
  overflow: hidden;
  padding: 5px;
  padding-top: 0;
}

/* 左侧面板 */
.left-panel {
  height: 100%;
  display: flex;
  flex-direction: column;
  background-color: #ffffff;
  overflow: hidden;
  box-sizing: border-box;
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
  border-radius: 0;
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

.list-divider {
  height: 1px;
  background-color: #e4e7ed;
  margin: 4px 0;
}

/* 右侧代码预览 */
.right-panel {
  height: 100%;
  overflow: hidden;
  border-radius: 0;
  border: 1px solid #e4e7ed;
  box-sizing: border-box;
}

/* splitpanes 自定义主题 */
:deep(.splitpanes.custom-theme) {
  border: none;
  height: 100%;
}

:deep(.splitpanes.custom-theme .splitpanes__pane) {
  background-color: transparent;
}

:deep(.splitpanes.custom-theme .splitpanes__splitter) {
  background-color: var(--el-border-color-extra-light);
  width: 1px;
  border: none;
  cursor: col-resize;
  position: relative;
  transition: background-color 0.2s;
}

:deep(.splitpanes.custom-theme .splitpanes__splitter:hover) {
  background-color: var(--el-color-primary);
  width: 3px;
}

:deep(.splitpanes.custom-theme .splitpanes__splitter:after) {
  content: "";
  position: absolute;
  left: -5px;
  right: -5px;
  top: 0;
  bottom: 0;
  z-index: 1;
}

:deep(.splitpanes__pane) {
  transition: none !important;
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
  border-radius: 0;
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

/* 执行进度模态框样式 */
.execution-content {
  padding: 10px 0;
}

.execution-warning {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 20px;
  color: #e6a23c;
  font-size: 14px;
  background: #fdf6ec;
  padding: 10px 15px;
  border-radius: 0;
}

.execution-steps {
  display: flex;
  flex-direction: column;
  gap: 12px;
  margin-bottom: 20px;
}

.step-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 10px 15px;
  border-radius: 0;
  background: #f8f9fa;
  border-left: 4px solid #dcdfe6;
  color: #606266;
}

.step-icon {
  font-size: 18px;
  display: flex;
  align-items: center;
}

.step-title {
  font-size: 14px;
  font-weight: 500;
}

.execution-error-msg {
  margin-top: 15px;
}

.execution-success-msg {
  margin-top: 10px;
}

.execution-success-msg :deep(.el-result) {
  padding: 10px 0;
}

/* 全局直角风格覆盖 */
:deep(.el-button),
:deep(.el-input__wrapper),
:deep(.el-textarea__inner),
:deep(.el-select__wrapper),
:deep(.el-dialog),
:deep(.el-card),
:deep(.el-alert) {
  border-radius: 0 !important;
}
</style>
