<template>
  <el-dropdown trigger="click" @visible-change="handleVisibleChange">
    <div class="notice-bell">
      <el-badge :value="processedCount" :hidden="count === 0" :max="99">
        <el-icon :size="20">
          <Bell />
        </el-icon>
      </el-badge>
    </div>
    <template #dropdown>
      <div class="notice-dropdown">
        <div class="notice-header">
          <span>通知中心 (未读数:{{ count }})</span>
          <el-button v-if="listData.length > 0" link type="primary" @click="readAllNotice">全部已读</el-button>
        </div>

        <div class="notice-list-container">
          <ul
            v-if="listData.length > 0 || loading"
            v-infinite-scroll="loadMore"
            :infinite-scroll-disabled="disabled"
            class="notice-list"
          >
            <li v-for="item in listData" :key="item.id" class="notice-item" @click="handleRead(item)">
              <div class="item-icon">
                <el-avatar :size="32" :icon="getIcon(item.kind)" :class="getIconClass(item.kind)" />
              </div>
              <div class="item-content">
                <div class="item-title" :title="item.title">{{ item.title }}</div>
                <div class="item-time">{{ item.createTime }}</div>
              </div>
              <div class="item-actions">
                <el-button link type="danger" :icon="Delete" @click.stop="handleDelete(item)"></el-button>
              </div>
            </li>
            <li v-if="loading" class="loading-text">加载中...</li>
            <li v-if="noMore && listData.length > 0" class="no-more-text">没有更多了</li>
          </ul>

          <div v-if="listData.length === 0 && !loading" class="notice-empty">
            <el-empty description="暂无新通知" :image-size="60" />
          </div>
        </div>

        <!-- <div class="notice-footer">
          <el-button link @click="handleViewMore">查看更多</el-button>
        </div> -->
      </div>
    </template>
  </el-dropdown>

  <!-- 通知详情模态框 -->
  <el-dialog v-model="modalVisible" title="通知详情" width="600px" @close="closeModal" :close-on-click-modal="false">
    <div v-loading="modalLoading" class="min-h-[200px] select-text">
      <template v-if="detailsData">
        <!-- 标题和标签 -->
        <div class="flex items-center gap-3 mb-5 pb-4 border-b border-gray-200">
          <h3 class="flex-1 m-0 text-lg font-bold text-gray-900">{{ detailsData.title }}</h3>
          <el-tag v-if="detailsData.kind === 0" type="primary">公告</el-tag>
          <el-tag v-if="detailsData.kind === 1" type="warning">业务提醒</el-tag>
          <el-tag v-if="detailsData.kind === 2" type="success">私信</el-tag>
        </div>

        <!-- 元信息 -->
        <div class="flex flex-col gap-2 mb-5">
          <div class="flex text-sm">
            <span class="text-gray-500 min-w-[80px]">发送人：</span>
            <span class="text-gray-900">{{ detailsData.senderName || "系统" }}</span>
          </div>
          <div class="flex text-sm">
            <span class="text-gray-500 min-w-[80px]">发送时间：</span>
            <span class="text-gray-900">{{ detailsData.createTime }}</span>
          </div>
          <div v-if="detailsData.category" class="flex text-sm">
            <span class="text-gray-500 min-w-[80px]">分类：</span>
            <span class="text-gray-900">{{ detailsData.category }}</span>
          </div>
        </div>

        <!-- 通知内容 -->
        <div>
          <div class="text-sm font-bold text-gray-900 mb-3">通知内容：</div>
          <div class="text-sm leading-relaxed text-gray-700 whitespace-pre-wrap break-words" v-html="detailsData.content"></div>
        </div>
      </template>
    </div>

    <template #footer>
      <el-button @click="closeModal">关闭</el-button>
      <el-button v-if="detailsData?.forward" type="primary" @click="handleForward"> 前往查看 </el-button>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { onMounted } from "vue";
import { Bell, Message, Warning, Promotion, Delete } from "@element-plus/icons-vue";
import UserNoticeService from "../../service/UserNoticeService";
import type { GetUserNoticeRcdListVo } from "../../api/NoticeRcdApi";
import { ElMessage, ElMessageBox } from "element-plus";

// 使用 Service 中的逻辑
const { count, processedCount, loadCount } = UserNoticeService.useUserNoticeCount();
const {
  listData,
  listLoading: loading,
  noMore,
  disabled,
  loadMore,
  resetList,
  removeNotice,
  readAllNotice,
} = UserNoticeService.useUserNoticeDropList(loadCount);
const { modalVisible, modalLoading, detailsData, openModal, closeModal } = UserNoticeService.useUserNoticeModal();

/**
 * 下拉框显示状态变化
 */
const handleVisibleChange = (visible: boolean) => {
  if (!visible) {
    return;
  }
  // 重置并加载第一页
  resetList();
  loadMore();
};

/**
 * 获取通知图标
 */
const getIcon = (kind: number) => {
  if (kind === 0) {
    return Promotion;
  }
  if (kind === 1) {
    return Warning;
  }
  if (kind === 2) {
    return Message;
  }
  return Bell;
};

/**
 * 获取图标样式类
 */
const getIconClass = (kind: number) => {
  if (kind === 0) {
    return "kind-notice";
  }
  if (kind === 1) {
    return "kind-alert";
  }
  if (kind === 2) {
    return "kind-message";
  }
  return "";
};

/**
 * 点击通知项
 */
const handleRead = (item: GetUserNoticeRcdListVo) => {
  // 打开详情模态框
  openModal(item.id);
};

/**
 * 删除通知
 */
const handleDelete = (item: GetUserNoticeRcdListVo) => {
  ElMessageBox.confirm("确定要删除这条通知吗？", "提示", {
    confirmButtonText: "确定",
    cancelButtonText: "取消",
    type: "warning",
  }).then(() => {
    removeNotice(item.id);
  });
};

/**
 * 跳转到关联页面
 */
const handleForward = () => {
  if (!detailsData.value?.forward) {
    return;
  }
  // router.push(detailsData.value.forward);
  closeModal();
  ElMessage.info("功能开发中");
};

onMounted(() => {
  loadCount();
});
</script>

<style scoped lang="scss">
.notice-bell {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 0 12px;
  height: 100%;
  cursor: pointer;
  transition: background-color 0.3s;

  &:hover {
    background-color: rgba(0, 0, 0, 0.05);
  }

  .el-icon {
    color: var(--el-text-color-regular);
  }
}

.notice-dropdown {
  width: 320px;
  background-color: var(--el-bg-color-overlay);
  border-radius: 4px;
  overflow: hidden;

  .notice-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 12px 16px;
    border-bottom: 1px solid var(--el-border-color-lighter);
    font-weight: bold;
    font-size: 14px;
  }

  .notice-list-container {
    height: 300px;
    overflow-y: auto;

    /* 自定义滚动条 */
    &::-webkit-scrollbar {
      width: 6px;
    }
    &::-webkit-scrollbar-thumb {
      background: var(--el-border-color);
      border-radius: 3px;
    }
    &::-webkit-scrollbar-track {
      background: transparent;
    }
  }

  .notice-empty {
    padding: 20px 0;
  }

  .notice-list {
    list-style: none;
    padding: 0;
    margin: 0;

    .notice-item {
      display: flex;
      padding: 12px 16px;
      cursor: pointer;
      transition: background-color 0.2s;
      border-bottom: 1px solid var(--el-border-color-extra-light);

      &:hover {
        background-color: var(--el-fill-color-light);
      }

      .item-icon {
        margin-right: 12px;
        display: flex;
        align-items: center;

        .kind-notice {
          background-color: var(--el-color-primary-light-8);
          color: var(--el-color-primary);
        }
        .kind-alert {
          background-color: var(--el-color-warning-light-8);
          color: var(--el-color-warning);
        }
        .kind-message {
          background-color: var(--el-color-success-light-8);
          color: var(--el-color-success);
        }
      }

      .item-content {
        flex: 1;
        overflow: hidden;

        .item-title {
          font-size: 14px;
          color: var(--el-text-color-primary);
          margin-bottom: 4px;
          white-space: nowrap;
          overflow: hidden;
          text-overflow: ellipsis;
        }

        .item-time {
          font-size: 12px;
          color: var(--el-text-color-secondary);
        }
      }

      .item-actions {
        display: none;
        align-items: center;
        margin-left: 8px;

        .el-button {
          padding: 4px;
        }
      }

      &:hover {
        background-color: var(--el-fill-color-light);

        .item-actions {
          display: flex;
        }
      }
    }

    .loading-text,
    .no-more-text {
      text-align: center;
      padding: 10px;
      color: var(--el-text-color-secondary);
      font-size: 12px;
    }
  }

  .notice-footer {
    padding: 8px;
    text-align: center;
    border-top: 1px solid var(--el-border-color-lighter);

    .el-button {
      width: 100%;
    }
  }
}
</style>
