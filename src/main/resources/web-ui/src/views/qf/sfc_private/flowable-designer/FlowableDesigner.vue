<script setup lang="ts">
import { nextTick, onBeforeUnmount, onMounted, ref } from "vue";
import {
  ZoomIn,
  ZoomOut,
  RefreshLeft,
  RefreshRight,
  Refresh,
  Download,
  FolderOpened,
  ScaleToOriginal,
  Document,
  Cpu,
} from "@element-plus/icons-vue";
import { ElMessage } from "element-plus";
import { Splitpanes, Pane } from "splitpanes";
import "splitpanes/dist/splitpanes.css";
import "bpmn-js/dist/assets/diagram-js.css";
import "bpmn-js/dist/assets/bpmn-font/css/bpmn.css";
import { useFlowableModeler } from "@/views/qf/sfc_private/flowable-designer/useFlowableModeler";
import FlowablePropertyPanel from "@/views/qf/sfc_private/flowable-designer/FlowablePropertyPanel.vue";

const props = withDefaults(
  defineProps<{
    /** 初始 BPMN XML，空则新建空白流程 */
    initialXml?: string;
  }>(),
  {
    initialXml: "",
  },
);

const emit = defineEmits<{
  (e: "update:xml", xml: string): void;
  (e: "save", xml: string): void;
}>();

const canvasRef = ref<HTMLElement | null>(null);
const fileInputRef = ref<HTMLInputElement | null>(null);

const {
  modeler,
  defaultZoom,
  revocable,
  recoverable,
  init,
  importXml,
  saveXml,
  saveSvg,
  zoomIn,
  zoomOut,
  zoomFit,
  undo,
  redo,
  newDiagram,
} = useFlowableModeler(canvasRef);

function setEncoded(type: string, filename: string, data: string): { filename: string; href: string } {
  const encodedData = encodeURIComponent(data);
  return {
    filename: `${filename}.${type}`,
    href: `data:application/${type === "svg" ? "svg+xml" : "bpmn20-xml"};charset=UTF-8,${encodedData}`,
  };
}

function downloadFunc(href: string, filename: string): void {
  if (!href || !filename) {
    return;
  }
  const a = document.createElement("a");
  a.download = filename;
  a.href = href;
  a.click();
  URL.revokeObjectURL(a.href);
}

async function emitXmlChanged(): Promise<void> {
  const xml = await saveXml(true);
  if (!xml) {
    return;
  }
  emit("update:xml", xml);
}

let offCommandStack: (() => void) | null = null;

async function bindStackListener(): Promise<void> {
  const m = modeler.value as any;
  if (!m) {
    return;
  }
  const eventBus = m.get("eventBus");
  eventBus.on("commandStack.changed", emitXmlChanged);
  offCommandStack = () => {
    eventBus.off("commandStack.changed", emitXmlChanged);
  };
}

async function onSave(): Promise<void> {
  const xml = await saveXml(true);
  if (!xml) {
    return;
  }
  emit("save", xml);
  ElMessage.success("已生成流程 XML");
}

async function downloadXml(): Promise<void> {
  const xml = await saveXml(true);
  if (!xml) {
    return;
  }
  const { href, filename } = setEncoded("bpmn", "diagram", xml);
  downloadFunc(href, filename);
}

async function downloadSvg(): Promise<void> {
  const svg = await saveSvg();
  if (!svg) {
    return;
  }
  const { href, filename } = setEncoded("svg", "diagram", svg);
  downloadFunc(href, filename);
}

function onImportClick(): void {
  fileInputRef.value?.click();
}

function onFileChange(): void {
  const input = fileInputRef.value;
  if (!input?.files?.length) {
    return;
  }
  const file = input.files[0];
  const reader = new FileReader();
  reader.onload = () => {
    const text = reader.result as string;
    importXml(text)
      .then(() => emitXmlChanged())
      .catch(() => ElMessage.error("导入失败"));
    input.value = "";
  };
  reader.readAsText(file);
}

function onRestart(): void {
  const pid = `Process_${Date.now()}`;
  const pname = `业务流程_${Date.now()}`;
  newDiagram(pid, pname)
    .then(() => emitXmlChanged())
    .catch(() => ElMessage.error("重置失败"));
}

onMounted(async () => {
  await nextTick();
  init();
  await bindStackListener();
  const initial = props.initialXml?.trim();
  if (initial) {
    await importXml(initial);
    await emitXmlChanged();
    return;
  }
  await newDiagram(`Process_${Date.now()}`, `业务流程_${Date.now()}`);
  await emitXmlChanged();
});

onBeforeUnmount((): void => {
  if (!offCommandStack) {
    return;
  }
  offCommandStack();
  offCommandStack = null;
});
</script>

<template>
  <div class="flowable-designer-root">
    <div class="flowable-designer-toolbar">
      <el-button-group size="small" type="primary">
        <el-button :icon="Document" @click="onSave">保存 XML</el-button>
        <el-button :icon="FolderOpened" @click="onImportClick">导入</el-button>
        <el-button :icon="Download" @click="downloadXml">导出 BPMN</el-button>
        <el-button :icon="Download" @click="downloadSvg">导出 SVG</el-button>
      </el-button-group>
      <el-button-group size="small" class="ml8">
        <el-tooltip content="缩小" placement="bottom">
          <el-button :icon="ZoomOut" :disabled="defaultZoom <= 0.2" @click="zoomOut()" />
        </el-tooltip>
        <el-button disabled>{{ Math.floor(defaultZoom * 100) }}%</el-button>
        <el-tooltip content="放大" placement="bottom">
          <el-button :icon="ZoomIn" :disabled="defaultZoom >= 4" @click="zoomIn()" />
        </el-tooltip>
        <el-tooltip content="适应画布" placement="bottom">
          <el-button :icon="ScaleToOriginal" @click="zoomFit()" />
        </el-tooltip>
      </el-button-group>
      <el-button-group size="small" class="ml8">
        <el-tooltip content="撤销" placement="bottom">
          <el-button :icon="RefreshLeft" :disabled="!revocable" @click="undo()" />
        </el-tooltip>
        <el-tooltip content="重做" placement="bottom">
          <el-button :icon="RefreshRight" :disabled="!recoverable" @click="redo()" />
        </el-tooltip>
        <el-tooltip content="新建空白流程" placement="bottom">
          <el-button :icon="Refresh" @click="onRestart" />
        </el-tooltip>
      </el-button-group>
      <span class="flowable-designer-hint ml8">
        <el-icon><Cpu /></el-icon>
        Flowable（flow 命名空间）
      </span>
    </div>

    <input
      ref="fileInputRef"
      type="file"
      accept=".xml,.bpmn"
      class="flowable-designer-file"
      @change="onFileChange"
    />

    <splitpanes class="flowable-designer-split custom-theme">
      <pane min-size="40" size="70">
        <div ref="canvasRef" class="flowable-designer-canvas" />
      </pane>
      <pane min-size="20" size="30">
        <FlowablePropertyPanel :modeler="modeler" />
      </pane>
    </splitpanes>
  </div>
</template>

<style scoped>
.flowable-designer-root {
  display: flex;
  flex-direction: column;
  height: 100%;
  min-height: 0;
  background: var(--el-bg-color);
}
.flowable-designer-toolbar {
  flex-shrink: 0;
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: 8px;
  padding: 8px 12px;
  border-bottom: 1px solid var(--el-border-color-lighter);
}
.ml8 {
  margin-left: 8px;
}
.flowable-designer-hint {
  font-size: 12px;
  color: var(--el-text-color-secondary);
  display: inline-flex;
  align-items: center;
  gap: 4px;
}
.flowable-designer-file {
  display: none;
}
.flowable-designer-split {
  flex: 1;
  min-height: 0;
}
.flowable-designer-canvas {
  height: 100%;
  min-height: 420px;
}
:deep(.splitpanes.custom-theme .splitpanes__pane) {
  background: var(--el-bg-color-page);
}
:deep(.splitpanes.custom-theme .splitpanes__splitter) {
  background: var(--el-border-color-lighter);
  width: 6px;
}
</style>
