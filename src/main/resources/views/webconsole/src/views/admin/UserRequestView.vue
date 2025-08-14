<template>
  <div class="request-viewer-container">
    <RequestTree style="width: 350px;" @select-request="onSelectRequest" />
    <RequestBuilder :request-id="requestId"/>
  </div>
</template>

<script setup lang="ts">
import { ref } from "vue";
import RequestTree from "@/components/user-request-view/RequestTree.vue";
import RequestBuilder from "@/components/user-request-view/RequestBuilder.vue";
import type { GetUserRequestDetailsVo } from "@/api/UserRequestApi";
import UserRequestApi from "@/api/UserRequestApi";

const requestId = ref<string | null>(null)

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

//选择器选择请求
const onSelectRequest = (id: string | null) => {
  requestId.value = id
}

const loadRequestDetail = async () => {

  if(requestId.value == null){
    console.log('无法加载请求详情,请求id为空')
    return
  }

  try{
    const res = await UserRequestApi.getUserRequestDetails({id:requestId.value || ''})
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
      requestHeaders: [],
      seq: null,
      url: null
    }
  }
}


</script>

<style scoped>
.request-viewer-container {
  display: flex;
  flex-direction: row;
  flex: 1;
  overflow: hidden;
  min-height: 0;
  width: 100%;
}
</style>