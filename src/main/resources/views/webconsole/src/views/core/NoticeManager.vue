<template>
  <div class="list-container">
    <div class="right-content">
      <!-- 查询条件区域 -->
      <div class="query-form">
        <el-form :model="listForm">
          <el-row>
            <el-col :span="5" :offset="1">
              <el-form-item label="标题">
                <el-input v-model="listForm.title" placeholder="请输入标题" clearable />
              </el-form-item>
            </el-col>
            <el-col :span="5" :offset="1">
              <el-form-item label="种类">
                <el-select v-model="listForm.kind" placeholder="请选择种类" clearable style="width: 100%">
                  <el-option label="公告" :value="0" />
                  <el-option label="业务提醒" :value="1" />
                  <el-option label="私信" :value="2" />
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :span="5" :offset="1">
              <el-form-item label="优先级">
                <el-select v-model="listForm.priority" placeholder="请选择优先级" clearable style="width: 100%">
                  <el-option label="低" :value="0" />
                  <el-option label="中" :value="1" />
                  <el-option label="高" :value="2" />
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :span="5" :offset="1">
              <el-form-item>
                <el-button type="primary" @click="loadList" :disabled="listLoading">查询</el-button>
                <el-button @click="resetList" :disabled="listLoading">重置</el-button>
                <ExpandButton v-model="uiState.isAdvancedSearch" :disabled="listLoading" />
              </el-form-item>
            </el-col>
          </el-row>
          <template v-if="uiState.isAdvancedSearch">
            <el-row>
              <el-col :span="5" :offset="1">
                <el-form-item label="通知内容">
                  <el-input v-model="listForm.content" placeholder="请输入通知内容" clearable />
                </el-form-item>
              </el-col>
              <el-col :span="5" :offset="1">
                <el-form-item label="业务类型">
                  <el-input v-model="listForm.category" placeholder="请输入业务类型" clearable />
                </el-form-item>
              </el-col>
              <el-col :span="5" :offset="1">
                <el-form-item label="发送人姓名">
                  <el-input v-model="listForm.senderName" placeholder="请输入发送人姓名" clearable />
                </el-form-item>
              </el-col>
            </el-row>
          </template>
        </el-form>
      </div>

      <!-- 操作按钮区域 -->
      <div class="action-buttons">
        <el-button type="success" @click="openModal('add', null)">新增消息</el-button>
      </div>

      <!-- 列表表格区域 -->
      <div class="list-table">
        <el-table :data="listData" stripe v-loading="listLoading" border>
          <el-table-column prop="title" label="标题" min-width="150" show-overflow-tooltip />
          <el-table-column prop="kind" label="种类" width="100" align="center">
            <template #default="scope">
              <el-tag v-if="scope.row.kind === 0" type="info">公告</el-tag>
              <el-tag v-else-if="scope.row.kind === 1" type="warning">业务提醒</el-tag>
              <el-tag v-else-if="scope.row.kind === 2" type="success">私信</el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="priority" label="优先级" width="80" align="center">
            <template #default="scope">
              <el-tag v-if="scope.row.priority === 0" type="info">低</el-tag>
              <el-tag v-else-if="scope.row.priority === 1" type="warning">中</el-tag>
              <el-tag v-else-if="scope.row.priority === 2" type="danger">高</el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="category" label="业务类型" min-width="120" show-overflow-tooltip>
            <template #default="scope">
              <span>{{ scope.row.category || "-" }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="senderName" label="发送人" min-width="100" show-overflow-tooltip>
            <template #default="scope">
              <span>{{ scope.row.senderName || "系统" }}</span>
            </template>
          </el-table-column>
          <el-table-column label="操作" fixed="right" width="150" align="center">
            <template #default="scope">
              <el-button link type="primary" size="small" @click="openModal('edit', scope.row)" :icon="EditIcon">
                编辑
              </el-button>
              <el-button link type="danger" size="small" @click="removeList(scope.row)" :icon="DeleteIcon"> 删除 </el-button>
            </template>
          </el-table-column>
        </el-table>

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
    </div>

    <CoreUserSelectInput />

    <!-- 新增/编辑模态框 -->
    <el-dialog
      v-model="modalVisible"
      :title="modalMode === 'edit' ? '编辑消息' : '新增消息'"
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
        <el-form-item label="标题" prop="title">
          <el-input v-model="modalForm.title" placeholder="请输入标题" maxlength="32" show-word-limit clearable />
        </el-form-item>
        <el-form-item label="种类" prop="kind">
          <el-select v-model="modalForm.kind" placeholder="请选择种类" style="width: 100%">
            <el-option label="公告" :value="0" />
            <el-option label="业务提醒" :value="1" />
            <el-option label="私信" :value="2" />
          </el-select>
        </el-form-item>
        <el-form-item label="通知内容" prop="content">
          <el-input v-model="modalForm.content" type="textarea" :rows="4" placeholder="请输入通知内容" clearable />
        </el-form-item>
        <el-form-item label="优先级" prop="priority">
          <el-select v-model="modalForm.priority" placeholder="请选择优先级" style="width: 100%">
            <el-option label="低" :value="0" />
            <el-option label="中" :value="1" />
            <el-option label="高" :value="2" />
          </el-select>
        </el-form-item>
        <el-form-item label="业务类型" prop="category">
          <el-input v-model="modalForm.category" placeholder="请输入业务类型" maxlength="32" show-word-limit clearable />
        </el-form-item>
        <el-form-item label="接收人" prop="receiverId">
          <CoreUserSelectInput multiple />
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
  </div>
</template>

<script setup lang="ts">
import { ref, markRaw, reactive } from "vue";
import { Edit, Delete } from "@element-plus/icons-vue";
import type { FormInstance } from "element-plus";
import NoticeService from "@/views/core/service/NoticeService.ts";
import ExpandButton from "@/components/common/ExpandButton.vue";
import CoreUserSelectInput from "@/views/core/components/public/CoreUserSelectInput.vue";

// 使用markRaw包装图标组件，防止被Vue响应式系统处理
const EditIcon = markRaw(Edit);
const DeleteIcon = markRaw(Delete);

// UI状态
const uiState = reactive({
  isAdvancedSearch: false,
});

// 列表管理打包
const { listForm, listData, listTotal, listLoading, loadList, resetList, removeList } = NoticeService.useNoticeList();

// 模态框表单引用
const modalFormRef = ref<FormInstance>();

// 模态框打包
const { modalVisible, modalLoading, modalMode, modalForm, modalRules, openModal, resetModal, submitModal } =
  NoticeService.useNoticeModal(modalFormRef, loadList);
</script>

<style scoped>
.list-container {
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
  background-color: var(--el-bg-color);
}

.right-content {
  height: 100%;
  display: flex;
  flex-direction: column;
  padding: 15px;
  box-sizing: border-box;
}

.action-buttons {
  margin-bottom: 15px;
  border-top: 2px dashed var(--el-border-color);
  padding-top: 15px;
}

.list-table {
  flex: 1;
  width: 100%;
  box-sizing: border-box;
  overflow-y: auto;
}

.pagination-container {
  display: flex;
  justify-content: flex-end;
  margin-top: 15px;
  padding-bottom: 15px;
}
</style>
