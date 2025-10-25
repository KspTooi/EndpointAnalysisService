<template>
  <div class="request-manager-container">
    <!-- 查询表单 -->
    <div class="query-form">
      <el-form :model="query">
        <el-row>
          <el-col :span="5" :offset="1">
            <el-form-item label="请求ID">
              <el-input v-model="query.requestId" placeholder="请输入请求ID" clearable />
            </el-form-item>
          </el-col>
          <el-col :span="5" :offset="1">
            <el-form-item label="请求方法">
              <el-input v-model="query.method" placeholder="请输入请求方法" clearable />
            </el-form-item>
          </el-col>
          <el-col :span="5" :offset="1">
            <el-form-item label="请求URL">
              <el-input v-model="query.url" placeholder="请输入请求URL" clearable />
            </el-form-item>
          </el-col>
          <el-col :span="5" :offset="1">
            <el-form-item>
              <el-button type="primary" @click="loadRequestList" :disabled="loading">查询</el-button>
              <el-button @click="resetQuery" :disabled="loading">重置</el-button>
              <ExpandButton v-model="uiState.isAdvancedSearch" :disabled="loading" />
            </el-form-item>
          </el-col>
        </el-row>
        <template v-if="uiState.isAdvancedSearch">
          <el-row>
            <el-col :span="5" :offset="1">
              <el-form-item label="来源">
                <el-input v-model="query.source" placeholder="请输入来源" clearable />
              </el-form-item>
            </el-col>
            <el-col :span="5" :offset="1">
              <el-form-item label="状态">
                <el-select v-model="query.status" placeholder="请选择状态" clearable>
                  <el-option label="正常" value="0" />
                  <el-option label="HTTP失败" value="1" />
                  <el-option label="业务失败" value="2" />
                  <el-option label="连接超时" value="3" />
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :span="5" :offset="1">
              <el-form-item label="是否重放">
                <el-select v-model="query.replay" placeholder="请选择是否重放" clearable>
                  <el-option label="全部" :value="0" />
                  <el-option label="是" :value="1" />
                </el-select>
              </el-form-item>
            </el-col>
          </el-row>

          <el-row>
            <el-col :span="5" :offset="1">
              <el-form-item label="时间区间">
                <el-date-picker v-model="timeRange" type="datetimerange" range-separator="至" start-placeholder="开始时间" end-placeholder="结束时间" />
              </el-form-item>
            </el-col>
            <el-col :span="5" :offset="1"></el-col>
            <el-col :span="5" :offset="1"></el-col>
          </el-row>
        </template>
      </el-form>
    </div>

    <div class="action-buttons">
      <el-button type="primary" @click="loadRequestList" :disabled="loading">查询</el-button>
      <el-button @click="resetQuery" :disabled="loading">重置</el-button>
    </div>

    <!-- 配置列表 -->
    <div class="request-table">
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
        <el-table-column prop="replayCount" label="重放" min-width="35" show-overflow-tooltip />
        <el-table-column prop="requestTime" label="请求时间" show-overflow-tooltip />

        <el-table-column label="操作" fixed="right" min-width="180">
          <template #default="scope">
            <el-button link type="primary" size="small" @click="openViewModal(scope.row)" :icon="ViewIcon"> 预览请求 </el-button>
            <el-button link type="success" size="small" @click="goToReplay(scope.row)" :icon="RightIcon" style="margin-left: 8px"> 转到重放 </el-button>
            <el-button link type="primary" size="small" @click="saveRequest(scope.row)" :icon="SaveIcon" style="margin-left: 8px"> 保存 </el-button>
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
              loadRequestList();
            }
          "
          @current-change="
            (val: number) => {
              query.pageNum = val;
              loadRequestList();
            }
          "
          background
        />
      </div>
    </div>

    <RequestPreviewModal ref="requestPreviewModalRef" />

    <!-- 保存请求对话框 -->
    <el-dialog v-model="saveDialogVisible" title="另存为" width="400px" :close-on-click-modal="false" :close-on-press-escape="false" class="modal-centered">
      <el-form
        ref="saveFormRef"
        :model="saveForm"
        label-width="80px"
        :rules="{
          name: [{ required: true, message: '请输入请求名称', trigger: 'blur' }],
        }"
      >
        <el-form-item label="请求名称" prop="name">
          <el-input v-model="saveForm.name" placeholder="请输入请求名称" maxlength="100" show-word-limit />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="saveDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="confirmSaveRequest" :loading="saveSubmitLoading"> 保存 </el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { reactive, ref, onMounted } from "vue";
import type { GetRequestListDto, GetRequestListVo, GetRequestDetailsVo } from "@/api/RequestApi.ts";
import RequestApi from "@/api/RequestApi.ts";
import { ElMessage } from "element-plus";
import { DocumentCopy, View, Right } from "@element-plus/icons-vue";
import { markRaw } from "vue";
import type { FormInstance } from "element-plus";
import { useRouter } from "vue-router";
import QueryPersistService from "@/service/QueryPersistService.ts";
import UserRequestApi from "@/api/userrequest/UserRequestApi.ts";
import RequestPreviewModal from "@/components/RequestPreviewModal.vue";
import type { RequestPreviewVo } from "@/components/RequestPreviewModal.vue";
import type { HttpHeaderVo } from "@/api/userrequest/UserRequestLogApi.ts";
import ExpandButton from "@/components/common/ExpandButton.vue";

const uiState = reactive({
  isAdvancedSearch: false,
});
const router = useRouter();
const queryPersistService = QueryPersistService;

const query = reactive<GetRequestListDto>({
  requestId: null,
  method: null,
  url: null,
  source: null,
  status: null,
  startTime: null,
  endTime: null,
  replay: 0,
  pageNum: 1,
  pageSize: 10,
});

const list = ref<GetRequestListVo[]>([]);
const total = ref(0);

// 加载状态
const loading = ref(false);

// 使用markRaw包装图标组件
const ViewIcon = markRaw(View);
const RightIcon = markRaw(Right);
const SaveIcon = markRaw(DocumentCopy);

// 模态框相关
const requestPreviewModalRef = ref<InstanceType<typeof RequestPreviewModal>>();

// 保存请求对话框相关
const saveDialogVisible = ref(false);
const saveFormRef = ref<FormInstance>();
const saveSubmitLoading = ref(false);
const saveForm = reactive({
  requestId: "",
  name: "",
  url: "",
});

const timeRange = ref<[Date, Date] | null>(null);

const formatDateTime = (date: Date): string => {
  const year = date.getFullYear();
  const month = String(date.getMonth() + 1).padStart(2, "0");
  const day = String(date.getDate()).padStart(2, "0");
  const hours = String(date.getHours()).padStart(2, "0");
  const minutes = String(date.getMinutes()).padStart(2, "0");
  const seconds = String(date.getSeconds()).padStart(2, "0");
  return `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`;
};

const loadRequestList = async () => {
  if (timeRange.value) {
    query.startTime = formatDateTime(timeRange.value[0]);
    query.endTime = formatDateTime(timeRange.value[1]);
  } else {
    query.startTime = null;
    query.endTime = null;
  }
  loading.value = true;
  try {
    const res = await RequestApi.getRequestList(query);
    list.value = res.data;
    total.value = res.total;
    queryPersistService.persistQuery("request-manager", query);
    console.log(res);
  } catch (e) {
    ElMessage.error("Failed to load configuration list");
    console.error("Failed to load configuration list", e);
  } finally {
    loading.value = false;
  }
};

const resetQuery = () => {
  query.requestId = null;
  query.method = null;
  query.url = null;
  query.source = null;
  query.status = null;
  query.startTime = null;
  query.endTime = null;
  query.replay = 0;
  timeRange.value = null;
  query.pageNum = 1;
  queryPersistService.clearQuery("request-manager");
  loadRequestList();
};

const resetForm = () => {
  // This function is no longer needed as the form is managed by RequestPreviewModal
};

//页面加载时自动加载数据
onMounted(() => {
  queryPersistService.loadQuery("request-manager", query);
  loadRequestList();
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

//打开预览请求模态框
const openViewModal = async (row: GetRequestListVo) => {
  try {
    //获取请求数据
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
    ElMessage.error("获取请求详情失败");
    console.error("获取请求详情失败", error);
  }
};

const saveRequest = (row: GetRequestListVo | GetRequestDetailsVo) => {
  saveForm.requestId = row.id.toString();
  saveForm.url = row.url;
  saveForm.name = row.url; // 默认使用URL作为名称
  saveDialogVisible.value = true;
};

const confirmSaveRequest = async () => {
  if (!saveFormRef.value) return;

  try {
    await saveFormRef.value.validate();
    saveSubmitLoading.value = true;

    await UserRequestApi.saveAsUserRequest({
      requestId: saveForm.requestId,
      name: saveForm.name,
    });

    ElMessage.success("保存成功");
    saveDialogVisible.value = false;
    loadRequestList();
  } catch (error) {
    console.error("保存失败:", error);
  } finally {
    saveSubmitLoading.value = false;
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

const goToReplay = (row: GetRequestListVo) => {
  localStorage.setItem("originRequestId", row.requestId);
  router.push({ name: "replay-request-manager" });
  ElMessage.success("已跳转到重放请求页面");
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

.request-table {
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

/* 保存请求对话框垂直居中 */
:deep(.modal-centered) {
  margin: 0 auto;
  top: 50%;
  transform: translateY(-50%);
}
</style>
