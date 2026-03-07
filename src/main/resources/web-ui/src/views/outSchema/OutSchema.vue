<template>
  <StdListContainer>
    <!-- 查询条件区域 -->
    <StdListAreaQuery>
      <el-form :model="listForm" inline class="flex justify-between">
        <div>
          <el-form-item label="数据源ID">
            <el-input v-model="listForm.dataSourceId" placeholder="输入数据源ID" clearable />
          </el-form-item>
          <el-form-item label="类型映射方案ID">
            <el-input v-model="listForm.typeSchemaId" placeholder="输入类型映射方案ID" clearable />
          </el-form-item>
          <el-form-item label="输入SCM ID">
            <el-input v-model="listForm.inputScmId" placeholder="输入输入SCM ID" clearable />
          </el-form-item>
          <el-form-item label="输出SCM ID">
            <el-input v-model="listForm.outputScmId" placeholder="输入输出SCM ID" clearable />
          </el-form-item>
          <el-form-item label="输出方案名称">
            <el-input v-model="listForm.name" placeholder="输入输出方案名称" clearable />
          </el-form-item>
          <el-form-item label="模型名称">
            <el-input v-model="listForm.modelName" placeholder="输入模型名称" clearable />
          </el-form-item>
          <el-form-item label="数据源表名">
            <el-input v-model="listForm.tableName" placeholder="输入数据源表名" clearable />
          </el-form-item>
          <el-form-item label="移除表前缀">
            <el-input v-model="listForm.removeTablePrefix" placeholder="输入移除表前缀" clearable />
          </el-form-item>
          <el-form-item label="权限码前缀">
            <el-input v-model="listForm.permCodePrefix" placeholder="输入权限码前缀" clearable />
          </el-form-item>
          <el-form-item label="写出策略 0:不覆盖 1:覆盖">
            <el-input v-model.number="listForm.policyOverride" placeholder="输入写出策略 0:不覆盖 1:覆盖" clearable />
          </el-form-item>
          <el-form-item label="输入基准路径">
            <el-input v-model="listForm.baseInput" placeholder="输入输入基准路径" clearable />
          </el-form-item>
          <el-form-item label="输出基准路径">
            <el-input v-model="listForm.baseOutput" placeholder="输入输出基准路径" clearable />
          </el-form-item>
          <el-form-item label="备注">
            <el-input v-model="listForm.remark" placeholder="输入备注" clearable />
          </el-form-item>
          <el-form-item label="字段数(原始)">
            <el-input v-model.number="listForm.fieldCountOrigin" placeholder="输入字段数(原始)" clearable />
          </el-form-item>
          <el-form-item label="字段数(聚合)">
            <el-input v-model.number="listForm.fieldCountPoly" placeholder="输入字段数(聚合)" clearable />
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
      <el-button type="success" @click="openModal('add', null)">新增输出方案表</el-button>
    </StdListAreaAction>

    <!-- 列表表格区域 -->
    <StdListAreaTable>
      <el-table :data="listData" stripe v-loading="listLoading" border height="100%">
        <el-table-column prop="id" label="主键ID" min-width="120" show-overflow-tooltip />
        <el-table-column prop="dataSourceId" label="数据源ID" min-width="120" show-overflow-tooltip />
        <el-table-column prop="typeSchemaId" label="类型映射方案ID" min-width="120" show-overflow-tooltip />
        <el-table-column prop="inputScmId" label="输入SCM ID" min-width="120" show-overflow-tooltip />
        <el-table-column prop="outputScmId" label="输出SCM ID" min-width="120" show-overflow-tooltip />
        <el-table-column prop="name" label="输出方案名称" min-width="120" show-overflow-tooltip />
        <el-table-column prop="modelName" label="模型名称" min-width="120" show-overflow-tooltip />
        <el-table-column prop="tableName" label="数据源表名" min-width="120" show-overflow-tooltip />
        <el-table-column prop="removeTablePrefix" label="移除表前缀" min-width="120" show-overflow-tooltip />
        <el-table-column prop="permCodePrefix" label="权限码前缀" min-width="120" show-overflow-tooltip />
        <el-table-column prop="policyOverride" label="写出策略 0:不覆盖 1:覆盖" min-width="120" show-overflow-tooltip />
        <el-table-column prop="baseInput" label="输入基准路径" min-width="120" show-overflow-tooltip />
        <el-table-column prop="baseOutput" label="输出基准路径" min-width="120" show-overflow-tooltip />
        <el-table-column prop="remark" label="备注" min-width="120" show-overflow-tooltip />
        <el-table-column prop="fieldCountOrigin" label="字段数(原始)" min-width="120" show-overflow-tooltip />
        <el-table-column prop="fieldCountPoly" label="字段数(聚合)" min-width="120" show-overflow-tooltip />
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
      :title="modalMode === 'edit' ? '编辑输出方案表' : '新增输出方案表'"
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
        <el-form-item label="数据源ID" prop="dataSourceId">
          <el-input v-model="modalForm.dataSourceId" placeholder="请输入数据源ID" clearable />
        </el-form-item>
        <el-form-item label="类型映射方案ID" prop="typeSchemaId">
          <el-input v-model="modalForm.typeSchemaId" placeholder="请输入类型映射方案ID" clearable />
        </el-form-item>
        <el-form-item label="输入SCM ID" prop="inputScmId">
          <el-input v-model="modalForm.inputScmId" placeholder="请输入输入SCM ID" clearable />
        </el-form-item>
        <el-form-item label="输出SCM ID" prop="outputScmId">
          <el-input v-model="modalForm.outputScmId" placeholder="请输入输出SCM ID" clearable />
        </el-form-item>
        <el-form-item label="输出方案名称" prop="name">
          <el-input v-model="modalForm.name" placeholder="请输入输出方案名称" clearable />
        </el-form-item>
        <el-form-item label="模型名称" prop="modelName">
          <el-input v-model="modalForm.modelName" placeholder="请输入模型名称" clearable />
        </el-form-item>
        <el-form-item label="数据源表名" prop="tableName">
          <el-input v-model="modalForm.tableName" placeholder="请输入数据源表名" clearable />
        </el-form-item>
        <el-form-item label="移除表前缀" prop="removeTablePrefix">
          <el-input v-model="modalForm.removeTablePrefix" placeholder="请输入移除表前缀" clearable />
        </el-form-item>
        <el-form-item label="权限码前缀" prop="permCodePrefix">
          <el-input v-model="modalForm.permCodePrefix" placeholder="请输入权限码前缀" clearable />
        </el-form-item>
        <el-form-item label="写出策略 0:不覆盖 1:覆盖" prop="policyOverride">
          <el-input v-model.number="modalForm.policyOverride" placeholder="请输入写出策略 0:不覆盖 1:覆盖" clearable />
        </el-form-item>
        <el-form-item label="输入基准路径" prop="baseInput">
          <el-input v-model="modalForm.baseInput" placeholder="请输入输入基准路径" clearable />
        </el-form-item>
        <el-form-item label="输出基准路径" prop="baseOutput">
          <el-input v-model="modalForm.baseOutput" placeholder="请输入输出基准路径" clearable />
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="modalForm.remark" placeholder="请输入备注" clearable />
        </el-form-item>
        <el-form-item label="字段数(原始)" prop="fieldCountOrigin">
          <el-input v-model.number="modalForm.fieldCountOrigin" placeholder="请输入字段数(原始)" clearable />
        </el-form-item>
        <el-form-item label="字段数(聚合)" prop="fieldCountPoly">
          <el-input v-model.number="modalForm.fieldCountPoly" placeholder="请输入字段数(聚合)" clearable />
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
import OutSchemaService from "@/views/outSchema/service/OutSchemaService.ts";
import StdListContainer from "@/soa/std-series/StdListContainer.vue";
import StdListAreaQuery from "@/soa/std-series/StdListAreaQuery.vue";
import StdListAreaAction from "@/soa/std-series/StdListAreaAction.vue";
import StdListAreaTable from "@/soa/std-series/StdListAreaTable.vue";

// 使用markRaw包装图标组件，防止被Vue响应式系统处理
const EditIcon = markRaw(Edit);
const DeleteIcon = markRaw(Delete);

// 列表管理打包
const { listForm, listData, listTotal, listLoading, loadList, resetList, removeList } = OutSchemaService.useOutSchemaList();

// 模态框表单引用
const modalFormRef = ref<FormInstance>();

// 模态框打包
const { modalVisible, modalLoading, modalMode, modalForm, modalRules, openModal, resetModal, submitModal } =
  OutSchemaService.useOutSchemaModal(modalFormRef, loadList);
</script>

<style scoped></style>
