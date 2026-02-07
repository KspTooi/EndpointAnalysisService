<template>
  <el-dialog
    v-model="visible"
    :title="title"
    :width="width"
    :close-on-click-modal="false"
    append-to-body
    destroy-on-close
    class="core-user-select-modal"
  >
    <div class="modal-body">
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
                  <el-button type="primary" @click="loadList(selectedOrgId)" :disabled="listLoading">查询</el-button>
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
</template>

<script setup lang="ts">
import { ref, watch, nextTick } from "vue";
import { Splitpanes, Pane } from "splitpanes";
import type { ElTable } from "element-plus";
import "splitpanes/dist/splitpanes.css";
import OrgTree from "@/views/core/components/OrgTree.vue";
import CoreUserSelectInputService from "@/views/core/service/CoreUserSelectInputService";
import type { GetOrgTreeVo } from "@/views/core/api/OrgApi";
import type { GetUserListVo } from "@/views/core/api/UserApi";

const props = withDefaults(
  defineProps<{
    modelValue?: boolean;             // 弹窗显隐控制 (支持 v-model)
    title?: string;                   // 弹窗标题，默认为 "选择用户"
    width?: string | number;          // 弹窗宽度，默认为 "75%"
    multiple?: boolean;               // 是否开启多选模式，默认为 false
    defaultSelected?: string | string[]; // 默认选中的用户 ID 或 ID 数组
  }>(),
  {
    modelValue: false,
    title: "选择用户",
    width: "75%",
    multiple: false,
  }
);

const emit = defineEmits<{
  (e: "update:modelValue", visible: boolean): void;
  (e: "confirm", data: GetUserListVo | GetUserListVo[]): void;
  (e: "cancel"): void;
}>();

const visible = ref(false);
const selectedOrgId = ref<string | null>(null);
const selectedUsers = ref<GetUserListVo[]>([]);
const tableRef = ref<InstanceType<typeof ElTable>>();

const { listForm, listData, listTotal, listLoading, loadList, resetList, selectedUser } =
  CoreUserSelectInputService.useUserSelect(null, props.multiple);

// 用于 Promise 式调用的状态
let promiseResolve: (value: GetUserListVo | GetUserListVo[]) => void;
let promiseReject: (reason?: any) => void;

// 同步外部 visible
watch(
  () => props.modelValue,
  (val) => {
    visible.value = val;
    if (val) {
      nextTick(() => {
        loadList(selectedOrgId.value);
        initSelection();
      });
    }
  },
  { immediate: true }
);

// 同步内部 visible 到外部
watch(
  () => visible.value,
  (val) => {
    emit("update:modelValue", val);
  }
);

/**
 * 命令式调用方法：打开弹窗并等待选择结果
 */
const select = (): Promise<GetUserListVo | GetUserListVo[]> => {
  visible.value = true;
  nextTick(() => {
    loadList(selectedOrgId.value);
    initSelection();
  });

  return new Promise((resolve, reject) => {
    promiseResolve = resolve;
    promiseReject = reject;
  });
};

const initSelection = () => {
  if (!props.defaultSelected || !tableRef.value) {
    return;
  }

  if (props.multiple && Array.isArray(props.defaultSelected)) {
    nextTick(() => {
      restoreMultipleSelection();
    });
  }
};

const restoreMultipleSelection = () => {
  if (!props.multiple || !tableRef.value) {
    return;
  }

  tableRef.value.clearSelection();

  if (!Array.isArray(props.defaultSelected)) {
    return;
  }

  const selectedIds = props.defaultSelected;
  selectedIds.forEach((id) => {
    const row = listData.value.find((item) => item.id === id);
    if (row) {
      tableRef.value?.toggleRowSelection(row, true);
    }
  });
};

const onSelectOrg = (org: GetOrgTreeVo | null) => {
  selectedOrgId.value = org?.id ?? null;
  loadList(selectedOrgId.value).then(() => {
    if (props.multiple) {
      nextTick(() => {
        restoreMultipleSelection();
      });
    }
  });
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
  let result: GetUserListVo | GetUserListVo[] | null = null;

  if (props.multiple) {
    result = [...selectedUsers.value];
  }
  if (!props.multiple) {
    result = selectedUser.value;
  }

  if (!result) {
    return;
  }

  if (promiseResolve) {
    promiseResolve(result);
  }
  emit("confirm", result);
  visible.value = false;
};

const handleCancel = () => {
  visible.value = false;
  if (promiseReject) {
    promiseReject("cancel");
  }
  emit("cancel");
};

defineExpose({
  open: () => (visible.value = true),
  close: () => (visible.value = false),
  select,
});
</script>

<style scoped>
.core-user-select-modal :deep(.el-dialog__body) {
  padding: 10px 20px;
}

.modal-body {
  height: 600px;
  display: flex;
  flex-direction: column;
  border-radius: 0;
  overflow: hidden;
  border: 1px solid var(--el-border-color-lighter);
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

.dialog-footer {
  padding-top: 10px;
}

/* 直角风格适配 */
:deep(.el-dialog) {
  border-radius: 0;
  overflow: hidden;
}

:deep(.el-dialog__header) {
  margin-right: 0;
  padding: 20px;
  border-bottom: 1px solid var(--el-border-color-lighter);
}

:deep(.el-dialog__title) {
  font-weight: 600;
  font-size: 18px;
  color: var(--el-text-color-primary);
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
</style>
