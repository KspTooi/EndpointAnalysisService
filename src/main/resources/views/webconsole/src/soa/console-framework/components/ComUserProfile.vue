<template>
  <div class="profile-drop-menu">
    <div class="modal-gradient-bar"></div>
    <div class="profile-header">
      <el-avatar :size="64" :src="avatarUrl" shape="square" />
      <div class="header-info">
        <div class="nickname">{{ profile?.nickname || "未设置昵称" }}</div>
        <div class="username">@{{ profile?.username }}</div>
      </div>
    </div>

    <div class="profile-details">
      <div class="info-item">
        <el-icon class="item-icon"><Message /></el-icon>
        <span class="label">电子邮箱:</span>
        <span class="value">{{ profile?.email || "未绑定" }}</span>
      </div>
      <div class="info-item">
        <el-icon class="item-icon"><Phone /></el-icon>
        <span class="label">手机号码:</span>
        <span class="value">{{ profile?.phone || "未绑定" }}</span>
      </div>
      <div class="info-item">
        <el-icon class="item-icon"><User /></el-icon>
        <span class="label">用户性别:</span>
        <span class="value">{{ genderText }}</span>
      </div>
      <div class="info-item">
        <el-icon class="item-icon"><Operation /></el-icon>
        <span class="label">所属角色:</span>
        <div class="group-tags">
          <el-tag v-for="group in profile?.groups" :key="group" size="small" effect="plain" type="info">
            {{ group }}
          </el-tag>
          <span v-if="!profile?.groups?.length" class="value">无</span>
        </div>
      </div>
      <div class="info-item">
        <el-icon class="item-icon"><Key /></el-icon>
        <span class="label">权限节点:</span>
        <span class="value count-badge">{{ profile?.permissions?.length || 0 }} 个节点</span>
      </div>
      <div class="info-item">
        <el-icon class="item-icon"><Calendar /></el-icon>
        <span class="label">注册时间:</span>
        <span class="value">{{ profile?.createTime }}</span>
      </div>
      <div class="info-item">
        <el-icon class="item-icon"><Clock /></el-icon>
        <span class="label">最后登录:</span>
        <span class="value">{{ profile?.lastLoginTime }}</span>
      </div>
    </div>

    <div class="profile-actions">
      <el-button class="action-btn" type="primary" plain @click="handleChangePassword">
        <el-icon><Key /></el-icon>
        修改密码
      </el-button>
      <el-button class="action-btn" type="danger" plain @click="handleLogout">
        <el-icon><SwitchButton /></el-icon>
        退出登录
      </el-button>
    </div>

    <!-- 修改密码弹窗 -->
    <com-password-reset ref="changePasswordModalRef" />
  </div>
</template>

<script setup lang="ts">
import { computed, ref } from "vue";
import { ElAvatar, ElTag, ElIcon, ElButton, ElMessage, ElMessageBox } from "element-plus";
import { Message, Phone, User, Operation, Key, Calendar, Clock, SwitchButton } from "@element-plus/icons-vue";
import type { GetCurrentUserProfile } from "@/soa/console-framework/api/AuthApi";
import AuthApi from "@/soa/console-framework/api/AuthApi";
import ComPasswordReset from "./ComPasswordReset.vue";

const props = defineProps<{
  profile: GetCurrentUserProfile | null;
}>();

const changePasswordModalRef = ref();

const handleLogout = async () => {
  try {
    await ElMessageBox.confirm("确定要注销登录吗？", "提示", {
      confirmButtonText: "确定",
      cancelButtonText: "取消",
      type: "warning",
    });

    await AuthApi.logout();
    ElMessage.success("注销成功");
    // 刷新页面或跳转到登录页
    window.location.href = "/login";
  } catch (error) {
    if (error === "cancel") return;
    ElMessage.error("注销失败: " + (error as Error).message);
  }
};

const handleChangePassword = () => {
  if (changePasswordModalRef.value) {
    changePasswordModalRef.value.openModal();
  }
};

const avatarUrl = computed(() => {
  if (props.profile?.avatarAttachId) {
    return `/getAttach?id=${props.profile.avatarAttachId}`;
  }
  return "/api/profile/getUserAvatar";
});

const genderText = computed(() => {
  if (props.profile?.gender === 0) return "男";
  if (props.profile?.gender === 1) return "女";
  return "保密";
});
</script>

<style scoped>
.profile-drop-menu {
  width: 320px;
  background-color: #fff;
  color: #333;
  position: relative;
}

.modal-gradient-bar {
  height: 4px;
  width: 100%;
  background: linear-gradient(90deg, #667eea 0%, #764ba2 100%);
}

.profile-header {
  padding: 20px;
  display: flex;
  align-items: center;
  gap: 16px;
  border-bottom: 1px solid #f0f0f0;
}

.header-info {
  display: flex;
  flex-direction: column;
}

.nickname {
  font-size: 18px;
  font-weight: 600;
  color: #000;
}

.username {
  font-size: 13px;
  color: #999;
}

.profile-details {
  padding: 16px 20px;
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.info-item {
  display: flex;
  align-items: center;
  font-size: 13px;
  line-height: 1.5;
  padding: 4px 8px;
  margin: 0 -8px;
  transition: background-color 0.2s;
}

.info-item:hover {
  background-color: #f5f7fa;
}

.item-icon {
  margin-right: 10px;
  color: #909399;
  font-size: 14px;
}

.label {
  color: #888;
  width: 75px;
  flex-shrink: 0;
}

.value {
  color: #333;
  word-break: break-all;
  flex: 1;
}

.count-badge {
  font-weight: 600;
  color: #009688;
}

.profile-actions {
  padding: 12px 20px 20px;
  border-top: 1px solid #f0f0f0;
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.action-btn {
  width: 100%;
  border-radius: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  margin-left: 0 !important; /* 覆盖 el-button 的默认左间距 */
}

.group-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 4px;
}

:deep(.el-tag) {
  border-radius: 0; /* 直角风格 */
}

:deep(.el-avatar) {
  border-radius: 0; /* 直角风格 */
}
</style>
