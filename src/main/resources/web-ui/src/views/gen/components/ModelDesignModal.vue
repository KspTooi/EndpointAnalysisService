<template>
  <el-dialog v-model="modalVisible" :title="title" width="1200px" :close-on-click-modal="false" @close="onClose">
    <el-tabs v-model="activeTab" type="border-card" style="height: 600px">
      <!-- 原始模型 TAB -->
      <el-tab-pane label="原始模型" name="origin" style="height: 100%; display: flex; flex-direction: column">
        <div style="display: flex; flex-direction: column; height: 540px">
          <el-table :data="originListData" stripe v-loading="originListLoading" border height="100%">
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
        </div>
      </el-tab-pane>

      <!-- 聚合模型 TAB -->
      <el-tab-pane label="聚合模型" name="poly" style="height: 100%; display: flex; flex-direction: column">
        <div style="display: flex; flex-direction: column; height: 540px">
          <StdListAreaAction class="flex gap-2">
            <el-button type="success" @click="openPolyModal('add', null)">新增聚合字段</el-button>
          </StdListAreaAction>

          <StdListAreaTable style="flex: 1">
            <el-table :data="polyListData" stripe v-loading="polyListLoading" border height="100%">
              <el-table-column prop="seq" label="序号" min-width="50" show-overflow-tooltip align="center" />
              <el-table-column prop="name" label="聚合字段名" min-width="150" show-overflow-tooltip />
              <el-table-column prop="kind" label="数据类型" min-width="120" show-overflow-tooltip />
              <el-table-column prop="length" label="长度" min-width="80" show-overflow-tooltip />
              <el-table-column prop="require" label="必填" min-width="50" align="center">
                <template #default="scope">
                  <span :style="{ color: scope.row.require === 1 ? '#f56c6c' : '#67c23a', fontWeight: 500 }">
                    {{ scope.row.require === 1 ? "是" : "否" }}
                  </span>
                </template>
              </el-table-column>
              <el-table-column prop="policyCrudJson" label="可见性策略" min-width="150" show-overflow-tooltip />
              <el-table-column prop="policyQuery" label="查询策略" min-width="80" show-overflow-tooltip />
              <el-table-column prop="policyView" label="显示策略" min-width="80" show-overflow-tooltip />
              <el-table-column prop="placeholder" label="placeholder" min-width="120" show-overflow-tooltip />
              <el-table-column label="操作" fixed="right" min-width="120" align="center">
                <template #default="scope">
                  <el-button link type="primary" size="small" @click="openPolyModal('edit', scope.row)" :icon="EditIcon">
                    编辑
                  </el-button>
                  <el-button link type="danger" size="small" @click="removePolyList(scope.row)" :icon="DeleteIcon">
                    删除
                  </el-button>
                </template>
              </el-table-column>
            </el-table>
          </StdListAreaTable>
        </div>
      </el-tab-pane>
    </el-tabs>

    <!-- 聚合模型 新增/编辑模态框 -->
    <el-dialog
      v-model="polyModalVisible"
      :title="polyModalMode === 'edit' ? '编辑聚合字段' : '新增聚合字段'"
      width="600px"
      :close-on-click-modal="false"
      @close="
        resetPolyModal();
        loadPolyList();
      "
    >
      <el-form
        v-if="polyModalVisible"
        ref="polyModalFormRef"
        :model="polyModalForm"
        :rules="polyModalRules"
        label-width="130px"
        :validate-on-rule-change="false"
      >
        <el-form-item label="原始字段ID" prop="outputModelOriginId">
          <el-input v-model="polyModalForm.outputModelOriginId" placeholder="请输入原始字段ID" clearable />
        </el-form-item>
        <el-form-item label="聚合字段名" prop="name">
          <el-input v-model="polyModalForm.name" placeholder="请输入聚合字段名" clearable maxlength="255" show-word-limit />
        </el-form-item>
        <el-form-item label="聚合数据类型" prop="kind">
          <el-input v-model="polyModalForm.kind" placeholder="请输入聚合数据类型" clearable maxlength="255" show-word-limit />
        </el-form-item>
        <el-form-item label="聚合长度" prop="length">
          <el-input v-model="polyModalForm.length" placeholder="请输入聚合长度" clearable maxlength="255" show-word-limit />
        </el-form-item>
        <el-form-item label="聚合必填" prop="require">
          <el-input v-model.number="polyModalForm.require" placeholder="0:否 1:是" clearable />
        </el-form-item>
        <el-form-item label="可见性策略" prop="policyCrudJson">
          <el-input v-model="polyModalForm.policyCrudJson" placeholder="ADD、EDIT、LIST_QUERY、LIST_VIEW" clearable />
        </el-form-item>
        <el-form-item label="查询策略" prop="policyQuery">
          <el-input v-model.number="polyModalForm.policyQuery" placeholder="0:等于 1:模糊" clearable />
        </el-form-item>
        <el-form-item label="显示策略" prop="policyView">
          <el-input
            v-model.number="polyModalForm.policyView"
            placeholder="0:文本框 1:文本域 2:下拉 3:单 4:多 5:LD 6:LDT"
            clearable
          />
        </el-form-item>
        <el-form-item label="placeholder" prop="placeholder">
          <el-input
            v-model="polyModalForm.placeholder"
            placeholder="请输入placeholder"
            clearable
            maxlength="80"
            show-word-limit
          />
        </el-form-item>
        <el-form-item label="聚合排序" prop="seq">
          <el-input v-model.number="polyModalForm.seq" placeholder="请输入聚合排序" clearable />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="polyModalVisible = false">取消</el-button>
          <el-button type="primary" @click="submitPolyModal" :loading="polyModalLoading">
            {{ polyModalMode === "add" ? "创建" : "保存" }}
          </el-button>
        </div>
      </template>
    </el-dialog>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref, markRaw, computed } from "vue";
import { Edit, Delete } from "@element-plus/icons-vue";
import type { FormInstance } from "element-plus";
import StdListAreaAction from "@/soa/std-series/StdListAreaAction.vue";
import StdListAreaTable from "@/soa/std-series/StdListAreaTable.vue";
import OutModelOriginService from "@/views/gen/service/OutModelOriginService";
import OutModelPolyService from "@/views/gen/service/OutModelPolyService";
import type { GetOutSchemaListVo } from "@/views/gen/api/OutSchemaApi";

const EditIcon = markRaw(Edit);
const DeleteIcon = markRaw(Delete);

const modalVisible = ref(false);
const activeTab = ref("origin");
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
} = OutModelPolyService.useOutModelPolyList(outputSchemaId);

// ==================== 聚合模型模态框 ====================

const polyModalFormRef = ref<FormInstance>();

const {
  modalVisible: polyModalVisible,
  modalLoading: polyModalLoading,
  modalMode: polyModalMode,
  modalForm: polyModalForm,
  modalRules: polyModalRules,
  openModal: openPolyModal,
  resetModal: resetPolyModal,
  submitModal: submitPolyModal,
} = OutModelPolyService.useOutModelPolyModal(polyModalFormRef, outputSchemaId, loadPolyList);

// ==================== 对外暴露 ====================

const onClose = () => {
  emit("on-close");
};

defineExpose({
  openModal: async (row: GetOutSchemaListVo) => {
    outSchemaVo.value = row;
    outputSchemaId.value = row.id;
    activeTab.value = "origin";
    modalVisible.value = true;
    await Promise.all([loadOriginList(), loadPolyList()]);
  },
});
</script>

<style scoped></style>
