<template>
  <div class="request-payload-container">
    <div class="rb-radio-group">
      <el-radio-group v-model="requestBodyType" size="small">
        <el-radio label="none">none</el-radio>
        <el-radio label="multipart/form-data">form-data</el-radio>
        <el-radio label="x-www-form-urlencoded">x-www-form-urlencoded</el-radio>
        <el-radio label="text/plain">text</el-radio>
        <el-radio label="application/json">json</el-radio>
        <el-radio label="application/xml">xml</el-radio>
        <el-radio label="application/javascript">javascript</el-radio>
        <el-radio label="application/html">html</el-radio>
        <el-radio label="binary">binary</el-radio>
      </el-radio-group>
    </div>
    <div class="rb-payload-content">
      <JsonEditorVue style="height: 450px"
                     v-model="requestBody"
                     v-bind="{/* 局部 props & attrs */}"
                     v-model:mode="editorMode"
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import { type GetUserRequestDetailsVo } from '@/api/UserRequestApi';
import { ref, computed, watch } from 'vue';
import JsonEditorVue from 'json-editor-vue'
import { Mode } from 'vanilla-jsoneditor'

const props = defineProps<{
  requestDetails: GetUserRequestDetailsVo
}>()

const editorMode = ref<Mode.text>(Mode.text)
const requestBody = ref<string>(props.requestDetails.requestBody || '')
const requestBodyType = ref<string>(props.requestDetails.requestBodyType || 'text/plain')

</script>

<style scoped>
.request-payload-container {
  padding: 5px 15px 0 10px;
  border-radius: 4px;
  background-color: #fff;
}

.rb-radio-group {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
  padding-bottom: 12px;
}

.rb-toolbar {
  display: flex;
  gap: 8px;
}

.rb-payload-content {
  overflow: hidden;
}

.rb-empty-state {
  text-align: center;
  padding: 60px 20px;
  color: #909399;
}

.rb-empty-state p {
  margin: 0;
  font-size: 14px;
}
</style>