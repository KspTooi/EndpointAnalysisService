<template>
  <StdListContainer>
    <!-- 操作按钮区域（含 CDRC 回源） -->
    <StdListAreaAction class="flex gap-2">
      <el-button v-if="cdrcCanReturn" type="primary" @click="cdrcReturn">{{ cdrcReturnName }}</el-button>
      <el-button type="success" @click="openModalSelf('add', null)">新增方案字段</el-button>
    </StdListAreaAction>

    <!-- 当前方案上下文信息（由 CDRC send 传入） -->
    <div class="schema-info-bar">
      <div class="schema-info-item">
        <span class="schema-info-label">方案名称</span>
        <span class="schema-info-value">{{ schemaRow?.name ?? "—" }}</span>
      </div>
      <div class="schema-info-divider" />
      <div class="schema-info-item">
        <span class="schema-info-label">方案编码</span>
        <span class="schema-info-value">{{ schemaRow?.code ?? "—" }}</span>
      </div>
    </div>

    <!-- 列表表格区域 -->
    <StdListAreaTable>
      <el-table v-loading="listLoading" :data="listData" stripe border height="100%">
        <el-table-column prop="source" label="匹配源类型" min-width="120" show-overflow-tooltip />
        <el-table-column prop="target" label="匹配目标类型" min-width="120" show-overflow-tooltip />
        <el-table-column prop="seq" label="排序" min-width="65" show-overflow-tooltip>
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
        <el-table-column label="操作" fixed="right" min-width="80" align="center">
          <template #default="scope">
            <el-button link type="primary" size="small" :icon="EditIcon" @click="openModalSelf('edit', scope.row)">
              编辑
            </el-button>
            <el-button link type="danger" size="small" :icon="DeleteIcon" @click="removeList(scope.row)"> 删除 </el-button>
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
          background
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
        label-width="110px"
        :validate-on-rule-change="false"
      >
        <el-form-item label="匹配源类型" prop="source">
          <el-input v-model="modalForm.source" placeholder="请输入匹配源类型" clearable maxlength="80" show-word-limit />
        </el-form-item>
        <el-form-item label="匹配目标类型" prop="target">
          <el-input v-model="modalForm.target" placeholder="请输入匹配目标类型" clearable maxlength="80" show-word-limit />
        </el-form-item>
        <el-form-item label="排序" prop="seq">
          <el-input v-model.number="modalForm.seq" placeholder="请输入排序" clearable />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="modalVisibleSelf = false">取消</el-button>
          <el-button type="primary" :loading="modalLoading" @click="submitModal">
            {{ modalMode === "add" ? "创建" : "保存" }}
          </el-button>
        </div>
      </template>
    </el-dialog>
  </StdListContainer>
</template>

<script setup lang="ts">
import { ref, markRaw, onMounted } from "vue";
import { Edit, Delete } from "@element-plus/icons-vue";
import type { FormInstance } from "element-plus";
import TymSchemaFieldService from "@/views/assembly/service/TymSchemaFieldService";
import StdListContainer from "@/soa/std-series/StdListContainer.vue";
import StdListAreaAction from "@/soa/std-series/StdListAreaAction.vue";
import StdListAreaTable from "@/soa/std-series/StdListAreaTable.vue";
import ComSeqFixer from "@/soa/com-series/ComSeqFixer.vue";
import TymSchemaFieldApi from "@/views/assembly/api/TymSchemaFieldApi";
import type { GetTymSchemaListVo } from "@/views/assembly/api/TymSchemaApi";
import ComDirectRouteContext from "@/soa/com-series/service/ComDirectRouteContext.ts";

// 使用 CDRC：来源页跳转时写入上下文，本页通过 getCdrcQuery 取类型映射方案行
const { cdrcCanReturn, cdrcReturnName, cdrcReturn, getCdrcQuery } = ComDirectRouteContext.useDirectRouteContext();
const schemaRow = getCdrcQuery() as GetTymSchemaListVo | null;
const typeSchemaId = ref(schemaRow?.id ?? "");

// 使用markRaw包装图标组件，防止被Vue响应式系统处理
const EditIcon = markRaw(Edit);
const DeleteIcon = markRaw(Delete);

// 列表管理打包
const { listForm, listData, listTotal, listLoading, loadList, removeList } =
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

// 进入页面后按 typeSchemaId 拉取字段列表（须由 CDRC 传入有效方案 id）
onMounted(async () => {
  if (!typeSchemaId.value) {
    return;
  }
  await loadList();
});

/**
 * 原先通过 defineExpose({ openModal }) 由父页面打开外层对话框嵌套列表；
 * 现改为独立路由 tym-schema-field-manager（CDRC），字段新增/编辑仍为页内 el-dialog，与 TymSchema 等模态框风格一致。
 */
</script>

<style scoped>
.schema-info-bar {
  display: flex;
  align-items: center;
  padding: 10px 16px;
  margin-bottom: 8px;
  background: #fff;
  border-radius: 0;
  border: 1px solid #e4e7ed;
  border-left: 4px solid #409eff;
  flex-shrink: 0;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.06);
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
</style>
