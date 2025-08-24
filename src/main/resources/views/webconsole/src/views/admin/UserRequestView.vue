<template>
  <div class="request-viewer-container">
    <RequestTree style="width: 350px" />
    <RequestEditor style="flex: 1" v-show="RequestTreeHolder().getActiveNodeType == 'request'" />
    <RequestEditorGroup style="flex: 1" v-show="RequestTreeHolder().getActiveNodeType == 'group'" />
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted, watch } from "vue";
import RequestTree from "@/components/user-request-view/RequestTree.vue";
import RequestEditor from "@/components/user-request-view/RequestEditor.vue";
import { EventHolder } from "@/store/EventHolder";
import { RequestTreeHolder } from "@/store/RequestTreeHolder.ts";
import { ElMessage, ElMessageBox } from "element-plus";
import UserRequestApi from "@/api/UserRequestApi";
import UserRequestTreeApi from "@/api/UserRequestTreeApi";

onMounted(() => {
  window.addEventListener("keydown", onKeyboardEvent);
});

onUnmounted(() => {
  window.removeEventListener("keydown", onKeyboardEvent);
});

//处理全局快捷键
const onKeyboardEvent = (event: KeyboardEvent) => {
  const isCtrlOrCmd = event.ctrlKey || event.metaKey;
  if (isCtrlOrCmd && (event.key === "s" || event.key === "S")) {
    event.preventDefault();
    event.stopPropagation();
    event.stopImmediatePropagation();
    EventHolder().onCtrlS();
  }
  if (isCtrlOrCmd && (event.key === "d" || event.key === "D")) {
    event.preventDefault();
    event.stopPropagation();
    event.stopImmediatePropagation();
    EventHolder().onCtrlD();
  }
  if (event.key === "delete" || event.key === "Delete") {
    event.preventDefault();
    event.stopPropagation();
    event.stopImmediatePropagation();
    EventHolder().onDelete();
  }
};

//处理CTRL+D复制
watch(
  () => EventHolder().isOnCtrlD,
  async (newVal) => {
    if (newVal) {
      if (RequestTreeHolder().getActiveNodeId == null) {
        ElMessage.error("请选择一个对象后，再使用CTRL+D复制");
        return;
      }

      try {
        await UserRequestTreeApi.copyUserRequestTree({
          id: RequestTreeHolder().getActiveNodeId,
        });
        ElMessage.success("复制请求成功");

        //刷新树
        EventHolder().requestReloadTree();
      } catch (e) {
        ElMessage.error("复制请求失败");
      }
    }
  }
);

//处理DELETE删除
watch(
  () => EventHolder().isOnDelete,
  async (newVal) => {
    if (newVal) {
      if (RequestTreeHolder().getActiveNodeId == null) {
        ElMessage.error("请选择一个对象后，再使用DELETE删除");
        return;
      }

      //提示用户确认
      const confirm = await ElMessageBox.confirm("确定删除该对象吗？", "提示", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning",
      });
      if (!confirm) {
        return;
      }

      //如果是请求则删请求
      if (RequestTreeHolder().getActiveNodeType == "request") {
        try {
          await UserRequestTreeApi.removeUserRequestTree({ id: RequestTreeHolder().getActiveNodeId });
          ElMessage.success("删除请求成功");
          //刷新树
          EventHolder().requestReloadTree();
        } catch (e: any) {
          ElMessage.error("删除请求失败:" + e.message);
        }
      }

      //如果是组则删组
      if (RequestTreeHolder().getActiveNodeType == "group") {
        try {
          await UserRequestTreeApi.removeUserRequestTree({ id: RequestTreeHolder().getActiveNodeId });
          ElMessage.success("删除组成功");
          //刷新树
          EventHolder().requestReloadTree();
        } catch (e: any) {
          ElMessage.error("删除组失败:" + e.message);
        }
      }
    }
  }
);
</script>

<style scoped>
.request-viewer-container {
  display: flex;
  flex-direction: row;
  flex: 1;
  overflow: hidden;
  min-height: 0;
  width: 100%;
}
</style>
