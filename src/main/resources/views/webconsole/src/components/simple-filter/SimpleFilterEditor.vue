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
              ><el-icon><Edit /></el-icon> 配置基本过滤器 <span class="gradient-text gradient-blue">{{ editForm.name }}</span>
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
            <el-form :model="editForm" label-width="120px">
              <el-form-item label="过滤器名称">
                <el-input v-model="editForm.name" placeholder="请输入过滤器名称" />
              </el-form-item>

              <el-form-item label="过滤器方向">
                <el-select v-model="editForm.direction" placeholder="请选择方向">
                  <el-option label="请求过滤器" :value="0" />
                  <el-option label="响应过滤器" :value="1" />
                </el-select>
              </el-form-item>

              <el-form-item label="状态">
                <el-select v-model="editForm.status" placeholder="请选择状态">
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
                在此过滤器上配置的触发器 ({{ editForm.triggers.length }})
                <el-tooltip content="触发器用于判断什么条件下执行此过滤器。可以根据请求的标头、载荷、URL或HTTP方法来设置触发条件。" placement="top">
                  <IFeQuestion style="vertical-align: -18%; color: #0095ff; cursor: pointer" />
                </el-tooltip>
              </div>
              <el-button type="primary" @click="openModalTrigger">新增触发器</el-button>
            </div>

            <el-table :data="editForm.triggers" :border="true">
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
                  <el-button type="primary" size="small" @click="editTrigger($index)"> 编辑 </el-button>
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
                在此过滤器上配置的操作 ({{ editForm.operations.length }})
                <el-tooltip content="操作定义了过滤器触发后要执行的动作。可以对数据进行持久化、缓存、注入或URL覆写等操作。" placement="top">
                  <IFeQuestion style="vertical-align: -18%; color: #0095ff; cursor: pointer" />
                </el-tooltip>
              </div>
              <el-button type="primary" @click="openModalOperation">新增操作</el-button>
            </div>

            <el-table :data="editForm.operations" :border="true">
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
                  <el-button type="primary" size="small" @click="editOperation($index)"> 编辑 </el-button>
                  <el-button type="danger" size="small" @click="removeOperation($index)"> 删除 </el-button>
                </template>
              </el-table-column>
            </el-table>
          </div>
        </div>
      </el-card>
    </div>

    <!-- 触发器编辑对话框 -->
    <el-dialog v-model="modalTriggerVisible" :title="modalMode === 'add' ? '新增触发器' : '编辑触发器'" width="500px" align-center>
      <el-form :model="modalTriggerForm" label-width="100px">
        <el-form-item label="名称">
          <el-input v-model="modalTriggerForm.name" placeholder="请输入触发器名称" disabled />
        </el-form-item>

        <el-form-item label="目标" required>
          <el-select v-model="modalTriggerForm.target" placeholder="请选择目标">
            <el-option label="标头" :value="0" />
            <el-option label="JSON载荷" :value="1" />
            <el-option label="URL" :value="2" />
            <el-option label="HTTP方法" :value="3" />
          </el-select>
        </el-form-item>

        <el-form-item label="条件" required>
          <el-select v-model="modalTriggerForm.kind" placeholder="请选择条件">
            <el-option label="包含" :value="0" />
            <el-option label="不包含" :value="1" />
            <el-option label="等于" :value="2" />
            <el-option label="不等于" :value="3" />
          </el-select>
        </el-form-item>

        <el-form-item label="目标键" required>
          <el-input v-model="modalTriggerForm.tk" placeholder="请输入目标键" />
        </el-form-item>

        <el-form-item label="比较值" required>
          <el-input v-model="modalTriggerForm.tv" placeholder="请输入比较值" />
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="modalTriggerVisible = false">取消</el-button>
        <el-button type="primary" @click="saveTrigger">确定</el-button>
      </template>
    </el-dialog>

    <!-- 操作编辑对话框 -->
    <el-dialog v-model="modalOperationVisible" :title="modalMode === 'add' ? '新增操作' : '编辑操作'" width="500px" align-center>
      <el-form :model="modalOperationForm" label-width="100px">
        <el-form-item label="名称">
          <el-input v-model="modalOperationForm.name" placeholder="请输入操作名称" disabled />
        </el-form-item>

        <el-form-item label="类型" required>
          <el-select v-model="modalOperationForm.kind" placeholder="请选择类型">
            <el-option label="持久化" :value="0" />
            <el-option label="缓存" :value="1" />
            <el-option label="注入缓存" :value="2" />
            <el-option label="注入持久化" :value="3" />
            <el-option label="覆写URL" :value="4" />
          </el-select>
        </el-form-item>

        <el-form-item label="目标" required>
          <el-select v-model="modalOperationForm.target" placeholder="请选择目标">
            <el-option label="标头" :value="0" />
            <el-option label="JSON载荷" :value="1" />
            <el-option label="URL" :value="2" v-if="modalOperationForm.kind === 4" />
          </el-select>
        </el-form-item>

        <el-form-item label="原始键" required>
          <el-input v-model="modalOperationForm.f" placeholder="请输入原始键" />
        </el-form-item>

        <el-form-item label="目标键" required>
          <el-input v-model="modalOperationForm.t" placeholder="请输入目标键" />
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="modalOperationVisible = false">取消</el-button>
        <el-button type="primary" @click="saveOperation">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { SimpleFilterStore } from "@/store/SimpleFilterStore";
import { onMounted, ref, watch } from "vue";
import { ElMessage, ElMessageBox } from "element-plus";
import SimpleFilterApi from "@/api/SimpleFilterApi";
import type { GetSimpleFilterDetailsVo, EditSimpleFilterDto, EditSimpleFilterTriggerDto, EditSimpleFilterOperationDto } from "@/api/SimpleFilterApi";

const filterStore = SimpleFilterStore();
const loading = ref(false);
const saving = ref(false);
const globalLoading = ref(false);

const formData = ref<GetSimpleFilterDetailsVo | null>(null);

//过滤器基础信息
const editForm = ref<GetSimpleFilterDetailsVo>({
  id: "",
  name: "",
  direction: 0,
  status: 0,
  triggers: [],
  operations: [],
  updateTimeEpochMill: "",
});

const modalMode = ref<"add" | "edit">("add");

//模态框相关 - 触发器
const modalTriggerVisible = ref(false);
const editingTriggerIndex = ref(-1);
const modalTriggerForm = ref<EditSimpleFilterTriggerDto>({
  id: null,
  name: "",
  target: 0,
  kind: 0,
  tk: "",
  tv: "",
});

//模态框相关 - 操作
const modalOperationVisible = ref(false);
const editingOperationIndex = ref(-1);
const modalOperationForm = ref<EditSimpleFilterOperationDto>({
  id: null,
  name: "",
  kind: 0,
  target: 0,
  f: "",
  t: "",
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
    formData.value = filter;

    // 转换为编辑表单格式
    editForm.value = {
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

// 新增触发器
const openModalTrigger = () => {
  modalMode.value = "add";
  clearModalForm();
  editingTriggerIndex.value = -1;
  modalTriggerVisible.value = true;
  modalTriggerForm.value.name = `触发器${editForm.value.triggers.length + 1}`;
};

// 编辑触发器
const editTrigger = (index: number) => {
  modalMode.value = "edit";
  editingTriggerIndex.value = index;
  const trigger = editForm.value.triggers[index];
  modalTriggerForm.value = {
    id: trigger.id,
    name: trigger.name,
    target: trigger.target,
    kind: trigger.kind,
    tk: trigger.tk,
    tv: trigger.tv,
  };
  modalTriggerVisible.value = true;
};

// 保存触发器
const saveTrigger = () => {
  //新增触发器
  if (modalMode.value === "add") {
    editForm.value.triggers.push({
      id: null,
      name: modalTriggerForm.value.name,
      target: modalTriggerForm.value.target,
      kind: modalTriggerForm.value.kind,
      tk: modalTriggerForm.value.tk,
      tv: modalTriggerForm.value.tv,
      seq: 0,
    });
  }

  //编辑触发器
  if (modalMode.value === "edit" && editingTriggerIndex.value >= 0) {
    editForm.value.triggers[editingTriggerIndex.value] = {
      id: modalTriggerForm.value.id,
      name: modalTriggerForm.value.name,
      target: modalTriggerForm.value.target,
      kind: modalTriggerForm.value.kind,
      tk: modalTriggerForm.value.tk,
      tv: modalTriggerForm.value.tv,
      seq: editForm.value.triggers[editingTriggerIndex.value].seq,
    };
  }

  modalTriggerVisible.value = false;
};

// 删除触发器
const removeTrigger = (index: number) => {
  ElMessageBox.confirm("确定删除该触发器吗？", "提示", {
    confirmButtonText: "确定",
    cancelButtonText: "取消",
    type: "warning",
  }).then(() => {
    editForm.value.triggers.splice(index, 1);
  });
};

/**
 * 操作模态框相关操作
 */

// 新增操作
const openModalOperation = () => {
  modalMode.value = "add";
  clearModalForm();
  editingOperationIndex.value = -1;
  modalOperationVisible.value = true;
  modalOperationForm.value.name = `操作${editForm.value.operations.length + 1}`;
};

// 编辑操作
const editOperation = (index: number) => {
  modalMode.value = "edit";
  editingOperationIndex.value = index;
  const operation = editForm.value.operations[index];
  modalOperationForm.value = {
    id: operation.id,
    name: operation.name,
    kind: operation.kind,
    target: operation.target,
    f: operation.f,
    t: operation.t,
  };
  modalOperationVisible.value = true;
};

// 保存操作
const saveOperation = () => {
  //新增操作
  if (modalMode.value === "add") {
    editForm.value.operations.push({
      id: null,
      name: modalOperationForm.value.name,
      kind: modalOperationForm.value.kind,
      target: modalOperationForm.value.target,
      f: modalOperationForm.value.f,
      t: modalOperationForm.value.t,
      seq: 0,
    });
  }

  //编辑操作
  if (modalMode.value === "edit" && editingOperationIndex.value >= 0) {
    editForm.value.operations[editingOperationIndex.value] = {
      id: modalOperationForm.value.id,
      name: modalOperationForm.value.name,
      kind: modalOperationForm.value.kind,
      target: modalOperationForm.value.target,
      f: modalOperationForm.value.f,
      t: modalOperationForm.value.t,
      seq: editForm.value.operations[editingOperationIndex.value].seq,
    };
  }

  modalOperationVisible.value = false;
};

// 删除操作
const removeOperation = (index: number) => {
  ElMessageBox.confirm("确定删除该操作吗？", "提示", {
    confirmButtonText: "确定",
    cancelButtonText: "取消",
    type: "warning",
  }).then(() => {
    editForm.value.operations.splice(index, 1);
  });
};

// 创建新的基本过滤器
const addFilter = async () => {
  //过滤器至少有一个触发器
  if (editForm.value.triggers.length === 0) {
    ElMessage.error("过滤器至少需要配置一个触发器");
    return;
  }

  //过滤器至少有一个操作
  if (editForm.value.operations.length === 0) {
    ElMessage.error("过滤器至少需要配置一个操作");
    return;
  }

  globalLoading.value = true;
  //创建过滤器
  try {
    const newFilterId: string = await SimpleFilterApi.addSimpleFilter({
      name: editForm.value.name,
      direction: editForm.value.direction,
      status: editForm.value.status,
      triggers: editForm.value.triggers,
      operations: editForm.value.operations,
    });
    filterStore.setSelectedFilterId(newFilterId);
    filterStore.setIsCreating(false);
    ElMessage.success("创建过滤器成功");
    clearForm();
    clearModalForm();
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
  if (editForm.value.triggers.length === 0) {
    ElMessage.error("过滤器至少需要配置一个触发器");
    return;
  }

  //过滤器至少有一个操作
  if (editForm.value.operations.length === 0) {
    ElMessage.error("过滤器至少需要配置一个操作");
    return;
  }

  globalLoading.value = true;
  try {
    await SimpleFilterApi.editSimpleFilter({
      id: editForm.value.id,
      name: editForm.value.name,
      direction: editForm.value.direction,
      status: editForm.value.status,
      triggers: editForm.value.triggers,
      operations: editForm.value.operations,
      updateTimeEpochMill: editForm.value.updateTimeEpochMill,
    });
    ElMessage.success("保存过滤器成功");
    clearForm();
    clearModalForm();
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
  clearModalForm();
  //重新获取详情
  loadFilterDetails();
  filterStore.requestReloadList();
};

onMounted(() => {
  loadFilterDetails();
});

const clearForm = () => {
  editForm.value = {
    id: "",
    name: "",
    direction: 0,
    status: 0,
    triggers: [],
    operations: [],
    updateTimeEpochMill: "",
  };
};
const clearModalForm = () => {
  modalTriggerForm.value = {
    id: null,
    name: "",
    target: 0,
    kind: 0,
    tk: "",
    tv: "",
  };
  modalOperationForm.value = {
    id: null,
    name: "",
    kind: 0,
    target: 0,
    f: "",
    t: "",
  };
};

watch(
  () => filterStore.isCreating,
  () => {
    clearForm();
    clearModalForm();
  }
);

// 监听选中的过滤器ID变化
watch(
  () => filterStore.getSelectedFilterId,
  () => {
    //如果选中的过滤器为空，则清空表单
    if (!filterStore.getSelectedFilterId) {
      clearForm();
      clearModalForm();
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
