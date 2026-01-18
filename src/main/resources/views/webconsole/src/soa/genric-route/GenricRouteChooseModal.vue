<template>
  <el-dialog v-model="modalVisible" title="选择路由" width="700px" :close-on-click-modal="false">
    <div class="route-choose-container">
      <!-- 搜索框 -->
      <el-input
        v-model="searchKeyword"
        placeholder="搜索路由路径、名称或面包屑"
        clearable
        style="margin-bottom: 15px"
        prefix-icon="el-icon-search"
      />

      <!-- 路由列表 -->
      <div class="route-list">
        <el-table
          :data="filteredRouteList"
          border
          highlight-current-row
          @current-change="(row: RouteEntryPo) => (selectedRoute = row)"
          height="400"
          :row-style="{ cursor: 'pointer' }"
        >
          <el-table-column label="路由路径" min-width="200">
            <template #default="scope">
              <span style="font-family: monospace">{{ buildPath(scope.row) }}</span>
            </template>
          </el-table-column>
          <el-table-column label="路由名称" prop="name" min-width="120" show-overflow-tooltip />
          <el-table-column label="面包屑" prop="breadcrumb" min-width="120" show-overflow-tooltip />
          <el-table-column label="业务域" prop="biz" width="100" show-overflow-tooltip>
            <template #default="scope">
              <span v-if="scope.row.biz">{{ scope.row.biz }}</span>
              <span v-else style="color: #999">-</span>
            </template>
          </el-table-column>
        </el-table>
      </div>

      <!-- 提示信息 -->
      <div v-if="filteredRouteList.length === 0" class="empty-tip">
        <el-empty description="未找到匹配的路由" :image-size="80" />
      </div>
    </div>

    <template #footer>
      <div class="dialog-footer">
        <el-button @click="cancelSelect">取消</el-button>
        <el-button type="primary" @click="confirmSelect" :disabled="!selectedRoute">确定</el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import GenricRouteChooseModalService from "@/soa/genric-route/service/GenricRouteChooseModalService";
import type { RouteEntryPo } from "./api/RouteEntryPo";

const modelValue = defineModel<string | null>({ default: null });

/**
 * 选择路由模态框打包
 */
const {
  modalVisible,
  selectedRoute,
  searchKeyword,
  filteredRouteList,
  buildPath,
  openModal,
  confirmSelect: originalConfirmSelect,
  cancelSelect: originalCancelSelect,
} = GenricRouteChooseModalService.useGenricRouteChooseModal();

/**
 * 确认选择（支持双向绑定）
 */
const confirmSelect = () => {
  if (!selectedRoute.value) {
    return;
  }
  modelValue.value = buildPath(selectedRoute.value);
  originalConfirmSelect();
};

/**
 * 取消选择（支持双向绑定）
 */
const cancelSelect = () => {
  modelValue.value = null;
  originalCancelSelect();
};

defineExpose({
  openModal,
});
</script>
