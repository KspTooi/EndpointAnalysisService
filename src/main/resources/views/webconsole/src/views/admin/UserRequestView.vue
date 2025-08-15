<template>
  <div class="request-viewer-container">
    <RequestTree style="width: 350px" />
    <RequestEditor style="flex: 1" />
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted, watch } from "vue";
import RequestTree from "@/components/user-request-view/RequestTree.vue";
import RequestEditor from "@/components/user-request-view/RequestEditor.vue";
import { EventHolder } from "@/store/EventHolder";
import { UserRequestHolder } from "@/store/RequestHolder";
import { ElMessage } from "element-plus";
import UserRequestApi from "@/api/UserRequestApi";

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
};

//处理CTRL+D复制
watch(
  () => EventHolder().isOnCtrlD,
  async (newVal) => {
    if (newVal) {
      if (UserRequestHolder().getRequestId == null) {
        ElMessage.error("请选择一个请求后，再使用CTRL+D复制");
        return;
      }

      try {
        const requestDetail = await UserRequestApi.copyUserRequest({
          id: UserRequestHolder().getRequestId,
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
