<script setup lang="ts">
import { ref } from "vue";
import MaintainApi from "@/api/core/MaintainApi.ts";
import { ElMessage, ElMessageBox } from "element-plus";
import { Lock, User, Setting, UserFilled, Cpu, Menu as IconMenu } from "@element-plus/icons-vue";
import { EventHolder } from "@/store/EventHolder.ts";

const eventHolder = EventHolder();

// 定义维护操作接口类型
interface MaintainOperation {
  title: string;
  description: string;
  icon: any;
  action: () => Promise<void>;
  buttonText: string;
  bgColor: string;
  iconColor: string;
  key: keyof typeof operationLoading.value;
  warning?: string;
  onComplete?: () => void;
}

// 维护操作状态
const operationLoading = ref<{ [key: string]: boolean }>({
  permissions: false,
  groups: false,
  users: false,
  configs: false,
  menus: false,
  endpoints: false,
});

// 执行维护操作的通用方法
const executeMaintainOperation = async (operationKey: keyof typeof operationLoading.value, apiMethod: () => Promise<string>) => {
  try {
    operationLoading.value[operationKey] = true;
    const result = await apiMethod();
    ElMessage.success({
      message: result || "校验完成",
      type: "success",
    });
  } catch (error: any) {
    ElMessage.error({
      message: error.message || "校验失败",
      type: "error",
    });
  } finally {
    operationLoading.value[operationKey] = false;
  }
};

const handleOperationClick = (operation: MaintainOperation) => {
  const performAction = async () => {
    await operation.action();
    if (operation.onComplete) {
      operation.onComplete();
    }
  };

  if (!operation.warning) {
    performAction();
    return;
  }

  ElMessageBox.confirm(operation.warning, "警告", {
    confirmButtonText: "确定执行",
    cancelButtonText: "取消",
    type: "warning",
    beforeClose: async (action, instance, done) => {
      if (action === "confirm") {
        instance.confirmButtonLoading = true;
        instance.confirmButtonText = "正在处理...";
        try {
          await performAction();
        } finally {
          instance.confirmButtonLoading = false;
          instance.confirmButtonText = "确定执行";
          done();
        }
      } else {
        ElMessage.info("操作已取消");
        done();
      }
    },
  }).catch(() => {
    ElMessage.info("操作已取消");
  });
};

// 维护操作列表
const maintainOperations: MaintainOperation[] = [
  {
    title: "权限节点校验",
    description: "校验系统内置权限节点是否完整，如有缺失将自动补充。",
    icon: Lock,
    buttonText: "权限节点校验",
    bgColor: "rgba(64, 158, 255, 0.1)",
    iconColor: "#409EFF",
    action: () => executeMaintainOperation("permissions", MaintainApi.validateSystemPermissions),
    key: "permissions",
  },
  {
    title: "用户组校验",
    description: "校验系统内置用户组是否完整，如有缺失将自动补充。管理员组将被赋予所有权限。",
    icon: UserFilled,
    buttonText: "用户组校验",
    bgColor: "rgba(103, 194, 58, 0.1)",
    iconColor: "#67C23A",
    action: () => executeMaintainOperation("groups", MaintainApi.validateSystemGroups),
    key: "groups",
  },
  {
    title: "用户校验",
    description: "校验系统内置用户是否完整，如有缺失将自动补充。管理员用户将被赋予所有用户组。",
    icon: User,
    buttonText: "用户校验",
    bgColor: "rgba(230, 162, 60, 0.1)",
    iconColor: "#E6A23C",
    action: () => executeMaintainOperation("users", MaintainApi.validateSystemUsers),
    key: "users",
  },
  {
    title: "全局配置校验",
    description: "校验系统全局配置项是否完整，如有缺失将自动补充默认配置。",
    icon: Setting,
    buttonText: "全局配置校验",
    bgColor: "rgba(144, 147, 153, 0.1)",
    iconColor: "#909399",
    action: () => executeMaintainOperation("configs", MaintainApi.validateSystemConfigs),
    key: "configs",
  },
  {
    title: "菜单重置",
    description: "删除所有菜单并恢复为默认菜单。这是一个危险操作，请谨慎使用。",
    icon: IconMenu,
    buttonText: "菜单重置",
    bgColor: "rgba(245, 108, 108, 0.1)",
    iconColor: "#F56C6C",
    action: () => executeMaintainOperation("menus", MaintainApi.resetMenus),
    key: "menus",
    warning: "这是一个危险操作! 这将会删除所有现有菜单并恢复为默认设置，该操作不可逆。是否确定要继续？",
    onComplete: () => {
      eventHolder.requestReloadLeftMenu();
    },
  },
  {
    title: "端点权限重置",
    description: "删除所有端点权限配置并恢复为默认配置。这是一个危险操作，请谨慎使用。",
    icon: Cpu,
    buttonText: "端点权限重置",
    bgColor: "rgba(245, 108, 108, 0.1)",
    iconColor: "#F56C6C",
    action: () => executeMaintainOperation("endpoints", MaintainApi.resetEndpointPermissionConfig),
    key: "endpoints",
    warning: "这是一个危险操作! 这将会删除所有现有端点权限配置并恢复为默认设置，该操作不可逆。是否确定要继续？",
  },
];
</script>

<template>
  <div class="application-maintain">
    <div class="page-header">
      <h1 class="page-title">系统维护工具</h1>
      <p class="page-description">提供各种系统维护功能，保障系统稳定运行</p>
    </div>

    <div class="maintain-cards">
      <div v-for="(operation, index) in maintainOperations" :key="index" class="maintain-card" :style="{ '--card-bg-color': operation.bgColor }">
        <div class="card-icon-wrapper" :style="{ backgroundColor: operation.bgColor }">
          <el-icon :size="30" :color="operation.iconColor">
            <component :is="operation.icon" />
          </el-icon>
        </div>
        <div class="card-content">
          <h3 class="card-title">{{ operation.title }}</h3>
          <p class="card-description">{{ operation.description }}</p>
          <el-button type="primary" :loading="operationLoading[operation.key]" @click="handleOperationClick(operation)" class="card-button">
            <el-icon class="button-icon"><component :is="operation.icon" /></el-icon>
            {{ operation.buttonText }}
          </el-button>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.application-maintain {
  padding: 24px;
  max-width: 100%;
  width: 100%;
  margin: 0 auto;
  height: 100%;
  overflow-y: auto;
  box-sizing: border-box;
}

.page-header {
  margin-bottom: 32px;
  text-align: center;
}

.page-title {
  font-size: 28px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 8px;
}

.page-description {
  font-size: 16px;
  color: #606266;
}

.maintain-cards {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 24px;
}

.maintain-card {
  position: relative;
  background-color: #fff;
  border-radius: 8px;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.08);
  overflow: hidden;
  transition: all 0.3s ease;
}

.maintain-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
}

.maintain-card::before {
  content: "";
  position: absolute;
  top: 0;
  left: 0;
  width: 4px;
  height: 100%;
}

.card-icon-wrapper {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 64px;
  height: 64px;
  border-radius: 50%;
  margin: 24px auto 16px;
}

.card-content {
  padding: 0 24px 24px;
  text-align: center;
}

.card-title {
  font-size: 18px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 12px;
}

.card-description {
  font-size: 14px;
  color: #606266;
  line-height: 1.6;
  margin-bottom: 24px;
  height: 67px;
  overflow: hidden;
}

.card-button {
  width: 100%;
  border-radius: 4px;
  font-weight: 500;
  display: flex;
  align-items: center;
  justify-content: center;
}

.button-icon {
  margin-right: 8px;
}

@media (max-width: 768px) {
  .maintain-cards {
    grid-template-columns: 1fr;
  }

  .application-maintain {
    padding: 16px;
  }

  .card-description {
    height: auto;
  }
}
</style>
