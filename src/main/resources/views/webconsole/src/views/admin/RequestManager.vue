<template>
  <div class="request-manager-container">
    <!-- 查询表单 -->
    <div class="query-form">
      <el-form :model="query" inline>
        <el-form-item label="请求ID"> 
          <el-input 
            v-model="query.requestId" 
            placeholder="请输入请求ID" 
            clearable 
            style="width: 200px"
          />
        </el-form-item>
        <el-form-item label="请求方法">
          <el-input
            v-model="query.method"
            placeholder="请输入请求方法"
            clearable
            style="width: 200px"
          />
        </el-form-item>
        <el-form-item label="请求URL">
          <el-input 
            v-model="query.url" 
            placeholder="请输入请求URL" 
            clearable 
            style="width: 200px"
          />
        </el-form-item>
        <el-form-item label="来源"> 
          <el-input 
            v-model="query.source" 
            placeholder="请输入来源" 
            clearable 
            style="width: 200px"
          />
        </el-form-item>
        <el-form-item label="状态">
          <el-select 
            v-model="query.status" 
            placeholder="请选择状态" 
            clearable 
            style="width: 200px"
          >
            <el-option label="正常" value="0" />
            <el-option label="HTTP失败" value="1" />
            <el-option label="业务失败" value="2" />
            <el-option label="连接超时" value="3" />
          </el-select>
        </el-form-item>
        <el-form-item label="开始时间">
          <el-date-picker
            v-model="query.startTime"
            type="datetime"
            placeholder="请选择开始时间区间"
            style="width: 200px"
          />
        </el-form-item>
        <el-form-item label="结束时间">
          <el-date-picker
            v-model="query.endTime"
            type="datetime"
            placeholder="请选择结束时间区间"
            style="width: 200px"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="loadRequestList">查询</el-button>
          <el-button @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>
    </div>

    <!-- 配置列表 -->
    <div class="request-table">
      <el-table
        :data="list"
        stripe
        v-loading="loading"
        border
      >
        <el-table-column 
          prop="requestId" 
          label="请求ID" 
          min-width="150"
          show-overflow-tooltip
        >
          <template #default="scope">
            <el-tooltip content="点击复制" placement="top">
              <el-link type="primary" underline="never" @click="copyText(scope.row.requestId)">
                {{ scope.row.requestId }}
              </el-link>
            </el-tooltip>
          </template>
        </el-table-column>
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
        
        <el-table-column label="操作" fixed="right" min-width="100">
          <template #default="scope">
            <el-button 
              link
              type="primary" 
              size="small" 
              @click="openViewModal(scope.row)"
              :icon="ViewIcon"
            >
              预览请求
            </el-button>
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
          @size-change="(val: number) => {
            query.pageSize = val
            loadRequestList()
          }"
          @current-change="(val: number) => {
            query.pageNum = val
            loadRequestList()
          }"
          background
        />
      </div>
    </div>

    <!-- 请求编辑模态框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="modalMode === 'view' ? '预览请求' : '编辑请求'"
      width="800px"
      :close-on-click-modal="true"
      class="centered-dialog"
    >
      <el-form
        v-if="dialogVisible"
        ref="formRef"
        :model="details"
        :rules="rules"
        label-width="100px"
        :validate-on-rule-change="false"
      >
        <el-tabs v-model="activeTab">
          <el-tab-pane label="负载" name="payload">
            <el-form-item label="请求体" prop="requestBody">
              <el-input
                :model-value="formatJson(details.requestBody)"
                type="textarea"
                :rows="14"
                readonly
              />
            </el-form-item>
            <el-form-item label="响应体" prop="responseBody">
              <el-input
                :model-value="formatJson(details.responseBody)"
                type="textarea"
                :rows="14"
                readonly
              />
            </el-form-item>
          </el-tab-pane>

          <el-tab-pane label="标头" name="headers">
            <el-form-item label="请求头" prop="requestHeaders">
              <el-input v-model="details.requestHeaders" type="textarea" :rows="12" disabled />
            </el-form-item>
            <el-form-item label="响应头" prop="responseHeaders">
              <el-input v-model="details.responseHeaders" type="textarea" :rows="12" disabled />
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
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">Cancel</el-button>
          <el-button type="primary" @click="saveRequest" :loading="submitLoading">
            Confirm
          </el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import {reactive, ref, onMounted} from "vue";
import type { GetRequestListDto, GetRequestListVo, GetRequestDetailsVo} from "@/api/RequestApi.ts";
import RequestApi from "@/api/RequestApi.ts";
import { ElMessage } from 'element-plus';
import { Edit, DocumentCopy, View } from '@element-plus/icons-vue';
import { markRaw } from 'vue';
import type { FormInstance } from 'element-plus';

const query = reactive<GetRequestListDto>({
  requestId: null,
  method: null,
  url: null,
  source: null,
  status: null,
  startTime: null,
  endTime: null,
  pageNum: 1,
  pageSize: 10
})

const list = ref<GetRequestListVo[]>([])
const total = ref(0)

// 加载状态
const loading = ref(false)

// 使用markRaw包装图标组件
const EditIcon = markRaw(Edit);
const ViewIcon = markRaw(View);

// 模态框相关
const dialogVisible = ref(false)
const formRef = ref<FormInstance>()
const submitLoading = ref(false)
const modalMode = ref<("view" | "edit")>("view") //view:预览,edit:编辑

// 预览Tab
const activeTab = ref<'payload' | 'headers' | 'meta'>('payload')


//表单数据
const details = reactive<GetRequestDetailsVo>({
  id: 0,
  requestId: "",
  method: "",
  url: "",
  source: "",
  requestHeaders: "",
  requestBodyLength: 0,
  requestBodyType: "",
  requestBody: {},
  responseHeaders: "",
  responseBodyLength: 0,
  responseBodyType: "",
  responseBody: {},
  statusCode: 0,
  redirectUrl: "",
  status: 0,
  requestTime: "",
  responseTime: ""
})

// 表单校验规则
const rules = {
  key: [
    { required: true, message: 'Please enter key', trigger: 'blur' }
  ],
  value: [
    { required: true, message: 'Please enter value', trigger: 'blur' }
  ],
  description: [
    { max: 200, message: 'Description cannot exceed 200 characters', trigger: 'blur' }
  ]
}

const updateTimeRange = ref<[string, string] | null>(null)

const loadRequestList = async () => {
  if (updateTimeRange.value) {
    query.startTime = updateTimeRange.value[0]
    query.endTime = updateTimeRange.value[1]
  } else {
    query.startTime = null
    query.endTime = null
  }
  loading.value = true
  try {
    const res = await RequestApi.getRequestList(query);
    list.value = res.data;
    total.value = res.total;
    console.log(res)
  } catch (e) {
    ElMessage.error('Failed to load configuration list');
    console.error("Failed to load configuration list", e);
  } finally {
    loading.value = false
  }
}

const resetQuery = () => {
  query.requestId = null
  query.method = null
  query.url = null
  query.source = null
  query.status = null
  query.startTime = null
  query.endTime = null
  updateTimeRange.value = null
  query.pageNum = 1
  loadRequestList()
}

const resetForm = () => {
  details.id = 0
  details.requestId = ""
  details.method = ""
  details.url = ""
  details.source = ""
  details.requestHeaders = ""
  details.requestBodyLength = 0
  details.requestBodyType = ""
  details.requestBody = {}
  details.responseHeaders = ""
  details.responseBodyLength = 0
  details.responseBodyType = ""
  details.responseBody = {}
  details.statusCode = 0
  details.redirectUrl = ""
  details.status = 0
  details.requestTime = ""
  details.responseTime = ""
  
  if (formRef.value) {
    formRef.value.resetFields()
  }
}

//页面加载时自动加载数据
onMounted(() => {
  loadRequestList()
})

//打开预览请求模态框
const openViewModal = async (row: GetRequestListVo) => {
  try {

    //获取请求数据
    const res = await RequestApi.getRequestDetails(row.id.toString())
    console.log(res)
    details.id = res.id
    details.requestId = res.requestId
    details.method = res.method
    details.url = res.url
    details.source = res.source
    details.requestHeaders = res.requestHeaders
    details.requestBody = res.requestBody
    details.responseHeaders = res.responseHeaders
    details.responseBody = res.responseBody
    details.statusCode = res.statusCode
    details.redirectUrl = res.redirectUrl
    details.status = res.status
    details.requestTime = res.requestTime
    details.responseTime = res.responseTime
    details.requestBodyLength = res.requestBodyLength
    details.requestBodyType = res.requestBodyType
    details.responseBodyLength = res.responseBodyLength
    details.responseBodyType = res.responseBodyType
    modalMode.value = "view"
    activeTab.value = 'payload'
    dialogVisible.value = true
  } catch (error) {
    ElMessage.error('获取请求详情失败')
    console.error('获取请求详情失败', error)
  }
}


const openUpdateModal = async (row: GetRequestListVo) => {
  try {
    resetForm()
    
    const res = await RequestApi.getRequestDetails(row.id.toString())
    Object.assign(details, res)
    
    dialogVisible.value = true
  } catch (error) {
    ElMessage.error('Failed to get configuration details')
    console.error('Failed to get configuration details', error)
  }
}

const saveRequest = async () => {
  if (!formRef.value) return
  
  await formRef.value.validate(async (valid) => {
    if (!valid) return
    
    submitLoading.value = true
    try {
      //await RequestApi.editRequest(details)
      console.log(details)
      ElMessage.success('Configuration updated successfully')
      dialogVisible.value = false
      loadRequestList()
    } catch (error) {
      const errorMsg = error instanceof Error ? error.message : 'Operation failed'
      ElMessage.error(errorMsg)
    } finally {
      submitLoading.value = false
    }
  })
}

const copyText = async (text: string) => {
  if (!text) {
    ElMessage.warning('内容为空，无法复制')
    return
  }
  try {
    if (navigator.clipboard && window.isSecureContext) {
      await navigator.clipboard.writeText(text)
      ElMessage.success('已复制到剪贴板')
      return
    }
    const textarea = document.createElement('textarea')
    textarea.value = text
    textarea.style.position = 'fixed'
    textarea.style.left = '-9999px'
    document.body.appendChild(textarea)
    textarea.focus()
    textarea.select()
    const success = document.execCommand('copy')
    document.body.removeChild(textarea)
    if (success) {
      ElMessage.success('已复制到剪贴板')
      return
    }
    ElMessage.error('复制失败')
  } catch (e) {
    ElMessage.error('复制失败')
  }
}

const formatJson = (data: unknown): string => {
  if (data === null || data === undefined) return ''
  if (typeof data === 'string') {
    const trimmed = data.trim()
    if (!trimmed) return ''
    try {
      const parsed = JSON.parse(trimmed)
      return JSON.stringify(parsed, null, 2)
    } catch (_) {
      return data
    }
  }
  try {
    return JSON.stringify(data, null, 2)
  } catch (_) {
    return String(data)
  }
}
</script>

<style scoped>
.request-manager-container {
  padding: 20px;
  max-width: 100%;
  overflow-x: auto;
}

.query-form {
  margin-bottom: 20px;
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