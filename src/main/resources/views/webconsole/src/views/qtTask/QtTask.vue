<template>
  <StdListContainer>
    <!-- 查询条件区域 -->
    <StdListAreaQuery>
      <el-form :model="listForm" inline class="flex justify-between">
        <div>
          <el-form-item label="任务分组ID">
            <el-input v-model="listForm.groupId" placeholder="输入任务分组ID" clearable />
          </el-form-item>
          <el-form-item label="任务名">
            <el-input v-model="listForm.name" placeholder="输入任务名" clearable />
          </el-form-item>
          <el-form-item label="状态">
            <el-select v-model="listForm.status" placeholder="请选择状态" clearable>
              <el-option label="正常" :value="0" />
              <el-option label="暂停" :value="1" />
            </el-select>
          </el-form-item>
        </div>
        <el-form-item>
          <el-button type="primary" @click="loadList" :disabled="listLoading">查询</el-button>
          <el-button @click="resetList" :disabled="listLoading">重置</el-button>
        </el-form-item>
      </el-form>
    </StdListAreaQuery>

    <!-- 操作按钮区域 -->
    <StdListAreaAction class="flex gap-2">
      <el-button type="success" @click="openModal('add', null)">新增任务调度表</el-button>
    </StdListAreaAction>

    <!-- 列表表格区域 -->
    <StdListAreaTable>
      <el-table :data="listData" stripe v-loading="listLoading" border height="100%">
        <el-table-column prop="id" label="任务ID" min-width="120" show-overflow-tooltip />
        <el-table-column prop="groupName" label="任务分组名" min-width="120" show-overflow-tooltip />
        <el-table-column prop="name" label="任务名" min-width="150" show-overflow-tooltip />
        <el-table-column prop="kind" label="任务类型" min-width="100" show-overflow-tooltip>
          <template #default="scope">
            {{ scope.row.kind === 0 ? "本地BEAN" : "远程HTTP" }}
          </template>
        </el-table-column>
        <el-table-column prop="cron" label="CRON表达式" min-width="150" show-overflow-tooltip />
        <el-table-column prop="target" label="调用目标" min-width="200" show-overflow-tooltip />
        <el-table-column prop="expireTime" label="任务有效期截止" min-width="160" show-overflow-tooltip />
        <el-table-column label="操作" fixed="right" min-width="180">
          <template #default="scope">
            <el-button link type="primary" size="small" @click="openModal('edit', scope.row)" :icon="EditIcon">
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
      v-model="modalVisible"
      :title="modalMode === 'edit' ? '编辑任务调度表' : '新增任务调度表'"
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
        label-width="120px"
        :validate-on-rule-change="false"
      >
        <el-form-item label="任务分组ID" prop="groupId">
          <el-input v-model="modalForm.groupId" placeholder="请输入任务分组ID" clearable />
        </el-form-item>
        <el-form-item label="任务分组名" prop="groupName">
          <el-input v-model="modalForm.groupName" placeholder="请输入任务分组名" clearable />
        </el-form-item>
        <el-form-item label="任务名" prop="name">
          <el-input v-model="modalForm.name" placeholder="请输入任务名" clearable />
        </el-form-item>
        <el-form-item label="任务类型" prop="kind">
          <el-select v-model="modalForm.kind" placeholder="请选择任务类型" clearable>
            <el-option label="本地BEAN" :value="0" />
            <el-option label="远程HTTP" :value="1" />
          </el-select>
        </el-form-item>
        <el-form-item label="CRON表达式" prop="cron">
          <el-input v-model="modalForm.cron" placeholder="请输入CRON表达式" clearable />
        </el-form-item>
        <el-form-item label="调用目标" prop="target">
          <el-input v-model="modalForm.target" placeholder="请输入BEAN代码或HTTP地址" clearable type="textarea" :rows="2" />
        </el-form-item>
        <el-form-item label="调用参数JSON" prop="targetParam">
          <el-input v-model="modalForm.targetParam" placeholder="请输入调用参数JSON" clearable type="textarea" :rows="3" />
        </el-form-item>
        <el-form-item label="请求方法" prop="reqMethod">
          <el-select v-model="modalForm.reqMethod" placeholder="请选择请求方法" clearable>
            <el-option label="GET" value="GET" />
            <el-option label="POST" value="POST" />
            <el-option label="PUT" value="PUT" />
            <el-option label="DELETE" value="DELETE" />
          </el-select>
        </el-form-item>
        <el-form-item label="并发执行" prop="concurrent">
          <el-select v-model="modalForm.concurrent" placeholder="请选择并发执行" clearable>
            <el-option label="允许" :value="0" />
            <el-option label="禁止" :value="1" />
          </el-select>
        </el-form-item>
        <el-form-item label="过期策略" prop="misfirePolicy">
          <el-select v-model="modalForm.misfirePolicy" placeholder="请选择过期策略" clearable>
            <el-option label="放弃执行" :value="0" />
            <el-option label="立即执行" :value="1" />
            <el-option label="全部执行" :value="2" />
          </el-select>
        </el-form-item>
        <el-form-item label="任务有效期截止" prop="expireTime">
          <el-date-picker
            v-model="modalForm.expireTime"
            type="datetime"
            placeholder="请选择任务有效期截止"
            clearable
            value-format="YYYY-MM-DD HH:mm:ss"
          />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-select v-model="modalForm.status" placeholder="请选择状态" clearable>
            <el-option label="正常" :value="0" />
            <el-option label="暂停" :value="1" />
          </el-select>
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
  </StdListContainer>
</template>

<script setup lang="ts">
import { ref, markRaw } from "vue";
import { Edit, Delete } from "@element-plus/icons-vue";
import type { FormInstance } from "element-plus";
import QtTaskService from "@/views/qtTask/service/QtTaskService.ts";
import StdListContainer from "@/soa/std-series/StdListContainer.vue";
import StdListAreaQuery from "@/soa/std-series/StdListAreaQuery.vue";
import StdListAreaAction from "@/soa/std-series/StdListAreaAction.vue";
import StdListAreaTable from "@/soa/std-series/StdListAreaTable.vue";

// 使用markRaw包装图标组件，防止被Vue响应式系统处理
const EditIcon = markRaw(Edit);
const DeleteIcon = markRaw(Delete);

// 列表管理打包
const { listForm, listData, listTotal, listLoading, loadList, resetList, removeList } = QtTaskService.useQtTaskList();

// 模态框表单引用
const modalFormRef = ref<FormInstance>();

// 模态框打包
const { modalVisible, modalLoading, modalMode, modalForm, modalRules, openModal, resetModal, submitModal } =
  QtTaskService.useQtTaskModal(modalFormRef, loadList);
</script>

<style scoped></style>
