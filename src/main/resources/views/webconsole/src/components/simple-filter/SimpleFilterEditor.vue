<template>
  <div class="filter-editor" v-loading="loading">
    <div v-if="!filterData" class="empty-state">
      <el-empty description="无法加载过滤器信息" />
    </div>

    <div v-if="filterData" class="editor-content">
      <!-- 主编辑卡片 -->
      <el-card class="main-card">
        <template #header>
          <div class="card-header">
            <span>过滤器编辑</span>
            <el-button type="primary" @click="saveFilter" :loading="saving">保存</el-button>
          </div>
        </template>
        <!-- 基础信息 -->
        <div class="section">
          <div class="section-title">基础信息</div>
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
            <div class="section-title">触发器列表</div>
            <el-button type="primary" @click="addTrigger">新增触发器</el-button>
          </div>

          <el-table :data="editForm.triggers" style="width: 100%">
            <el-table-column prop="name" label="名称" width="120" />
            <el-table-column prop="target" label="目标" width="100">
              <template #default="{ row }">
                {{ getTargetText(row.target) }}
              </template>
            </el-table-column>
            <el-table-column prop="kind" label="条件" width="100">
              <template #default="{ row }">
                {{ getKindText(row.kind) }}
              </template>
            </el-table-column>
            <el-table-column prop="tk" label="目标键" width="120" />
            <el-table-column prop="tv" label="比较值" width="120" />
            <el-table-column label="操作" width="100">
              <template #default="{ row, $index }">
                <el-button type="danger" size="small" @click="removeTrigger($index)"> 删除 </el-button>
              </template>
            </el-table-column>
          </el-table>
        </div>

        <el-divider />

        <!-- 操作列表 -->
        <div class="section">
          <div class="section-header">
            <div class="section-title">操作列表</div>
            <el-button type="primary" @click="addOperation">新增操作</el-button>
          </div>

          <el-table :data="editForm.operations" style="width: 100%">
            <el-table-column prop="name" label="名称" width="120" />
            <el-table-column prop="kind" label="类型" width="120">
              <template #default="{ row }">
                {{ getOperationKindText(row.kind) }}
              </template>
            </el-table-column>
            <el-table-column prop="target" label="目标" width="100">
              <template #default="{ row }">
                {{ getOperationTargetText(row.target) }}
              </template>
            </el-table-column>
            <el-table-column prop="f" label="原始键" width="120" />
            <el-table-column prop="t" label="目标键" width="120" />
            <el-table-column label="操作" width="100">
              <template #default="{ row, $index }">
                <el-button type="danger" size="small" @click="removeOperation($index)"> 删除 </el-button>
              </template>
            </el-table-column>
          </el-table>
        </div>
      </el-card>
    </div>

    <!-- 触发器编辑对话框 -->
    <el-dialog v-model="triggerDialogVisible" title="编辑触发器" width="500px">
      <el-form :model="currentTrigger" label-width="100px">
        <el-form-item label="名称" required>
          <el-input v-model="currentTrigger.name" placeholder="请输入触发器名称" />
        </el-form-item>

        <el-form-item label="目标" required>
          <el-select v-model="currentTrigger.target" placeholder="请选择目标">
            <el-option label="标头" :value="0" />
            <el-option label="JSON载荷" :value="1" />
            <el-option label="URL" :value="2" />
            <el-option label="HTTP方法" :value="3" />
          </el-select>
        </el-form-item>

        <el-form-item label="条件" required>
          <el-select v-model="currentTrigger.kind" placeholder="请选择条件">
            <el-option label="包含" :value="0" />
            <el-option label="不包含" :value="1" />
            <el-option label="等于" :value="2" />
            <el-option label="不等于" :value="3" />
          </el-select>
        </el-form-item>

        <el-form-item label="目标键" required>
          <el-input v-model="currentTrigger.tk" placeholder="请输入目标键" />
        </el-form-item>

        <el-form-item label="比较值" required>
          <el-input v-model="currentTrigger.tv" placeholder="请输入比较值" />
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="triggerDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="saveTrigger">确定</el-button>
      </template>
    </el-dialog>

    <!-- 操作编辑对话框 -->
    <el-dialog v-model="operationDialogVisible" title="编辑操作" width="500px">
      <el-form :model="currentOperation" label-width="100px">
        <el-form-item label="名称" required>
          <el-input v-model="currentOperation.name" placeholder="请输入操作名称" />
        </el-form-item>

        <el-form-item label="类型" required>
          <el-select v-model="currentOperation.kind" placeholder="请选择类型">
            <el-option label="持久化" :value="0" />
            <el-option label="缓存" :value="1" />
            <el-option label="注入缓存" :value="2" />
            <el-option label="注入持久化" :value="3" />
            <el-option label="覆写URL" :value="4" />
          </el-select>
        </el-form-item>

        <el-form-item label="目标" required>
          <el-select v-model="currentOperation.target" placeholder="请选择目标">
            <el-option label="标头" :value="0" />
            <el-option label="JSON载荷" :value="1" />
            <el-option label="URL" :value="2" v-if="currentOperation.kind === 4" />
          </el-select>
        </el-form-item>

        <el-form-item label="原始键" required>
          <el-input v-model="currentOperation.f" placeholder="请输入原始键" />
        </el-form-item>

        <el-form-item label="目标键" required>
          <el-input v-model="currentOperation.t" placeholder="请输入目标键" />
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="operationDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="saveOperation">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { SimpleFilterStore } from "@/store/SimpleFilterStore";
import { onMounted, ref, watch } from "vue";
import { ElMessage } from "element-plus";
import SimpleFilterApi from "@/api/SimpleFilterApi";
import type { GetSimpleFilterDetailsVo, EditSimpleFilterDto, EditSimpleFilterTriggerDto, EditSimpleFilterOperationDto } from "@/api/SimpleFilterApi";

const filterStore = SimpleFilterStore();
const loading = ref(false);
const saving = ref(false);
const filterData = ref<GetSimpleFilterDetailsVo | null>(null);
const editForm = ref<EditSimpleFilterDto>({
  id: "",
  name: "",
  direction: 0,
  status: 0,
  triggers: [],
  operations: [],
});

// 触发器编辑
const triggerDialogVisible = ref(false);
const currentTrigger = ref<EditSimpleFilterTriggerDto>({
  id: null,
  name: "",
  target: 0,
  kind: 0,
  tk: "",
  tv: "",
});
const editingTriggerIndex = ref(-1);

// 操作编辑
const operationDialogVisible = ref(false);
const currentOperation = ref<EditSimpleFilterOperationDto>({
  id: null,
  name: "",
  kind: 0,
  target: 0,
  f: "",
  t: "",
});
const editingOperationIndex = ref(-1);

// 加载过滤器详情
const loadFilterDetails = async () => {
  const selectedFilterId = filterStore.getSelectedFilterId;
  if (!selectedFilterId) {
    return;
  }

  loading.value = true;
  try {
    const filter = await SimpleFilterApi.getSimpleFilterDetails({ id: selectedFilterId });
    filterData.value = filter;

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
      })),
      operations: filter.operations.map((o) => ({
        id: o.id,
        name: o.name,
        kind: o.kind,
        target: o.target,
        f: o.f,
        t: o.t,
      })),
    };
  } catch (error) {
    ElMessage.error("加载过滤器详情失败");
    console.error(error);
  } finally {
    loading.value = false;
  }
};

// 监听选中的过滤器ID变化
watch(
  () => filterStore.getSelectedFilterId,
  () => {
    loadFilterDetails();
  }
);

// 新增触发器
const addTrigger = () => {
  currentTrigger.value = {
    id: null,
    name: "",
    target: 0,
    kind: 0,
    tk: "",
    tv: "",
  };
  editingTriggerIndex.value = -1;
  triggerDialogVisible.value = true;
};

// 保存触发器
const saveTrigger = () => {
  if (!currentTrigger.value.name.trim()) {
    ElMessage.error("请输入触发器名称");
    return;
  }
  if (!currentTrigger.value.tk.trim()) {
    ElMessage.error("请输入目标键");
    return;
  }
  if (!currentTrigger.value.tv.trim()) {
    ElMessage.error("请输入比较值");
    return;
  }

  if (editingTriggerIndex.value >= 0) {
    // 编辑现有触发器
    editForm.value.triggers[editingTriggerIndex.value] = { ...currentTrigger.value };
  } else {
    // 新增触发器
    editForm.value.triggers.push({ ...currentTrigger.value });
  }

  triggerDialogVisible.value = false;
};

// 删除触发器
const removeTrigger = (index: number) => {
  editForm.value.triggers.splice(index, 1);
};

// 新增操作
const addOperation = () => {
  currentOperation.value = {
    id: null,
    name: "",
    kind: 0,
    target: 0,
    f: "",
    t: "",
  };
  editingOperationIndex.value = -1;
  operationDialogVisible.value = true;
};

// 保存操作
const saveOperation = () => {
  if (!currentOperation.value.name.trim()) {
    ElMessage.error("请输入操作名称");
    return;
  }
  if (!currentOperation.value.f.trim()) {
    ElMessage.error("请输入原始键");
    return;
  }
  if (!currentOperation.value.t.trim()) {
    ElMessage.error("请输入目标键");
    return;
  }

  if (editingOperationIndex.value >= 0) {
    // 编辑现有操作
    editForm.value.operations[editingOperationIndex.value] = { ...currentOperation.value };
  } else {
    // 新增操作
    editForm.value.operations.push({ ...currentOperation.value });
  }

  operationDialogVisible.value = false;
};

// 删除操作
const removeOperation = (index: number) => {
  editForm.value.operations.splice(index, 1);
};

// 保存过滤器
const saveFilter = async () => {
  if (!editForm.value.name.trim()) {
    ElMessage.error("请输入过滤器名称");
    return;
  }

  saving.value = true;
  try {
    await SimpleFilterApi.editSimpleFilter(editForm.value);
    ElMessage.success("保存成功");
  } catch (error) {
    ElMessage.error("保存失败");
    console.error(error);
  } finally {
    saving.value = false;
  }
};

// 获取目标文本
const getTargetText = (target: number) => {
  const targetMap: Record<number, string> = { 0: "标头", 1: "JSON载荷", 2: "URL", 3: "HTTP方法" };
  return targetMap[target] || "未知";
};

// 获取条件文本
const getKindText = (kind: number) => {
  const kindMap: Record<number, string> = { 0: "包含", 1: "不包含", 2: "等于", 3: "不等于" };
  return kindMap[kind] || "未知";
};

// 获取操作类型文本
const getOperationKindText = (kind: number) => {
  const kindMap: Record<number, string> = { 0: "持久化", 1: "缓存", 2: "注入缓存", 3: "注入持久化", 4: "覆写URL" };
  return kindMap[kind] || "未知";
};

// 获取操作目标文本
const getOperationTargetText = (target: number) => {
  const targetMap: Record<number, string> = { 0: "标头", 1: "JSON载荷", 2: "URL" };
  return targetMap[target] || "未知";
};

onMounted(() => {
  loadFilterDetails();
});
</script>

<style scoped>
.filter-editor {
  height: 100%;
  overflow-y: auto;
}

.empty-state {
  height: 100%;
  display: flex;
  justify-content: center;
  align-items: center;
}

.editor-content {
  max-width: 1200px;
}

.main-card {
  margin-bottom: 20px;
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
  margin-bottom: 16px;
}
</style>
