<template>
  <div class="filter-editor" v-loading="loading">
    <!-- Loading 遮罩 -->
    <div v-show="globalLoading" class="sf-loading-overlay">
      <div class="sf-loading-spinner"></div>
      <div class="sf-loading-text">正在处理...</div>
    </div>

    <div v-if="!formData && !filterStore.isCreating" class="empty-state">
      <el-empty description="无法加载过滤器信息" />
    </div>

    <div v-if="formData || filterStore.isCreating" class="editor-content">
      <!-- 主编辑卡片 -->
      <el-card class="main-card">
        <template #header>
          <div class="card-header">
            <span v-show="!filterStore.isCreating"
              ><el-icon><Edit /></el-icon> 配置基本过滤器 <span class="gradient-text gradient-blue">{{ formData.name }}</span>
            </span>
            <span v-show="filterStore.isCreating">创建基本过滤器</span>
            <div>
              <el-button type="primary" @click="loadFilterDetails" v-show="!filterStore.isCreating">加载</el-button>
              <el-button type="primary" @click="saveFilter" :loading="saving" v-show="!filterStore.isCreating">保存过滤器</el-button>
              <el-button type="danger" @click="cancelCreate" :loading="saving" v-show="filterStore.isCreating">取消创建</el-button>
              <el-button type="primary" @click="addFilter" :loading="saving" v-show="filterStore.isCreating">创建新的基本过滤器</el-button>
            </div>
          </div>
        </template>

        <div class="card-content">
          <!-- 基础信息 -->
          <div class="section">
            <div class="section-title"></div>
            <el-form :model="formData" label-width="120px">
              <el-form-item label="过滤器名称">
                <el-input v-model="formData.name" placeholder="请输入过滤器名称" />
              </el-form-item>

              <el-form-item label="过滤器方向">
                <el-select v-model="formData.direction" placeholder="请选择方向">
                  <el-option label="请求过滤器" :value="0" />
                  <el-option label="响应过滤器" :value="1" />
                </el-select>
              </el-form-item>

              <el-form-item label="状态">
                <el-select v-model="formData.status" placeholder="请选择状态">
                  <el-option label="启用" :value="0" />
                  <el-option label="禁用" :value="1" />
                </el-select>
              </el-form-item>
            </el-form>
          </div>

          <el-divider />

          <!-- 触发器列表 -->
          <div class="section">
            <div class="section-header">
              <div class="section-title">
                在此过滤器上配置的触发器 ({{ formData.triggers.length }})
                <el-tooltip content="触发器用于判断什么条件下执行此过滤器。可以根据请求的标头、载荷、URL或HTTP方法来设置触发条件。" placement="top">
                  <IFeQuestion style="vertical-align: -18%; color: #0095ff; cursor: pointer" />
                </el-tooltip>
              </div>
              <el-button type="primary" @click="() => triggerFormRef?.openWithAdd()">新增触发器</el-button>
            </div>

            <el-table :data="formData.triggers" :border="true">
              <el-table-column prop="name" label="名称" />
              <el-table-column prop="target" label="目标">
                <template #default="{ row }">
                  <span v-show="row.target === 0" class="gradient-text gradient-blue">标头</span>
                  <span v-show="row.target === 1" class="gradient-text gradient-green">JSON载荷</span>
                  <span v-show="row.target === 2" class="gradient-text gradient-purple">URL</span>
                  <span v-show="row.target === 3" class="gradient-text gradient-orange">HTTP方法</span>
                </template>
              </el-table-column>
              <el-table-column prop="kind" label="条件">
                <template #default="{ row }">
                  <span v-show="row.kind === 0" class="gradient-text gradient-cyan">包含</span>
                  <span v-show="row.kind === 1" class="gradient-text gradient-red">不包含</span>
                  <span v-show="row.kind === 2" class="gradient-text gradient-teal">等于</span>
                  <span v-show="row.kind === 3" class="gradient-text gradient-pink">不等于</span>
                </template>
              </el-table-column>
              <el-table-column prop="tk" label="目标键" />
              <el-table-column prop="tv" label="比较值" />
              <el-table-column label="操作">
                <template #default="{ row, $index }">
                  <el-button type="primary" size="small" @click="() => triggerFormRef?.openWithEdit(formData.triggers[$index], $index)"> 编辑 </el-button>
                  <el-button type="danger" size="small" @click="removeTrigger($index)"> 删除 </el-button>
                </template>
              </el-table-column>
            </el-table>
          </div>

          <el-divider />

          <!-- 操作列表 -->
          <div class="section">
            <div class="section-header">
              <div class="section-title">
                在此过滤器上配置的操作 ({{ formData.operations.length }})
                <el-tooltip content="操作定义了过滤器触发后要执行的动作。可以对数据进行持久化、缓存、注入或URL覆写等操作。" placement="top">
                  <IFeQuestion style="vertical-align: -18%; color: #0095ff; cursor: pointer" />
                </el-tooltip>
              </div>
              <el-button type="primary" @click="() => operationFormRef?.openWithAdd()">新增操作</el-button>
            </div>

            <el-table :data="formData.operations" :border="true">
              <el-table-column prop="name" label="名称" />
              <el-table-column prop="kind" label="类型">
                <template #default="{ row }">
                  <span v-show="row.kind === 0" class="gradient-text gradient-indigo">持久化</span>
                  <span v-show="row.kind === 1" class="gradient-text gradient-emerald">缓存</span>
                  <span v-show="row.kind === 2" class="gradient-text gradient-amber">注入缓存</span>
                  <span v-show="row.kind === 3" class="gradient-text gradient-violet">注入持久化</span>
                  <span v-show="row.kind === 4" class="gradient-text gradient-rose">覆写URL</span>
                </template>
              </el-table-column>
              <el-table-column prop="target" label="目标">
                <template #default="{ row }">
                  <span v-show="row.target === 0" class="gradient-text gradient-blue">标头</span>
                  <span v-show="row.target === 1" class="gradient-text gradient-green">JSON载荷</span>
                  <span v-show="row.target === 2" class="gradient-text gradient-purple">URL</span>
                </template>
              </el-table-column>
              <el-table-column prop="f" label="原始键" />
              <el-table-column prop="t" label="目标键" />
              <el-table-column label="操作">
                <template #default="{ row, $index }">
                  <el-button type="primary" size="small" @click="() => operationFormRef?.openWithEdit(formData.operations[$index], $index)"> 编辑 </el-button>
                  <el-button type="danger" size="small" @click="removeOperation($index)"> 删除 </el-button>
                </template>
              </el-table-column>
            </el-table>
          </div>
        </div>
      </el-card>
    </div>

    <!-- 触发器编辑对话框 -->
    <SimpleFilterFormTrigger ref="triggerFormRef" @add="onTriggerFormAdd" @edit="onTriggerFormEdit" />

    <!-- 操作编辑对话框 -->
    <SimpleFilterFormOperation ref="operationFormRef" @add="onOperationFormAdd" @edit="onOperationFormEdit" />
  </div>
</template>

<script setup lang="ts">
import { SimpleFilterStore } from "@/store/SimpleFilterStore";
import { onMounted, ref, watch } from "vue";
import { ElMessage, ElMessageBox } from "element-plus";
import SimpleFilterApi from "@/api/SimpleFilterApi";
import type {
  GetSimpleFilterDetailsVo,
  EditSimpleFilterTriggerDto,
  EditSimpleFilterOperationDto,
  AddSimpleFilterTriggerDto,
  AddSimpleFilterOperationDto,
} from "@/api/SimpleFilterApi";
import SimpleFilterFormTrigger from "./SimpleFilterFormTrigger.vue";
import SimpleFilterFormOperation from "./SimpleFilterFormOperation.vue";

const filterStore = SimpleFilterStore();
const loading = ref(false);
const saving = ref(false);
const globalLoading = ref(false);
const triggerFormRef = ref<InstanceType<typeof SimpleFilterFormTrigger>>();
const operationFormRef = ref<InstanceType<typeof SimpleFilterFormOperation>>();

//核心表单
const formData = ref<GetSimpleFilterDetailsVo>({
  id: "",
  name: "",
  direction: 0,
  status: 0,
  triggers: [],
  operations: [],
  updateTimeEpochMill: "",
});

// 加载过滤器详情
const loadFilterDetails = async () => {
  const selectedFilterId = filterStore.getSelectedFilterId;
  if (!selectedFilterId) {
    return;
  }

  globalLoading.value = true;
  try {
    const filter = await SimpleFilterApi.getSimpleFilterDetails({ id: selectedFilterId });

    //组装局部数据
    const incomingFormdata: GetSimpleFilterDetailsVo = {
      id: filter.id,
      name: filter.name,
      direction: filter.direction,
      status: filter.status,
      triggers: filter.triggers.map((t) => ({
        id: t.id,
        name: t.name,
        target: t.target,
        kind: t.kind,
        tk: t.tk,
        tv: t.tv,
        seq: t.seq,
      })),
      operations: filter.operations.map((o) => ({
        id: o.id,
        name: o.name,
        kind: o.kind,
        target: o.target,
        f: o.f,
        t: o.t,
        seq: o.seq,
      })),
      updateTimeEpochMill: filter.updateTimeEpochMill,
    };

    //替换核心表单数据
    formData.value = incomingFormdata;
  } catch (error) {
    ElMessage.error("加载过滤器详情失败");
    console.error(error);
  } finally {
    globalLoading.value = false;
  }
};

/**
 * 触发器模态框相关操作
 */
const onTriggerFormAdd = (data: AddSimpleFilterTriggerDto) => {
  formData.value.triggers.push({
    id: null,
    name: data.name,
    target: data.target,
    kind: data.kind,
    tk: data.tk,
    tv: data.tv,
    seq: 0,
  });
};

const onTriggerFormEdit = (data: EditSimpleFilterTriggerDto, idx: number) => {
  formData.value.triggers[idx] = {
    id: data.id,
    name: data.name,
    target: data.target,
    kind: data.kind,
    tk: data.tk,
    tv: data.tv,
    seq: 0,
  };
};

// 删除触发器
const removeTrigger = (index: number) => {
  ElMessageBox.confirm("确定删除该触发器吗？", "提示", {
    confirmButtonText: "确定",
    cancelButtonText: "取消",
    type: "warning",
  }).then(() => {
    formData.value.triggers.splice(index, 1);
  });
};

/**
 * 操作模态框相关操作
 */
const onOperationFormAdd = (data: AddSimpleFilterOperationDto) => {
  formData.value.operations.push({
    id: null,
    name: data.name,
    kind: data.kind,
    target: data.target,
    f: data.f,
    t: data.t,
    seq: 0,
  });
};

const onOperationFormEdit = (data: EditSimpleFilterOperationDto, idx: number) => {
  formData.value.operations[idx] = {
    id: data.id,
    name: data.name,
    kind: data.kind,
    target: data.target,
    f: data.f,
    t: data.t,
    seq: 0,
  };
};

// 删除操作
const removeOperation = (index: number) => {
  ElMessageBox.confirm("确定删除该操作吗？", "提示", {
    confirmButtonText: "确定",
    cancelButtonText: "取消",
    type: "warning",
  }).then(() => {
    formData.value.operations.splice(index, 1);
  });
};

// 创建新的基本过滤器
const addFilter = async () => {
  //过滤器至少有一个触发器
  if (formData.value.triggers.length === 0) {
    ElMessage.error("过滤器至少需要配置一个触发器");
    return;
  }

  //过滤器至少有一个操作
  if (formData.value.operations.length === 0) {
    ElMessage.error("过滤器至少需要配置一个操作");
    return;
  }

  globalLoading.value = true;
  //创建过滤器
  try {
    const newFilterId: string = await SimpleFilterApi.addSimpleFilter({
      name: formData.value.name,
      direction: formData.value.direction,
      status: formData.value.status,
      triggers: formData.value.triggers,
      operations: formData.value.operations,
    });
    filterStore.setSelectedFilterId(newFilterId);
    filterStore.setIsCreating(false);
    ElMessage.success("创建过滤器成功");
    clearForm();
  } catch (error) {
    ElMessage.error("创建过滤器失败");
    console.error(error);
  } finally {
    globalLoading.value = false;
  }
};

// 保存过滤器
const saveFilter = async () => {
  //过滤器至少有一个触发器
  if (formData.value.triggers.length === 0) {
    ElMessage.error("过滤器至少需要配置一个触发器");
    return;
  }

  //过滤器至少有一个操作
  if (formData.value.operations.length === 0) {
    ElMessage.error("过滤器至少需要配置一个操作");
    return;
  }

  globalLoading.value = true;
  try {
    await SimpleFilterApi.editSimpleFilter({
      id: formData.value.id,
      name: formData.value.name,
      direction: formData.value.direction,
      status: formData.value.status,
      triggers: formData.value.triggers,
      operations: formData.value.operations,
      updateTimeEpochMill: formData.value.updateTimeEpochMill,
    });
    ElMessage.success("保存过滤器成功");
    clearForm();
    loadFilterDetails();

    //通知列表重新加载
    filterStore.requestReloadList();
  } catch (error) {
    ElMessage.error("保存过滤器失败:" + error);
    console.error(error);
  } finally {
    globalLoading.value = false;
  }
};

const cancelCreate = () => {
  filterStore.setIsCreating(false);
  clearForm();
  //重新获取详情
  loadFilterDetails();
  filterStore.requestReloadList();
};

onMounted(() => {
  loadFilterDetails();
});

const clearForm = () => {
  formData.value = {
    id: "",
    name: "",
    direction: 0,
    status: 0,
    triggers: [],
    operations: [],
    updateTimeEpochMill: "",
  };
};

watch(
  () => filterStore.isCreating,
  () => {
    clearForm();
  }
);

// 监听选中的过滤器ID变化
watch(
  () => filterStore.getSelectedFilterId,
  () => {
    //如果选中的过滤器为空，则清空表单
    if (!filterStore.getSelectedFilterId) {
      clearForm();
      return;
    }
    loadFilterDetails();
  }
);
</script>

<style scoped>
.filter-editor {
  position: relative;
  height: 100%;
  overflow: hidden;
  display: flex;
  flex-direction: column;
}

.empty-state {
  height: 100%;
  display: flex;
  justify-content: center;
  align-items: center;
}

.editor-content {
  max-width: 1200px;
  flex: 1;
  overflow: hidden;
}

.main-card {
  height: 100%;
  display: flex;
  flex-direction: column;
}

.main-card :deep(.el-card__body) {
  flex: 1;
  overflow: hidden;
  padding: 0;
}

.card-content {
  height: calc(100% - 50px);
  overflow-y: auto;
  padding: 20px;
  padding-bottom: 500px;
  /* 修复快速滚动出现的绘制错位问题：为滚动容器创建独立合成层并限制重绘范围 */
  will-change: transform;
  transform: translateZ(0);
  backface-visibility: hidden;
  contain: paint;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  width: 100%;
}

.section {
  margin-bottom: 20px;
}

.section:last-child {
  margin-bottom: 0;
}

.section-title {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 16px;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 5px;
}

/* 彩色文字样式 */
.gradient-text {
  font-weight: 600;
}

.gradient-blue {
  color: #4f46e5;
}

.gradient-green {
  color: #059669;
}

.gradient-purple {
  color: #7c3aed;
}

.gradient-orange {
  color: #ea580c;
}

.gradient-cyan {
  color: #0891b2;
}

.gradient-red {
  color: #dc2626;
}

.gradient-teal {
  color: #0d9488;
}

.gradient-pink {
  color: #db2777;
}

.gradient-indigo {
  color: #3730a3;
}

.gradient-emerald {
  color: #047857;
}

.gradient-amber {
  color: #d97706;
}

.gradient-violet {
  color: #6d28d9;
}

.gradient-rose {
  color: #be185d;
}

/* Loading 遮罩样式 */
.sf-loading-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(255, 255, 255, 0.8);
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  z-index: 100000;
}

.sf-loading-spinner {
  width: 40px;
  height: 40px;
  border: 4px solid #f3f3f3;
  border-top: 4px solid #007bff;
  border-radius: 50%;
  animation: sf-spin 1s linear infinite;
}

.sf-loading-text {
  margin-top: 16px;
  color: #495057;
  font-size: 14px;
}

@keyframes sf-spin {
  0% {
    transform: rotate(0deg);
  }
  100% {
    transform: rotate(360deg);
  }
}
</style>
