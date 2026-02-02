<template>
  <div class="list-container">
    <!-- 查询表单 -->
    <div class="query-form">
      <el-form :model="listForm">
        <el-row>
          <el-col :span="5" :offset="1">
            <el-form-item label="简称">
              <el-input v-model="listForm.sourceName" placeholder="请输入简称" clearable />
            </el-form-item>
          </el-col>
          <el-col :span="5" :offset="1">
            <el-form-item label="全称">
              <el-input v-model="listForm.sourceNameFull" placeholder="请输入全称" clearable />
            </el-form-item>
          </el-col>
          <el-col :span="5" :offset="1">
            <el-form-item label="英文简称">
              <el-input v-model="listForm.targetName" placeholder="请输入英文简称" clearable />
            </el-form-item>
          </el-col>
          <el-col :span="4" :offset="2">
            <el-form-item>
              <el-dropdown split-button type="primary" @click="loadList" :disabled="listLoading">
                查询
                <template #dropdown>
                  <el-dropdown-menu>
                    <el-dropdown-item :icon="DownloadIcon" @click="handleExport">导出查询结果</el-dropdown-item>
                  </el-dropdown-menu>
                </template>
              </el-dropdown>
              <el-button @click="resetList" :disabled="listLoading" style="margin-left: 12px">重置</el-button>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
    </div>

    <div class="action-buttons">
      <el-button type="success" @click="openModal('add', null)">新增标准词</el-button>
      <el-button
        type="danger"
        @click="() => removeListBatch(listSelected)"
        :disabled="listSelected.length === 0"
        :loading="listLoading"
      >
        删除选中项
      </el-button>
      <el-button type="primary" @click="importWizardRef?.openModal()" :icon="UploadIcon">导入标准词</el-button>
    </div>

    <!-- 标准词列表 -->
    <div class="list-table">
      <el-table
        :data="listData"
        stripe
        v-loading="listLoading"
        border
        @selection-change="(val: GetEpStdWordListVo[]) => (listSelected = val)"
      >
        <el-table-column type="selection" width="40" />
        <el-table-column prop="sourceName" label="简称" min-width="150" show-overflow-tooltip />
        <el-table-column prop="sourceNameFull" label="全称" min-width="200" show-overflow-tooltip />
        <el-table-column prop="targetName" label="英文简称" min-width="150" show-overflow-tooltip />
        <el-table-column prop="targetNameFull" label="英文全称" min-width="150" show-overflow-tooltip />
        <el-table-column prop="remark" label="备注" min-width="200" show-overflow-tooltip />
        <el-table-column prop="createTime" label="创建时间" min-width="180" />
        <el-table-column label="操作" fixed="right" min-width="140">
          <template #default="scope">
            <el-button link type="primary" size="small" @click="openModal('edit', scope.row)" :icon="EditIcon">
              编辑
            </el-button>
            <el-button link type="danger" size="small" @click="removeList(scope.row.id)" :icon="DeleteIcon"> 删除 </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination-container">
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
      </div>
    </div>

    <!-- 标准词编辑/新增模态框 -->
    <el-dialog
      v-model="modalVisible"
      :title="modalMode === 'edit' ? '编辑标准词' : '新增标准词'"
      width="600px"
      :close-on-click-modal="false"
      @close="
        resetModal();
        loadList();
      "
    >
      <el-form
        v-if="modalVisible"
        ref="modalFormRef"
        :model="modalForm"
        :rules="modalRules"
        label-width="100px"
        :validate-on-rule-change="false"
      >
        <el-form-item label="简称" prop="sourceName">
          <el-input v-model="modalForm.sourceName" placeholder="请输入简称" maxlength="128" show-word-limit />
        </el-form-item>
        <el-form-item label="全称" prop="sourceNameFull">
          <el-input v-model="modalForm.sourceNameFull" placeholder="请输入全称" maxlength="255" show-word-limit />
        </el-form-item>
        <el-form-item label="英文简称" prop="targetName">
          <el-input v-model="modalForm.targetName" placeholder="请输入英文简称" maxlength="128" show-word-limit />
        </el-form-item>
        <el-form-item label="英文全称" prop="targetNameFull">
          <el-input v-model="modalForm.targetNameFull" placeholder="请输入英文全称" maxlength="128" show-word-limit />
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input
            v-model="modalForm.remark"
            type="textarea"
            :rows="4"
            placeholder="请输入备注"
            maxlength="1000"
            show-word-limit
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="modalVisible = false">取消</el-button>
          <el-button type="primary" @click="submitModal" :loading="modalLoading">
            {{ modalMode === "add" ? "创建" : "保存" }}
          </el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 导入向导 -->
    <ImportWizardModal
      ref="importWizardRef"
      url="/epStdWord/importEpStdWord"
      templateCode="ep_std_word"
      @on-success="loadList"
      @on-close="loadList"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, markRaw } from "vue";
import { Edit, Delete, ArrowDown, Download, Upload } from "@element-plus/icons-vue";
import type { FormInstance } from "element-plus";
import EpStdWordService from "@/views/document/service/EpStdWordService.ts";
import EpStdWordApi, { type GetEpStdWordListVo } from "@/views/document/api/EpStdWordApi.ts";
import ImportWizardModal from "@/soa/console-framework/ImportWizardModal.vue";

// 使用markRaw包装图标组件
const EditIcon = markRaw(Edit);
const DeleteIcon = markRaw(Delete);
const DownloadIcon = markRaw(Download);
const UploadIcon = markRaw(Upload);
const ArrowDownIcon = markRaw(ArrowDown);

const importWizardRef = ref<InstanceType<typeof ImportWizardModal>>();

// 列表打包
const { listForm, listData, listTotal, listLoading, loadList, resetList, removeList, removeListBatch } =
  EpStdWordService.useEpStdWordList();

const handleExport = async () => {
  try {
    await EpStdWordApi.exportEpStdWord(listForm.value);
  } catch (e: any) {
    console.error(e);
  }
};

// 选中的列表项
const listSelected = ref<GetEpStdWordListVo[]>([]);

// 模态框表单引用
const modalFormRef = ref<FormInstance>();

// 模态框打包
const { modalVisible, modalLoading, modalMode, modalForm, modalRules, openModal, resetModal, submitModal } =
  EpStdWordService.useEpStdWordModal(modalFormRef, loadList);
</script>

<style scoped>
.list-container {
  padding: 20px;
  max-width: 100%;
  overflow-x: auto;
  width: 100%;
}

.list-table {
  margin-bottom: 20px;
  width: 100%;
  overflow-x: auto;
}

.pagination-container {
  display: flex;
  justify-content: flex-end;
  margin-top: 20px;
  width: 100%;
}

.action-buttons {
  margin-bottom: 15px;
  border-top: 2px dashed var(--el-border-color);
  padding-top: 15px;
}
</style>
