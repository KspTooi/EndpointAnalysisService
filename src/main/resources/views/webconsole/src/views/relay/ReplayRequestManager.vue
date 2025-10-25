<template>
  <div class="request-manager-container">
    <!-- 查询表单 -->
    <div class="query-form">
      <el-form :model="query">
        <el-row>
          <el-col :span="5" :offset="1">
            <el-form-item label="原始请求ID">
              <el-input ref="originRequestIdInput" v-model="query.originRequestId" placeholder="请输入原始请求ID" clearable style="width: 200px" /> </el-form-item
          ></el-col>
          <el-col :span="5" :offset="1"></el-col>
          <el-col :span="5" :offset="1"></el-col>
          <el-col :span="3" :offset="3">
            <el-button type="primary" @click="loadOriginRequestList" :disabled="loading">查询</el-button>
            <el-button @click="resetQuery" :disabled="loading">重置</el-button>
          </el-col>
        </el-row>
      </el-form>
    </div>

    <div class="action-buttons">
      <el-button type="success" @click="executeReplay" :disabled="executeLoading">
        <span v-show="!executeLoading">执行重放</span>
        <span v-show="executeLoading">正在处理</span>
      </el-button>
    </div>

    <!-- 空状态提示 -->
    <div class="empty-state" v-if="originRequestList.length === 0 && !loading">
      <el-empty description="请输入原始请求ID进行查询">
        <el-button type="primary" @click="originRequestIdInput?.focus()">开始查询</el-button>
      </el-empty>
    </div>

    <div class="request-area" v-if="originRequestList.length > 0">
      <div class="origin-request-title" style="margin-top: 20px; margin-bottom: 10px; font-size: 16px; font-weight: bold; color: #006aac" v-if="originRequestList.length > 0">
        原始请求ID: {{ query.originRequestId }}
      </div>
      <!-- 原始请求列表 -->
      <div class="origin-request-table">
        <el-table :data="originRequestList" v-loading="loading" border>
          <el-table-column prop="requestId" label="请求ID" min-width="150" show-overflow-tooltip>
            <template #default="scope">
              <el-tooltip content="点击复制" placement="top">
                <el-link type="primary" underline="never" @click="copyText(scope.row.requestId)">
                  {{ scope.row.requestId }}
                </el-link>
              </el-tooltip>
            </template>
          </el-table-column>
          <el-table-column prop="method" label="方法" min-width="50" show-overflow-tooltip>
            <template #default="scope">
              <span
                :style="{
                  color:
                    scope.row.method === 'DELETE'
                      ? '#E74C3C'
                      : scope.row.method === 'GET'
                        ? '#3498DB'
                        : scope.row.method === 'POST'
                          ? '#2ECC71'
                          : scope.row.method === 'PUT'
                            ? '#F1C40F'
                            : '#95A5A6',
                }"
              >
                {{ scope.row.method.toUpperCase() }}
              </span>
            </template>
          </el-table-column>
          <el-table-column prop="url" label="请求URL" min-width="180" show-overflow-tooltip />
          <el-table-column prop="source" label="来源" min-width="60" show-overflow-tooltip />
          <el-table-column prop="status" label="状态" min-width="50" show-overflow-tooltip>
            <template #default="scope">
              <span :style="{ color: scope.row.status === 0 ? '#2ECC71' : scope.row.status === 1 ? '#E74C3C' : scope.row.status === 2 ? '#F1C40F' : '#95A5A6' }">
                {{ scope.row.status === 0 ? "正常" : scope.row.status === 1 ? "HTTP失败" : scope.row.status === 2 ? "业务失败" : "连接超时" }}
              </span>
            </template>
          </el-table-column>
          <el-table-column prop="statusCode" label="HTTP" min-width="35" show-overflow-tooltip>
            <template #default="scope">
              <span
                :style="{
                  color:
                    scope.row.statusCode >= 200 && scope.row.statusCode < 300
                      ? '#2ECC71'
                      : scope.row.statusCode >= 300 && scope.row.statusCode < 400
                        ? '#F1C40F'
                        : scope.row.statusCode >= 400 && scope.row.statusCode < 500
                          ? '#E74C3C'
                          : '#95A5A6',
                }"
              >
                {{ scope.row.statusCode }}
              </span>
            </template>
          </el-table-column>
          <el-table-column prop="requestTime" label="请求时间" show-overflow-tooltip />

          <el-table-column label="操作" fixed="right" min-width="100">
            <template #default="scope">
              <el-button link type="primary" size="small" @click="openOriginViewModal(scope.row)" :icon="ViewIcon"> 预览请求 </el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>

      <div class="replay-request-title" style="margin-top: 20px; margin-bottom: 10px; font-size: 16px; font-weight: bold; color: #009175">重放请求列表</div>

      <!-- 回放请求列表 -->
      <div class="replay-request-table">
        <el-table :data="list" stripe v-loading="loading" border>
          <el-table-column prop="requestId" label="请求ID" min-width="150" show-overflow-tooltip>
            <template #default="scope">
              <el-tooltip content="点击复制" placement="top">
                <el-link type="primary" underline="never" @click="copyText(scope.row.requestId)">
                  {{ scope.row.requestId }}
                </el-link>
              </el-tooltip>
            </template>
          </el-table-column>
          <el-table-column prop="method" label="方法" min-width="50" show-overflow-tooltip>
            <template #default="scope">
              <span
                :style="{
                  color:
                    scope.row.method === 'DELETE'
                      ? '#E74C3C'
                      : scope.row.method === 'GET'
                        ? '#3498DB'
                        : scope.row.method === 'POST'
                          ? '#2ECC71'
                          : scope.row.method === 'PUT'
                            ? '#F1C40F'
                            : '#95A5A6',
                }"
              >
                {{ scope.row.method.toUpperCase() }}
              </span>
            </template>
          </el-table-column>
          <el-table-column prop="url" label="请求URL" min-width="180" show-overflow-tooltip />
          <el-table-column prop="source" label="来源" min-width="60" show-overflow-tooltip />
          <el-table-column prop="status" label="状态" min-width="50" show-overflow-tooltip>
            <template #default="scope">
              <span :style="{ color: scope.row.status === 0 ? '#2ECC71' : scope.row.status === 1 ? '#E74C3C' : scope.row.status === 2 ? '#F1C40F' : '#95A5A6' }">
                {{ scope.row.status === 0 ? "正常" : scope.row.status === 1 ? "HTTP失败" : scope.row.status === 2 ? "业务失败" : "连接超时" }}
              </span>
            </template>
          </el-table-column>
          <el-table-column prop="statusCode" label="HTTP" min-width="35" show-overflow-tooltip>
            <template #default="scope">
              <span
                :style="{
                  color:
                    scope.row.statusCode >= 200 && scope.row.statusCode < 300
                      ? '#2ECC71'
                      : scope.row.statusCode >= 300 && scope.row.statusCode < 400
                        ? '#F1C40F'
                        : scope.row.statusCode >= 400 && scope.row.statusCode < 500
                          ? '#E74C3C'
                          : '#95A5A6',
                }"
              >
                {{ scope.row.statusCode }}
              </span>
            </template>
          </el-table-column>
          <el-table-column prop="requestTime" label="请求时间" show-overflow-tooltip />

          <el-table-column label="操作" fixed="right" min-width="100">
            <template #default="scope">
              <el-button link type="primary" size="small" @click="openViewModal(scope.row)" :icon="ViewIcon"> 预览请求 </el-button>
            </template>
          </el-table-column>
        </el-table>

        <!-- 分页 -->
        <div class="pagination-container">
          <el-pagination
            v-model:current-page="query.pageNum"
            v-model:page-size="query.pageSize"
            :page-sizes="[10, 20, 50, 100]"
            layout="total, sizes, prev, pager, next, jumper"
            :total="total"
            @size-change="
              (val: number) => {
                query.pageSize = val;
                loadReplayRequestList();
              }
            "
            @current-change="
              (val: number) => {
                query.pageNum = val;
                loadReplayRequestList();
              }
            "
            background
          />
        </div>
      </div>
    </div>

    <RequestPreviewModal ref="requestPreviewModalRef" />
  </div>
</template>

<script setup lang="ts">
import { reactive, ref, onMounted } from "vue";
import type { GetReplayRequestListDto, GetReplayRequestListVo, GetReplayRequestDetailsVo, GetOriginRequestVo } from "@/api/relay/ReplayRequestApi.ts";
import ReplayRequestApi from "@/api/relay/ReplayRequestApi.ts";
import { ElMessage } from "element-plus";
import { View } from "@element-plus/icons-vue";
import { markRaw } from "vue";
import RequestApi from "@/api/relay/RequestApi.ts";
import RequestPreviewModal from "@/components/RequestPreviewModal.vue";
import type { RequestPreviewVo } from "@/components/RequestPreviewModal.vue";
import type { HttpHeaderVo } from "@/api/requestdebug/UserRequestLogApi.ts";

//原始请求列表
const originRequestList = ref<GetOriginRequestVo[]>([]);

//输入框引用
const originRequestIdInput = ref();

//回放请求列表查询条件
const query = reactive<GetReplayRequestListDto>({
  originRequestId: null,
  relayServerId: null,
  requestId: null,
  method: null,
  url: null,
  source: null,
  status: null,
  pageNum: 1,
  pageSize: 10,
});

//回放请求列表
const list = ref<GetReplayRequestListVo[]>([]);

//回放请求列表总条数
const total = ref(0);

// 加载状态
const loading = ref(false);

// 使用markRaw包装图标组件
const ViewIcon = markRaw(View);

// 模态框相关
const requestPreviewModalRef = ref<InstanceType<typeof RequestPreviewModal>>();

/**
 * 加载回放请求列表
 */
const loadReplayRequestList = async () => {
  loading.value = true;
  try {
    const res = await ReplayRequestApi.getReplayRequestList(query);
    list.value = res.data;
    total.value = res.total;
  } catch (e) {
    ElMessage.error("无法获取回放请求");
    console.error("无法获取回放请求", e);
  } finally {
    loading.value = false;
  }
};

/**
 * 加载原始请求列表
 */
const loadOriginRequestList = async () => {
  loading.value = true;
  try {
    localStorage.setItem("originRequestId", query.originRequestId || "");
    const res = await ReplayRequestApi.getOriginRequest({ requestId: query.originRequestId });
    originRequestList.value = [res];
    loadReplayRequestList();
  } catch (e) {
    ElMessage.error("无法获取原始请求");
    console.error("无法获取原始请求", e);
  } finally {
    loading.value = false;
  }
};

const executeLoading = ref(false);

const executeReplay = async () => {
  try {
    executeLoading.value = true;
    await ReplayRequestApi.replayRequest(query.originRequestId || "");
    ElMessage.success("执行重放成功");
    loadReplayRequestList();
    loadOriginRequestList();
  } catch (e: any) {
    ElMessage.error(e.message);
    console.error("执行重放失败", e);
  } finally {
    executeLoading.value = false;
  }
};

const resetQuery = () => {
  query.originRequestId = null;
  query.requestId = null;
  query.method = null;
  query.url = null;
  query.source = null;
  query.status = null;
  query.relayServerId = null;
  localStorage.removeItem("originRequestId");
  originRequestList.value = [];
  list.value = [];
  total.value = 0;
  query.pageNum = 1;
  query.pageSize = 10;
};

//页面加载时自动加载数据
onMounted(() => {
  //从localStorage获取保存的原始请求ID
  const originRequestId = localStorage.getItem("originRequestId");
  if (originRequestId) {
    query.originRequestId = originRequestId;
    loadOriginRequestList();
    loadReplayRequestList();
  }
});

const parseHeadersFromString = (headers: string): HttpHeaderVo[] => {
  if (!headers) {
    return [];
  }
  try {
    const parsed = JSON.parse(headers);
    if (typeof parsed === "object" && parsed !== null) {
      if (Array.isArray(parsed)) {
        // 如果已经是HttpHeaderVo[]格式
        return parsed;
      }
      // 如果是 {k:v, k2:v2} 格式
      return Object.entries(parsed).map(([k, v]) => ({ k, v: String(v) }));
    }
  } catch (e) {
    console.warn("无法将标头解析为JSON，将返回空数组", headers, e);
  }
  return [];
};

const openOriginViewModal = async (row: GetOriginRequestVo) => {
  try {
    const res = await RequestApi.getRequestDetails(row.id.toString());
    const previewData: RequestPreviewVo = {
      id: res.id.toString(),
      requestId: res.requestId,
      method: res.method,
      url: res.url,
      source: res.source,
      requestHeaders: parseHeadersFromString(res.requestHeaders),
      requestBodyLength: res.requestBodyLength,
      requestBodyType: res.requestBodyType,
      requestBody: res.requestBody,
      responseHeaders: parseHeadersFromString(res.responseHeaders),
      responseBodyLength: res.responseBodyLength,
      responseBodyType: res.responseBodyType,
      responseBody: res.responseBody,
      statusCode: res.statusCode,
      redirectUrl: res.redirectUrl,
      status: res.status,
      requestTime: res.requestTime,
      responseTime: res.responseTime,
    };
    requestPreviewModalRef.value?.openPreview(previewData);
  } catch (error) {
    ElMessage.error("获取原始请求详情失败");
  }
};

//打开预览请求模态框
const openViewModal = async (row: GetReplayRequestListVo) => {
  try {
    //获取请求数据
    const res = await ReplayRequestApi.getReplayRequestDetails(row.id.toString());
    const previewData: RequestPreviewVo = {
      id: res.id.toString(),
      requestId: res.requestId,
      method: res.method,
      url: res.url,
      source: res.source,
      requestHeaders: parseHeadersFromString(res.requestHeaders),
      requestBodyLength: res.requestBodyLength,
      requestBodyType: res.requestBodyType,
      requestBody: res.requestBody,
      responseHeaders: parseHeadersFromString(res.responseHeaders),
      responseBodyLength: res.responseBodyLength,
      responseBodyType: res.responseBodyType,
      responseBody: res.responseBody,
      statusCode: res.statusCode,
      redirectUrl: res.redirectUrl,
      status: res.status,
      requestTime: res.requestTime,
      responseTime: res.responseTime,
    };
    requestPreviewModalRef.value?.openPreview(previewData);
  } catch (error) {
    ElMessage.error("获取请求详情失败");
    console.error("获取请求详情失败", error);
  }
};

const copyText = async (text: string) => {
  if (!text) {
    ElMessage.warning("内容为空，无法复制");
    return;
  }
  try {
    if (navigator.clipboard && window.isSecureContext) {
      await navigator.clipboard.writeText(text);
      ElMessage.success("已复制到剪贴板");
      return;
    }
    const textarea = document.createElement("textarea");
    textarea.value = text;
    textarea.style.position = "fixed";
    textarea.style.left = "-9999px";
    document.body.appendChild(textarea);
    textarea.focus();
    textarea.select();
    const success = document.execCommand("copy");
    document.body.removeChild(textarea);
    if (success) {
      ElMessage.success("已复制到剪贴板");
      return;
    }
    ElMessage.error("复制失败");
  } catch (e) {
    ElMessage.error("复制失败");
  }
};

const formatJson = (data: unknown): string => {
  if (data === null || data === undefined) return "";
  if (typeof data === "string") {
    const trimmed = data.trim();
    if (!trimmed) return "";
    try {
      const parsed = JSON.parse(trimmed);
      return JSON.stringify(parsed, null, 2);
    } catch (_) {
      return data;
    }
  }
  try {
    return JSON.stringify(data, null, 2);
  } catch (_) {
    return String(data);
  }
};
</script>

<style scoped>
.request-manager-container {
  padding: 20px;
  max-width: 100%;
  overflow-x: auto;
  width: 100%;
}

.action-buttons {
  margin-bottom: 15px;
  border-top: 2px dashed var(--el-border-color);
  padding-top: 15px;
}

.empty-state {
  padding: 40px 0;
  text-align: center;
}

.replay-request-table {
  margin-bottom: 20px;
  width: 100%;
  overflow-x: auto;
}

.pagination-container {
  display: flex;
  justify-content: flex-end;
  margin-top: 20px;
  width: 100%;
}

.copy-icon {
  cursor: pointer;
}

/* 垂直居中对话框并在小屏自适应高度 */
:deep(.centered-dialog) {
  margin: 0 auto;
  top: 50%;
  transform: translateY(-50%);
  height: 70vh;
  max-height: 70vh;
  display: flex;
  flex-direction: column;
}

/* 内容区滚动以防止超出屏幕 */
:deep(.centered-dialog .el-dialog__body) {
  flex: 1 1 auto;
  min-height: 0;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

/* 表单充满对话框，便于内部滚动控制 */
:deep(.centered-dialog .el-form) {
  display: flex;
  flex-direction: column;
  height: 100%;
  min-height: 0;
}

/* Tabs 使用列布局，头部固定，内容区自适应 */
:deep(.centered-dialog .el-tabs) {
  display: flex;
  flex-direction: column;
  flex: 1 1 auto;
  min-height: 0;
}

/* 仅内容区滚动 */
:deep(.centered-dialog .el-tabs__content) {
  flex: 1 1 auto;
  min-height: 0;
  overflow: auto;
}
</style>
