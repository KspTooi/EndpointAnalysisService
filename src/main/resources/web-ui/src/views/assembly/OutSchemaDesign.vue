<template>
  <StdListContainer>
    <!-- 当前输出方案信息区域 -->
    <div v-loading="polyListLoading || originListLoading" class="schema-info-bar">
      <div class="schema-info-item">
        <span class="schema-info-label">名称</span>
        <span class="schema-info-value">{{ schemaInfo.name }}</span>
      </div>
      <div class="schema-info-divider" />
      <div class="schema-info-item">
        <span class="schema-info-label">模型名称</span>
        <span class="schema-info-value">{{ schemaInfo.modelName }}</span>
      </div>
      <div class="schema-info-divider" />
      <div class="schema-info-item">
        <span class="schema-info-label">数据源表名</span>
        <span class="schema-info-value">{{ schemaInfo.tableName ?? "未配置" }}</span>
      </div>
      <div class="schema-info-divider" />
      <div class="schema-info-item">
        <span class="schema-info-label">显示原始模型</span>
        <el-switch
          v-model="listViewModel"
          :disabled="polyListLoading || originListLoading"
          :active-value="'origin'"
          :inactive-value="'poly'"
          size="small"
        />
      </div>
      <div class="schema-info-divider" />
      <div v-show="listViewModel === 'poly'" class="schema-info-item">
        <span class="schema-info-label">从原始模型生成</span>
        <el-button type="primary" size="small" :icon="MagicStickIcon" @click="syncPolyFromOrigin">开始</el-button>
      </div>
      <div v-show="listViewModel === 'origin'" class="schema-info-item">
        <span class="schema-info-label">从数据源生成</span>
        <el-button type="primary" size="small" :icon="MagicStickIcon">开始</el-button>
      </div>
    </div>

    <!-- 操作按钮区域 -->
    <div v-loading="polyListLoading || originListLoading" class="action-bar">
      <el-button v-if="cdrcCanReturn" type="primary" :icon="CloseIcon" link @click="cdrcReturn">回退</el-button>
      <el-button :disabled="listViewModel === 'origin'" type="success" :icon="AddFieldIcon" link @click="openPolyAddModal()"
        >添加字段</el-button
      >
    </div>

    <!-- 原始模型列表表格区域 -->
    <StdListAreaTable v-show="listViewModel === 'origin'">
      <el-table v-loading="originListLoading" :data="originListData" stripe border height="100%">
        <el-table-column prop="seq" label="序号" min-width="45" show-overflow-tooltip align="center" />
        <el-table-column prop="name" label="字段名" min-width="120" show-overflow-tooltip />
        <el-table-column prop="kind" label="数据类型" min-width="120" show-overflow-tooltip />
        <el-table-column prop="length" label="长度" min-width="100" show-overflow-tooltip />
        <el-table-column prop="require" label="必填" min-width="100" show-overflow-tooltip align="center">
          <template #default="scope">
            <span :style="{ color: scope.row.require === 1 ? '#F56C6C' : '#909399' }">
              {{ scope.row.require === 1 ? "是" : "否" }}
            </span>
          </template>
        </el-table-column>
        <el-table-column prop="remark" label="注释" min-width="120" show-overflow-tooltip />
      </el-table>
    </StdListAreaTable>

    <!-- 聚合模型列表表格区域 -->
    <StdListAreaTable v-show="listViewModel === 'poly'">
      <el-table v-loading="polyListLoading" :data="polyListData" row-key="id" stripe border height="100%">
        <!-- 序号 -->
        <el-table-column prop="seq" label="序号" min-width="70" align="center">
          <template #default="scope">
            <el-input
              v-if="isEditingCell(scope.row.id, 'seq')"
              v-model.number="scope.row.seq"
              size="small"
              @blur="submitCell(scope.row, 'seq')"
            />
            <div v-if="!isEditingCell(scope.row.id, 'seq')" class="editable-cell" @click="activateCell(scope.row.id, 'seq')">
              {{ scope.row.seq ?? "-" }}
            </div>
          </template>
        </el-table-column>

        <!-- 聚合字段名 -->
        <el-table-column prop="name" label="聚合字段名" min-width="150">
          <template #default="scope">
            <el-input
              v-if="isEditingCell(scope.row.id, 'name')"
              v-model="scope.row.name"
              size="small"
              @blur="submitCell(scope.row, 'name')"
            />
            <div v-if="!isEditingCell(scope.row.id, 'name')" class="editable-cell" @click="activateCell(scope.row.id, 'name')">
              {{ scope.row.name || "-" }}
            </div>
          </template>
        </el-table-column>

        <!-- 备注 -->
        <el-table-column prop="remark" label="备注" min-width="150">
          <template #default="scope">
            <el-input
              v-if="isEditingCell(scope.row.id, 'remark')"
              v-model="scope.row.remark"
              size="small"
              @blur="submitCell(scope.row, 'remark')"
            />
            <div
              v-if="!isEditingCell(scope.row.id, 'remark')"
              class="editable-cell"
              @click="activateCell(scope.row.id, 'remark')"
            >
              {{ scope.row.remark || "-" }}
            </div>
          </template>
        </el-table-column>

        <!-- 数据类型 -->
        <el-table-column prop="kind" label="数据类型" min-width="120">
          <template #default="scope">
            <el-input
              v-if="isEditingCell(scope.row.id, 'kind')"
              v-model="scope.row.kind"
              size="small"
              @blur="submitCell(scope.row, 'kind')"
            />
            <div v-if="!isEditingCell(scope.row.id, 'kind')" class="editable-cell" @click="activateCell(scope.row.id, 'kind')">
              {{ scope.row.kind || "-" }}
            </div>
          </template>
        </el-table-column>

        <!-- 长度 -->
        <el-table-column prop="length" label="长度" min-width="80">
          <template #default="scope">
            <el-input
              v-if="isEditingCell(scope.row.id, 'length')"
              v-model="scope.row.length"
              size="small"
              @blur="submitCell(scope.row, 'length')"
            />
            <div
              v-if="!isEditingCell(scope.row.id, 'length')"
              class="editable-cell"
              @click="activateCell(scope.row.id, 'length')"
            >
              {{ scope.row.length || "-" }}
            </div>
          </template>
        </el-table-column>

        <!-- 必填 -->
        <el-table-column prop="require" label="必填" min-width="60" align="center">
          <template #default="scope">
            <div class="editable-cell editable-cell-center" @click="toggleRequire(scope.row)">
              <span :style="{ color: scope.row.require === 1 ? '#F56C6C' : '#909399' }">
                {{ scope.row.require === 1 ? "是" : "否" }}
              </span>
            </div>
          </template>
        </el-table-column>

        <!-- 可见性策略 -->
        <el-table-column prop="policyCrudJson" label="可见性策略" min-width="120">
          <template #default="scope">
            <el-select
              v-if="isEditingCell(scope.row.id, 'policyCrudJson')"
              v-model="scope.row.policyCrudJson"
              multiple
              collapse-tags
              collapse-tags-tooltip
              size="small"
              @visible-change="(visible: boolean) => onPolicyCrudVisibleChange(scope.row, visible)"
            >
              <el-option value="AD" label="新增" />
              <el-option value="ED" label="编辑" />
              <el-option value="DV" label="详情" />
              <el-option value="LD" label="列表查询" />
              <el-option value="LV" label="列表显示" />
            </el-select>
            <div
              v-if="!isEditingCell(scope.row.id, 'policyCrudJson')"
              class="editable-cell editable-cell-inline"
              @click="activateCell(scope.row.id, 'policyCrudJson')"
            >
              <span
                v-for="key in POLICY_CRUD_ORDER"
                :key="key"
                :style="{
                  color: scope.row.policyCrudJson?.includes(key) ? POLICY_CRUD_COLOR_MAP[key] : '#c0c4cc',
                  fontWeight: 500,
                }"
                >{{ POLICY_CRUD_LABEL_MAP[key] }}</span
              >
            </div>
          </template>
        </el-table-column>

        <!-- 查询策略 -->
        <el-table-column prop="policyQuery" label="查询策略" min-width="110">
          <template #default="scope">
            <el-select
              v-if="isEditingCell(scope.row.id, 'policyQuery')"
              :model-value="scope.row.policyQuery"
              size="small"
              @change="(val: number) => submitField(scope.row, 'policyQuery', val)"
            >
              <el-option :value="0" label="等于" />
              <el-option :value="1" label="模糊" />
            </el-select>
            <div
              v-if="!isEditingCell(scope.row.id, 'policyQuery')"
              class="editable-cell"
              @click="activateCell(scope.row.id, 'policyQuery')"
            >
              {{ formatPolicyQuery(scope.row.policyQuery) }}
            </div>
          </template>
        </el-table-column>

        <!-- 显示策略 -->
        <el-table-column prop="policyView" label="显示策略" min-width="120">
          <template #default="scope">
            <el-select
              v-if="isEditingCell(scope.row.id, 'policyView')"
              :model-value="scope.row.policyView"
              size="small"
              @change="(val: number) => submitField(scope.row, 'policyView', val)"
            >
              <el-option :value="0" label="文本框" />
              <el-option :value="1" label="文本域" />
              <el-option :value="2" label="下拉" />
              <el-option :value="3" label="单选" />
              <el-option :value="4" label="多选" />
              <el-option :value="5" label="LD" />
              <el-option :value="6" label="LDT" />
            </el-select>
            <div
              v-if="!isEditingCell(scope.row.id, 'policyView')"
              class="editable-cell"
              @click="activateCell(scope.row.id, 'policyView')"
            >
              {{ formatPolicyView(scope.row.policyView) }}
            </div>
          </template>
        </el-table-column>

        <!-- 操作 -->
        <el-table-column label="操作" fixed="right" min-width="80" align="center">
          <template #default="scope">
            <el-button link type="danger" size="small" :icon="DeleteIcon" @click="removePolyList(scope.row)"> 删除 </el-button>
          </template>
        </el-table-column>
      </el-table>
    </StdListAreaTable>

    <!-- 新增聚合字段模态框 -->
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
            <el-checkbox value="AD" label="增" />
            <el-checkbox value="ED" label="编" />
            <el-checkbox value="DV" label="详" />
            <el-checkbox value="LD" label="查" />
            <el-checkbox value="LV" label="列" />
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
          <el-button type="primary" :loading="polyAddLoading" @click="submitPolyAdd">创建</el-button>
        </div>
      </template>
    </el-dialog>
  </StdListContainer>
</template>

<script setup lang="ts">
import { ref, onMounted, watch } from "vue";
import type { FormInstance } from "element-plus";
import StdListContainer from "@/soa/std-series/StdListContainer.vue";
import StdListAreaTable from "@/soa/std-series/StdListAreaTable.vue";
import ComDirectRouteContext from "@/soa/com-series/service/ComDirectRouteContext.ts";
import OutModelPolyService from "@/views/assembly/service/OutModelPolyService";
import OutSchemaDesignService from "@/views/assembly/service/OutSchemaDesignSevice";
import type { GetOutModelPolyListVo } from "@/views/assembly/api/OutModelPolyApi";
import type { GetOpSchemaListVo } from "@/views/assembly/api/OpSchemaApi";
import ComIconService from "@/soa/com-series/service/ComIconService";

const { resolveIcon } = ComIconService.useIconService();

const DeleteIcon = resolveIcon("delete");
const MagicStickIcon = resolveIcon("magic-stick");
const CloseIcon = resolveIcon("fontisto:close");
const AddFieldIcon = resolveIcon("zondicons:add-outline");

const POLICY_CRUD_LABEL_MAP: Record<string, string> = {
  AD: "增",
  ED: "编",
  DV: "详",
  LD: "查",
  LV: "列",
};
const POLICY_CRUD_COLOR_MAP: Record<string, string> = {
  AD: "#41b7cc",
  ED: "#41b7cc",
  DV: "#41b7cc",
  LD: "#41b7cc",
  LV: "#41b7cc",
};
const POLICY_CRUD_ORDER = ["AD", "ED", "DV", "LD", "LV"] as const;
const POLICY_QUERY_LABEL_MAP: Record<number, string> = {
  0: "等于",
  1: "模糊",
};
const POLICY_VIEW_LABEL_MAP: Record<number, string> = {
  0: "文本框",
  1: "文本域",
  2: "下拉",
  3: "单选",
  4: "多选",
  5: "LD",
  6: "LDT",
};

const { cdrcCanReturn, cdrcReturn, getCdrcQuery } = ComDirectRouteContext.useDirectRouteContext();

const schemaInfo = getCdrcQuery() as GetOpSchemaListVo;
const outputSchemaId = ref(schemaInfo?.id ?? "");

//列表视图模式 poly: 聚合模型, origin: 原始模型
const listViewModel = ref<"poly" | "origin">("poly");

//聚合模型列表打包
const {
  listData: polyListData,
  listLoading: polyListLoading,
  loadList: loadPolyList,
  removeList: removePolyList,
  syncFromOrigin: syncPolyFromOrigin,
} = OutModelPolyService.useOutModelPolyList(outputSchemaId);

//原始模型列表打包
const {
  listData: originListData,
  listLoading: originListLoading,
  loadList: loadOriginList,
} = OutSchemaDesignService.useOutModelOriginList(outputSchemaId);

//监听列表视图模式变化
watch(listViewModel, (newVal) => {
  if (newVal === "origin") {
    loadOriginList();
  }
  if (newVal === "poly") {
    loadPolyList();
  }
});

// ==================== 单元格内联编辑 ====================

const editingCellKey = ref("");

const { submitRow, commitField } = OutModelPolyService.useCellEdit();

const buildCellKey = (rowId: string, field: string): string => `${rowId}_${field}`;

const activateCell = (rowId: string, field: string): void => {
  editingCellKey.value = buildCellKey(rowId, field);
};

const clearEditingCell = (): void => {
  editingCellKey.value = "";
};

const isEditingCell = (rowId: string, field: string): boolean => editingCellKey.value === buildCellKey(rowId, field);

const submitCell = async (row: GetOutModelPolyListVo, field: string): Promise<void> => {
  const success = await submitRow(row);
  if (!success) {
    return;
  }
  if (!isEditingCell(row.id, field)) {
    return;
  }
  clearEditingCell();
  if (field === "seq") {
    await loadPolyList();
  }
};

const submitField = async (row: GetOutModelPolyListVo, field: string, value: any): Promise<void> => {
  const success = await commitField(row, field, value);
  if (!success) {
    return;
  }
  clearEditingCell();
};

const onPolicyCrudVisibleChange = async (row: GetOutModelPolyListVo, visible: boolean): Promise<void> => {
  if (visible) {
    return;
  }
  await submitCell(row, "policyCrudJson");
};

const toggleRequire = async (row: GetOutModelPolyListVo): Promise<void> => {
  const nextValue = row.require === 1 ? 0 : 1;
  await submitField(row, "require", nextValue);
};

const formatPolicyQuery = (value: number): string => POLICY_QUERY_LABEL_MAP[value] ?? "-";
const formatPolicyView = (value: number): string => POLICY_VIEW_LABEL_MAP[value] ?? "-";

// ==================== 新增模态框 ====================

const polyAddFormRef = ref<FormInstance>();

const {
  modalVisible: polyAddModalVisible,
  modalLoading: polyAddLoading,
  modalForm: polyAddForm,
  modalRequireChecked: polyAddFormRequireChecked,
  modalRules: polyAddRules,
  openModal: openPolyAddModal,
  resetModal: resetPolyAddModal,
  submitModal: submitPolyAdd,
} = OutSchemaDesignService.usePolyAddModal(outputSchemaId, polyAddFormRef, loadPolyList);

onMounted(() => {
  loadPolyList();
});
</script>

<style scoped>
.std-list-container {
  padding-top: 0;
}

.schema-info-bar {
  display: flex;
  align-items: center;
  padding: 10px 16px;
  margin-bottom: 8px;
  background: #fff;
  border-radius: 0;
  border: 1px solid #e4e7ed;
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

.editable-cell {
  min-height: 24px;
  line-height: 24px;
  cursor: pointer;
  padding: 0 4px;
}

.editable-cell-center {
  text-align: center;
}

.editable-cell-inline {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
}

.action-bar {
  display: flex;
  margin-bottom: 8px;
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

.action-bar :deep(.el-button--success.is-link:hover) {
  background-color: var(--el-color-success-light-9);
  color: var(--el-color-success);
}

.action-bar :deep(.el-button--danger.is-link:hover) {
  background-color: var(--el-color-danger-light-9);
  color: var(--el-color-danger);
}
</style>
