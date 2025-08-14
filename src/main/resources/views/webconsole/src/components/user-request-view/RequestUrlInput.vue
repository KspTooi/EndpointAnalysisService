<template>
    <div class="url-input">
        <div class="url-input-url">
            <el-input v-model="url" placeholder="请输入请求URL" >
                <template #prepend>
                    <el-select  v-model="method" placeholder="请选择请求方法" :filterable="true" style="width: 110px">
                        <el-option label="GET" value="GET" />
                        <el-option label="POST" value="POST" />
                        <el-option label="PUT" value="PUT" />
                        <el-option label="DELETE" value="DELETE" />
                        <el-option label="PATCH" value="PATCH" />
                        <el-option label="HEAD" value="HEAD" />
                        <el-option label="OPTIONS" value="OPTIONS" />
                        <el-option label="TRACE" value="TRACE" />
                        <el-option label="CONNECT" value="CONNECT" />
                    </el-select>
                </template>
            </el-input>
        </div>
        <div class="url-input-send">
            <el-button type="primary" @click="sendRequest">发送</el-button>
        </div>
    </div>
</template>

<script setup lang="ts">
import { ref } from 'vue';
import { watch } from 'vue';

const props = defineProps<{
    url: string | null
    method: string | null
}>()

const method = ref<string>(props.method || 'GET')
const url = ref<string>(props.url || 'http://')

watch(() => props.method, (newVal) => {
    method.value = newVal || 'GET'
})
watch(() => props.url, (newVal) => {
    url.value = newVal || 'http://'
})


watch(method, (newVal) => {
    emit('onUrlChange', newVal, url.value)
})
watch(url, (newVal) => {
    emit('onUrlChange', method.value, newVal)
})

const emit = defineEmits<{
  (event: 'onSendRequest', method: string, url: string): void;
  (event: 'onUrlChange', method: string, url: string): void;
}>()


const sendRequest = () => {
    emit('onSendRequest', method.value, url.value)
}

</script>

<style scoped>
.url-input {
    display: flex;
    flex-direction: row;
    align-items: center;
    justify-content: flex-start;
    width: 100%;
    gap: 10px;
}


.url-input-url {
    width: 100%;
}

</style>