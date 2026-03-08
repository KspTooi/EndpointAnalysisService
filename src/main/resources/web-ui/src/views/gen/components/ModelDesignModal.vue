<template>
  <el-dialog v-model="modalVisible" :title="title" fullscreen :close-on-click-modal="false" @close="onClose">
    <el-tabs v-model="activeTab" type="border-card" class="fullscreen-tabs">
      <!-- 原始模型 TAB -->
      <el-tab-pane label="原始模型" name="origin">
        <el-table :data="originListData" stripe v-loading="originListLoading" border class="tab-table">
          <el-table-column prop="seq" label="序号" min-width="50" show-overflow-tooltip align="center" />
          <el-table-column prop="name" label="原始字段名" min-width="150" show-overflow-tooltip />
          <el-table-column prop="kind" label="数据类型" min-width="120" show-overflow-tooltip />
          <el-table-column prop="length" label="长度" min-width="80" show-overflow-tooltip />
          <el-table-column prop="require" label="必填" min-width="50" align="center">
            <template #default="scope">
              <span :style="{ color: scope.row.require === 1 ? '#f56c6c' : '#67c23a', fontWeight: 500 }">
                {{ scope.row.require === 1 ? "是" : "否" }}
              </span>
            </template>
          </el-table-column>
          <el-table-column prop="remark" label="备注" min-width="150" show-overflow-tooltip />
        </el-table>
      </el-tab-pane>

      <!-- 聚合模型 TAB -->
      <el-tab-pane label="聚合模型" name="poly">
        <div class="poly-tab-content">
          <StdListAreaAction class="flex gap-2">
            <el-button type="warning" @click="syncPolyFromOrigin">从原始模型同步</el-button>
            <el-button type="success" @click="openPolyAddModal">新增聚合字段</el-button>
          </StdListAreaAction>

          <el-table :data="polyListData" stripe v-loading="polyListLoading" border class="tab-table poly-table">
            <!-- 序号 -->
            <el-table-column prop="seq" label="序号" min-width="70" align="center">
              <template #default="scope">
                <el-input
                  v-if="isEditing(scope.row.id, 'seq')"
                  v-model.number="editingValue"
                  size="small"
                  autofocus
                  @blur="submitEdit(scope.row)"
                  @keyup.enter="submitEdit(scope.row)"
                  @keyup.esc="cancelEdit"
                />
                <span v-else class="cell-text" @click="startEdit(scope.row, 'seq')">{{ scope.row.seq }}</span>
              </template>
            </el-table-column>

            <!-- 聚合字段名 -->
            <el-table-column prop="name" label="聚合字段名" min-width="150">
              <template #default="scope">
                <el-input
                  v-if="isEditing(scope.row.id, 'name')"
                  v-model="editingValue"
                  size="small"
                  autofocus
                  @blur="submitEdit(scope.row)"
                  @keyup.enter="submitEdit(scope.row)"
                  @keyup.esc="cancelEdit"
                />
                <span v-else class="cell-text" @click="startEdit(scope.row, 'name')">{{ scope.row.name }}</span>
              </template>
            </el-table-column>

            <!-- 数据类型 -->
            <el-table-column prop="kind" label="数据类型" min-width="120">
              <template #default="scope">
                <el-input
                  v-if="isEditing(scope.row.id, 'kind')"
                  v-model="editingValue"
                  size="small"
                  autofocus
                  @blur="submitEdit(scope.row)"
                  @keyup.enter="submitEdit(scope.row)"
                  @keyup.esc="cancelEdit"
                />
                <span v-else class="cell-text" @click="startEdit(scope.row, 'kind')">{{ scope.row.kind }}</span>
              </template>
            </el-table-column>

            <!-- 长度 -->
            <el-table-column prop="length" label="长度" min-width="80">
              <template #default="scope">
                <el-input
                  v-if="isEditing(scope.row.id, 'length')"
                  v-model="editingValue"
                  size="small"
                  autofocus
                  @blur="submitEdit(scope.row)"
                  @keyup.enter="submitEdit(scope.row)"
                  @keyup.esc="cancelEdit"
                />
                <span v-else class="cell-text" @click="startEdit(scope.row, 'length')">{{ scope.row.length }}</span>
              </template>
            </el-table-column>

            <!-- 必填 -->
            <el-table-column prop="require" label="必填" min-width="60" align="center">
              <template #default="scope">
                <el-checkbox
                  :model-value="scope.row.require === 1"
                  @change="(val: boolean) => commitField(scope.row, 'require', val ? 1 : 0)"
                />
              </template>
            </el-table-column>

            <!-- 可见性策略 -->
            <el-table-column prop="policyCrudJson" label="可见性策略" min-width="220">
              <template #default="scope">
                <el-checkbox-group
                  :model-value="scope.row.policyCrudJson"
                  @change="(val: string[]) => commitField(scope.row, 'policyCrudJson', val)"
                >
                  <el-checkbox value="ADD" label="ADD" />
                  <el-checkbox value="EDIT" label="EDIT" />
                  <el-checkbox value="LQ" label="LQ" />
                  <el-checkbox value="LW" label="LW" />
                </el-checkbox-group>
              </template>
            </el-table-column>

            <!-- 查询策略 -->
            <el-table-column prop="policyQuery" label="查询策略" min-width="110">
              <template #default="scope">
                <el-select
                  :model-value="scope.row.policyQuery"
                  size="small"
                  @change="(val: number) => commitField(scope.row, 'policyQuery', val)"
                >
                  <el-option :value="0" label="等于" />
                  <el-option :value="1" label="模糊" />
                </el-select>
              </template>
            </el-table-column>

            <!-- 显示策略 -->
            <el-table-column prop="policyView" label="显示策略" min-width="120">
              <template #default="scope">
                <el-select
                  :model-value="scope.row.policyView"
                  size="small"
                  @change="(val: number) => commitField(scope.row, 'policyView', val)"
                >
                  <el-option :value="0" label="文本框" />
                  <el-option :value="1" label="文本域" />
                  <el-option :value="2" label="下拉" />
                  <el-option :value="3" label="单选" />
                  <el-option :value="4" label="多选" />
                  <el-option :value="5" label="LD" />
                  <el-option :value="6" label="LDT" />
                </el-select>
              </template>
            </el-table-column>

            <!-- 备注 -->
            <el-table-column prop="remark" label="备注" min-width="150">
              <template #default="scope">
                <el-input
                  v-if="isEditing(scope.row.id, 'remark')"
                  v-model="editingValue"
                  size="small"
                  autofocus
                  @blur="submitEdit(scope.row)"
                  @keyup.enter="submitEdit(scope.row)"
                  @keyup.esc="cancelEdit"
                />
                <span v-else class="cell-text" @click="startEdit(scope.row, 'remark')">{{ scope.row.remark }}</span>
              </template>
            </el-table-column>

            <!-- 操作 -->
            <el-table-column label="操作" fixed="right" min-width="80" align="center">
              <template #default="scope">
                <el-button link type="danger" size="small" @click="removePolyList(scope.row)" :icon="DeleteIcon">
                  删除
                </el-button>
              </template>
            </el-table-column>
          </el-table>
        </div>
      </el-tab-pane>
    </el-tabs>

    <!-- 聚合模型 新增模态框 -->
    <el-dialog
      v-model="polyAddModalVisible"
      title="新增聚合字段"
      width="600px"
      :close-on-click-modal="false"
      @close="resetPolyAddModal"
    >
      <el-form
        v-if="polyAddModalVisible"
        ref="polyAddFormRef"
        :model="polyAddForm"
        :rules="polyAddRules"
        label-width="130px"
        :validate-on-rule-change="false"
      >
        <el-form-item label="原始字段ID" prop="outputModelOriginId">
          <el-input v-model="polyAddForm.outputModelOriginId" placeholder="请输入原始字段ID" clearable />
        </el-form-item>
        <el-form-item label="聚合字段名" prop="name">
          <el-input v-model="polyAddForm.name" placeholder="请输入聚合字段名" clearable maxlength="255" show-word-limit />
        </el-form-item>
        <el-form-item label="聚合数据类型" prop="kind">
          <el-input v-model="polyAddForm.kind" placeholder="请输入聚合数据类型" clearable maxlength="255" show-word-limit />
        </el-form-item>
        <el-form-item label="聚合长度" prop="length">
          <el-input v-model="polyAddForm.length" placeholder="请输入聚合长度" clearable maxlength="255" show-word-limit />
        </el-form-item>
        <el-form-item label="聚合必填" prop="require">
          <el-checkbox v-model="polyAddFormRequireChecked" />
        </el-form-item>
        <el-form-item label="可见性策略" prop="policyCrudJson">
          <el-checkbox-group v-model="polyAddForm.policyCrudJson">
            <el-checkbox value="ADD" label="ADD" />
            <el-checkbox value="EDIT" label="EDIT" />
            <el-checkbox value="LQ" label="LQ" />
            <el-checkbox value="LW" label="LW" />
          </el-checkbox-group>
        </el-form-item>
        <el-form-item label="查询策略" prop="policyQuery">
          <el-select v-model="polyAddForm.policyQuery">
            <el-option :value="0" label="等于" />
            <el-option :value="1" label="模糊" />
          </el-select>
        </el-form-item>
        <el-form-item label="显示策略" prop="policyView">
          <el-select v-model="polyAddForm.policyView">
            <el-option :value="0" label="文本框" />
            <el-option :value="1" label="文本域" />
            <el-option :value="2" label="下拉" />
            <el-option :value="3" label="单选" />
            <el-option :value="4" label="多选" />
            <el-option :value="5" label="LD" />
            <el-option :value="6" label="LDT" />
          </el-select>
        </el-form-item>
        <el-form-item label="聚合字段备注" prop="remark">
          <el-input v-model="polyAddForm.remark" placeholder="请输入聚合字段备注" clearable maxlength="80" show-word-limit />
        </el-form-item>
        <el-form-item label="聚合排序" prop="seq">
          <el-input v-model.number="polyAddForm.seq" placeholder="请输入聚合排序" clearable />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="polyAddModalVisible = false">取消</el-button>
          <el-button type="primary" @click="submitPolyAdd" :loading="polyAddLoading">创建</el-button>
        </div>
      </template>
    </el-dialog>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref, reactive, computed, watch } from "vue";
import { Delete } from "@element-plus/icons-vue";
import { markRaw } from "vue";
import type { FormInstance, FormRules } from "element-plus";
import { ElMessage } from "element-plus";
import StdListAreaAction from "@/soa/std-series/StdListAreaAction.vue";
import OutModelOriginService from "@/views/gen/service/OutModelOriginService";
import OutModelPolyService from "@/views/gen/service/OutModelPolyService";
import OutModelPolyApi from "@/views/gen/api/OutModelPolyApi";
import type { AddOutModelPolyDto } from "@/views/gen/api/OutModelPolyApi";
import type { GetOutSchemaListVo } from "@/views/gen/api/OutSchemaApi";

const DeleteIcon = markRaw(Delete);

const ACTIVE_TAB_KEY = "ModelDesignModal_activeTab";

const modalVisible = ref(false);
const activeTab = ref(localStorage.getItem(ACTIVE_TAB_KEY) ?? "origin");
const outputSchemaId = ref("");
const outSchemaVo = ref<GetOutSchemaListVo | null>(null);

const emit = defineEmits<{
  (e: "on-close"): void;
}>();

const title = computed(() => {
  if (outSchemaVo.value) {
    return `模型设计: ${outSchemaVo.value.name}（${outSchemaVo.value.modelName}）`;
  }
  return "模型设计";
});

// ==================== 原始模型列表 ====================

const {
  listData: originListData,
  listLoading: originListLoading,
  loadList: loadOriginList,
} = OutModelOriginService.useOutModelOriginList(outputSchemaId);

// ==================== 聚合模型列表 ====================

const {
  listData: polyListData,
  listLoading: polyListLoading,
  loadList: loadPolyList,
  removeList: removePolyList,
  syncFromOrigin: syncPolyFromOrigin,
} = OutModelPolyService.useOutModelPolyList(outputSchemaId);

// ==================== 单元格内联编辑 ====================

const { editingValue, startEdit, cancelEdit, isEditing, submitEdit, commitField } =
  OutModelPolyService.useCellEdit();

// ==================== 新增模态框 ====================

const polyAddModalVisible = ref(false);
const polyAddLoading = ref(false);
const polyAddFormRef = ref<FormInstance>();

const polyAddForm = reactive<AddOutModelPolyDto>({
  outputSchemaId: "",
  outputModelOriginId: "",
  name: "",
  kind: "",
  length: "",
  require: 0,
  policyCrudJson: [],
  policyQuery: 0,
  policyView: 0,
  remark: "",
  seq: 0,
});

const polyAddFormRequireChecked = computed({
  get: () => polyAddForm.require === 1,
  set: (val: boolean) => {
    polyAddForm.require = val ? 1 : 0;
  },
});

const polyAddRules: FormRules = {
  outputModelOriginId: [{ required: true, message: "请输入原始字段ID", trigger: "blur" }],
  name: [{ required: true, message: "请输入聚合字段名", trigger: "blur" }],
  kind: [{ required: true, message: "请输入聚合数据类型", trigger: "blur" }],
  require: [{ required: true, message: "请选择聚合必填", trigger: "change" }],
  policyCrudJson: [{ required: true, message: "请选择聚合可见性策略", trigger: "change" }],
  policyQuery: [{ required: true, message: "请选择聚合查询策略", trigger: "change" }],
  policyView: [{ required: true, message: "请选择聚合显示策略", trigger: "change" }],
  remark: [{ required: true, message: "请输入聚合字段备注", trigger: "blur" }],
  seq: [{ required: true, message: "请输入聚合排序", trigger: "blur" }],
};

const openPolyAddModal = () => {
  polyAddForm.outputSchemaId = outputSchemaId.value;
  polyAddForm.outputModelOriginId = "";
  polyAddForm.name = "";
  polyAddForm.kind = "";
  polyAddForm.length = "";
  polyAddForm.require = 0;
  polyAddForm.policyCrudJson = [];
  polyAddForm.policyQuery = 0;
  polyAddForm.policyView = 0;
  polyAddForm.remark = "";
  polyAddForm.seq = 0;
  polyAddModalVisible.value = true;
};

const resetPolyAddModal = () => {
  polyAddFormRef.value?.resetFields();
};

const submitPolyAdd = async () => {
  if (!polyAddFormRef.value) return;

  try {
    await polyAddFormRef.value.validate();
  } catch (error) {
    return;
  }

  polyAddLoading.value = true;

  try {
    await OutModelPolyApi.addOutModelPoly(polyAddForm);
    ElMessage.success("新增成功");
    polyAddModalVisible.value = false;
    await loadPolyList();
  } catch (error: any) {
    ElMessage.error(error.message);
  }

  polyAddLoading.value = false;
};

watch(activeTab, (val) => localStorage.setItem(ACTIVE_TAB_KEY, val));

// ==================== 对外暴露 ====================

const onClose = () => {
  emit("on-close");
};

defineExpose({
  openModal: async (row: GetOutSchemaListVo) => {
    outSchemaVo.value = row;
    outputSchemaId.value = row.id;
    activeTab.value = localStorage.getItem(ACTIVE_TAB_KEY) ?? "origin";
    modalVisible.value = true;
    await Promise.all([loadOriginList(), loadPolyList()]);
  },
});
</script>

<style scoped>
/*
  全屏 dialog 标题栏约 55px，tabs 导航栏约 41px，
  操作按钮行（StdListAreaAction）约 46px，内边距合计约 24px。
  tab-table: 撑满除 tabs 导航之外的剩余空间
  poly-table: 再减去操作按钮行高度
*/
.fullscreen-tabs {
  height: calc(100vh - 80px);
}

.tab-table {
  height: calc(100vh - 55px - 41px - 24px);
}

.poly-tab-content {
  display: flex;
  flex-direction: column;
}

.poly-table {
  height: calc(100vh - 55px - 41px - 46px - 24px) !important;
}

.cell-text {
  display: block;
  cursor: pointer;
  min-height: 20px;
  border-radius: 3px;
  padding: 0 2px;
}

.cell-text:hover {
  background-color: var(--el-fill-color-light);
}
</style>
