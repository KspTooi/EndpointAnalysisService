<template>
  <div class="list-container">
    <!-- 查询表单 -->
    <div class="query-form">
      <QueryPersistTip />
      <el-form :model="listForm">
        <el-row>
          <el-col :span="5" :offset="1">
            <el-form-item label="请求ID">
              <el-input v-model="listForm.requestId" placeholder="请输入请求ID" clearable />
            </el-form-item>
          </el-col>
          <el-col :span="5" :offset="1">
            <el-form-item label="中继服务器">
              <el-select v-model="listForm.relayServerId" placeholder="请选择中继服务器" clearable filterable>
                <el-option v-for="item in relayServerList" :key="item.id" :label="item.name" :value="item.id" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="5" :offset="1">
            <el-form-item label="请求URL">
              <el-input v-model="listForm.url" placeholder="请输入请求URL" clearable />
            </el-form-item>
          </el-col>
          <el-col :span="5" :offset="1">
            <el-form-item>
              <el-button type="primary" @click="loadList" :disabled="listLoading">查询</el-button>
              <el-button @click="resetList" :disabled="listLoading">重置</el-button>
              <ExpandButton v-model="uiState.isAdvancedSearch" :disabled="listLoading" />
            </el-form-item>
          </el-col>
        </el-row>
        <template v-if="uiState.isAdvancedSearch">
          <el-row>
            <el-col :span="5" :offset="1">
              <el-form-item label="请求方法">
                <el-input v-model="listForm.method" placeholder="请输入请求方法" clearable />
              </el-form-item>
            </el-col>
            <el-col :span="5" :offset="1">
              <el-form-item label="来源">
                <el-input v-model="listForm.source" placeholder="请输入来源" clearable />
              </el-form-item>
            </el-col>
            <el-col :span="5" :offset="1">
              <el-form-item label="状态">
                <el-select v-model="listForm.status" placeholder="请选择状态" clearable>
                  <el-option label="正常" value="0" />
                  <el-option label="HTTP失败" value="1" />
                  <el-option label="业务失败" value="2" />
                  <el-option label="连接超时" value="3" />
                </el-select>
              </el-form-item>
            </el-col>
          </el-row>

          <el-row>
            <el-col :span="5" :offset="1">
              <el-form-item label="时间区间">
                <el-date-picker
                  v-model="timeRange"
                  type="datetimerange"
                  range-separator="至"
                  start-placeholder="开始时间"
                  end-placeholder="结束时间"
                />
              </el-form-item>
            </el-col>
            <el-col :span="5" :offset="1">
              <el-form-item label="是否重放">
                <el-select v-model="listForm.replay" placeholder="请选择是否重放" clearable>
                  <el-option label="全部" :value="0" />
                  <el-option label="是" :value="1" />
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :span="5" :offset="1"></el-col>
          </el-row>
        </template>
      </el-form>
    </div>

    <div class="action-buttons">
      <el-button type="primary" @click="loadList" :disabled="listLoading">查询</el-button>
      <el-button @click="resetList" :disabled="listLoading">重置</el-button>
    </div>

    <!-- 配置列表 -->
    <div class="list-table">
      <div v-if="!listForm.relayServerId" class="empty-state">
        <el-empty description="请先选择中继服务器" />
      </div>
      <el-table v-else :data="listData" stripe v-loading="listLoading" border>
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
            <span
              :style="{
                color:
                  scope.row.status === 0
                    ? '#2ECC71'
                    : scope.row.status === 1
                      ? '#E74C3C'
                      : scope.row.status === 2
                        ? '#F1C40F'
                        : '#95A5A6',
              }"
            >
              {{
                scope.row.status === 0
                  ? "正常"
                  : scope.row.status === 1
                    ? "HTTP失败"
                    : scope.row.status === 2
                      ? "业务失败"
                      : "连接超时"
              }}
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
            <el-button link type="primary" size="small" @click="openViewModal(scope.row)" :icon="ViewIcon">
              预览请求
            </el-button>
            <el-button
              link
              type="success"
              size="small"
              @click="goToReplay(scope.row)"
              :icon="RightIcon"
              style="margin-left: 8px"
            >
              转到重放
            </el-button>
            <el-button
              link
              type="primary"
              size="small"
              @click="saveRequest(scope.row)"
              :icon="SaveIcon"
              style="margin-left: 8px"
            >
              保存
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div v-if="listForm.relayServerId" class="pagination-container">
        <el-pagination
          v-model:current-page="listForm.pageNum"
          v-model:page-size="listForm.pageSize"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          :total="listTotal"
          @size-change="
            (val: number) => {
              listForm.pageSize = val;
              loadList();
            }
          "
          @current-change="
            (val: number) => {
              listForm.pageNum = val;
              loadList();
            }
          "
          background
        />
      </div>
    </div>

    <RequestPreviewModal ref="requestPreviewModalRef" />

    <!-- 保存请求对话框 -->
    <el-dialog
      v-model="saveDialogVisible"
      title="另存为"
      width="400px"
      :close-on-click-modal="false"
      :close-on-press-escape="false"
      class="modal-centered"
    >
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
import { reactive, ref, onMounted, markRaw, watch } from "vue";
import type { GetRequestListDto, GetRequestListVo, GetRequestDetailsVo } from "@/api/relay/RequestApi.ts";
import RequestApi from "@/api/relay/RequestApi.ts";
import { ElMessage } from "element-plus";
import { DocumentCopy, View, Right } from "@element-plus/icons-vue";
import type { FormInstance } from "element-plus";
import { useRouter } from "vue-router";
import QueryPersistService from "@/service/QueryPersistService.ts";
import UserRequestApi from "@/api/requestdebug/UserRequestApi.ts";
import RequestPreviewModal from "@/components/RequestPreviewModal.vue";
import type { RequestPreviewVo } from "@/components/RequestPreviewModal.vue";
import type { HttpHeaderVo } from "@/api/requestdebug/UserRequestLogApi.ts";
import ExpandButton from "@/components/common/ExpandButton.vue";
import type { GetRelayServerListVo } from "@/api/relay/RelayServerApi";
import RelayServerApi from "@/api/relay/RelayServerApi";
import { Result } from "@/commons/entity/Result";

// 图标常量
const ViewIcon = markRaw(View);
const RightIcon = markRaw(Right);
const SaveIcon = markRaw(DocumentCopy);

// 列表相关变量
const listForm = reactive<GetRequestListDto>({
  relayServerId: null,
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

const listData = ref<GetRequestListVo[]>([]);
const listTotal = ref(0);
const listLoading = ref(false);

// UI状态变量
const uiState = reactive({
  isAdvancedSearch: false,
});

// 中继服务器列表
const relayServerList = ref<GetRelayServerListVo[]>([]);

// 时间范围
const timeRange = ref<[Date, Date] | null>(null);

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

// 路由和服务
const router = useRouter();
const queryPersistService = QueryPersistService;

// 格式化日期时间
const formatDateTime = (date: Date): string => {
  const year = date.getFullYear();
  const month = String(date.getMonth() + 1).padStart(2, "0");
  const day = String(date.getDate()).padStart(2, "0");
  const hours = String(date.getHours()).padStart(2, "0");
  const minutes = String(date.getMinutes()).padStart(2, "0");
  const seconds = String(date.getSeconds()).padStart(2, "0");
  return `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`;
};

// 解析请求头
const parseHeadersFromString = (headers: string): HttpHeaderVo[] => {
  if (!headers) {
    return [];
  }
  try {
    const parsed = JSON.parse(headers);
    if (typeof parsed === "object" && parsed !== null) {
      if (Array.isArray(parsed)) {
        return parsed;
      }
      return Object.entries(parsed).map(([k, v]) => ({ k, v: String(v) }));
    }
  } catch (e) {
    console.warn("无法将标头解析为JSON，将返回空数组", headers, e);
  }
  return [];
};

// 加载列表
const loadList = async () => {
  if (!listForm.relayServerId) {
    ElMessage.warning("请先选择中继服务器");
    return;
  }

  if (timeRange.value) {
    listForm.startTime = formatDateTime(timeRange.value[0]);
    listForm.endTime = formatDateTime(timeRange.value[1]);
  } else {
    listForm.startTime = null;
    listForm.endTime = null;
  }
  listLoading.value = true;
  try {
    const res = await RequestApi.getRequestList(listForm);
    listData.value = res.data;
    listTotal.value = res.total;
  } catch (e) {
    ElMessage.error(e.message);
    console.error("Failed to load configuration list", e);
  } finally {
    listLoading.value = false;
    queryPersistService.persistQuery("request-manager", listForm);
  }
};

// 重置查询条件
const resetList = () => {
  listForm.requestId = null;
  listForm.method = null;
  listForm.url = null;
  listForm.source = null;
  listForm.status = null;
  listForm.startTime = null;
  listForm.endTime = null;
  listForm.replay = 0;

  //重置时间范围
  timeRange.value = null;
  listForm.startTime = null;
  listForm.endTime = null;

  listForm.pageNum = 1;
  queryPersistService.clearQuery("request-manager");
  loadList();
};

// 加载中继服务器列表
const loadRelayServerList = async () => {
  const res = await RelayServerApi.getRelayServerList({
    pageNum: 1,
    pageSize: 100000,
    name: null,
    forwardUrl: null,
  });
  if (Result.isSuccess(res)) {
    relayServerList.value = res.data;
  }
};

// 打开预览请求模态框
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

// 保存请求
const saveRequest = (row: GetRequestListVo | GetRequestDetailsVo) => {
  saveForm.requestId = row.id.toString();
  saveForm.url = row.url;
  saveForm.name = row.url;
  saveDialogVisible.value = true;
};

// 确认保存请求
const confirmSaveRequest = async () => {
  if (!saveFormRef.value) {
    return;
  }

  try {
    await saveFormRef.value.validate();
    saveSubmitLoading.value = true;

    await UserRequestApi.saveAsUserRequest({
      requestId: saveForm.requestId,
      name: saveForm.name,
    });

    ElMessage.success("保存成功");
    saveDialogVisible.value = false;
    loadList();
  } catch (error) {
    console.error("保存失败:", error);
  } finally {
    saveSubmitLoading.value = false;
  }
};

// 复制文本
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

// 跳转到重放页面
const goToReplay = (row: GetRequestListVo) => {
  localStorage.setItem("originRequestId", row.requestId);
  router.push({ name: "replay-request-manager" });
  ElMessage.success("已跳转到重放请求页面");
};

// 监听时间范围变化
watch(timeRange, (newValue) => {
  if (!newValue) {
    return;
  }

  const [startDate, endDate] = newValue;
  const diffTime = Math.abs(endDate.getTime() - startDate.getTime());
  const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24));

  if (diffDays > 15) {
    ElMessage.warning("时间范围不可以超过15天");
    timeRange.value = null;
    return;
  }
});

// 生命周期
onMounted(async () => {
  queryPersistService.loadQuery("request-manager", listForm);
  await loadRelayServerList();
  await loadList();
});
</script>

<style scoped>
.list-container {
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

.list-table {
  margin-bottom: 20px;
  width: 100%;
  overflow-x: auto;
}

.empty-state {
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 40px 0;
  background-color: var(--el-bg-color);
  border-radius: 4px;
  border: 1px solid var(--el-border-color-light);
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
