<template>
    <div class="request-builder-container">
        <div class="request-builder-header">
            <div class="request-builder-header-title">
                <span>请求构建</span>
            </div>
        </div>
    </div>
</template>

<script setup lang="ts">
import UserRequestApi from '@/api/UserRequestApi';


const props = defineProps<{
    requestId: string
}>()

const loadRequestDetail = async () => {

    const res = await UserRequestApi.getUserRequest(props.requestId)

    console.log(res)



}




</script>

<style scoped>

</style>