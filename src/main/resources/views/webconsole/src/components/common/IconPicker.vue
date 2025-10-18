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
              <div v-for="icon in filteredEpIcons" :key="icon" class="icon-item" :class="{ active: modelValue === icon }" @click="selectIcon(icon)">
                <Icon :icon="icon" :width="24" :height="24" />
                <span class="icon-name">{{ getIconName(icon) }}</span>
              </div>
            </div>
          </el-tab-pane>

          <el-tab-pane label="Material Design" name="mdi">
            <div class="icon-list">
              <div v-for="icon in filteredMdiIcons" :key="icon" class="icon-item" :class="{ active: modelValue === icon }" @click="selectIcon(icon)">
                <Icon :icon="icon" :width="24" :height="24" />
                <span class="icon-name">{{ getIconName(icon) }}</span>
              </div>
            </div>
          </el-tab-pane>

          <el-tab-pane label="Bootstrap" name="bi">
            <div class="icon-list">
              <div v-for="icon in filteredBiIcons" :key="icon" class="icon-item" :class="{ active: modelValue === icon }" @click="selectIcon(icon)">
                <Icon :icon="icon" :width="24" :height="24" />
                <span class="icon-name">{{ getIconName(icon) }}</span>
              </div>
            </div>
          </el-tab-pane>

          <el-tab-pane label="Carbon" name="carbon">
            <div class="icon-list">
              <div v-for="icon in filteredCarbonIcons" :key="icon" class="icon-item" :class="{ active: modelValue === icon }" @click="selectIcon(icon)">
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
import { ref, computed } from "vue";
import { Icon } from "@iconify/vue";
import { Search } from "@element-plus/icons-vue";

const props = defineProps<{
  modelValue?: string;
}>();

const emit = defineEmits<{
  "update:modelValue": [value: string];
}>();

const dialogVisible = ref(false);
const searchText = ref("");
const activeTab = ref("ep");
const tempSelectedIcon = ref("");

const epIcons = [
  "ep:aim",
  "ep:alarm-clock",
  "ep:apple",
  "ep:arrow-down",
  "ep:arrow-left",
  "ep:arrow-right",
  "ep:arrow-up",
  "ep:avatar",
  "ep:back",
  "ep:bell",
  "ep:box",
  "ep:briefcase",
  "ep:calendar",
  "ep:camera",
  "ep:chat-dot-round",
  "ep:chat-line-round",
  "ep:check",
  "ep:circle-check",
  "ep:circle-close",
  "ep:close",
  "ep:coin",
  "ep:collection",
  "ep:comment",
  "ep:connection",
  "ep:cpu",
  "ep:credit-card",
  "ep:crop",
  "ep:data-analysis",
  "ep:data-board",
  "ep:data-line",
  "ep:delete",
  "ep:discount",
  "ep:document",
  "ep:download",
  "ep:edit",
  "ep:eleme",
  "ep:expand",
  "ep:files",
  "ep:filter",
  "ep:flag",
  "ep:folder",
  "ep:folder-opened",
  "ep:goods",
  "ep:grid",
  "ep:guide",
  "ep:help",
  "ep:hide",
  "ep:histogram",
  "ep:home-filled",
  "ep:hot-water",
  "ep:house",
  "ep:info-filled",
  "ep:key",
  "ep:link",
  "ep:list",
  "ep:location",
  "ep:lock",
  "ep:management",
  "ep:menu",
  "ep:message",
  "ep:message-box",
  "ep:microphone",
  "ep:monitor",
  "ep:more",
  "ep:mouse",
  "ep:notification",
  "ep:odometer",
  "ep:office-building",
  "ep:operation",
  "ep:opportunity",
  "ep:paperclip",
  "ep:picture",
  "ep:pie-chart",
  "ep:place",
  "ep:plus",
  "ep:position",
  "ep:postcard",
  "ep:printer",
  "ep:promotion",
  "ep:question-filled",
  "ep:reading",
  "ep:refresh",
  "ep:remove",
  "ep:right",
  "ep:scale-to-original",
  "ep:search",
  "ep:select",
  "ep:sell",
  "ep:service",
  "ep:set-up",
  "ep:setting",
  "ep:share",
  "ep:ship",
  "ep:shopping-bag",
  "ep:shopping-cart",
  "ep:sort",
  "ep:star",
  "ep:success-filled",
  "ep:suitcase",
  "ep:switch",
  "ep:ticket",
  "ep:tickets",
  "ep:timer",
  "ep:tools",
  "ep:trophy",
  "ep:turn-off",
  "ep:upload",
  "ep:user",
  "ep:user-filled",
  "ep:van",
  "ep:video-camera",
  "ep:view",
  "ep:wallet",
  "ep:warning",
  "ep:warning-filled",
  "ep:zoom-in",
  "ep:zoom-out",
];

const mdiIcons = [
  "mdi:account",
  "mdi:account-group",
  "mdi:alert",
  "mdi:apps",
  "mdi:archive",
  "mdi:arrow-down",
  "mdi:arrow-left",
  "mdi:arrow-right",
  "mdi:arrow-up",
  "mdi:bell",
  "mdi:book",
  "mdi:bookmark",
  "mdi:calendar",
  "mdi:camera",
  "mdi:chart-line",
  "mdi:check",
  "mdi:check-circle",
  "mdi:clock",
  "mdi:close",
  "mdi:close-circle",
  "mdi:cloud",
  "mdi:cog",
  "mdi:comment",
  "mdi:content-copy",
  "mdi:content-save",
  "mdi:database",
  "mdi:delete",
  "mdi:download",
  "mdi:earth",
  "mdi:email",
  "mdi:eye",
  "mdi:file",
  "mdi:filter",
  "mdi:folder",
  "mdi:heart",
  "mdi:home",
  "mdi:image",
  "mdi:information",
  "mdi:link",
  "mdi:lock",
  "mdi:logout",
  "mdi:magnify",
  "mdi:menu",
  "mdi:message",
  "mdi:minus",
  "mdi:pencil",
  "mdi:phone",
  "mdi:plus",
  "mdi:refresh",
  "mdi:server",
  "mdi:settings",
  "mdi:share",
  "mdi:star",
  "mdi:tag",
  "mdi:trash-can",
  "mdi:upload",
  "mdi:view-list",
];

const biIcons = [
  "bi:alarm",
  "bi:archive",
  "bi:arrow-down",
  "bi:arrow-left",
  "bi:arrow-right",
  "bi:arrow-up",
  "bi:bell",
  "bi:book",
  "bi:bookmark",
  "bi:box",
  "bi:briefcase",
  "bi:calendar",
  "bi:camera",
  "bi:cart",
  "bi:chat",
  "bi:check",
  "bi:check-circle",
  "bi:chevron-down",
  "bi:chevron-left",
  "bi:chevron-right",
  "bi:chevron-up",
  "bi:clock",
  "bi:cloud",
  "bi:code",
  "bi:cpu",
  "bi:credit-card",
  "bi:database",
  "bi:download",
  "bi:envelope",
  "bi:eye",
  "bi:file-earmark",
  "bi:filter",
  "bi:folder",
  "bi:gear",
  "bi:globe",
  "bi:graph-up",
  "bi:grid",
  "bi:heart",
  "bi:house",
  "bi:image",
  "bi:info-circle",
  "bi:key",
  "bi:link",
  "bi:list",
  "bi:lock",
  "bi:menu-button",
  "bi:people",
  "bi:person",
  "bi:phone",
  "bi:plus",
  "bi:printer",
  "bi:question-circle",
  "bi:save",
  "bi:search",
  "bi:server",
  "bi:share",
  "bi:shield",
  "bi:star",
  "bi:tag",
  "bi:trash",
  "bi:upload",
  "bi:x",
  "bi:x-circle",
];

const carbonIcons = [
  "carbon:add",
  "carbon:archive",
  "carbon:arrow-down",
  "carbon:arrow-left",
  "carbon:arrow-right",
  "carbon:arrow-up",
  "carbon:calendar",
  "carbon:camera",
  "carbon:chart-line",
  "carbon:checkmark",
  "carbon:chevron-down",
  "carbon:chevron-left",
  "carbon:chevron-right",
  "carbon:chevron-up",
  "carbon:close",
  "carbon:cloud",
  "carbon:code",
  "carbon:copy",
  "carbon:dashboard",
  "carbon:data-base",
  "carbon:delete",
  "carbon:document",
  "carbon:download",
  "carbon:edit",
  "carbon:email",
  "carbon:filter",
  "carbon:folder",
  "carbon:home",
  "carbon:information",
  "carbon:link",
  "carbon:list",
  "carbon:location",
  "carbon:lock",
  "carbon:menu",
  "carbon:notification",
  "carbon:save",
  "carbon:search",
  "carbon:settings",
  "carbon:share",
  "carbon:star",
  "carbon:trash-can",
  "carbon:upload",
  "carbon:user",
  "carbon:view",
  "carbon:warning",
];

const filterIcons = (icons: string[]) => {
  if (!searchText.value) {
    return icons;
  }
  return icons.filter((icon) => icon.toLowerCase().includes(searchText.value.toLowerCase()));
};

const filteredEpIcons = computed(() => filterIcons(epIcons));
const filteredMdiIcons = computed(() => filterIcons(mdiIcons));
const filteredBiIcons = computed(() => filterIcons(biIcons));
const filteredCarbonIcons = computed(() => filterIcons(carbonIcons));

const getIconName = (icon: string) => {
  const parts = icon.split(":");
  if (parts.length !== 2) {
    return icon;
  }
  return parts[1];
};

const selectIcon = (icon: string) => {
  tempSelectedIcon.value = icon;
  emit("update:modelValue", icon);
};

const confirmSelect = () => {
  dialogVisible.value = false;
};
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
