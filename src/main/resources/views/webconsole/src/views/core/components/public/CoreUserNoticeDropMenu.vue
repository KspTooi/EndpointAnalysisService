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
  // 后端查询时已自动标记为已读，无需手动调用接口
  if (item.forward) {
    // 如果有跳转地址，可以在这里处理跳转逻辑
    // router.push(item.forward);
  }
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
