<script setup lang="ts">
import { computed, onBeforeUnmount, ref, watch } from "vue";
import { is } from "bpmn-js/lib/util/ModelUtil";
import { ElMessage } from "element-plus";

const props = defineProps<{
  modeler: unknown;
}>();

const selected = ref<unknown>(null);

function getModeler(): any {
  return props.modeler;
}

function findProcessElement(m: any): unknown {
  const registry = m.get("elementRegistry");
  const list = registry.getAll() as Array<{ type: string; businessObject: unknown }>;
  const direct = list.find((e) => e.type === "bpmn:Process");
  if (direct) {
    return direct;
  }
  return list.find((e) => is(e.businessObject, "bpmn:Process")) ?? null;
}

const targetElement = computed(() => {
  const m = getModeler();
  if (!m) {
    return null;
  }
  const el = selected.value as { type?: string } | null;
  if (!el) {
    return findProcessElement(m);
  }
  if (el.type === "label") {
    return findProcessElement(m);
  }
  return el;
});

const bo = computed(() => {
  const el = targetElement.value as { businessObject?: Record<string, unknown> } | null;
  if (!el?.businessObject) {
    return null;
  }
  return el.businessObject;
});

const elementType = computed(() => (bo.value?.$type as string) || "");

/** UserTask 已在专属区块展示异步；其余带 AsyncCapable 的类型单独展示 */
const isFlowableAsyncOnly = computed(() => {
  const t = elementType.value;
  if (!t) {
    return false;
  }
  if (t === "bpmn:UserTask") {
    return false;
  }
  const asyncTypes = new Set([
    "bpmn:ServiceTask",
    "bpmn:ScriptTask",
    "bpmn:ReceiveTask",
    "bpmn:ManualTask",
    "bpmn:BusinessRuleTask",
    "bpmn:SendTask",
    "bpmn:CallActivity",
    "bpmn:SubProcess",
    "bpmn:ExclusiveGateway",
    "bpmn:ParallelGateway",
    "bpmn:InclusiveGateway",
    "bpmn:EventBasedGateway",
    "bpmn:StartEvent",
    "bpmn:EndEvent",
    "bpmn:IntermediateCatchEvent",
    "bpmn:IntermediateThrowEvent",
    "bpmn:BoundaryEvent",
  ]);
  return asyncTypes.has(t);
});

const form = ref({
  id: "",
  name: "",
  candidateStarterUsers: "",
  candidateStarterGroups: "",
  versionTag: "",
  historyTimeToLive: "",
  assignee: "",
  candidateUsers: "",
  candidateGroups: "",
  dueDate: "",
  followUpDate: "",
  priority: "",
  formKey: "",
  initiator: "",
  async: false,
  asyncBefore: false,
  asyncAfter: false,
  exclusive: true,
});

function loadFormFromBo(): void {
  const b = bo.value;
  if (!b) {
    return;
  }
  form.value.id = (b.id as string) || "";
  form.value.name = (b.name as string) || "";
  form.value.candidateStarterUsers = (b.candidateStarterUsers as string) || "";
  form.value.candidateStarterGroups = (b.candidateStarterGroups as string) || "";
  form.value.versionTag = (b.versionTag as string) || "";
  form.value.historyTimeToLive = (b.historyTimeToLive as string) || "";
  form.value.assignee = (b.assignee as string) || "";
  form.value.candidateUsers = (b.candidateUsers as string) || "";
  form.value.candidateGroups = (b.candidateGroups as string) || "";
  form.value.dueDate = (b.dueDate as string) || "";
  form.value.followUpDate = (b.followUpDate as string) || "";
  form.value.priority = (b.priority as string) || "";
  form.value.formKey = (b.formKey as string) || "";
  form.value.initiator = (b.initiator as string) || "";
  form.value.async = Boolean(b.async);
  form.value.asyncBefore = Boolean(b.asyncBefore);
  form.value.asyncAfter = Boolean(b.asyncAfter);
  form.value.exclusive = b.exclusive !== false;
}

watch([bo, elementType], () => {
  loadFormFromBo();
});

let selectionOff: (() => void) | null = null;

watch(
  () => props.modeler,
  (m) => {
    if (selectionOff) {
      selectionOff();
      selectionOff = null;
    }
    if (!m) {
      selected.value = null;
      return;
    }
    const eventBus = (m as any).get("eventBus");
    const handler = (e: { newSelection: unknown[] }): void => {
      selected.value = e.newSelection?.[0] ?? null;
    };
    eventBus.on("selection.changed", handler);
    selectionOff = () => {
      eventBus.off("selection.changed", handler);
    };
  },
  { immediate: true },
);

onBeforeUnmount(() => {
  if (selectionOff) {
    selectionOff();
  }
});

function applyProps(patch: Record<string, unknown>): void {
  const m = getModeler();
  const el = targetElement.value as { id?: string } | null;
  if (!m || !el) {
    return;
  }
  const modeling = m.get("modeling");
  modeling.updateProperties(el, patch);
}

function onIdCommit(): void {
  const m = getModeler();
  if (!m) {
    return;
  }
  const el = targetElement.value as { id?: string } | null;
  if (!el?.id) {
    return;
  }
  const newId = form.value.id?.trim();
  if (!newId) {
    ElMessage.warning("ID 不能为空");
    loadFormFromBo();
    return;
  }
  if (newId === el.id) {
    return;
  }
  const modeling = m.get("modeling");
  modeling.updateProperties(el, { id: newId });
}

function onNameCommit(): void {
  applyProps({ name: form.value.name });
}

function onProcessExtrasCommit(): void {
  applyProps({
    candidateStarterUsers: form.value.candidateStarterUsers || undefined,
    candidateStarterGroups: form.value.candidateStarterGroups || undefined,
    versionTag: form.value.versionTag || undefined,
    historyTimeToLive: form.value.historyTimeToLive || undefined,
  });
}

function onUserTaskCommit(): void {
  applyProps({
    assignee: form.value.assignee || undefined,
    candidateUsers: form.value.candidateUsers || undefined,
    candidateGroups: form.value.candidateGroups || undefined,
    dueDate: form.value.dueDate || undefined,
    followUpDate: form.value.followUpDate || undefined,
    priority: form.value.priority || undefined,
  });
}

function onAsyncCommit(): void {
  applyProps({
    async: form.value.async,
    asyncBefore: form.value.asyncBefore,
    asyncAfter: form.value.asyncAfter,
    exclusive: form.value.exclusive,
  });
}

function onStartEventCommit(): void {
  applyProps({
    formKey: form.value.formKey || undefined,
    initiator: form.value.initiator || undefined,
  });
}
</script>

<template>
  <div class="flowable-property-panel">
    <div class="flowable-property-panel__title">属性</div>
    <div v-if="!modeler" class="flowable-property-panel__empty">设计器未就绪</div>
    <el-form v-else-if="bo" label-position="top" size="small" class="flowable-property-panel__form">
      <el-divider content-position="left">基础</el-divider>
      <el-form-item label="类型">
        <el-input :model-value="elementType" disabled />
      </el-form-item>
      <el-form-item label="ID">
        <el-input v-model="form.id" @change="onIdCommit" />
      </el-form-item>
      <el-form-item label="名称">
        <el-input v-model="form.name" @change="onNameCommit" />
      </el-form-item>

      <template v-if="elementType === 'bpmn:Process'">
        <el-divider content-position="left">Flowable / 流程</el-divider>
        <el-form-item label="候选启动用户">
          <el-input v-model="form.candidateStarterUsers" placeholder="逗号分隔" @change="onProcessExtrasCommit" />
        </el-form-item>
        <el-form-item label="候选启动组">
          <el-input v-model="form.candidateStarterGroups" placeholder="逗号分隔" @change="onProcessExtrasCommit" />
        </el-form-item>
        <el-form-item label="版本标签">
          <el-input v-model="form.versionTag" @change="onProcessExtrasCommit" />
        </el-form-item>
        <el-form-item label="历史保留时间">
          <el-input v-model="form.historyTimeToLive" @change="onProcessExtrasCommit" />
        </el-form-item>
      </template>

      <template v-if="elementType === 'bpmn:UserTask'">
        <el-divider content-position="left">Flowable / 用户任务</el-divider>
        <el-form-item label="办理人">
          <el-input v-model="form.assignee" @change="onUserTaskCommit" />
        </el-form-item>
        <el-form-item label="候选用户">
          <el-input v-model="form.candidateUsers" placeholder="逗号分隔" @change="onUserTaskCommit" />
        </el-form-item>
        <el-form-item label="候选组">
          <el-input v-model="form.candidateGroups" placeholder="逗号分隔" @change="onUserTaskCommit" />
        </el-form-item>
        <el-form-item label="到期时间">
          <el-input v-model="form.dueDate" @change="onUserTaskCommit" />
        </el-form-item>
        <el-form-item label="跟进时间">
          <el-input v-model="form.followUpDate" @change="onUserTaskCommit" />
        </el-form-item>
        <el-form-item label="优先级">
          <el-input v-model="form.priority" @change="onUserTaskCommit" />
        </el-form-item>
        <el-divider content-position="left">Flowable / 异步</el-divider>
        <el-form-item label="异步">
          <el-switch v-model="form.async" @change="onAsyncCommit" />
        </el-form-item>
        <el-form-item label="异步前">
          <el-switch v-model="form.asyncBefore" @change="onAsyncCommit" />
        </el-form-item>
        <el-form-item label="异步后">
          <el-switch v-model="form.asyncAfter" @change="onAsyncCommit" />
        </el-form-item>
        <el-form-item label="独占">
          <el-switch v-model="form.exclusive" @change="onAsyncCommit" />
        </el-form-item>
      </template>

      <template v-if="elementType === 'bpmn:StartEvent'">
        <el-divider content-position="left">Flowable / 开始事件</el-divider>
        <el-form-item label="表单 Key">
          <el-input v-model="form.formKey" @change="onStartEventCommit" />
        </el-form-item>
        <el-form-item label="发起人变量">
          <el-input v-model="form.initiator" @change="onStartEventCommit" />
        </el-form-item>
      </template>

      <template v-if="isFlowableAsyncOnly">
        <el-divider content-position="left">Flowable / 异步</el-divider>
        <el-form-item label="异步">
          <el-switch v-model="form.async" @change="onAsyncCommit" />
        </el-form-item>
        <el-form-item label="异步前">
          <el-switch v-model="form.asyncBefore" @change="onAsyncCommit" />
        </el-form-item>
        <el-form-item label="异步后">
          <el-switch v-model="form.asyncAfter" @change="onAsyncCommit" />
        </el-form-item>
        <el-form-item label="独占">
          <el-switch v-model="form.exclusive" @change="onAsyncCommit" />
        </el-form-item>
      </template>
    </el-form>
    <div v-else class="flowable-property-panel__empty">未选中元素</div>
  </div>
</template>

<style scoped>
.flowable-property-panel {
  height: 100%;
  display: flex;
  flex-direction: column;
  min-height: 0;
  padding: 8px 12px;
  box-sizing: border-box;
}
.flowable-property-panel__title {
  font-weight: 600;
  margin-bottom: 8px;
}
.flowable-property-panel__form {
  flex: 1;
  overflow: auto;
}
.flowable-property-panel__empty {
  color: var(--el-text-color-secondary);
  font-size: 13px;
}
</style>
