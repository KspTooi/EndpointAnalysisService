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
        <div v-if="requestDetail.requestHeaders && Object.keys(requestDetail.requestHeaders).length > 0" class="headers-list">
          <div v-for="(value, key) in requestDetail.requestHeaders" :key="key" class="header-item">
            <div class="header-key">{{ key }}</div>
            <div class="header-value">{{ value }}</div>
          </div>
        </div>
        <div v-else class="empty-state">
          暂无请求头
        </div>
      </div>

      <!-- 载荷内容 -->
      <div v-if="activeTab === 'body'" class="tab-panel">
        <RequestPayload :requestDetails="requestDetail" />
      </div>

    </div>

    <div v-else class="rb-loading">
      加载中...
    </div>

  </div>
</template>

<script setup lang="ts">
import UserRequestApi, { type GetUserRequestDetailsVo } from '@/api/UserRequestApi';
import { ref, watch, onMounted, nextTick } from 'vue';
import RequestUrlInput from "@/components/user-request-view/RequestUrlInput.vue";
import RequestPayload from './RequestPayload.vue';

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
  requestHeaders: new Map<string,string>(),
  seq: null,
  url: null
})



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
  }catch(e){
    requestDetail.value = {
      id: "",
      method: null,
      name: null,
      requestBody: null,
      requestBodyType: null,
      requestHeaders: new Map<string,string>(),
      seq: null,
      url: null
    }
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
      requestHeaders: new Map<string,string>(),
      seq: null,
      url: null
    }
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

.headers-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.header-item {
  display: flex;
  background: #f8f9fa;
  border-radius: 6px;
  overflow: hidden;
  border: 1px solid #e9ecef;
}

.header-key {
  background: #e9ecef;
  padding: 12px 16px;
  font-weight: 500;
  color: #495057;
  min-width: 150px;
  border-right: 1px solid #dee2e6;
}

.header-value {
  padding: 12px 16px;
  color: #6c757d;
  flex: 1;
  word-break: break-all;
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