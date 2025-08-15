<template>
  <div class="container">
    <div class="response-list-table">
      <el-table :data="list" v-loading="props.loading" border stripe size="small">
        <el-table-column prop="requestId" label="请求ID" min-width="150" show-overflow-tooltip />
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
        <el-table-column label="预览" min-width="50" show-overflow-tooltip>
          <template #default="scope">
            <el-button type="primary" @click="previewRequest(scope.row)" size="small" link :icon="ViewIcon"> 预览 </el-button>
          </template>
        </el-table-column>
      </el-table>
      <div class="pagination-container">
        <el-pagination
          size="small"
          v-model:current-page="query.pageNum"
          v-model:page-size="query.pageSize"
          :total="total"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="loadUserRequestLogList"
          @current-change="loadUserRequestLogList"
        />
      </div>
    </div>

    <!-- 请求详情模态框 -->
    <el-dialog v-model="dialogVisible" title="请求详情" width="800px" :close-on-click-modal="true" class="centered-dialog">
      <el-form v-if="dialogVisible" ref="formRef" :model="details" label-width="100px" :validate-on-rule-change="false">
        <el-tabs v-model="activeTab">
          <el-tab-pane label="负载" name="payload">
            <el-form-item label="请求体" prop="requestBody">
              <el-input :model-value="formatJson(details.requestBody)" type="textarea" :rows="14" readonly />
            </el-form-item>
            <el-form-item label="响应体" prop="responseBody">
              <el-input :model-value="formatJson(details.responseBody)" type="textarea" :rows="14" readonly />
            </el-form-item>
          </el-tab-pane>

          <el-tab-pane label="标头" name="headers">
            <el-form-item label="请求头" prop="requestHeaders">
              <el-table :data="details.requestHeaders" border stripe size="small">
                <el-table-column prop="k" label="键" />
                <el-table-column prop="v" label="值" />
              </el-table>
            </el-form-item>
            <el-form-item label="响应头" prop="responseHeaders">
              <el-table :data="details.responseHeaders" border stripe size="small">
                <el-table-column prop="k" label="键" />
                <el-table-column prop="v" label="值" />
              </el-table>
            </el-form-item>
          </el-tab-pane>

          <el-tab-pane label="详情" name="meta">
            <el-form-item label="请求ID" prop="requestId">
              <el-input v-model="details.requestId" disabled />
            </el-form-item>
            <el-form-item label="请求方法" prop="method">
              <el-input v-model="details.method" disabled />
            </el-form-item>
            <el-form-item label="请求URL" prop="url">
              <el-input v-model="details.url" disabled />
            </el-form-item>
            <el-form-item label="来源" prop="source">
              <el-input v-model="details.source" disabled />
            </el-form-item>

            <el-form-item label="请求体类型" prop="requestBodyType">
              <el-input v-model="details.requestBodyType" disabled />
            </el-form-item>
            <el-form-item label="请求体长度" prop="requestBodyLength">
              <el-input v-model="details.requestBodyLength" disabled />
            </el-form-item>
            <el-form-item label="响应体类型" prop="responseBodyType">
              <el-input v-model="details.responseBodyType" disabled />
            </el-form-item>
            <el-form-item label="响应体长度" prop="responseBodyLength">
              <el-input v-model="details.responseBodyLength" disabled />
            </el-form-item>

            <el-form-item label="HTTP状态码" prop="statusCode">
              <el-input v-model="details.statusCode" disabled />
            </el-form-item>
            <el-form-item label="重定向URL" prop="redirectUrl">
              <el-input v-model="details.redirectUrl" disabled />
            </el-form-item>
            <el-form-item label="状态" prop="status">
              <el-select v-model="details.status" disabled>
                <el-option label="正常" :value="0" />
                <el-option label="HTTP失败" :value="1" />
                <el-option label="业务失败" :value="2" />
                <el-option label="连接超时" :value="3" />
              </el-select>
            </el-form-item>
            <el-form-item label="请求时间" prop="requestTime">
              <el-input v-model="details.requestTime" disabled />
            </el-form-item>
            <el-form-item label="响应时间" prop="responseTime">
              <el-input v-model="details.responseTime" disabled />
            </el-form-item>
          </el-tab-pane>
        </el-tabs>
      </el-form>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, reactive, watch } from "vue";
import UserRequestLogApi, { type GetUserRequestLogDetailsVo, type GetUserRequestLogListDto, type GetUserRequestLogListVo } from "@/api/UserRequestLogApi";
import { View as ViewIcon } from "@element-plus/icons-vue";
import { RequestTreeHolder } from "@/store/RequestTreeHolder";

const props = defineProps<{
  loading: boolean;
}>();

//查询条件
const query = reactive<GetUserRequestLogListDto>({
  userRequestId: RequestTreeHolder().getActiveRequestId,
  pageNum: 1,
  pageSize: 10,
});

const details = reactive<GetUserRequestLogDetailsVo>({
  id: "",
  requestId: "",
  method: "",
  url: "",
  source: "",
  requestHeaders: [],
  requestBodyLength: 0,
  requestBodyType: "",
  requestBody: "",
  responseHeaders: [],
  responseBodyLength: 0,
  responseBodyType: "",
  responseBody: "",
  statusCode: 0,
  redirectUrl: "",
  status: 0,
  requestTime: "",
  responseTime: "",
});
const dialogVisible = ref(false);
const activeTab = ref<"payload" | "headers" | "meta">("payload");

const list = ref<GetUserRequestLogListVo[]>([]);
const total = ref(0);

const loadUserRequestLogList = async () => {
  if (!query.userRequestId) {
    list.value = [];
    return;
  }

  const res = await UserRequestLogApi.getUserRequestLogList(query);
  list.value = res.data;
  total.value = res.total;
};

const loadUserRequestDetails = async (id: string) => {
  if (!id) {
    return;
  }
  const res = await UserRequestLogApi.getUserRequestDetails(id);
  details.id = res.id;
  details.requestId = res.requestId;
  details.method = res.method;
  details.url = res.url;
  details.source = res.source;
  details.requestHeaders = res.requestHeaders;
  details.requestBodyLength = res.requestBodyLength;
  details.requestBodyType = res.requestBodyType;
  details.requestBody = res.requestBody;
  details.responseHeaders = res.responseHeaders;
  details.responseBodyLength = res.responseBodyLength;
  details.responseBodyType = res.responseBodyType;
  details.responseBody = res.responseBody;
  details.statusCode = res.statusCode;
  details.redirectUrl = res.redirectUrl;
  details.status = res.status;
  details.requestTime = res.requestTime;
  details.responseTime = res.responseTime;
};

const previewRequest = (row: GetUserRequestLogListVo) => {
  dialogVisible.value = true;
  loadUserRequestDetails(row.id);
};

onMounted(() => {
  loadUserRequestLogList();
});

watch(
  () => RequestTreeHolder().getActiveRequestId,
  () => {
    query.userRequestId = RequestTreeHolder().getActiveRequestId;
    loadUserRequestLogList();
  }
);

defineExpose({
  loadUserRequestLogList,
});

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
.response-list-table {
  display: flex;
  flex-direction: column;
  height: 100%;
}

.pagination-container {
  display: flex;
  justify-content: flex-end;
  margin-top: 20px;
  width: 100%;
}
</style>
