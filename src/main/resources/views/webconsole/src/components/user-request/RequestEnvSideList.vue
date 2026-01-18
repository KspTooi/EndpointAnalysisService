<template>
  <div class="side-list-container">
    <div class="side-list-header">
      <div @click="emit('onAdd')" class="add-button">
        <IIcOutlinePlaylistAdd />
      </div>
      <el-input v-model="query.name" placeholder="搜索环境" clearable />
    </div>

    <el-scrollbar class="side-list-body" v-loading="loading">
      <div v-if="paginatedList.length > 0">
        <div v-for="item in paginatedList" :key="item.id" class="side-list-item" :class="{ active: selectedId === item.id }" @click="handleSelect(item)">
          <span class="item-name">{{ item.name }}</span>
          <div class="item-actions">
            <div class="item-actions-button-active" v-if="item.active === 1">
              <IIcRoundCheckCircle />
            </div>
            <div class="item-actions-button item-action-edit" @click.stop="handleActivate(item.id)">
              <IIcRoundCheckCircle />
            </div>
            <div class="item-actions-button item-action-edit" @click.stop="emit('onEdit', item)">
              <IIcBaselineBuildCircle />
            </div>
            <div class="item-actions-button item-action-remove" @click.stop="removeList(item.id)">
              <IIcSharpRemoveCircle />
            </div>
          </div>
        </div>
      </div>
      <el-empty v-else description="暂无数据" :image-size="60" />
    </el-scrollbar>

    <div class="side-list-footer">
      <el-pagination small background layout="prev, pager, next" :total="total" :page-size="query.pageSize" :current-page="query.pageNum" @current-change="handlePageChange" />
    </div>
  </div>
</template>

<script setup lang="ts">
import type { GetUserRequestEnvListDto, GetUserRequestEnvListVo } from "@/views/requestdebug/api/UserRequestEnvApi.ts";
import { reactive, ref, computed, watch } from "vue";
import UserRequestEnvApi from "@/views/requestdebug/api/UserRequestEnvApi.ts";
import { Result } from "@/commons/entity/Result";
import { ElMessage, ElMessageBox } from "element-plus";
import { Delete, Edit, Plus } from "@element-plus/icons-vue";
import { RequestEnvSideListHolder } from "@/store/RequestEnvSideListHolder.ts";

const emit = defineEmits<{
  (e: "onSelect", item: GetUserRequestEnvListVo): void;
  (e: "onAdd"): void;
  (e: "onEdit", item: GetUserRequestEnvListVo): void;
}>();

//列表内容
const query = reactive({
  name: null as string | null,
  pageNum: 1,
  pageSize: 200,
});

const fullList = ref<GetUserRequestEnvListVo[]>([]);
const total = computed(() => filteredList.value.length);
const loading = ref(false);
const selectedId = ref<string | null>(null);

const holder = RequestEnvSideListHolder();

const filteredList = computed(() => {
  if (!query.name) {
    return fullList.value;
  }
  return fullList.value.filter((item) => item.name.toLowerCase().includes(query.name!.toLowerCase()));
});

const paginatedList = computed(() => {
  const start = (query.pageNum - 1) * query.pageSize;
  const end = start + query.pageSize;
  return filteredList.value.slice(start, end);
});

watch(
  () => query.name,
  () => {
    query.pageNum = 1;
  }
);

watch(filteredList, (newList) => {
  const currentSelectionStillExists = newList.some((item) => item.id === selectedId.value);
  if (!currentSelectionStillExists && newList.length > 0) {
    handleSelect(newList[0]);
  } else if (newList.length === 0) {
    selectedId.value = null;
    holder.setSelectedId(null);
  }
});

const loadList = async () => {
  loading.value = true;
  const result = await UserRequestEnvApi.getUserRequestEnvList({ pageNum: 1, pageSize: 10000 });
  loading.value = false;

  if (Result.isSuccess(result)) {
    fullList.value = result.data;
    const persistedSelectedId = holder.getSelectedId;

    if (persistedSelectedId && fullList.value.some((item) => item.id === persistedSelectedId)) {
      selectedId.value = persistedSelectedId;
    } else if (fullList.value.length > 0) {
      selectedId.value = fullList.value[0].id;
    } else {
      selectedId.value = null;
    }
    holder.setSelectedId(selectedId.value);

    if (selectedId.value) {
      const item = fullList.value.find((i) => i.id === selectedId.value);
      if (item) emit("onSelect", item);
    }
  }

  if (Result.isError(result)) {
    ElMessage.error(result.message);
  }
};

const resetList = () => {
  query.name = null;
  query.pageNum = 1;
  loadList();
};

const removeList = async (id: string) => {
  try {
    await ElMessageBox.confirm("确定删除该环境吗？", "提示", {
      confirmButtonText: "确定",
      cancelButtonText: "取消",
      type: "warning",
    });
  } catch (error) {
    return;
  }

  try {
    const result = await UserRequestEnvApi.removeUserRequestEnv({ id });
    if (Result.isSuccess(result)) {
      ElMessage.success("删除成功");
      if (selectedId.value === id) {
        selectedId.value = null;
        holder.setSelectedId(null);
      }
    }
    if (Result.isError(result)) {
      ElMessage.error(result.message);
      return;
    }
  } catch (error: any) {
    ElMessage.error(error.message);
    return;
  }
  loadList();
};

const handleSelect = (item: GetUserRequestEnvListVo) => {
  selectedId.value = item.id;
  holder.setSelectedId(item.id);
  emit("onSelect", item);
};

const handlePageChange = (page: number) => {
  query.pageNum = page;
};

const handleActivate = async (id: string) => {
  try {
    const result = await UserRequestEnvApi.activateUserRequestEnv({ id });
    if (Result.isSuccess(result)) {
      ElMessage.success("激活成功");
      loadList();
    }
    if (Result.isError(result)) {
      ElMessage.error(result.message);
    }
  } catch (error: any) {
    ElMessage.error(error.message);
  }
};

loadList();

defineExpose({
  loadList,
  resetList,
  removeList,
});
</script>

<style scoped>
.side-list-container {
  display: flex;
  flex-direction: column;
  height: 100%;
  border-right: 1px solid var(--el-border-color-lighter);
  border-left: 1px solid var(--el-border-color-lighter);
  width: 280px;
}

.side-list-header {
  padding: 10px;
  padding-bottom: 5px;
  display: flex;
  align-items: center;
  border-bottom: 1px solid var(--el-border-color-lighter);
}

.add-button {
  background: transparent;
  border: none;
  cursor: pointer;
  padding: 5px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 25px;
  transition: opacity 0.3s;
  color: var(--el-color-primary);
}

.add-button:hover {
  opacity: 0.5;
  transition: opacity 0.3s;
  transform: scale(1.1);
  transition: transform 0.3s;
}

.add-button:active {
  transform: scale(0.9);
  transition: transform 0.3s;
}

.side-list-body {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: flex-end;
  overflow-y: auto;
  padding-top: 1px;
}

.side-list-item {
  padding: 1px 0 0 25px;
  cursor: pointer;
  display: flex;
  justify-content: flex-end;
  align-items: center;
  font-size: 14px;
  height: 30px;
  user-select: none;
}

.side-list-item:hover {
  background-color: var(--el-fill-color-light);
}

.side-list-item.active {
  background-color: var(--el-color-primary-light-9);
  color: var(--el-color-primary);
}

.item-name {
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  flex: 1;
  margin-right: 10px;
  color: var(--el-text-color-regular);
  font-size: 13px;
}

.item-actions {
  display: flex;
  justify-content: flex-start;
  height: 30px;
  width: 80px;
  margin-right: 5px;
}

.item-action-edit {
  color: var(--el-color-primary);
}
.item-action-remove {
  color: var(--el-color-danger);
}

.item-actions-button:hover {
  opacity: 0.5;
  transition: opacity 0.3s;
  transition: transform 0.3s;
  transform: scale(1.1);
}

.item-actions-button:active {
  transform: scale(0.9);
}

.side-list-item:hover .item-actions {
  justify-content: flex-end;
}

.side-list-item:hover .item-actions .item-actions-button {
  display: flex;
}
.side-list-item:hover .item-actions .item-actions-button-active {
  display: none;
}

.item-actions-button {
  cursor: pointer;
  display: none;
  align-items: center;
  justify-content: center;
  height: 32px;
  margin-right: 5px;
  font-size: 18px;
}

.item-actions-button-active {
  cursor: pointer;
  display: flex;
  justify-content: flex-start;
  align-items: center;
  justify-content: center;
  height: 32px;
  margin-right: 5px;
  font-size: 18px;
}

.side-list-footer {
  padding: 10px;
  display: flex;
  justify-content: center;
  border-top: 1px solid var(--el-border-color-lighter);
}

.ml-10 {
  margin-left: 10px;
}
</style>
