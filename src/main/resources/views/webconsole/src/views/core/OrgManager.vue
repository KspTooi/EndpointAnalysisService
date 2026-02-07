<template>
  <StdListLayout show-persist-tip>
    <template #query>
      <el-form :model="queryForm">
        <el-row>
          <el-col :span="5" :offset="1">
            <el-form-item label="组织机构名称">
              <el-input v-model="queryForm.name" placeholder="输入组织机构名称查询" clearable />
            </el-form-item>
          </el-col>
          <el-col :span="5" :offset="1">
            <!-- 占位，保持布局一致性 -->
          </el-col>
          <el-col :span="5" :offset="1">
            <!-- 占位，保持布局一致性 -->
          </el-col>
          <el-col :span="3" :offset="3">
            <el-form-item>
              <el-button type="primary" @click="loadList" :disabled="listLoading">查询</el-button>
              <el-button @click="resetQuery" :disabled="listLoading">重置</el-button>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
    </template>

    <template #actions>
      <el-button type="success" @click="openModal('add', null)">创建组织机构</el-button>
    </template>

    <template #table>
      <el-table ref="listTableRef" :data="filteredData" stripe v-loading="listLoading" border row-key="id" default-expand-all height="100%">
        <el-table-column prop="name" label="组织机构名称" min-width="200" show-overflow-tooltip />
        <el-table-column prop="kind" label="类型" min-width="100">
          <template #default="scope">
            <el-tag :type="scope.row.kind === 1 ? 'primary' : 'info'">
              {{ scope.row.kind === 1 ? "企业" : "部门" }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="seq" label="排序" min-width="100">
          <template #default="scope">
            <SeqQuickPopover
              :id="scope.row.id"
              :seqField="'seq'"
              :getDetailApi="getOrgDetail"
              :editApi="editOrgSeq"
              :displayValue="scope.row.seq"
              :onSuccess="loadList"
            />
          </template>
        </el-table-column>
        <el-table-column label="操作" fixed="right" min-width="200">
          <template #default="scope">
            <el-button link type="success" size="small" @click="openModal('add-item', scope.row)" :icon="PlusIcon">
              新增子级
            </el-button>
            <el-button link type="primary" size="small" @click="openModal('edit', scope.row)" :icon="EditIcon">
              编辑
            </el-button>
            <el-button link type="danger" size="small" @click="removeList(scope.row.id)" :icon="DeleteIcon"> 删除 </el-button>
          </template>
        </el-table-column>
      </el-table>
    </template>
  </StdListLayout>

  <!-- 组织机构编辑/新增模态框 -->
  <el-dialog
    v-model="modalVisible"
    :title="
      modalMode === 'edit'
        ? '编辑' + modalKindName
        : modalMode === 'add-item'
          ? '新增子级' + modalKindName
          : '添加' + modalKindName
    "
    width="500px"
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
      <el-form-item :label="modalKindName + '名称'" prop="name">
        <el-input v-model="modalForm.name" :placeholder="'请输入' + modalKindName + '名称'" />
      </el-form-item>

      <el-form-item :label="modalKindName + '类型'" prop="kind" v-if="modalMode !== 'edit'">
        <el-radio-group v-model="modalForm.kind" :disabled="modalMode === 'add-item'">
          <el-radio :value="1">企业</el-radio>
          <el-radio :value="0">部门</el-radio>
        </el-radio-group>
      </el-form-item>

      <el-form-item label="上级组织" prop="parentId" v-show="modalKind == 0">
        <el-tree-select
          v-model="modalForm.parentId"
          :data="filterTreeSelectData"
          placeholder="请选择上级组织"
          clearable
          check-strictly
          :render-after-expand="true"
          :disabled="modalMode === 'add-item'"
          node-key="value"
        />
      </el-form-item>

      <!-- <el-form-item :label="modalKindName + '主管ID'" prop="principalId" v-if="modalForm.kind === 0">
        <el-input v-model="modalForm.principalId" placeholder="请输入主管ID" clearable />
      </el-form-item> -->
      <el-form-item :label="modalKindName + '排序'" prop="seq">
        <el-input-number v-model="modalForm.seq" :min="0" :max="655350" style="width: 100%" />
      </el-form-item>
    </el-form>
    <template #footer>
      <div class="dialog-footer">
        <el-button @click="modalVisible = false">取消</el-button>
        <el-button type="primary" @click="submitModal" :loading="modalLoading">
          {{ modalMode === "add" ? "创建" : modalMode === "add-item" ? "创建" : "保存" }}
        </el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { computed, ref } from "vue";
import { Edit, Delete, Plus } from "@element-plus/icons-vue";
import { markRaw } from "vue";
import type { FormInstance, TableInstance } from "element-plus";
import OrgManagerService from "@/views/core/service/OrgManagerService.ts";
import SeqQuickPopover from "@/soa/console-framework/SeqQuickPopover.vue";
import OrgApi from "@/views/core/api/OrgApi.ts";
import { Result } from "@/commons/entity/Result";
import StdListLayout from "@/soa/std-series/StdListLayout.vue";

const EditIcon = markRaw(Edit);
const DeleteIcon = markRaw(Delete);
const PlusIcon = markRaw(Plus);

const listTableRef = ref<TableInstance>();
const modalFormRef = ref<FormInstance>();

const { queryForm, listLoading, filteredData, treeSelectData, filterData, resetQuery, removeList, loadList } =
  OrgManagerService.useOrgTree(listTableRef);

const {
  modalKindName,
  modalKind,
  modalVisible,
  modalLoading,
  modalMode,
  modalForm,
  modalRules,
  openModal,
  resetModal,
  submitModal,
} = OrgManagerService.useOrgModal(modalFormRef, loadList, treeSelectData);

const filterTreeSelectData = computed(() => {
  const disableNode = (tree: any[], id: string): any[] => {
    return tree.map((node) => {
      if (node.value === id) {
        const disableAllChildren = (children: any[]): any[] => {
          return children.map((child) => ({
            ...child,
            disabled: true,
            children: child.children ? disableAllChildren(child.children) : undefined,
          }));
        };
        return { ...node, disabled: true, children: node.children ? disableAllChildren(node.children) : undefined };
      }
      if (node.children && node.children.length > 0) {
        return { ...node, children: disableNode(node.children, id) };
      }
      return node;
    });
  };

  if (modalMode.value === "edit" && modalForm.kind === 0) {
    const currentRootId = modalForm.rootId;
    const currentId = modalForm.id;

    for (let item of treeSelectData.value) {
      if (item.value !== currentRootId) {
        item.disabled = true;
      }
    }

    return disableNode(treeSelectData.value, currentId);
  }

  return treeSelectData.value;
});

const getOrgDetail = async (id: string) => {
  const result = await OrgApi.getOrgDetails({ id });
  if (!result) {
    throw new Error("获取数据失败");
  }
  return result;
};

const editOrgSeq = async (id: string, dto: any) => {
  const result = await OrgApi.editOrg(dto);
  if (!Result.isSuccess(result)) {
    throw new Error(result.message);
  }
};
</script>

<style scoped>
.form-tip {
  font-size: 12px;
  color: var(--el-color-info);
  margin-top: 5px;
}
</style>
