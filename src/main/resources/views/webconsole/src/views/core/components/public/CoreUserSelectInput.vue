<template>
  <div class="core-user-select-input">
    <el-input
      v-model="displayValue"
      :placeholder="placeholder"
      :disabled="disabled"
      readonly
      @click="handleInputClick"
      :clearable="clearable"
      @clear="handleClear"
    >
      <template #suffix>
        <el-icon class="el-input__icon cursor-pointer">
          <component :is="SearchIcon" />
        </el-icon>
      </template>
    </el-input>

    <el-dialog
      v-model="visible"
      title="选择用户"
      width="75%"
      :close-on-click-modal="false"
      @close="handleDialogClose"
    >
      <div class="user-select-dialog">
        <splitpanes class="custom-theme">
          <pane size="20" min-size="10" max-size="40">
            <div class="mt-2 px-1">
              <OrgTree @on-select="onSelectOrg" :show-header="true" />
            </div>
          </pane>

          <pane size="80">
            <div class="right-content">
              <div class="query-form">
                <el-form :model="listForm" inline>
                  <el-form-item label="用户名">
                    <el-input v-model="listForm.username" placeholder="输入用户名" clearable style="width: 180px" />
                  </el-form-item>
                  <el-form-item label="状态">
                    <el-select v-model="listForm.status" placeholder="选择状态" clearable style="width: 180px">
                      <el-option label="正常" :value="0" />
                      <el-option label="封禁" :value="1" />
                    </el-select>
                  </el-form-item>
                  <el-form-item>
                    <el-button type="primary" @click="loadList" :disabled="listLoading">查询</el-button>
                    <el-button @click="resetList" :disabled="listLoading">重置</el-button>
                  </el-form-item>
                </el-form>
              </div>

              <div class="list-table">
                <el-table
                  :data="listData"
                  stripe
                  v-loading="listLoading"
                  border
                  :highlight-current-row="!multiple"
                  @current-change="handleCurrentChange"
                  @selection-change="handleSelectionChange"
                  @row-click="handleRowClick"
                  :row-style="getRowStyle"
                  style="cursor: pointer"
                  ref="tableRef"
                  row-key="id"
                >
                  <el-table-column v-if="multiple" type="selection" width="55" :reserve-selection="true" />
                  <el-table-column prop="username" label="用户名" min-width="120" />
                  <el-table-column prop="nickname" label="昵称" min-width="120" />
                  <el-table-column label="性别" min-width="80">
                    <template #default="scope">
                      <span v-if="scope.row.gender === 0">男</span>
                      <span v-if="scope.row.gender === 1">女</span>
                      <span v-if="scope.row.gender === 2">不愿透露</span>
                    </template>
                  </el-table-column>
                  <el-table-column prop="deptName" label="部门" min-width="150">
                    <template #default="scope">
                      <span v-if="scope.row.deptName">{{ scope.row.deptName }}</span>
                      <span v-else>-</span>
                    </template>
                  </el-table-column>
                  <el-table-column prop="phone" label="手机号" min-width="120" />
                  <el-table-column prop="email" label="邮箱" min-width="160" />
                  <el-table-column label="状态" min-width="80">
                    <template #default="scope">
                      <el-tag :type="scope.row.status === 0 ? 'success' : 'danger'" size="small">
                        {{ scope.row.status === 0 ? "正常" : "封禁" }}
                      </el-tag>
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
                    @size-change="handleSizeChange"
                    @current-change="handlePageChange"
                    background
                  />
                </div>
              </div>
            </div>
          </pane>
        </splitpanes>
      </div>

      <template #footer>
        <div class="dialog-footer">
          <el-button @click="handleCancel">取消</el-button>
          <el-button
            type="primary"
            @click="handleConfirm"
            :disabled="multiple ? selectedUsers.length === 0 : !selectedUser"
          >
            确定{{ multiple && selectedUsers.length > 0 ? `(${selectedUsers.length})` : "" }}
          </el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch, markRaw, nextTick } from "vue";
import { Search } from "@element-plus/icons-vue";
import { Splitpanes, Pane } from "splitpanes";
import type { ElTable } from "element-plus";
import "splitpanes/dist/splitpanes.css";
import OrgTree from "@/views/core/components/OrgTree.vue";
import CoreUserSelectInputService from "@/views/core/service/CoreUserSelectInputService";
import type { GetOrgTreeVo } from "@/views/core/api/OrgApi";
import type { GetUserListVo } from "@/views/core/api/UserApi";

const SearchIcon = markRaw(Search);
const tableRef = ref<InstanceType<typeof ElTable>>();

const props = withDefaults(
  defineProps<{
    modelValue?: string | string[] | null;
    placeholder?: string;
    disabled?: boolean;
    clearable?: boolean;
    multiple?: boolean;
  }>(),
  {
    modelValue: null,
    placeholder: "请选择用户",
    disabled: false,
    clearable: true,
    multiple: false,
  }
);

const emit = defineEmits<{
  (e: "update:modelValue", value: string | string[] | null): void;
  (e: "change", user: GetUserListVo | GetUserListVo[] | null): void;
}>();

const visible = ref(false);
const selectedOrgId = ref<string | null>(null);
const selectedUsers = ref<GetUserListVo[]>([]);

const { listForm, listData, listTotal, listLoading, loadList, resetList, selectedUser, displayValue } =
  CoreUserSelectInputService.useUserSelect(props.modelValue, props.multiple);

const handleInputClick = () => {
  if (props.disabled) {
    return;
  }
  visible.value = true;
  loadList(selectedOrgId.value);
  
  if (props.multiple) {
    nextTick(() => {
      restoreMultipleSelection();
    });
  }
};

const handleClear = () => {
  if (props.multiple) {
    selectedUsers.value = [];
    displayValue.value = "";
    emit("update:modelValue", []);
    emit("change", []);
    return;
  }
  emit("update:modelValue", null);
  emit("change", null);
};

const onSelectOrg = (org: GetOrgTreeVo | null) => {
  selectedOrgId.value = org?.id ?? null;
  loadList(selectedOrgId.value);
};

const handleCurrentChange = (row: GetUserListVo | null) => {
  if (!props.multiple) {
    selectedUser.value = row;
  }
};

const handleSelectionChange = (rows: GetUserListVo[]) => {
  if (props.multiple) {
    selectedUsers.value = rows;
  }
};

const handleRowClick = (row: GetUserListVo) => {
  if (!props.multiple) {
    return;
  }
  
  if (!tableRef.value) {
    return;
  }
  
  tableRef.value.toggleRowSelection(row);
};

const restoreMultipleSelection = () => {
  if (!props.multiple || !tableRef.value) {
    return;
  }
  
  tableRef.value.clearSelection();
  
  selectedUsers.value.forEach((user) => {
    const row = listData.value.find((item) => item.id === user.id);
    if (row) {
      tableRef.value?.toggleRowSelection(row, true);
    }
  });
};

const getRowStyle = ({ row }: { row: GetUserListVo }) => {
  if (selectedUser.value && selectedUser.value.id === row.id) {
    return { cursor: "pointer" };
  }
  return { cursor: "pointer" };
};

const handleSizeChange = (val: number) => {
  listForm.value.pageSize = val;
  loadList(selectedOrgId.value).then(() => {
    if (props.multiple) {
      nextTick(() => {
        restoreMultipleSelection();
      });
    }
  });
};

const handlePageChange = (val: number) => {
  listForm.value.pageNum = val;
  loadList(selectedOrgId.value).then(() => {
    if (props.multiple) {
      nextTick(() => {
        restoreMultipleSelection();
      });
    }
  });
};

const handleConfirm = () => {
  if (props.multiple) {
    if (selectedUsers.value.length === 0) {
      return;
    }
    const userIds = selectedUsers.value.map((u) => u.id);
    const displayText = selectedUsers.value.map((u) => `${u.username} (${u.nickname || "-"})`).join(", ");
    displayValue.value = displayText;
    emit("update:modelValue", userIds);
    emit("change", selectedUsers.value);
    visible.value = false;
    return;
  }
  
  if (!selectedUser.value) {
    return;
  }
  emit("update:modelValue", selectedUser.value.id);
  emit("change", selectedUser.value);
  visible.value = false;
};

const handleCancel = () => {
  visible.value = false;
};

const handleDialogClose = () => {
  selectedOrgId.value = null;
};

watch(
  () => props.modelValue,
  (newVal) => {
    if (!newVal) {
      selectedUser.value = null;
      selectedUsers.value = [];
      displayValue.value = "";
      return;
    }
    
    if (props.multiple && Array.isArray(newVal)) {
      if (newVal.length === 0) {
        selectedUsers.value = [];
        displayValue.value = "";
      }
    }
    
    if (!props.multiple && typeof newVal === "string") {
      if (!newVal) {
        selectedUser.value = null;
        displayValue.value = "";
      }
    }
  }
);

defineExpose({
  open: () => {
    visible.value = true;
    loadList(selectedOrgId.value);
  },
  close: () => {
    visible.value = false;
  },
});
</script>

<style scoped>
.core-user-select-input {
  width: 100%;
}

.user-select-dialog {
  height: 600px;
}

.right-content {
  height: 100%;
  display: flex;
  flex-direction: column;
  padding: 15px;
  box-sizing: border-box;
}

.query-form {
  margin-bottom: 10px;
  background-color: var(--el-fill-color-blank);
  border-bottom: 1px dashed var(--el-border-color-light);
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
}

:deep(.splitpanes.custom-theme) {
  border: none;
}

:deep(.splitpanes.custom-theme .splitpanes__pane) {
  background-color: transparent;
}

:deep(.splitpanes.custom-theme .splitpanes__splitter) {
  background-color: var(--el-border-color-extra-light);
  width: 1px;
  border: none;
  cursor: col-resize;
  position: relative;
  transition: background-color 0.2s;
}

:deep(.splitpanes.custom-theme .splitpanes__splitter:hover) {
  background-color: var(--el-color-primary);
  width: 3px;
}

:deep(.splitpanes.custom-theme .splitpanes__splitter:after) {
  content: "";
  position: absolute;
  left: -5px;
  right: -5px;
  top: 0;
  bottom: 0;
  z-index: 1;
}

:deep(.splitpanes__pane) {
  transition: none !important;
}

.cursor-pointer {
  cursor: pointer;
}
</style>
