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
  modelValue?: string | null;
}>();

const emit = defineEmits<{
  "update:modelValue": [value: string];
}>();

const dialogVisible = ref(false);
const searchText = ref("");
const activeTab = ref("ep");
const tempSelectedIcon = ref("");

const epIcons = [
  // System
  "ep:plus",
  "ep:minus",
  "ep:circle-plus",
  "ep:search",
  "ep:female",
  "ep:male",
  "ep:aim",
  "ep:house",
  "ep:full-screen",
  "ep:loading",
  "ep:link",
  "ep:service",
  "ep:pointer",
  "ep:star",
  "ep:notification",
  "ep:connection",
  "ep:chat-dot-round",
  "ep:setting",
  "ep:clock",
  "ep:position",
  "ep:discount",
  "ep:odometer",
  "ep:chat-square",
  "ep:chat-round",
  "ep:chat-line-round",
  "ep:chat-line-square",
  "ep:chat-dot-square",
  "ep:view",
  "ep:hide",
  "ep:unlock",
  "ep:lock",
  "ep:refresh-right",
  "ep:refresh-left",
  "ep:refresh",
  "ep:bell",
  "ep:mute-notification",
  "ep:user",
  "ep:check",
  "ep:circle-check",
  "ep:warning",
  "ep:circle-close",
  "ep:close",
  "ep:pie-chart",
  "ep:more",
  "ep:compass",
  "ep:filter",
  "ep:switch",
  "ep:select",
  "ep:semi-select",
  "ep:close-bold",
  "ep:edit-pen",
  "ep:edit",
  "ep:message",
  "ep:message-box",
  "ep:turn-off",
  "ep:finished",
  "ep:delete",
  "ep:crop",
  "ep:switch-button",
  "ep:operation",
  "ep:open",
  "ep:remove",
  "ep:zoom-out",
  "ep:zoom-in",
  "ep:info-filled",
  "ep:circle-check-filled",
  "ep:success-filled",
  "ep:warning-filled",
  "ep:circle-close-filled",
  "ep:question-filled",
  "ep:warn-triangle-filled",
  "ep:user-filled",
  "ep:more-filled",
  "ep:tools",
  "ep:home-filled",
  "ep:menu",
  "ep:upload-filled",
  "ep:avatar",
  "ep:help-filled",
  "ep:share",
  "ep:star-filled",
  "ep:comment",
  "ep:histogram",
  "ep:grid",
  "ep:promotion",
  "ep:delete-filled",
  "ep:remove-filled",
  "ep:circle-plus-filled",
  // Arrow
  "ep:arrow-left",
  "ep:arrow-up",
  "ep:arrow-right",
  "ep:arrow-down",
  "ep:arrow-left-bold",
  "ep:arrow-up-bold",
  "ep:arrow-right-bold",
  "ep:arrow-down-bold",
  "ep:d-arrow-right",
  "ep:d-arrow-left",
  "ep:download",
  "ep:upload",
  "ep:top",
  "ep:bottom",
  "ep:back",
  "ep:right",
  "ep:top-right",
  "ep:top-left",
  "ep:bottom-right",
  "ep:bottom-left",
  "ep:sort",
  "ep:sort-up",
  "ep:sort-down",
  "ep:rank",
  "ep:caret-left",
  "ep:caret-top",
  "ep:caret-right",
  "ep:caret-bottom",
  "ep:d-caret",
  "ep:expand",
  "ep:fold",
  // Document
  "ep:document-add",
  "ep:document",
  "ep:notebook",
  "ep:tickets",
  "ep:memo",
  "ep:collection",
  "ep:postcard",
  "ep:scale-to-original",
  "ep:set-up",
  "ep:document-delete",
  "ep:document-checked",
  "ep:data-board",
  "ep:data-analysis",
  "ep:copy-document",
  "ep:folder-checked",
  "ep:files",
  "ep:folder",
  "ep:folder-delete",
  "ep:folder-remove",
  "ep:folder-opened",
  "ep:document-copy",
  "ep:document-remove",
  "ep:folder-add",
  "ep:first-aid-kit",
  "ep:reading",
  "ep:data-line",
  "ep:management",
  "ep:checked",
  "ep:ticket",
  "ep:failed",
  "ep:trend-charts",
  "ep:list",
  // Media
  "ep:microphone",
  "ep:mute",
  "ep:mic",
  "ep:video-pause",
  "ep:video-camera",
  "ep:video-play",
  "ep:headset",
  "ep:monitor",
  "ep:film",
  "ep:camera",
  "ep:picture",
  "ep:picture-rounded",
  "ep:iphone",
  "ep:cellphone",
  "ep:video-camera-filled",
  "ep:picture-filled",
  "ep:platform",
  "ep:camera-filled",
  "ep:bell-filled",
  // Traffic
  "ep:location",
  "ep:location-information",
  "ep:delete-location",
  "ep:coordinate",
  "ep:bicycle",
  "ep:office-building",
  "ep:school",
  "ep:guide",
  "ep:add-location",
  "ep:map-location",
  "ep:place",
  "ep:location-filled",
  "ep:van",
  // Food
  "ep:watermelon",
  "ep:pear",
  "ep:no-smoking",
  "ep:smoking",
  "ep:mug",
  "ep:goblet-square-full",
  "ep:goblet-full",
  "ep:knife-fork",
  "ep:sugar",
  "ep:bowl",
  "ep:milk-tea",
  "ep:lollipop",
  "ep:coffee",
  "ep:chicken",
  "ep:dish",
  "ep:ice-tea",
  "ep:cold-drink",
  "ep:coffee-cup",
  "ep:dish-dot",
  "ep:ice-drink",
  "ep:ice-cream",
  "ep:dessert",
  "ep:ice-cream-square",
  "ep:fork-spoon",
  "ep:ice-cream-round",
  "ep:food",
  "ep:hot-water",
  "ep:grape",
  "ep:fries",
  "ep:apple",
  "ep:burger",
  "ep:goblet",
  "ep:goblet-square",
  "ep:orange",
  "ep:cherry",
  // Items
  "ep:printer",
  "ep:calendar",
  "ep:credit-card",
  "ep:box",
  "ep:money",
  "ep:refrigerator",
  "ep:cpu",
  "ep:football",
  "ep:brush",
  "ep:suitcase",
  "ep:suitcase-line",
  "ep:umbrella",
  "ep:alarm-clock",
  "ep:medal",
  "ep:gold-medal",
  "ep:present",
  "ep:mouse",
  "ep:watch",
  "ep:quartz-watch",
  "ep:magnet",
  "ep:help",
  "ep:soccer",
  "ep:toilet-paper",
  "ep:reading-lamp",
  "ep:paperclip",
  "ep:magic-stick",
  "ep:basketball",
  "ep:baseball",
  "ep:coin",
  "ep:goods",
  "ep:sell",
  "ep:sold-out",
  "ep:key",
  "ep:shopping-cart",
  "ep:shopping-cart-full",
  "ep:shopping-trolley",
  "ep:phone",
  "ep:scissor",
  "ep:handbag",
  "ep:shopping-bag",
  "ep:trophy",
  "ep:trophy-base",
  "ep:stopwatch",
  "ep:timer",
  "ep:collection-tag",
  "ep:takeaway-box",
  "ep:price-tag",
  "ep:wallet",
  "ep:opportunity",
  "ep:phone-filled",
  "ep:wallet-filled",
  "ep:goods-filled",
  "ep:flag",
  "ep:brush-filled",
  "ep:briefcase",
  "ep:stamp",
  // Weather
  "ep:sunrise",
  "ep:sunny",
  "ep:ship",
  "ep:mostly-cloudy",
  "ep:partly-cloudy",
  "ep:sunset",
  "ep:drizzling",
  "ep:pouring",
  "ep:cloudy",
  "ep:moon",
  "ep:moon-night",
  "ep:lightning",
  // Other
  "ep:chrome-filled",
  "ep:eleme",
  "ep:eleme-filled",
  "ep:element-plus",
  "ep:shop",
  "ep:switch-filled",
  "ep:wind-power",
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
