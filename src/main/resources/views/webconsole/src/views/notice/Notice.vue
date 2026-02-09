<template>
  <div class="list-container">
    <div class="right-content">
      <!-- 查询条件区域 -->
      <div class="query-form">
        <el-form :model="listForm" inline class="flex justify-between">
          <div>
            <el-form-item label="标题">
              <el-input v-model="listForm.title" placeholder="输入标题" clearable />
            </el-form-item>
            <el-form-item label="种类: 0公告, 1业务提醒, 2私信">
              <el-input v-model.number="listForm.kind" placeholder="输入种类: 0公告, 1业务提醒, 2私信" clearable />
            </el-form-item>
            <el-form-item label="通知内容">
              <el-input v-model="listForm.content" placeholder="输入通知内容" clearable />
            </el-form-item>
            <el-form-item label="优先级: 0:低 1:中 2:高">
              <el-input v-model.number="listForm.priority" placeholder="输入优先级: 0:低 1:中 2:高" clearable />
            </el-form-item>
            <el-form-item label="业务类型/分类">
              <el-input v-model="listForm.category" placeholder="输入业务类型/分类" clearable />
            </el-form-item>
            <el-form-item label="发送人ID (NULL为系统)">
              <el-input v-model="listForm.senderId" placeholder="输入发送人ID (NULL为系统)" clearable />
            </el-form-item>
            <el-form-item label="发送人姓名">
              <el-input v-model="listForm.senderName" placeholder="输入发送人姓名" clearable />
            </el-form-item>
            <el-form-item label="跳转URL/路由地址">
              <el-input v-model="listForm.forward" placeholder="输入跳转URL/路由地址" clearable />
            </el-form-item>
            <el-form-item label="动态参数 (JSON格式)">
              <el-input v-model="listForm.params" placeholder="输入动态参数 (JSON格式)" clearable />
            </el-form-item>
          </div>
          <el-form-item>
            <el-button type="primary" @click="loadList" :disabled="listLoading">查询</el-button>
            <el-button @click="resetList" :disabled="listLoading">重置</el-button>
          </el-form-item>
        </el-form>
      </div>

      <!-- 操作按钮区域 -->
      <div class="action-buttons">
        <el-button type="success" @click="openModal('add', null)">新增</el-button>
      </div>

      <!-- 列表表格区域 -->
      <div class="list-table">
        <el-table :data="listData" stripe v-loading="listLoading" border>
          <el-table-column prop="id" label="主键ID" min-width="120" show-overflow-tooltip />
          <el-table-column prop="title" label="标题" min-width="120" show-overflow-tooltip />
          <el-table-column prop="kind" label="种类: 0公告, 1业务提醒, 2私信" min-width="120" show-overflow-tooltip />
          <el-table-column prop="content" label="通知内容" min-width="120" show-overflow-tooltip />
          <el-table-column prop="priority" label="优先级: 0:低 1:中 2:高" min-width="120" show-overflow-tooltip />
          <el-table-column prop="category" label="业务类型/分类" min-width="120" show-overflow-tooltip />
          <el-table-column prop="senderId" label="发送人ID (NULL为系统)" min-width="120" show-overflow-tooltip />
          <el-table-column prop="senderName" label="发送人姓名" min-width="120" show-overflow-tooltip />
          <el-table-column prop="forward" label="跳转URL/路由地址" min-width="120" show-overflow-tooltip />
          <el-table-column prop="params" label="动态参数 (JSON格式)" min-width="120" show-overflow-tooltip />
          <el-table-column prop="createTime" label="创建时间" min-width="120" show-overflow-tooltip />
          <el-table-column label="操作" fixed="right" min-width="180">
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

    <!-- 新增/编辑模态框 -->
    <el-dialog
      v-model="modalVisible"
      :title="modalMode === 'edit' ? '编辑消息表' : '新增消息表'"
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
          <el-input v-model="modalForm.title" placeholder="请输入标题" clearable />
        </el-form-item>
        <el-form-item label="种类: 0公告, 1业务提醒, 2私信" prop="kind">
          <el-input v-model.number="modalForm.kind" placeholder="请输入种类: 0公告, 1业务提醒, 2私信" clearable />
        </el-form-item>
        <el-form-item label="通知内容" prop="content">
          <el-input v-model="modalForm.content" placeholder="请输入通知内容" clearable />
        </el-form-item>
        <el-form-item label="优先级: 0:低 1:中 2:高" prop="priority">
          <el-input v-model.number="modalForm.priority" placeholder="请输入优先级: 0:低 1:中 2:高" clearable />
        </el-form-item>
        <el-form-item label="业务类型/分类" prop="category">
          <el-input v-model="modalForm.category" placeholder="请输入业务类型/分类" clearable />
        </el-form-item>
        <el-form-item label="发送人ID (NULL为系统)" prop="senderId">
          <el-input v-model="modalForm.senderId" placeholder="请输入发送人ID (NULL为系统)" clearable />
        </el-form-item>
        <el-form-item label="发送人姓名" prop="senderName">
          <el-input v-model="modalForm.senderName" placeholder="请输入发送人姓名" clearable />
        </el-form-item>
        <el-form-item label="跳转URL/路由地址" prop="forward">
          <el-input v-model="modalForm.forward" placeholder="请输入跳转URL/路由地址" clearable />
        </el-form-item>
        <el-form-item label="动态参数 (JSON格式)" prop="params">
          <el-input v-model="modalForm.params" placeholder="请输入动态参数 (JSON格式)" clearable />
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
import { ref, markRaw } from "vue";
import { Edit, Delete } from "@element-plus/icons-vue";
import type { FormInstance } from "element-plus";
import NoticeService from "@/views/notice/service/NoticeService.ts";

// 使用markRaw包装图标组件，防止被Vue响应式系统处理
const EditIcon = markRaw(Edit);
const DeleteIcon = markRaw(Delete);

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
