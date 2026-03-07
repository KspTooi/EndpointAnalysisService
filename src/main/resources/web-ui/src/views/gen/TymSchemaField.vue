<template>
  <el-dialog v-model="modalVisible" title="管理方案字段" width="1200px" :close-on-click-modal="false" @close="onClose">
    <!-- 操作按钮区域 -->
    <StdListAreaAction class="flex gap-2">
      <el-button type="success" @click="openModalSelf('add', null)">新增方案字段</el-button>
    </StdListAreaAction>

    <!-- 列表表格区域 -->
    <StdListAreaTable style="height: 500px">
      <el-table :data="listData" stripe v-loading="listLoading" border height="100%">
        <el-table-column prop="source" label="匹配源类型" min-width="120" show-overflow-tooltip />
        <el-table-column prop="target" label="匹配目标类型" min-width="120" show-overflow-tooltip />
        <el-table-column prop="seq" label="排序" min-width="120" show-overflow-tooltip>
          <template #default="scope">
            <ComSeqFixer
              :id="scope.row.id"
              :seq-field="'seq'"
              :get-detail-api="(id) => TymSchemaFieldApi.getTymSchemaFieldDetails({ id })"
              :edit-api="(id, dto) => TymSchemaFieldApi.editTymSchemaField(dto)"
              :display-value="scope.row.seq"
              @success="loadList"
            />
          </template>
        </el-table-column>
        <el-table-column label="操作" fixed="right" min-width="180">
          <template #default="scope">
            <el-button link type="primary" size="small" @click="openModalSelf('edit', scope.row)" :icon="EditIcon">
              编辑
            </el-button>
            <el-button link type="danger" size="small" @click="removeList(scope.row)" :icon="DeleteIcon"> 删除 </el-button>
          </template>
        </el-table-column>
      </el-table>

      <template #pagination>
        <el-pagination
          v-model:current-page="listForm.pageNum"
          v-model:page-size="listForm.pageSize"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          :total="listTotal"
          @size-change="
            (val: number) => {
              listForm.pageSize = val;
              loadList();
            }
          "
          @current-change="
            (val: number) => {
              listForm.pageNum = val;
              loadList();
            }
          "
          background
        />
      </template>
    </StdListAreaTable>

    <!-- 新增/编辑模态框 -->
    <el-dialog
      v-model="modalVisibleSelf"
      :title="modalMode === 'edit' ? '编辑类型映射方案字段表' : '新增类型映射方案字段表'"
      width="600px"
      :close-on-click-modal="false"
      @close="
        resetModal();
        loadList();
      "
    >
      <el-form
        v-if="modalVisibleSelf"
        ref="modalFormRef"
        :model="modalForm"
        :rules="modalRules"
        label-width="100px"
        :validate-on-rule-change="false"
      >
        <el-form-item label="匹配源类型" prop="source">
          <el-input v-model="modalForm.source" placeholder="请输入匹配源类型" clearable />
        </el-form-item>
        <el-form-item label="匹配目标类型" prop="target">
          <el-input v-model="modalForm.target" placeholder="请输入匹配目标类型" clearable />
        </el-form-item>
        <el-form-item label="排序" prop="seq">
          <el-input v-model.number="modalForm.seq" placeholder="请输入排序" clearable />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="modalVisibleSelf = false">取消</el-button>
          <el-button type="primary" @click="submitModal" :loading="modalLoading">
            {{ modalMode === "add" ? "创建" : "保存" }}
          </el-button>
        </div>
      </template>
    </el-dialog>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref, markRaw } from "vue";
import { Edit, Delete } from "@element-plus/icons-vue";
import type { FormInstance } from "element-plus";
import TymSchemaFieldService from "@/views/gen/service/TymSchemaFieldService";
import StdListAreaAction from "@/soa/std-series/StdListAreaAction.vue";
import StdListAreaTable from "@/soa/std-series/StdListAreaTable.vue";
import ComSeqFixer from "@/soa/console-framework/ComSeqFixer.vue";
import TymSchemaFieldApi from "@/views/gen/api/TymSchemaFieldApi";
import type { GetTymSchemaListVo } from "./api/TymSchemaApi";

const modalVisible = ref(false);
const typeSchemaId = ref<string>("");
const getTymSchemaListVo = ref<GetTymSchemaListVo | null>(null);
const emit = defineEmits<{
  (e: "on-close"): void;
}>();

// 使用markRaw包装图标组件，防止被Vue响应式系统处理
const EditIcon = markRaw(Edit);
const DeleteIcon = markRaw(Delete);

// 列表管理打包
const { listForm, listData, listTotal, listLoading, loadList, resetList, removeList } =
  TymSchemaFieldService.useTymSchemaFieldList(typeSchemaId);

// 模态框表单引用
const modalFormRef = ref<FormInstance>();

// 模态框打包
const {
  modalVisible: modalVisibleSelf,
  modalLoading,
  modalMode,
  modalForm,
  modalRules,
  openModal: openModalSelf,
  resetModal,
  submitModal,
} = TymSchemaFieldService.useTymSchemaFieldModal(modalFormRef, typeSchemaId, loadList);

/**
 * 关闭模态框
 */
const onClose = () => {
  emit("on-close");
};

defineExpose({
  /**
   * 打开模态框
   * @param typeSchemaId 类型映射方案ID
   */
  openModal: async (row: GetTymSchemaListVo | null) => {
    if (row) {
      getTymSchemaListVo.value = row;
    }
    modalVisible.value = true;
    typeSchemaId.value = row.id;
    await loadList();
  },
});
</script>

<style scoped></style>
