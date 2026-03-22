<template>
  <div class="icon-picker">
    <el-input :model-value="modelValue" placeholder="选择图标" readonly @click="dialogVisible = true">
      <template #prefix>
        <Icon v-if="modelValue" :icon="modelValue" />
      </template>
      <template #append>
        <el-button :icon="Search" @click="dialogVisible = true" />
      </template>
    </el-input>

    <el-dialog v-model="dialogVisible" title="选择图标" width="800px" :close-on-click-modal="false">
      <div class="icon-picker-dialog">
        <el-input v-model="searchText" placeholder="搜索图标名称" clearable :prefix-icon="Search" class="search-input" />

        <el-tabs v-model="activeTab" class="icon-tabs">
          <el-tab-pane label="Element Plus" name="ep">
            <div class="icon-list">
              <div
                v-for="icon in filteredEpIcons"
                :key="icon"
                class="icon-item"
                :class="{ active: modelValue === icon }"
                @click="selectIcon(icon)"
              >
                <Icon :icon="icon" :width="24" :height="24" />
                <span class="icon-name">{{ getIconName(icon) }}</span>
              </div>
            </div>
          </el-tab-pane>

          <el-tab-pane label="Material Design" name="mdi">
            <div class="icon-list">
              <div
                v-for="icon in filteredMdiIcons"
                :key="icon"
                class="icon-item"
                :class="{ active: modelValue === icon }"
                @click="selectIcon(icon)"
              >
                <Icon :icon="icon" :width="24" :height="24" />
                <span class="icon-name">{{ getIconName(icon) }}</span>
              </div>
            </div>
          </el-tab-pane>

          <el-tab-pane label="Bootstrap" name="bi">
            <div class="icon-list">
              <div
                v-for="icon in filteredBiIcons"
                :key="icon"
                class="icon-item"
                :class="{ active: modelValue === icon }"
                @click="selectIcon(icon)"
              >
                <Icon :icon="icon" :width="24" :height="24" />
                <span class="icon-name">{{ getIconName(icon) }}</span>
              </div>
            </div>
          </el-tab-pane>

          <el-tab-pane label="Carbon" name="carbon">
            <div class="icon-list">
              <div
                v-for="icon in filteredCarbonIcons"
                :key="icon"
                class="icon-item"
                :class="{ active: modelValue === icon }"
                @click="selectIcon(icon)"
              >
                <Icon :icon="icon" :width="24" :height="24" />
                <span class="icon-name">{{ getIconName(icon) }}</span>
              </div>
            </div>
          </el-tab-pane>
        </el-tabs>
      </div>

      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmSelect">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { Icon } from "@iconify/vue";
import { Search } from "@element-plus/icons-vue";
import StdIconPickerService from "@/soa/std-series/service/StdIconPickerService";

const props = defineProps<{
  modelValue?: string | null;
}>();

const emit = defineEmits<{
  "update:modelValue": [value: string];
}>();

const {
  dialogVisible,
  searchText,
  activeTab,
  filteredEpIcons,
  filteredMdiIcons,
  filteredBiIcons,
  filteredCarbonIcons,
  getIconName,
  selectIcon,
  confirmSelect,
} = StdIconPickerService.useStdIconPicker(() => props.modelValue, emit);
</script>

<style scoped>
.icon-picker {
  width: 100%;
}

.icon-picker-dialog {
  min-height: 400px;
}

.search-input {
  margin-bottom: 16px;
}

.icon-tabs {
  height: 500px;
}

.icon-list {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(100px, 1fr));
  gap: 8px;
  max-height: 420px;
  overflow-y: auto;
  padding: 8px;
}

.icon-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 12px;
  border: 1px solid #e0e0e0;
  border-radius: 4px;
  cursor: pointer;
  transition: all 0.3s;
}

.icon-item:hover {
  border-color: #409eff;
  background-color: #ecf5ff;
}

.icon-item.active {
  border-color: #409eff;
  background-color: #409eff;
  color: white;
}

.icon-name {
  margin-top: 8px;
  font-size: 12px;
  text-align: center;
  word-break: break-all;
}

.icon-item.active .icon-name {
  color: white;
}
</style>
