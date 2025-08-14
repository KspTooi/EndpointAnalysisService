<template>
  <div class="rb-container">

    <div class="rb-header">

      <div class="rb-header-title">
        <span>{{ requestDetail?.name || '未命名请求' }}</span>
      </div>

      <div class="rb-header-input" style="margin-top: 12px;">
        <RequestUrlInput :url="requestDetail.url" 
                       :method="requestDetail.method" 
                       @onUrlChange="onUrlChange"
                       @onSendRequest="onSendRequest"
                       :loading="loading"
                       />
      </div>

    </div>
    <!-- 选项卡 -->
    <div class="rb-tab">
      <div class="rb-tab-item" :class="{ active: activeTab === 'header' }" @click="activeTab = 'header'">
        标头
      </div>
      <div class="rb-tab-item" :class="{ active: activeTab === 'body' }" @click="activeTab = 'body'">
        载荷
      </div>
      <div class="rb-tab-item" :class="{ active: activeTab === 'response' }" @click="activeTab = 'response'">
        响应列表
      </div>
    </div>

    <div v-if="requestDetail" class="rb-content">

      <!-- 请求头内容 -->
      <div v-if="activeTab === 'header'" class="tab-panel">
        <div class="headers-editor">
          <div class="headers-toolbar">
            <button @click="addHeader" class="btn-add">添加请求头</button>
          </div>
          <div class="headers-table">
            <div class="headers-table-header">
              <div class="header-key-col">键</div>
              <div class="header-value-col">值</div>
              <div class="header-action-col">操作</div>
            </div>
            <div v-if="editableHeaders.length === 0" class="empty-state-compact">
              点击"添加请求头"开始编辑
            </div>
            <div v-for="(header, index) in editableHeaders" :key="index" class="header-row">
              <input 
                v-model="header.k" 
                @blur="onHeaderChange"
                class="header-key-input" 
                placeholder="请求头名称"
              />
              <input 
                v-model="header.v" 
                @blur="onHeaderChange"
                class="header-value-input" 
                placeholder="请求头值"
              />
              <button @click="removeHeader(index)" class="btn-remove">删除</button>
            </div>
          </div>
        </div>
      </div>

      <!-- 载荷内容 -->
      <div v-if="activeTab === 'body'" class="tab-panel">
        <RequestPayload :requestDetails="requestDetail" @onRequestBodyChange="onRequestBodyChange" />
      </div>

      <!-- 响应列表 -->
      <div v-if="activeTab === 'response'" class="tab-panel">
        <UrResponseList ref="urResponseListRef" :requestId="requestId" :loading="loading" />
      </div>

    </div>

    <div v-else class="rb-loading">
      加载中...
    </div>

  </div>
</template>

<script setup lang="ts">
import UserRequestApi, { type GetUserRequestDetailsVo, type RequestHeaderVo } from '@/api/UserRequestApi';
import { ref, watch, onMounted, nextTick } from 'vue';
import RequestUrlInput from "@/components/user-request-view/RequestUrlInput.vue";
import RequestPayload from './RequestPayload.vue';
import UrResponseList from './UrResponseList.vue';

const props = defineProps<{
  requestId: string | null
}>()

//当前选中的选项卡
const activeTab = ref<string>('header')

//完整用户请求数据
const requestDetail = ref<GetUserRequestDetailsVo>({
  id: "",
  method: null,
  name: null,
  requestBody: null,
  requestBodyType: null,
  requestHeaders: [],
  seq: null,
  url: null
})

//可编辑的请求头数据
const editableHeaders = ref<RequestHeaderVo[]>([])
const urResponseListRef = ref<InstanceType<typeof UrResponseList>>()

const loading = ref(false)

const loadRequestDetail = async () => {

  if(props.requestId == null){
    console.log('请求id为空')
    return
  }

  try{
    const res = await UserRequestApi.getUserRequestDetails({id:props.requestId || ''})
    requestDetail.value.id = res.id
    requestDetail.value.method = res.method
    requestDetail.value.name = res.name
    requestDetail.value.requestBody = res.requestBody
    requestDetail.value.requestBodyType = res.requestBodyType
    requestDetail.value.requestHeaders = res.requestHeaders
    requestDetail.value.seq = res.seq
    requestDetail.value.url = res.url
    
    // 初始化可编辑请求头
    editableHeaders.value = [...(res.requestHeaders || [])]
  }catch(e){
    requestDetail.value = {
      id: "",
      method: null,
      name: null,
      requestBody: null,
      requestBodyType: null,
      requestHeaders: [],
      seq: null,
      url: null
    }
    editableHeaders.value = []
  }
}

onMounted(()=>{
  loadComponentState()
})



watch(activeTab, (newVal) => {
  persistComponentState()
})

//监听外部请求id变化
watch(()=>props.requestId,async ()=>{
  if(props.requestId){
    console.log('监听外部请求id变化',props.requestId)
    loadRequestDetail()
  }
  if(props.requestId == null){
    console.log('监听外部请求id变化 为空')
    requestDetail.value = {
      id: "",
      method: null,
      name: null,
      requestBody: null,
      requestBodyType: null,
      requestHeaders: [],
      seq: null,
      url: null
    }
    editableHeaders.value = []
  }
})


/**
 * 持久化组件状态
 */
const persistComponentState = ()=>{
  localStorage.setItem('request_builder_activeTab', activeTab.value)
}

/**
 * 加载组件状态
 */
const loadComponentState = ()=>{
  const tab = localStorage.getItem('request_builder_activeTab')
  if(tab){
    activeTab.value = tab
  }
}

const onUrlChange = (method: string, url: string) => {
  requestDetail.value.method = method
  requestDetail.value.url = url
}

const onSendRequest = async () => {
  loading.value = true

  //先保存请求
  await UserRequestApi.editUserRequest({
    id: requestDetail.value.id,
    name: requestDetail.value.name,
    method: requestDetail.value.method,
    url: requestDetail.value.url,
    requestHeaders: requestDetail.value.requestHeaders,
    requestBodyType: requestDetail.value.requestBodyType,
    requestBody: requestDetail.value.requestBody,
  })

  await UserRequestApi.sendUserRequest({id: requestDetail.value.id})
  await urResponseListRef.value?.loadUserRequestLogList()
  loading.value = false
}

const onRequestBodyChange = (requestBody: string) => {
  requestDetail.value.requestBody = requestBody
}

// 添加请求头
const addHeader = () => {
  editableHeaders.value.push({ k: '', v: '' })
}

// 删除请求头
const removeHeader = (index: number) => {
  editableHeaders.value.splice(index, 1)
  onHeaderChange()
}

// 请求头变化时同步数据并保存
const onHeaderChange = () => {
  // 过滤掉空的请求头
  const validHeaders = editableHeaders.value.filter(h => h.k && h.k.trim())
  requestDetail.value.requestHeaders = validHeaders
  
  // 自动保存
  if(requestDetail.value.id){
    UserRequestApi.editUserRequest({
      id: requestDetail.value.id,
      name: requestDetail.value.name,
      method: requestDetail.value.method,
      url: requestDetail.value.url,
      requestHeaders: validHeaders,
      requestBodyType: requestDetail.value.requestBodyType,
      requestBody: requestDetail.value.requestBody,
    })
  }
}

</script>

<style scoped>
.rb-container {
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
  padding: 5px 5px 5px 15px;
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
  padding: 8px 0;
  margin-bottom: 8px;
}

.btn-add {
  background: #60b0ff;
  color: white;
  border: none;
  padding: 4px 12px;
  border-radius: 4px;
  cursor: pointer;
  font-size: 12px;
  transition: background 0.2s;
}

.btn-add:hover {
  background: #218838;
}

.headers-table {
  flex: 1;
  overflow-y: auto;
  padding-bottom: 25px;
}

.headers-table-header {
  display: grid;
  grid-template-columns: 2fr 3fr 80px;
  gap: 8px;
  padding: 6px 8px;
  background: #f8f9fa;
  border: 1px solid #e9ecef;
  border-radius: 4px;
  font-weight: 500;
  font-size: 12px;
  color: #495057;
  margin-bottom: 4px;
}

.header-key-col,
.header-value-col,
.header-action-col {
  text-align: left;
}

.header-row {
  display: grid;
  grid-template-columns: 2fr 3fr 80px;
  gap: 8px;
  padding: 4px 8px;
  align-items: center;
}

.header-key-input,
.header-value-input {
  border: 1px solid #ddd;
  border-radius: 3px;
  padding: 4px 8px;
  font-size: 12px;
  height: 28px;
  box-sizing: border-box;
}

.header-key-input:focus,
.header-value-input:focus {
  outline: none;
  border-color: #007bff;
  box-shadow: 0 0 0 2px rgba(0, 123, 255, 0.1);
}

.btn-remove {
  background: #dc3545;
  color: white;
  border: none;
  padding: 2px 8px;
  border-radius: 3px;
  cursor: pointer;
  font-size: 11px;
  height: 24px;
  transition: background 0.2s;
}

.btn-remove:hover {
  background: #c82333;
}

.empty-state-compact {
  text-align: center;
  color: #6c757d;
  padding: 20px;
  font-size: 12px;
  font-style: italic;
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
  font-family: 'Courier New', Courier, monospace;
  font-size: 13px;
  line-height: 1.5;
}

.empty-state {
  text-align: center;
  color: #6c757d;
  padding: 40px;
  font-style: italic;
}

.rb-loading {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 200px;
  color: #6c757d;
  font-size: 14px;
}
</style>