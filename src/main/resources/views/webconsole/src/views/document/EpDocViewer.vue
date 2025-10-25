<template>
  <div class="doc-viewer-container">
    <EpDocTagTree ref="epDocTagTreeRef" @select-operation="onSelectOperation" style="width: 400px;"/>
    <EpDocOperation ref="epDocOperationRef" :operation-id="activeOperationId" style="width: 100%;"/>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import EpDocTagTree from '@/components/EpDocTagTree.vue' 
import EpDocOperation from '@/components/EpDocOpeation.vue'

const epDocTagTreeRef = ref<InstanceType<typeof EpDocTagTree>>()
const epDocOperationRef = ref<InstanceType<typeof EpDocOperation>>()
const activeOperationId = ref<string | undefined>()

const onSelectOperation = (operationId: string) => {
  console.log('onSelectOperation', operationId)
  activeOperationId.value = operationId
}

</script>

<style scoped>
.doc-viewer-container {
  display: flex;
  flex-direction: row;
  height: 100%;
  width: 100%;
}
</style>