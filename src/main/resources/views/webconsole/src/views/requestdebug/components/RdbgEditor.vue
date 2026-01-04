<template>
  <div class="container">
    <div class="editor-wrapper">
      <div class="header">
        <div class="header-title">
          <input class="name-input" v-model="requestDetail.name" />
          <div class="header-env-selector">
            <rdbg-editor-env-selector />
          </div>
        </div>

        <div class="header-input" style="margin-top: 12px">
          <rdbg-editor-url-input
            :url="requestDetail.url"
            :method="requestDetail.method"
            @onUrlChange="onUrlChange"
            :loading="loading"
          />
        </div>
      </div>

      <!-- 选项卡 -->
      <div class="tab">
        <div
          class="tab-item"
          :class="{ active: PreferenceHolder().getRequestEditorTab === 'header' }"
          @click="PreferenceHolder().setRequestEditorTab('header')"
        >
          标头
        </div>
        <div
          class="tab-item"
          :class="{ active: PreferenceHolder().getRequestEditorTab === 'body' }"
          @click="PreferenceHolder().setRequestEditorTab('body')"
        >
          载荷
        </div>
      </div>

      <div v-show="requestDetail" class="content">
        <!-- 请求头内容 -->
        <div v-show="PreferenceHolder().getRequestEditorTab === 'header'" class="tab-panel">
          <div class="headers-editor">
            <div class="headers-toolbar">
              <el-button @click="addHeader" type="primary" size="small">添加请求头</el-button>
            </div>
            <el-table
              :data="editableHeaders"
              style="width: 100%"
              height="100%"
              empty-text="点击'添加请求头'开始编辑"
              size="small"
              border
            >
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
          <!--          <RequestPayload :requestDetails="requestDetail" @onRequestBodyChange="onRequestBodyChange" />-->
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import UserRequestApi, { type GetUserRequestDetailsVo, type RequestHeaderVo } from "@/api/requestdebug/UserRequestApi.ts";
import { ref, watch, onMounted, nextTick, onUnmounted } from "vue";
import RequestUrlInput from "@/components/user-request-view/RequestUrlInput.vue";
//import RequestPayload from "./RequestPayload.vue";
//import UrResponseList from "./UrResponseList.vue";
import { ElMessage } from "element-plus";
import { RequestTreeHolder } from "@/store/RequestTreeHolder";
import { EventHolder } from "@/store/EventHolder";
import { PreferenceHolder } from "@/store/PreferenceHolder";
import UserRequestLogApi from "@/api/requestdebug/UserRequestLogApi.ts";
import type { GetUserRequestLogDetailsVo } from "@/api/requestdebug/UserRequestLogApi.ts";
import RequestPreview from "@/components/user-request-view/RequestPreview.vue";
import RdbgEditorEnvSelector from "@/views/requestdebug/components/RdbgEditorEnvSelector.vue";
import RdbgEditorUrlInput from "@/views/requestdebug/components/RdbgEditorUrlInput.vue";

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

const loading = ref(false);
const globalLoading = ref(false);
const lastResponse = ref<GetUserRequestLogDetailsVo | null>(null);

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
    lastResponse.value = null;
  } finally {
    globalLoading.value = false;
  }
  // 加载最后一次响应
  try {
    lastResponse.value = await UserRequestLogApi.getLastUserRequestLogDetails(RequestTreeHolder().getActiveRequestId || "");
  } catch (e) {
    //需要清空lastResponse
    lastResponse.value = null;
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
      lastResponse.value = null;
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

watch(
  () => EventHolder().isNeedReloadRequestDetails,
  (newVal) => {
    if (newVal) {
      loadRequestDetail();
    }
  }
);
</script>

<style scoped>
.container {
  position: relative;
  display: flex;
  flex-direction: column;
  height: 100%;
  background: #fff;
  overflow: hidden;
  border-left: 1px solid #e4e7ed;
  /* box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1); */
}

.header {
  background: #f8f9fa;
  padding: 10px 20px;
  border-bottom: 1px solid #e9ecef;
}

.header-title {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  font-size: 14px;
  font-weight: 600;
  color: #495057;
}

.content {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.tab {
  display: flex;
  background: #fff;
  border-bottom: 1px solid #e9ecef;
}

.tab-item {
  padding: 5px 20px;
  cursor: pointer;
  border-bottom: 2px solid transparent;
  transition: all 0.3s;
  color: #6c757d;
  font-weight: 400;
  font-size: 14px;
}

.tab-item:hover {
  background: #f8f9fa;
  color: #495057;
}

.tab-item.active {
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

.name-input {
  border: none;
  outline: none;
  font-size: 14px;
  font-weight: 600;
  color: #495057;
  flex: 1;
  background: transparent;
  height: 20px;
  min-width: 0;
}

.header-env-selector {
  flex-shrink: 0;
}
</style>
