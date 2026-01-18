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

    <RequestPreviewModal ref="requestPreviewModalRef" />
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, reactive, watch } from "vue";
import UserRequestLogApi, { type GetUserRequestLogDetailsVo, type GetUserRequestLogListDto, type GetUserRequestLogListVo } from "@/views/requestdebug/api/UserRequestLogApi.ts";
import { View as ViewIcon } from "@element-plus/icons-vue";
import { RequestTreeHolder } from "@/store/RequestTreeHolder";
import RequestPreviewModal from "@/components/RequestPreviewModal.vue";

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
const requestPreviewModalRef = ref<InstanceType<typeof RequestPreviewModal>>();

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

const previewRequest = async (row: GetUserRequestLogListVo) => {
  await loadUserRequestDetails(row.id);
  requestPreviewModalRef.value?.openPreview({
    id: details.id,
    requestId: details.requestId,
    method: details.method,
    url: details.url,
    source: details.source,
    requestHeaders: details.requestHeaders,
    requestBodyLength: details.requestBodyLength,
    requestBodyType: details.requestBodyType,
    requestBody: details.requestBody,
    responseHeaders: details.responseHeaders,
    responseBodyLength: details.responseBodyLength,
    responseBodyType: details.responseBodyType,
    responseBody: details.responseBody,
    statusCode: details.statusCode,
    redirectUrl: details.redirectUrl,
    status: details.status,
    requestTime: details.requestTime,
    responseTime: details.responseTime,
  });
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
