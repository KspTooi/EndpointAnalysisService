<template>
  <StdListContainer>
    <!-- 查询条件区域 -->
    <StdListAreaQuery>
      <el-form :model="listForm" inline class="flex justify-between">
        <div>
          <el-form-item label="任务分组">
            <el-select v-model="listForm.groupId" placeholder="选择任务分组" clearable class="!w-[200px]" filterable>
              <el-option v-for="item in groupListData" :key="item.id" :label="item.name" :value="item.id" />
            </el-select>
          </el-form-item>
          <el-form-item label="任务名">
            <el-input v-model="listForm.name" placeholder="输入任务名" clearable />
          </el-form-item>
          <el-form-item label="状态">
            <el-select v-model="listForm.status" placeholder="请选择状态" clearable class="!w-[180px]">
              <el-option label="正常" :value="0" />
              <el-option label="暂停" :value="1" />
            </el-select>
          </el-form-item>
        </div>
        <el-form-item>
          <div class="w-[140px]">
            <el-button type="primary" @click="loadList" :disabled="listLoading">查询</el-button>
            <el-button @click="resetList" :disabled="listLoading">重置</el-button>
          </div>
        </el-form-item>
      </el-form>
    </StdListAreaQuery>

    <!-- 操作按钮区域 -->
    <StdListAreaAction class="flex gap-2">
      <el-button type="success" @click="openModal('add', null)">新增任务调度</el-button>
    </StdListAreaAction>

    <!-- 列表表格区域 -->
    <StdListAreaTable>
      <el-table :data="listData" stripe v-loading="listLoading" border height="100%">
        <el-table-column prop="name" label="任务名称" min-width="150" show-overflow-tooltip />
        <el-table-column prop="groupName" label="任务分组" min-width="120" show-overflow-tooltip>
          <template #default="scope">
            <span class="text-gray-400 text-sm" v-if="!scope.row.groupName"> 未配置 </span>
            <span v-else>{{ scope.row.groupName }} </span>
          </template>
        </el-table-column>
        <el-table-column prop="kind" label="任务类型" min-width="100" show-overflow-tooltip>
          <template #default="scope">
            {{ scope.row.kind === 0 ? "本地BEAN" : "远程HTTP" }}
          </template>
        </el-table-column>
        <el-table-column prop="cron" label="CRON表达式" min-width="150" show-overflow-tooltip />
        <el-table-column prop="target" label="调用目标" min-width="200" show-overflow-tooltip />
        <el-table-column prop="expireTime" label="任务有效期截止" min-width="160" show-overflow-tooltip />
        <el-table-column prop="status" label="状态" min-width="90" show-overflow-tooltip>
          <template #default="scope">
            <el-tag :type="scope.row.status === 0 ? 'success' : 'info'">
              {{ scope.row.status === 0 ? "正常" : "暂停" }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" min-width="160" show-overflow-tooltip />
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
      :title="modalMode === 'edit' ? '编辑任务调度' : '新增任务调度'"
      width="850px"
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
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="任务名称" prop="name">
              <el-input v-model="modalForm.name" placeholder="请输入任务名称" clearable maxlength="80" show-word-limit />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="任务分组" prop="groupId">
              <el-select v-model="modalForm.groupId" placeholder="选择任务分组" clearable filterable>
                <el-option v-for="item in groupListData" :key="item.id" :label="item.name" :value="item.id" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="任务类型" prop="kind">
              <el-radio-group v-model="modalForm.kind">
                <el-radio label="本地BEAN" :value="0" />
                <el-radio label="远程HTTP(暂不支持)" :value="1" disabled />
              </el-radio-group>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="请求方法" prop="reqMethod" v-if="modalForm.kind === 1">
              <el-select v-model="modalForm.reqMethod" placeholder="请选择请求方法" clearable style="width: 100%">
                <el-option label="GET" value="GET" />
                <el-option label="POST" value="POST" />
                <el-option label="PUT" value="PUT" />
                <el-option label="DELETE" value="DELETE" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row>
          <el-col :span="24">
            <el-form-item label="调用目标" prop="target">
              <el-input
                v-if="modalForm.kind === 1"
                v-model="modalForm.target"
                placeholder="请输入HTTP地址"
                clearable
                type="text"
                :rows="2"
                maxlength="1000"
                show-word-limit
              />
              <el-select
                v-if="modalForm.kind === 0"
                v-model="modalForm.target"
                placeholder="请选择本地任务Bean"
                clearable
                style="width: 100%"
                filterable
                v-loading="localBeanListLoading"
              >
                <el-option v-for="item in localBeanListData" :key="item.name" :label="item.name" :value="item.name">
                  <span>{{ item.name }}</span>
                  <span class="text-gray-400 text-sm ml-2">{{ item.fullClassName }}</span>
                </el-option>
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col>
            <el-form-item label="CRON表达式" prop="cron">
              <el-input v-model="modalForm.cron" placeholder="请输入CRON表达式" clearable maxlength="64" show-word-limit />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="过期策略" prop="misfirePolicy">
              <el-select v-model="modalForm.misfirePolicy" placeholder="请选择过期策略" clearable style="width: 100%">
                <el-option label="放弃执行" :value="0" />
                <el-option label="立即执行" :value="1" />
                <el-option label="全部执行" :value="2" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="任务有效期截止" prop="expireTime">
              <el-date-picker
                v-model="modalForm.expireTime"
                type="datetime"
                placeholder="请选择任务有效期截止"
                clearable
                value-format="YYYY-MM-DD HH:mm:ss"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="并发执行" prop="concurrent">
              <el-radio-group v-model="modalForm.concurrent">
                <el-radio label="允许" :value="0" />
                <el-radio label="禁止" :value="1" />
              </el-radio-group>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="状态" prop="status">
              <el-radio-group v-model="modalForm.status">
                <el-radio label="正常" :value="0" />
                <el-radio label="暂停" :value="1" />
              </el-radio-group>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row>
          <el-col :span="24">
            <el-form-item label="调用参数JSON" prop="targetParam">
              <el-input v-model="modalForm.targetParam" placeholder="请输入调用参数JSON" clearable type="textarea" :rows="3" />
            </el-form-item>
          </el-col>
        </el-row>
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
import { ref, markRaw, watch } from "vue";
import { Edit, Delete } from "@element-plus/icons-vue";
import type { FormInstance } from "element-plus";
import QtTaskService from "@/views/qt/service/QtTaskService.ts";
import StdListContainer from "@/soa/std-series/StdListContainer.vue";
import StdListAreaQuery from "@/soa/std-series/StdListAreaQuery.vue";
import StdListAreaAction from "@/soa/std-series/StdListAreaAction.vue";
import StdListAreaTable from "@/soa/std-series/StdListAreaTable.vue";
import QtTaskGroupService from "./service/QtTaskGroupService";

// 使用markRaw包装图标组件，防止被Vue响应式系统处理
const EditIcon = markRaw(Edit);
const DeleteIcon = markRaw(Delete);

// 列表管理打包
const { listForm, listData, listTotal, listLoading, loadList, resetList, removeList } = QtTaskService.useQtTaskList();

// 任务分组列表管理打包
const { listData: groupListData, listForm: groupListForm, loadList: loadGroupList } = QtTaskGroupService.useQtTaskGroupList();
groupListForm.value.pageSize = 10000;

// 模态框表单引用
const modalFormRef = ref<FormInstance>();

// 模态框打包
const { modalVisible, modalLoading, modalMode, modalForm, modalRules, openModal, resetModal, submitModal } =
  QtTaskService.useQtTaskModal(modalFormRef, loadList);

// 本地任务Bean列表管理打包
const {
  listData: localBeanListData,
  listLoading: localBeanListLoading,
  loadList: loadLocalBeanList,
} = QtTaskService.useLocalBeanList();

//模态框打开时加载本地任务Bean列表
watch(
  modalVisible,
  async (newVal) => {
    if (newVal) {
      await loadLocalBeanList();
    }
  },
  { immediate: true }
);
</script>

<style scoped></style>
