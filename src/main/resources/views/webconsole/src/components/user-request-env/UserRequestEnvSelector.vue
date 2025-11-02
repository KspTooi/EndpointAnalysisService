<template>
  <el-select
    v-model="selectedEnvId"
    placeholder="选择环境"
    filterable
    clearable
    :loading="loading"
    size="small"
    @change="handleChange"
    @clear="handleClear"
    class="user-request-env-selector"
  >
    <el-option v-for="env in envList" :key="env.id" :label="env.name" :value="env.id">
      <div class="env-option-item">
        <span class="env-name">{{ env.name }}</span>
        <el-icon v-if="env.active === 1" class="active-icon"><CircleCheck /></el-icon>
      </div>
    </el-option>
  </el-select>
</template>

<script setup lang="ts">
import { ref, onMounted, watch } from "vue";
import { ElMessage } from "element-plus";
import { CircleCheck } from "@element-plus/icons-vue";
import UserRequestEnvApi, { type GetUserRequestEnvListVo } from "@/api/requestdebug/UserRequestEnvApi.ts";
import { Result } from "@/commons/entity/Result.ts";

const selectedEnvId = ref<string | null>(null);
const envList = ref<GetUserRequestEnvListVo[]>([]);
const loading = ref(false);

const loadEnvList = async () => {
  loading.value = true;
  try {
    const result = await UserRequestEnvApi.getUserRequestEnvList({
      pageNum: 1,
      pageSize: 10000,
      name: null,
    });

    if (Result.isSuccess(result)) {
      envList.value = result.data;

      // 查找已激活的环境
      const activeEnv = envList.value.find((env) => env.active === 1);
      if (activeEnv) {
        selectedEnvId.value = activeEnv.id;
      } else {
        selectedEnvId.value = null;
      }
    }

    if (Result.isError(result)) {
      ElMessage.error(result.message);
    }
  } catch (error: any) {
    ElMessage.error(error.message || "加载环境列表失败");
  } finally {
    loading.value = false;
  }
};

const handleChange = async (envId: string | null) => {
  if (!envId) {
    selectedEnvId.value = null;
    return;
  }

  // 如果选中的环境已经激活，不需要再次激活
  const selectedEnv = envList.value.find((env) => env.id === envId);
  if (selectedEnv && selectedEnv.active === 1) {
    return;
  }

  // 激活选中的环境
  try {
    const result = await UserRequestEnvApi.activateUserRequestEnv({ id: envId });
    if (Result.isSuccess(result)) {
      ElMessage.success("环境已激活");
      // 重新加载列表以更新激活状态
      await loadEnvList();
    }
    if (Result.isError(result)) {
      ElMessage.error(result.message);
      // 激活失败，恢复之前的选择
      const previousActiveEnv = envList.value.find((env) => env.active === 1);
      selectedEnvId.value = previousActiveEnv ? previousActiveEnv.id : null;
    }
  } catch (error: any) {
    ElMessage.error(error.message || "激活环境失败");
    // 激活失败，恢复之前的选择
    const previousActiveEnv = envList.value.find((env) => env.active === 1);
    selectedEnvId.value = previousActiveEnv ? previousActiveEnv.id : null;
  }
};

const handleClear = () => {
  selectedEnvId.value = null;
};

// 监听环境列表变化，确保选中已激活的环境
watch(
  envList,
  () => {
    const activeEnv = envList.value.find((env) => env.active === 1);
    if (activeEnv) {
      if (selectedEnvId.value !== activeEnv.id) {
        selectedEnvId.value = activeEnv.id;
      }
    } else {
      if (selectedEnvId.value !== null) {
        selectedEnvId.value = null;
      }
    }
  },
  { deep: true }
);

onMounted(() => {
  loadEnvList();
});

// 暴露刷新方法，供外部调用
defineExpose({
  loadEnvList,
  refresh: loadEnvList,
});
</script>

<style scoped>
.user-request-env-selector {
  margin: 0;
  width: 100%;
}

.env-option-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  width: 100%;
}

.env-name {
  flex: 1;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.active-icon {
  color: var(--el-color-success);
  margin-left: 8px;
  flex-shrink: 0;
}
</style>
