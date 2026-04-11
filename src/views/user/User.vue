<template>
  <StdListContainer>
    <!-- 查询条件区域 -->
    <StdListAreaQuery>
      <el-form :model="listForm" inline class="flex justify-between">
        <div>
          <el-form-item label="用户名">
            <el-input v-model="listForm.username" placeholder="输入用户名" clearable />
          </el-form-item>
          <el-form-item label="密码">
            <el-input v-model="listForm.password" placeholder="输入密码" clearable />
          </el-form-item>
          <el-form-item label="昵称">
            <el-input v-model="listForm.nickname" placeholder="输入昵称" clearable />
          </el-form-item>
          <el-form-item label="性别 0:男 1:女 2:不愿透露">
            <el-input v-model="listForm.gender" placeholder="输入性别 0:男 1:女 2:不愿透露" clearable />
          </el-form-item>
          <el-form-item label="手机号码">
            <el-input v-model="listForm.phone" placeholder="输入手机号码" clearable />
          </el-form-item>
          <el-form-item label="邮箱">
            <el-input v-model="listForm.email" placeholder="输入邮箱" clearable />
          </el-form-item>
          <el-form-item label="登录次数">
            <el-input v-model="listForm.loginCount" placeholder="输入登录次数" clearable />
          </el-form-item>
          <el-form-item label="用户状态 0:正常 1:封禁">
            <el-input v-model="listForm.status" placeholder="输入用户状态 0:正常 1:封禁" clearable />
          </el-form-item>
          <el-form-item label="最后登录时间">
            <el-input v-model="listForm.lastLoginTime" placeholder="输入最后登录时间" clearable />
          </el-form-item>
          <el-form-item label="所属企业ID">
            <el-input v-model="listForm.rootId" placeholder="输入所属企业ID" clearable />
          </el-form-item>
          <el-form-item label="所属企业名称">
            <el-input v-model="listForm.rootName" placeholder="输入所属企业名称" clearable />
          </el-form-item>
          <el-form-item label="部门ID">
            <el-input v-model="listForm.deptId" placeholder="输入部门ID" clearable />
          </el-form-item>
          <el-form-item label="部门名称">
            <el-input v-model="listForm.deptName" placeholder="输入部门名称" clearable />
          </el-form-item>
          <el-form-item label="已激活的公司ID(兼容字段)">
            <el-input v-model="listForm.activeCompanyId" placeholder="输入已激活的公司ID(兼容字段)" clearable />
          </el-form-item>
          <el-form-item label="已激活的环境ID(兼容字段)">
            <el-input v-model="listForm.activeEnvId" placeholder="输入已激活的环境ID(兼容字段)" clearable />
          </el-form-item>
          <el-form-item label="用户头像附件ID">
            <el-input v-model="listForm.avatarAttachId" placeholder="输入用户头像附件ID" clearable />
          </el-form-item>
          <el-form-item label="内置用户 0:否 1:是">
            <el-input v-model="listForm.isSystem" placeholder="输入内置用户 0:否 1:是" clearable />
          </el-form-item>
          <el-form-item label="数据版本号">
            <el-input v-model="listForm.dataVersion" placeholder="输入数据版本号" clearable />
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
      <el-button type="success" @click="openModal('add', null)">新增${model.comment}</el-button>
    </StdListAreaAction>

    <!-- 列表表格区域 -->
    <StdListAreaTable>
      <el-table :data="listData" stripe v-loading="listLoading" border height="100%">
        <el-table-column prop="id" label="用户ID" min-width="120" show-overflow-tooltip />
        <el-table-column prop="username" label="用户名" min-width="120" show-overflow-tooltip />
        <el-table-column prop="password" label="密码" min-width="120" show-overflow-tooltip />
        <el-table-column prop="nickname" label="昵称" min-width="120" show-overflow-tooltip />
        <el-table-column prop="gender" label="性别 0:男 1:女 2:不愿透露" min-width="120" show-overflow-tooltip />
        <el-table-column prop="phone" label="手机号码" min-width="120" show-overflow-tooltip />
        <el-table-column prop="email" label="邮箱" min-width="120" show-overflow-tooltip />
        <el-table-column prop="loginCount" label="登录次数" min-width="120" show-overflow-tooltip />
        <el-table-column prop="status" label="用户状态 0:正常 1:封禁" min-width="120" show-overflow-tooltip />
        <el-table-column prop="lastLoginTime" label="最后登录时间" min-width="120" show-overflow-tooltip />
        <el-table-column prop="rootId" label="所属企业ID" min-width="120" show-overflow-tooltip />
        <el-table-column prop="rootName" label="所属企业名称" min-width="120" show-overflow-tooltip />
        <el-table-column prop="deptId" label="部门ID" min-width="120" show-overflow-tooltip />
        <el-table-column prop="deptName" label="部门名称" min-width="120" show-overflow-tooltip />
        <el-table-column prop="activeCompanyId" label="已激活的公司ID(兼容字段)" min-width="120" show-overflow-tooltip />
        <el-table-column prop="activeEnvId" label="已激活的环境ID(兼容字段)" min-width="120" show-overflow-tooltip />
        <el-table-column prop="avatarAttachId" label="用户头像附件ID" min-width="120" show-overflow-tooltip />
        <el-table-column prop="isSystem" label="内置用户 0:否 1:是" min-width="120" show-overflow-tooltip />
        <el-table-column prop="dataVersion" label="数据版本号" min-width="120" show-overflow-tooltip />
        <el-table-column prop="createTime" label="创建时间" min-width="120" show-overflow-tooltip />
        <el-table-column prop="updateTime" label="修改时间" min-width="120" show-overflow-tooltip />
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
      :title="modalMode === 'edit' ? '编辑${model.comment}' : '新增${model.comment}'"
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
        <el-form-item label="用户名" prop="username">
          <el-input v-model="modalForm.username" placeholder="请输入用户名" clearable />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input v-model="modalForm.password" placeholder="请输入密码" clearable />
        </el-form-item>
        <el-form-item label="昵称" prop="nickname">
          <el-input v-model="modalForm.nickname" placeholder="请输入昵称" clearable />
        </el-form-item>
        <el-form-item label="性别 0:男 1:女 2:不愿透露" prop="gender">
          <el-input v-model="modalForm.gender" placeholder="请输入性别 0:男 1:女 2:不愿透露" clearable />
        </el-form-item>
        <el-form-item label="手机号码" prop="phone">
          <el-input v-model="modalForm.phone" placeholder="请输入手机号码" clearable />
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="modalForm.email" placeholder="请输入邮箱" clearable />
        </el-form-item>
        <el-form-item label="登录次数" prop="loginCount">
          <el-input v-model="modalForm.loginCount" placeholder="请输入登录次数" clearable />
        </el-form-item>
        <el-form-item label="用户状态 0:正常 1:封禁" prop="status">
          <el-input v-model="modalForm.status" placeholder="请输入用户状态 0:正常 1:封禁" clearable />
        </el-form-item>
        <el-form-item label="最后登录时间" prop="lastLoginTime">
          <el-input v-model="modalForm.lastLoginTime" placeholder="请输入最后登录时间" clearable />
        </el-form-item>
        <el-form-item label="所属企业ID" prop="rootId">
          <el-input v-model="modalForm.rootId" placeholder="请输入所属企业ID" clearable />
        </el-form-item>
        <el-form-item label="所属企业名称" prop="rootName">
          <el-input v-model="modalForm.rootName" placeholder="请输入所属企业名称" clearable />
        </el-form-item>
        <el-form-item label="部门ID" prop="deptId">
          <el-input v-model="modalForm.deptId" placeholder="请输入部门ID" clearable />
        </el-form-item>
        <el-form-item label="部门名称" prop="deptName">
          <el-input v-model="modalForm.deptName" placeholder="请输入部门名称" clearable />
        </el-form-item>
        <el-form-item label="已激活的公司ID(兼容字段)" prop="activeCompanyId">
          <el-input v-model="modalForm.activeCompanyId" placeholder="请输入已激活的公司ID(兼容字段)" clearable />
        </el-form-item>
        <el-form-item label="已激活的环境ID(兼容字段)" prop="activeEnvId">
          <el-input v-model="modalForm.activeEnvId" placeholder="请输入已激活的环境ID(兼容字段)" clearable />
        </el-form-item>
        <el-form-item label="用户头像附件ID" prop="avatarAttachId">
          <el-input v-model="modalForm.avatarAttachId" placeholder="请输入用户头像附件ID" clearable />
        </el-form-item>
        <el-form-item label="内置用户 0:否 1:是" prop="isSystem">
          <el-input v-model="modalForm.isSystem" placeholder="请输入内置用户 0:否 1:是" clearable />
        </el-form-item>
        <el-form-item label="数据版本号" prop="dataVersion">
          <el-input v-model="modalForm.dataVersion" placeholder="请输入数据版本号" clearable />
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
import UserService from "@/views/user/service/UserService.ts";
import StdListContainer from "@/soa/std-series/StdListContainer.vue";
import StdListAreaQuery from "@/soa/std-series/StdListAreaQuery.vue";
import StdListAreaAction from "@/soa/std-series/StdListAreaAction.vue";
import StdListAreaTable from "@/soa/std-series/StdListAreaTable.vue";

// 使用markRaw包装图标组件，防止被Vue响应式系统处理
const EditIcon = markRaw(Edit);
const DeleteIcon = markRaw(Delete);

// 列表管理打包
const { listForm, listData, listTotal, listLoading, loadList, resetList, removeList } = UserService.useUserList();

// 模态框表单引用
const modalFormRef = ref<FormInstance>();

// 模态框打包
const { modalVisible, modalLoading, modalMode, modalForm, modalRules, openModal, resetModal, submitModal } =
  UserService.useUserModal(modalFormRef, loadList);
</script>

<style scoped></style>
