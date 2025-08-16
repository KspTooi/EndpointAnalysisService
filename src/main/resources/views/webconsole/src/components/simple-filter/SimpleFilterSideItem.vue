<template>
  <div class="filter-item" :class="{ disabled: item.status === 1, selected: isSelected }" @click="handleClick">
    <div class="item-header">
      <div class="item-title">
        <span class="name">{{ item.name }}</span>
        <el-tag :class="['direction-tag', item.direction === 0 ? 'direction-request' : 'direction-response']" size="small" style="display: flex; align-items: center; gap: 4px">
          <IIconamoonCloudUploadThin style="width: 16px; height: 16px; vertical-align: -28%" />
          {{ getDirectionText(item.direction) }}
        </el-tag>
        <el-tag :class="['status-tag', item.status === 0 ? 'status-enabled' : 'status-disabled']" size="small">
          {{ getStatusText(item.status) }}
        </el-tag>
      </div>
      <div class="item-actions">
        <el-button type="danger" size="small" plain @click.stop="handleDelete" icon="Delete" />
      </div>
    </div>

    <div class="item-info">
      <div class="info-item">
        <span class="label">触发器:</span>
        <span class="value">{{ item.triggerCount }}</span>
      </div>
      <div class="info-item">
        <span class="label">操作:</span>
        <span class="value">{{ item.operationCount }}</span>
      </div>
    </div>

    <div class="item-time">
      <span class="create-time">{{ item.createTime }}</span>
    </div>
  </div>
</template>

<script setup lang="ts">
import { Delete } from "@element-plus/icons-vue";
import type { GetSimpleFilterListVo } from "@/api/SimpleFilterApi";
import { computed } from "vue";
import { SimpleFilterStore } from "@/store/SimpleFilterStore";

const props = defineProps<{
  item: GetSimpleFilterListVo;
}>();

const emit = defineEmits<{
  click: [];
  delete: [];
}>();

const filterStore = SimpleFilterStore();

const isSelected = computed(() => {
  return filterStore.getSelectedFilterId === props.item.id;
});

const handleClick = () => {
  emit("click");
};

const handleDelete = () => {
  emit("delete");
};

const getDirectionText = (direction: number) => {
  return direction === 0 ? "请求" : "响应";
};

const getDirectionTagType = (direction: number) => {
  return direction === 0 ? "primary" : "success";
};

const getStatusText = (status: number) => {
  return status === 0 ? "启用" : "禁用";
};

const getStatusTagType = (status: number) => {
  return status === 0 ? "success" : "danger";
};

</script>

<style scoped>
.filter-item {
  background: #ffffff;
  border: 1px solid #e4e7ed;
  border-radius: 8px;
  padding: 12px;
  margin-bottom: 8px;
  cursor: pointer;
  transition: all 0.2s ease;
}

.filter-item:hover {
  border-color: #409eff;
  box-shadow: 0 2px 8px rgba(64, 158, 255, 0.1);
}

.filter-item.disabled {
  background: #f5f7fa;
  opacity: 0.6;
}

.filter-item.selected {
  background: linear-gradient(135deg, #f0ffff 0%, rgb(211, 250, 255) 100%);
  border: 1px solid #5dc7cd;
  box-shadow: 0 2px 8px rgba(64, 158, 255, 0.2);
}

.filter-item.selected .name {
  color: #32c6ab;
  font-weight: 600;
}

.item-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 8px;
}

.item-title {
  display: flex;
  align-items: center;
  gap: 8px;
  flex: 1;
  min-width: 0;
}

.name {
  font-weight: 500;
  color: #303133;
  flex: 1;
  min-width: 0;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.item-actions {
  display: flex;
  gap: 4px;
  opacity: 0;
  transition: opacity 0.2s ease;
}

.filter-item:hover .item-actions {
  opacity: 1;
}

.item-info {
  display: flex;
  gap: 16px;
  margin-bottom: 8px;
}

.info-item {
  display: flex;
  align-items: center;
  gap: 4px;
}

.label {
  color: #909399;
  font-size: 12px;
}

.value {
  color: #409eff;
  font-weight: 500;
  font-size: 12px;
}

.item-time {
  display: flex;
  justify-content: flex-end;
}

.create-time {
  color: #c0c4cc;
  font-size: 11px;
}

/* 方向标签样式 */
.direction-tag.direction-request {
  background: linear-gradient(135deg, #e6f3ff 0%, #b8e0ff 100%) !important;
  color: #2970cc !important;
  border: 1px solid #7cc7ff !important;
}

.direction-tag.direction-response {
  background: linear-gradient(135deg, #f0f9ff 0%, #c7f0db 100%) !important;
  color: #239874 !important;
  border: 1px solid #76dcb3 !important;
}

/* 状态标签样式 */
.status-tag.status-enabled {
  background: linear-gradient(135deg, #ecfdf5 0%, #a7f3d0 100%) !important;
  color: #247861 !important;
  border: 1px solid #6cc5a8 !important;
}

.status-tag.status-disabled {
  background: linear-gradient(135deg, #fef2f2 0%, #fecaca 100%) !important;
  color: #991b1b !important;
  border: 1px solid #f87171 !important;
}
</style>
