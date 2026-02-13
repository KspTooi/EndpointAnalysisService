<template>
  <el-dialog v-model="modalVisible" title="选择路由" width="800px" :close-on-click-modal="false">
    <div class="route-choose-container">
      <!-- 搜索框 -->
      <el-input
        v-model="searchKeyword"
        placeholder="通过 业务域、面包屑、路径、名称搜索"
        clearable
        style="margin-bottom: 15px"
        prefix-icon="Search"
      />

      <!-- 路由列表 -->
      <div class="route-list">
        <el-table
          ref="tableRef"
          :data="filteredRouteList"
          border
          highlight-current-row
          :row-key="buildRowKey"
          @selection-change="handleSelectionChange"
          @select-all="handleSelectAll"
          @row-click="handleRowClick"
          height="400"
          :row-style="{ cursor: 'pointer' }"
        >
          <el-table-column type="selection" width="40" />
          <el-table-column label="业务域" prop="biz" width="100" show-overflow-tooltip>
            <template #default="scope">
              <span v-if="scope.row.biz">{{ scope.row.biz }}</span>
              <span v-if="!scope.row.biz" style="color: #999">不可用</span>
            </template>
          </el-table-column>
          <el-table-column label="面包屑" min-width="120" show-overflow-tooltip>
            <template #default="scope">{{ scope.row.meta?.breadcrumb ?? "" }}</template>
          </el-table-column>
          <el-table-column label="路由路径" min-width="200">
            <template #default="scope">
              <span>{{ scope.row.buildPath() }}</span>
            </template>
          </el-table-column>
          <el-table-column label="路由名称" prop="name" min-width="80" show-overflow-tooltip />
        </el-table>
      </div>
    </div>

    <template #footer>
      <div class="dialog-footer">
        <el-button @click="cancelSelect">退出</el-button>
        <el-button type="primary" @click="confirmSelect" :disabled="!selectedRoute">选择指定</el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { nextTick, ref, watch } from "vue";
import type { TableInstance } from "element-plus";
import GenricRouteChooseModalService from "@/soa/genric-route/service/GenricRouteChooseModalService";
import type { RouteEntryPo } from "@/soa/genric-route/api/RouteEntryPo";

const modelValue = defineModel<string | null>({ default: null });
const searchKeywordModel = defineModel<string>("searchKeyword", { default: "" });

/**
 * 选择路由模态框打包
 */
const {
  modalVisible,
  selectedRoute,
  searchKeyword,
  filteredRouteList,
  openModal,
  confirmSelect: serviceConfirmSelect,
  cancelSelect: serviceCancelSelect,
} = GenricRouteChooseModalService.useGenricRouteChooseModal();

const tableRef = ref<TableInstance>();
const syncingSelection = ref(false);
const selectingAll = ref(false);

type RouteEntryRow = RouteEntryPo & { __rowKey?: string };

const buildRowKey = (row: RouteEntryPo): string => {
  const rowKey = (row as RouteEntryRow).__rowKey;
  if (rowKey) {
    return rowKey;
  }
  return `${row.buildPath()}::${row.name || ""}`;
};

watch(
  searchKeywordModel,
  (value) => {
    if (value === searchKeyword.value) {
      return;
    }
    searchKeyword.value = value;
  },
  { immediate: true }
);

watch(searchKeyword, (value) => {
  if (value === searchKeywordModel.value) {
    return;
  }
  searchKeywordModel.value = value;
});

watch(modalVisible, (visible) => {
  if (!visible) {
    return;
  }

  if (!modelValue.value) {
    return;
  }

  nextTick(() => {
    const route = filteredRouteList.value.find((r) => r.buildPath() === modelValue.value);
    if (route) {
      applySingleSelection(route);
    }
  });
});

const applySingleSelection = (row: RouteEntryPo | null) => {
  if (!tableRef.value) {
    selectedRoute.value = row;
    return;
  }

  syncingSelection.value = true;
  tableRef.value.clearSelection();

  if (!row) {
    tableRef.value.setCurrentRow(null);
    selectedRoute.value = null;
    syncingSelection.value = false;
    return;
  }

  tableRef.value.toggleRowSelection(row, true);
  tableRef.value.setCurrentRow(row);
  selectedRoute.value = row;
  syncingSelection.value = false;
};

const handleSelectionChange = (rows: RouteEntryPo[]) => {
  if (syncingSelection.value) {
    return;
  }
  if (selectingAll.value) {
    return;
  }

  if (!rows || rows.length === 0) {
    applySingleSelection(null);
    return;
  }

  const dataList = filteredRouteList.value;
  if (dataList && dataList.length > 0 && rows.length === dataList.length) {
    applySingleSelection(rows[0]);
    return;
  }

  const lastRow = rows[rows.length - 1];
  applySingleSelection(lastRow);
};

const handleSelectAll = (rows: RouteEntryPo[]) => {
  if (syncingSelection.value) {
    return;
  }

  selectingAll.value = true;

  if (!rows || rows.length === 0) {
    applySingleSelection(null);
    nextTick(() => {
      selectingAll.value = false;
    });
    return;
  }

  applySingleSelection(rows[0]);
  nextTick(() => {
    selectingAll.value = false;
  });
};

const handleRowClick = (row: RouteEntryPo) => {
  if (syncingSelection.value) {
    return;
  }

  if (!row) {
    return;
  }

  if (selectedRoute.value && selectedRoute.value.buildPath() === row.buildPath()) {
    applySingleSelection(null);
    return;
  }

  applySingleSelection(row);
};

/**
 * 确认选择（支持双向绑定）
 */
const confirmSelect = () => {
  if (!selectedRoute.value) {
    return;
  }
  modelValue.value = selectedRoute.value.buildPath();
  serviceConfirmSelect();
};

/**
 * 取消选择（支持双向绑定）
 */
const cancelSelect = () => {
  serviceCancelSelect();
};

defineExpose({
  openModal,
});
</script>
