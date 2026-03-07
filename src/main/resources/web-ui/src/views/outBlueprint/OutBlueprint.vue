<template>
  <StdListContainer>
    <!-- 查询条件区域 -->
    <StdListAreaQuery>
      <el-form :model="listForm" inline class="flex justify-between">
        <div>
          <el-form-item label="蓝图名称">
            <el-input v-model="listForm.name" placeholder="输入蓝图名称" clearable />
          </el-form-item>
          <el-form-item label="项目名称">
            <el-input v-model="listForm.projectName" placeholder="输入项目名称" clearable />
          </el-form-item>
          <el-form-item label="蓝图编码">
            <el-input v-model="listForm.code" placeholder="输入蓝图编码" clearable />
          </el-form-item>
          <el-form-item label="SCM仓库地址">
            <el-input v-model="listForm.scmUrl" placeholder="输入SCM仓库地址" clearable />
          </el-form-item>
          <el-form-item label="SCM认证方式 0:公开 1:账号密码 2:SSH KEY">
            <el-input v-model.number="listForm.scmAuthKind" placeholder="输入SCM认证方式 0:公开 1:账号密码 2:SSH KEY" clearable />
          </el-form-item>
          <el-form-item label="SCM用户名">
            <el-input v-model="listForm.scmUsername" placeholder="输入SCM用户名" clearable />
          </el-form-item>
          <el-form-item label="SCM密码">
            <el-input v-model="listForm.scmPassword" placeholder="输入SCM密码" clearable />
          </el-form-item>
          <el-form-item label="SSH KEY">
            <el-input v-model="listForm.scmPk" placeholder="输入SSH KEY" clearable />
          </el-form-item>
          <el-form-item label="SCM分支">
            <el-input v-model="listForm.scmBranch" placeholder="输入SCM分支" clearable />
          </el-form-item>
          <el-form-item label="基准路径">
            <el-input v-model="listForm.scmBasePath" placeholder="输入基准路径" clearable />
          </el-form-item>
          <el-form-item label="蓝图备注">
            <el-input v-model="listForm.remark" placeholder="输入蓝图备注" clearable />
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
      <el-button type="success" @click="openModal('add', null)">新增输出蓝图表</el-button>
    </StdListAreaAction>

    <!-- 列表表格区域 -->
    <StdListAreaTable>
      <el-table :data="listData" stripe v-loading="listLoading" border height="100%">
        <el-table-column prop="id" label="主键ID" min-width="120" show-overflow-tooltip />
        <el-table-column prop="name" label="蓝图名称" min-width="120" show-overflow-tooltip />
        <el-table-column prop="projectName" label="项目名称" min-width="120" show-overflow-tooltip />
        <el-table-column prop="code" label="蓝图编码" min-width="120" show-overflow-tooltip />
        <el-table-column prop="scmUrl" label="SCM仓库地址" min-width="120" show-overflow-tooltip />
        <el-table-column prop="scmAuthKind" label="SCM认证方式 0:公开 1:账号密码 2:SSH KEY" min-width="120" show-overflow-tooltip />
        <el-table-column prop="scmUsername" label="SCM用户名" min-width="120" show-overflow-tooltip />
        <el-table-column prop="scmPassword" label="SCM密码" min-width="120" show-overflow-tooltip />
        <el-table-column prop="scmPk" label="SSH KEY" min-width="120" show-overflow-tooltip />
        <el-table-column prop="scmBranch" label="SCM分支" min-width="120" show-overflow-tooltip />
        <el-table-column prop="scmBasePath" label="基准路径" min-width="120" show-overflow-tooltip />
        <el-table-column prop="remark" label="蓝图备注" min-width="120" show-overflow-tooltip />
        <el-table-column prop="createTime" label="创建时间" min-width="120" show-overflow-tooltip />
        <el-table-column prop="updateTime" label="更新时间" min-width="120" show-overflow-tooltip />
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
      :title="modalMode === 'edit' ? '编辑输出蓝图表' : '新增输出蓝图表'"
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
        <el-form-item label="蓝图名称" prop="name">
          <el-input v-model="modalForm.name" placeholder="请输入蓝图名称" clearable />
        </el-form-item>
        <el-form-item label="项目名称" prop="projectName">
          <el-input v-model="modalForm.projectName" placeholder="请输入项目名称" clearable />
        </el-form-item>
        <el-form-item label="蓝图编码" prop="code">
          <el-input v-model="modalForm.code" placeholder="请输入蓝图编码" clearable />
        </el-form-item>
        <el-form-item label="SCM仓库地址" prop="scmUrl">
          <el-input v-model="modalForm.scmUrl" placeholder="请输入SCM仓库地址" clearable />
        </el-form-item>
        <el-form-item label="SCM认证方式 0:公开 1:账号密码 2:SSH KEY" prop="scmAuthKind">
          <el-input v-model.number="modalForm.scmAuthKind" placeholder="请输入SCM认证方式 0:公开 1:账号密码 2:SSH KEY" clearable />
        </el-form-item>
        <el-form-item label="SCM用户名" prop="scmUsername">
          <el-input v-model="modalForm.scmUsername" placeholder="请输入SCM用户名" clearable />
        </el-form-item>
        <el-form-item label="SCM密码" prop="scmPassword">
          <el-input v-model="modalForm.scmPassword" placeholder="请输入SCM密码" clearable />
        </el-form-item>
        <el-form-item label="SSH KEY" prop="scmPk">
          <el-input v-model="modalForm.scmPk" placeholder="请输入SSH KEY" clearable />
        </el-form-item>
        <el-form-item label="SCM分支" prop="scmBranch">
          <el-input v-model="modalForm.scmBranch" placeholder="请输入SCM分支" clearable />
        </el-form-item>
        <el-form-item label="基准路径" prop="scmBasePath">
          <el-input v-model="modalForm.scmBasePath" placeholder="请输入基准路径" clearable />
        </el-form-item>
        <el-form-item label="蓝图备注" prop="remark">
          <el-input v-model="modalForm.remark" placeholder="请输入蓝图备注" clearable />
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
import OutBlueprintService from "@/views/outBlueprint/service/OutBlueprintService.ts";
import StdListContainer from "@/soa/std-series/StdListContainer.vue";
import StdListAreaQuery from "@/soa/std-series/StdListAreaQuery.vue";
import StdListAreaAction from "@/soa/std-series/StdListAreaAction.vue";
import StdListAreaTable from "@/soa/std-series/StdListAreaTable.vue";

// 使用markRaw包装图标组件，防止被Vue响应式系统处理
const EditIcon = markRaw(Edit);
const DeleteIcon = markRaw(Delete);

// 列表管理打包
const { listForm, listData, listTotal, listLoading, loadList, resetList, removeList } = OutBlueprintService.useOutBlueprintList();

// 模态框表单引用
const modalFormRef = ref<FormInstance>();

// 模态框打包
const { modalVisible, modalLoading, modalMode, modalForm, modalRules, openModal, resetModal, submitModal } =
  OutBlueprintService.useOutBlueprintModal(modalFormRef, loadList);
</script>

<style scoped></style>
