<template>
  <el-dialog v-model="props.visible" title="管理权限" width="1000px" :close-on-click-modal="false" class="modal-centered" @close="resetModal">
    <div class="modal-content">
      <el-tabs v-model="tab">
        <el-tab-pane label="菜单视图" name="menu">
          <div class="filter-container" style="margin-bottom: 16px">
            <el-input v-model="menuFilterKeyword" placeholder="根据名称或所需权限过滤" clearable style="width: 300px">
              <template #prefix>
                <Icon icon="mdi:magnify" :width="16" :height="16" />
              </template>
            </el-input>
            <el-button type="primary" @click="loadList" style="margin-left: 10px">查询</el-button>
          </div>
          <div class="list-table">
            <el-table
              :data="listData"
              v-loading="listLoading"
              border
              row-key="id"
              default-expand-all
              ref="listTableRef"
              max-height="500"
              height="500"
              @selection-change="(val: GetGroupPermissionMenuViewVo[]) => (listSelected = val)"
              @row-click="handleRowClick"
            >
              <el-table-column type="selection" width="40" />
              <el-table-column label="菜单名称" prop="name" show-overflow-tooltip width="360">
                <template #default="scope">
                  <Icon v-if="scope.row.menuIcon" :icon="scope.row.menuIcon" :width="16" :height="16" style="margin-right: 8px; vertical-align: middle" />
                  {{ scope.row.name }}
                  <span v-if="scope.row.menuKind === 2" style="color: #999; font-size: 14px"> ({{ scope.row.menuBtnId }}) </span>
                </template>
              </el-table-column>
              <el-table-column label="类型" prop="menuKind" width="60">
                <template #default="scope">
                  <span v-if="scope.row.menuKind === 0" style="color: var(--el-color-info)">目录</span>
                  <span v-if="scope.row.menuKind === 1" style="color: var(--el-color-success)">菜单</span>
                  <span v-if="scope.row.menuKind === 2" style="color: var(--el-color-primary)">按钮</span>
                </template>
              </el-table-column>
              <el-table-column label="所需权限" prop="permission" show-overflow-tooltip>
                <template #default="scope">
                  <span v-if="scope.row.menuKind === 0" style="color: #999; font-size: 12px">不适用</span>
                  <span
                    v-else
                    :style="{
                      color:
                        scope.row.missingPermission === 1
                          ? '#f56c6c'
                          : scope.row.missingPermission === 2
                            ? '#e6a23c'
                            : scope.row.missingPermission === 0 && scope.row.permission !== '*'
                              ? '#67c23a'
                              : '',
                    }"
                  >
                    {{ scope.row.permission }}
                  </span>
                </template>
              </el-table-column>
              <el-table-column label="状态" prop="hasPermission" width="75">
                <template #default="scope">
                  <span v-if="scope.row.hasPermission === 1" style="color: #67c23a">已授权</span>
                  <span v-if="scope.row.hasPermission === 0" style="color: #f56c6c">未授权</span>
                </template>
              </el-table-column>
              <el-table-column label="操作" width="100">
                <template #default="scope">
                  <el-button type="primary" @click="" link v-if="scope.row.hasPermission === 0">授权</el-button>
                  <el-button type="danger" @click="" link v-if="scope.row.hasPermission === 1">取消授权</el-button>
                </template>
              </el-table-column>
            </el-table>
          </div>
        </el-tab-pane>
        <el-tab-pane label="节点视图" name="node">
          <div class="node-filter-container">
            <div class="filter-item">
              <el-input v-model="listNodeForm.keyword" placeholder="根据名称或权限代码过滤" clearable>
                <template #prefix>
                  <Icon icon="mdi:magnify" :width="16" :height="16" />
                </template>
              </el-input>
            </div>
            <div class="filter-item">
              <el-select v-model="listNodeForm.hasPermission" placeholder="是否已授权">
                <el-option label="全部" :value="null" />
                <el-option label="已授权" :value="1" />
                <el-option label="未授权" :value="0" />
              </el-select>
            </div>
            <div class="filter-item">
              <el-button type="primary" @click="loadNodeList">查询</el-button>
              <el-button @click="resetNodeList">重置</el-button>
            </div>
          </div>
          <div class="list-table">
            <el-table
              :data="listNodeData"
              v-loading="listNodeLoading"
              border
              row-key="id"
              ref="listNodeTableRef"
              max-height="450"
              height="450"
              @selection-change="(val: GetGroupPermissionNodeVo[]) => (listNodeSelected = val)"
              @row-click="handleNodeRowClick"
            >
              <el-table-column type="selection" width="40" />
              <el-table-column
                prop="code"
                label="权限代码"
                min-width="150"
                show-overflow-tooltip
                :show-overflow-tooltip-props="{
                  effect: 'dark',
                  placement: 'top',
                  enterable: false,
                }"
              />
              <el-table-column
                prop="name"
                label="权限名称"
                min-width="150"
                show-overflow-tooltip
                :show-overflow-tooltip-props="{
                  effect: 'dark',
                  placement: 'top',
                  enterable: false,
                }"
              />
              <el-table-column
                prop="description"
                label="权限描述"
                min-width="200"
                show-overflow-tooltip
                :show-overflow-tooltip-props="{
                  effect: 'dark',
                  placement: 'top',
                  enterable: false,
                }"
              />
              <el-table-column label="状态" prop="hasPermission" width="75">
                <template #default="scope">
                  <span v-if="scope.row.hasPermission === 1" style="color: #67c23a">已授权</span>
                  <span v-if="scope.row.hasPermission === 0" style="color: #f56c6c">未授权</span>
                </template>
              </el-table-column>
              <el-table-column label="操作" width="100">
                <template #default="scope">
                  <el-button type="primary" @click="" link v-if="scope.row.hasPermission === 0">授权</el-button>
                  <el-button type="danger" @click="" link v-if="scope.row.hasPermission === 1">取消授权</el-button>
                </template>
              </el-table-column>
            </el-table>

            <div class="pagination-container">
              <el-pagination
                v-model:current-page="listNodeForm.pageNum"
                v-model:page-size="listNodeForm.pageSize"
                :page-sizes="[10, 20, 50, 100]"
                layout="total, sizes, prev, pager, next, jumper"
                :total="listNodeTotal"
                @size-change="
                  (val: number) => {
                    listNodeForm.pageSize = val;
                    loadNodeList();
                  }
                "
                @current-change="
                  (val: number) => {
                    listNodeForm.pageNum = val;
                    loadNodeList();
                  }
                "
                background
              />
            </div>
          </div>
        </el-tab-pane>
      </el-tabs>
    </div>
    <template #footer>
      <div class="dialog-footer">
        <el-button @click="resetModal">取消</el-button>
        <el-button type="primary" @click="submitModal">应用权限</el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import type {
  GetGroupDetailsVo,
  GetGroupListVo,
  GetGroupPermissionMenuViewVo,
  GetGroupPermissionNodeDto,
  GetGroupPermissionNodeVo,
  GroupPermissionDefinitionVo,
} from "@/api/core/GroupApi";
import type { GetMenuTreeVo } from "@/api/core/MenuApi";
import MenuApi from "@/api/core/MenuApi";
import { Result } from "@/commons/entity/Result";
import { ElMessage, type TableInstance } from "element-plus";
import { nextTick, onMounted, ref, watch, reactive, computed } from "vue";
import { Icon } from "@iconify/vue";
import GroupApi from "@/api/core/GroupApi";
import PermissionApi, { type GetPermissionListVo, type GetPermissionListDto } from "@/api/core/PermissionApi";

const props = defineProps<{
  visible: boolean;
  row?: GetGroupListVo | null;
}>();

const emit = defineEmits<{
  (e: "close"): void;
}>();

//node显示权限节点，menu显示菜单
const tab = ref<"node" | "menu">("menu");

const listNodeTableRef = ref<TableInstance>();
const listNodeData = ref<GetGroupPermissionNodeVo[]>([]);
const listNodeSelected = ref<GetGroupPermissionNodeVo[]>([]);
const listNodeSelectedGlobal = ref<Set<string>>(new Set());
const listNodeLoading = ref(false);
const listNodeTotal = ref(0);
const listNodeForm = reactive<GetGroupPermissionNodeDto>({
  groupId: props.row?.id || "",
  keyword: null,
  hasPermission: null,
  pageNum: 1,
  pageSize: 20,
});

const listTableRef = ref<TableInstance>();
const listData = ref<GetGroupPermissionMenuViewVo[]>([]);
const listSelected = ref<GetGroupPermissionMenuViewVo[]>([]);
const listLoading = ref(true);
const menuFilterKeyword = ref("");
const groupDetails = ref<GetGroupDetailsVo | null>(null);

const loadList = async () => {
  listLoading.value = true;
  const result = await GroupApi.getGroupPermissionMenuView({ groupId: props.row?.id || "", keyword: menuFilterKeyword.value });
  listData.value = result;
  listLoading.value = false;
};

const loadNodeList = async () => {
  listNodeLoading.value = true;
  const result = await GroupApi.getGroupPermissionNodeView(listNodeForm);
  listNodeData.value = result.data;
  listNodeTotal.value = result.total;
  listNodeLoading.value = false;
};

const resetNodeList = () => {
  listNodeForm.keyword = null;
  listNodeForm.hasPermission = null;
  listNodeForm.pageNum = 1;
  listNodeForm.pageSize = 20;
  loadNodeList();
};

const resetModal = () => {
  listSelected.value = [];
  listTableRef.value?.clearSelection();
  listNodeSelected.value = [];
  listNodeTableRef.value?.clearSelection();
  listNodeSelectedGlobal.value.clear();
  menuFilterKeyword.value = "";
  emit("close");
};

/**
 * 处理行点击事件
 */
const handleRowClick = (row: GetMenuTreeVo, column: any, event: Event) => {
  const target = event.target as HTMLElement;
  if (target.closest(".el-checkbox") || target.closest(".el-table-column--selection")) {
    return;
  }

  const isSelected = listSelected.value.some((item) => item.id === row.id);
  listTableRef.value?.toggleRowSelection(row, !isSelected);
};

/**
 * 处理节点行点击事件
 */
const handleNodeRowClick = (row: GetPermissionListVo, column: any, event: Event) => {
  const target = event.target as HTMLElement;
  if (target.closest(".el-checkbox") || target.closest(".el-table-column--selection")) {
    return;
  }

  const isSelected = listNodeSelected.value.some((item) => item.id === row.id);
  listNodeTableRef.value?.toggleRowSelection(row, !isSelected);
};

/**
 * 应用权限
 */
const submitModal = async () => {
  let permissionCodes: string[] = [];

  if (tab.value === "menu") {
    //permissionCodes = grabSelectedPermissionCodes(listSelected.value);
  }

  if (tab.value === "node") {
    permissionCodes = Array.from(listNodeSelectedGlobal.value);
  }

  //应用权限
  const result = await GroupApi.applyPermission({
    groupId: props.row?.id || "",
    permissionCodes: permissionCodes,
  });

  if (Result.isSuccess(result)) {
    ElMessage.success("应用权限成功");
    if (tab.value === "menu") {
      await loadList();
    }
    if (tab.value === "node") {
      await loadNodeList();
    }
  }
};

watch(
  () => props.visible,
  async (newVal) => {
    if (newVal) {
      if (tab.value === "menu") {
        await loadList();
      }
      if (tab.value === "node") {
        await loadNodeList();
      }
    }
  }
);

watch(tab, async (newVal) => {
  if (newVal === "menu") {
    await loadList();
  }
  if (newVal === "node") {
    await loadNodeList();
  }
});

watch(menuFilterKeyword, () => {
  if (tab.value === "menu" && groupDetails.value) {
    nextTick(() => {
      listTableRef.value?.clearSelection();
    });
  }
});

watch(
  () => props.row,
  () => {
    if (props.row) {
      listNodeForm.groupId = props.row.id;
      console.log(props.row);
    }
  }
);
</script>

<style scoped>
:deep(.modal-centered) {
  margin: 0 auto;
  top: 50%;
  transform: translateY(-50%);
}

:deep(.el-table__body tr) {
  cursor: pointer;
}

.node-filter-container {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 16px;
  flex-wrap: wrap;
}

.node-filter-container .filter-item {
  display: flex;
  align-items: center;
}

.node-filter-container .filter-item .el-input {
  width: 300px;
}

.node-filter-container .filter-item .el-select {
  width: 150px;
}

.pagination-container {
  display: flex;
  justify-content: flex-end;
  margin-top: 20px;
  width: 100%;
}
</style>
