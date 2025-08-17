<template>
  <div class="rb-container">
    <!-- Loading 遮罩 -->
    <div v-show="globalLoading" class="rb-loading-overlay">
      <div class="rb-loading-spinner"></div>
      <div class="rb-loading-text">正在处理...</div>
    </div>

    <el-empty description="请选择一个请求" v-show="RequestTreeHolder().getActiveRequestId == null || requestDetail.id == null" style="height: 100%; width: 100%" />

    <div v-show="RequestTreeHolder().getActiveRequestId != null && requestDetail.id != null" class="rb-editor-wrapper">
      <div class="rb-header">
        <div class="rb-header-title">
          <input class="rb-name-input" v-model="requestDetail.name" />
        </div>

        <div class="rb-header-input" style="margin-top: 12px">
          <RequestUrlInput :url="requestDetail.url" :method="requestDetail.method" @onUrlChange="onUrlChange" @onSendRequest="onSendRequest" :loading="loading" />
        </div>
      </div>

      <!-- 选项卡 -->
      <div class="rb-tab">
        <div class="rb-tab-item" :class="{ active: PreferenceHolder().getRequestEditorTab === 'header' }" @click="PreferenceHolder().setRequestEditorTab('header')">标头</div>
        <div class="rb-tab-item" :class="{ active: PreferenceHolder().getRequestEditorTab === 'body' }" @click="PreferenceHolder().setRequestEditorTab('body')">载荷</div>
        <div class="rb-tab-item" :class="{ active: PreferenceHolder().getRequestEditorTab === 'response' }" @click="PreferenceHolder().setRequestEditorTab('response')">
          响应列表
        </div>
      </div>

      <div v-show="requestDetail" class="rb-content">
        <!-- 请求头内容 -->
        <div v-show="PreferenceHolder().getRequestEditorTab === 'header'" class="tab-panel">
          <div class="headers-editor">
            <div class="headers-toolbar">
              <el-button @click="addHeader" type="primary" size="small">添加请求头</el-button>
            </div>
            <el-table :data="editableHeaders" style="width: 100%" height="100%" empty-text="点击'添加请求头'开始编辑" size="small" border>
              <el-table-column prop="k" label="键" width="200">
                <template #default="scope">
                  <el-input v-model="scope.row.k" @blur="onHeaderChange" placeholder="请求头名称" size="small" />
                </template>
              </el-table-column>
              <el-table-column prop="v" label="值">
                <template #default="scope">
                  <el-input v-model="scope.row.v" @blur="onHeaderChange" placeholder="请求头值" size="small" />
                </template>
              </el-table-column>
              <el-table-column label="操作" width="80" align="center">
                <template #default="scope">
                  <el-button @click="removeHeader(scope.$index)" type="danger" size="small" plain>删除</el-button>
                </template>
              </el-table-column>
            </el-table>
          </div>
        </div>

        <!-- 载荷内容 -->
        <div v-show="PreferenceHolder().getRequestEditorTab === 'body'" class="tab-panel">
          <RequestPayload :requestDetails="requestDetail" @onRequestBodyChange="onRequestBodyChange" />
        </div>

        <!-- 响应列表 -->
        <div v-if="PreferenceHolder().getRequestEditorTab === 'response'" class="tab-panel">
          <UrResponseList ref="urResponseListRef" :loading="loading" />
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import UserRequestApi, { type GetUserRequestDetailsVo, type RequestHeaderVo } from "@/api/UserRequestApi";
import { ref, watch, onMounted, nextTick, onUnmounted } from "vue";
import RequestUrlInput from "@/components/user-request-view/RequestUrlInput.vue";
import RequestPayload from "./RequestPayload.vue";
import UrResponseList from "./UrResponseList.vue";
import { ElMessage } from "element-plus";
import { RequestTreeHolder } from "@/store/RequestTreeHolder";
import { EventHolder } from "@/store/EventHolder";
import { PreferenceHolder } from "@/store/PreferenceHolder";

//完整用户请求数据
const requestDetail = ref<GetUserRequestDetailsVo>({
  id: null,
  method: null,
  name: null,
  requestBody: null,
  requestBodyType: null,
  requestHeaders: [],
  seq: null,
  url: null,
});

//可编辑的请求头数据
const editableHeaders = ref<RequestHeaderVo[]>([]);
const urResponseListRef = ref<InstanceType<typeof UrResponseList>>();

const loading = ref(false);
const globalLoading = ref(false);

const loadRequestDetail = async () => {
  if (RequestTreeHolder().getActiveRequestId == null) {
    console.log("请求id为空");
    return;
  }

  globalLoading.value = true;

  try {
    const res = await UserRequestApi.getUserRequestDetails({ id: RequestTreeHolder().getActiveRequestId || "" });
    requestDetail.value.id = res.id;
    requestDetail.value.method = res.method;
    requestDetail.value.name = res.name;
    requestDetail.value.requestBody = res.requestBody;
    requestDetail.value.requestBodyType = res.requestBodyType;
    requestDetail.value.requestHeaders = res.requestHeaders;
    requestDetail.value.seq = res.seq;
    requestDetail.value.url = res.url;

    // 初始化可编辑请求头
    editableHeaders.value = [...(res.requestHeaders || [])];
  } catch (e) {
    ElMessage.error(`无法加载请求配置:${e}`);
    requestDetail.value = {
      id: null,
      method: null,
      name: null,
      requestBody: null,
      requestBodyType: null,
      requestHeaders: [],
      seq: null,
      url: null,
    };
    editableHeaders.value = [];
  } finally {
    globalLoading.value = false;
  }
};

onMounted(() => {
  loadRequestDetail();
});

//监听外部请求id变化
watch(
  () => RequestTreeHolder().getActiveRequestId,
  async () => {
    if (RequestTreeHolder().getActiveRequestId) {
      loadRequestDetail();
    }
    if (RequestTreeHolder().getActiveRequestId == null) {
      requestDetail.value = {
        id: null,
        method: null,
        name: null,
        requestBody: null,
        requestBodyType: null,
        requestHeaders: [],
        seq: null,
        url: null,
      };
      editableHeaders.value = [];
    }
  }
);

/**
 * 请求url变化
 */
const onUrlChange = (method: string, url: string) => {
  requestDetail.value.method = method;
  requestDetail.value.url = url;
};

/**
 * 发送请求
 */
const onSendRequest = async () => {
  loading.value = true;

  //先保存请求
  await UserRequestApi.editUserRequest({
    id: requestDetail.value.id,
    name: requestDetail.value.name,
    method: requestDetail.value.method,
    url: requestDetail.value.url,
    requestHeaders: requestDetail.value.requestHeaders,
    requestBodyType: requestDetail.value.requestBodyType,
    requestBody: requestDetail.value.requestBody,
  });

  try {
    await UserRequestApi.sendUserRequest({ id: requestDetail.value.id || "" });
    await urResponseListRef.value?.loadUserRequestLogList();
  } catch (e) {
    ElMessage.error(`发送请求失败:${e}`);
  } finally {
    await urResponseListRef.value?.loadUserRequestLogList();
    loading.value = false;
  }
};

/**
 * 请求体变化
 */
const onRequestBodyChange = (requestBody: string) => {
  requestDetail.value.requestBody = requestBody;
};

// 添加请求头
const addHeader = () => {
  editableHeaders.value.push({ k: "", v: "" });
};

// 删除请求头
const removeHeader = (index: number) => {
  editableHeaders.value.splice(index, 1);
  onHeaderChange();
};

// 请求头变化时同步数据并保存
const onHeaderChange = () => {
  // 过滤掉空的请求头
  const validHeaders = editableHeaders.value.filter((h) => h.k && h.k.trim());
  requestDetail.value.requestHeaders = validHeaders;

  // 自动保存
  if (requestDetail.value.id) {
    UserRequestApi.editUserRequest({
      id: requestDetail.value.id,
      name: requestDetail.value.name,
      method: requestDetail.value.method,
      url: requestDetail.value.url,
      requestHeaders: validHeaders,
      requestBodyType: requestDetail.value.requestBodyType,
      requestBody: requestDetail.value.requestBody,
    });
    // 通知树重新加载
    EventHolder().requestReloadTree();
  }
};

//监听CTRL+S保存快捷键
watch(
  () => EventHolder().isOnCtrlS,
  async (newVal) => {
    if (RequestTreeHolder().getActiveRequestId == null) {
      ElMessage.error("请选择一个请求后，再使用CTRL+S保存");
      return;
    }

    globalLoading.value = true;

    try {
      await UserRequestApi.editUserRequest({
        id: requestDetail.value.id,
        name: requestDetail.value.name,
        method: requestDetail.value.method,
        url: requestDetail.value.url,
        requestHeaders: requestDetail.value.requestHeaders,
        requestBodyType: requestDetail.value.requestBodyType,
        requestBody: requestDetail.value.requestBody,
      });
      // 通知树重新加载
      EventHolder().requestReloadTree();
      ElMessage.success(`请求 [${requestDetail.value.name}] 已保存`);
    } catch (e) {
      ElMessage.error(`无法保存请求配置:${e}`);
      console.error("保存请求失败", e);
    } finally {
      globalLoading.value = false;
    }
  }
);
</script>

<style scoped>
.rb-container {
  position: relative;
  display: flex;
  flex-direction: column;
  height: 100%;
  background: #fff;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.rb-header {
  background: #f8f9fa;
  padding: 10px 20px;
  border-bottom: 1px solid #e9ecef;
}

.rb-header-title {
  font-size: 14px;
  font-weight: 600;
  color: #495057;
}

.rb-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.rb-info-row label {
  min-width: 80px;
  font-weight: 500;
  color: #6c757d;
  margin-right: 12px;
}

.rb-tab {
  display: flex;
  background: #fff;
  border-bottom: 1px solid #e9ecef;
}

.rb-tab-item {
  padding: 5px 20px;
  cursor: pointer;
  border-bottom: 2px solid transparent;
  transition: all 0.3s;
  color: #6c757d;
  font-weight: 400;
  font-size: 14px;
}

.rb-tab-item:hover {
  background: #f8f9fa;
  color: #495057;
}

.rb-tab-item.active {
  color: #007bff;
  border-bottom-color: #007bff;
  background: #fff;
}

/* tab-panel滚动条样式 */
.tab-panel::-webkit-scrollbar {
  width: 8px;
}

.tab-panel::-webkit-scrollbar-track {
  background: #f1f1f1;
  border-radius: 4px;
}

.tab-panel::-webkit-scrollbar-thumb {
  background: #c1c1c1;
  border-radius: 4px;
}

.tab-panel::-webkit-scrollbar-thumb:hover {
  background: #a8a8a8;
}

.tab-panel {
  padding: 5px 5px 15px 15px;
  height: 100%;
  overflow: auto;
}

/* 请求头编辑器样式 */
.headers-editor {
  display: flex;
  flex-direction: column;
  height: 100%;
}

.headers-toolbar {
  padding: 5px 0;
  margin-bottom: 5px;
}

.body-type label {
  font-weight: 500;
  color: #6c757d;
}

.body-type span {
  background: #e9ecef;
  padding: 4px 8px;
  border-radius: 4px;
  font-size: 12px;
  color: #495057;
}

.body-text {
  background: #f8f9fa;
  border: 1px solid #e9ecef;
  border-radius: 6px;
  overflow: auto;
  max-height: 400px;
}

.body-text pre {
  margin: 0;
  padding: 16px;
  white-space: pre-wrap;
  word-wrap: break-word;
  color: #495057;
  font-family: "Courier New", Courier, monospace;
  font-size: 13px;
  line-height: 1.5;
}

.empty-state {
  text-align: center;
  color: #6c757d;
  padding: 40px;
  font-style: italic;
}

.rb-name-input {
  border: none;
  outline: none;
  font-size: 14px;
  font-weight: 600;
  color: #495057;
  width: 100%;
  background: transparent;
  height: 20px;
}

.rb-editor-wrapper {
  display: flex;
  flex-direction: column;
  height: 100%;
  overflow: hidden;
}

/* Loading 遮罩样式 */
.rb-loading-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(255, 255, 255, 0.8);
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  z-index: 100000;
}

.rb-loading-spinner {
  width: 40px;
  height: 40px;
  border: 4px solid #f3f3f3;
  border-top: 4px solid #007bff;
  border-radius: 50%;
  animation: spin 1s linear infinite;
}

.rb-loading-text {
  margin-top: 16px;
  color: #495057;
  font-size: 14px;
}

@keyframes spin {
  0% {
    transform: rotate(0deg);
  }
  100% {
    transform: rotate(360deg);
  }
}
</style>
