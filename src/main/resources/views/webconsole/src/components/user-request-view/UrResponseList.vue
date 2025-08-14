<template>
  <div class="container">
    <div class="response-list-table">
      <el-table :data="list" v-loading="loading" border stripe size="small">
        <el-table-column
              prop="requestId"
              label="请求ID"
              min-width="150"
              show-overflow-tooltip
          />
          <el-table-column
              prop="method"
              label="方法"
              min-width="50"
              show-overflow-tooltip
          >
            <template #default="scope">
            <span :style="{color: scope.row.method === 'DELETE' ? '#E74C3C' : scope.row.method === 'GET' ? '#3498DB' : scope.row.method === 'POST' ? '#2ECC71' : scope.row.method === 'PUT' ? '#F1C40F' : '#95A5A6'}">
              {{ scope.row.method.toUpperCase() }}
            </span>
            </template>
          </el-table-column>
          <el-table-column
              prop="url"
              label="请求URL"
              min-width="180"
              show-overflow-tooltip
          />
          <el-table-column
              prop="source"
              label="来源"
              min-width="60"
              show-overflow-tooltip
          />
          <el-table-column
              prop="status"
              label="状态"
              min-width="50"
              show-overflow-tooltip
          >
            <template #default="scope">
              <span :style="{color: scope.row.status === 0 ? '#2ECC71' : scope.row.status === 1 ? '#E74C3C' : scope.row.status === 2 ? '#F1C40F' : '#95A5A6'}">
                {{ scope.row.status === 0 ? '正常' : scope.row.status === 1 ? 'HTTP失败' : scope.row.status === 2 ? '业务失败' : '连接超时' }}
              </span>
            </template>
          </el-table-column>
          <el-table-column
              prop="statusCode"
              label="HTTP"
              min-width="35"
              show-overflow-tooltip
          >
            <template #default="scope">
            <span :style="{color: scope.row.statusCode >= 200 && scope.row.statusCode < 300 ? '#2ECC71' : scope.row.statusCode >= 300 && scope.row.statusCode < 400 ? '#F1C40F' : scope.row.statusCode >= 400 && scope.row.statusCode < 500 ? '#E74C3C' : '#95A5A6'}">
              {{ scope.row.statusCode }}
            </span>
            </template>
          </el-table-column>
          <el-table-column
              prop="requestTime"
              label="请求时间"
              show-overflow-tooltip
          />
      </el-table>
      <div class="pagination-container">
          <el-pagination size="small"
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
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, reactive, watch } from 'vue';
import UserRequestLogApi, { type GetUserRequestLogListDto, type GetUserRequestLogListVo } from '@/api/UserRequestLogApi';

const props = defineProps<{
  requestId: string | null
}>()

//查询条件
const query = reactive<GetUserRequestLogListDto>({
  userRequestId: props.requestId,
  pageNum: 1,
  pageSize: 10
})

const list = ref<GetUserRequestLogListVo[]>([])
const total = ref(0)
const loading = ref(false)

const loadUserRequestLogList = async () => {

  if (!query.userRequestId) {
    list.value = []
    return
  }

  loading.value = true
  const res = await UserRequestLogApi.getUserRequestLogList(query)
  list.value = res.data
  total.value = res.total
  loading.value = false
}

onMounted(() => {
  loadUserRequestLogList()
})


watch(() => props.requestId, () => {
  query.userRequestId = props.requestId
  loadUserRequestLogList()
})

defineExpose({
  loadUserRequestLogList
})

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