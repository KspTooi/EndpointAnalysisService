<template>
  <div class="request-payload-container">
    <div class="rb-radio-group">
      <el-radio-group v-model="requestBodyType" size="small">
        <el-radio :disabled="true" :value="'none'">none</el-radio>
        <el-radio :disabled="true" :value="'multipart/form-data'">form-data</el-radio>
        <el-radio :disabled="true" :value="'x-www-form-urlencoded'">x-www-form-urlencoded</el-radio>
        <el-radio :disabled="true" :value="'text/plain'">text</el-radio>
        <el-radio :value="'application/json'">json</el-radio>
        <el-radio :disabled="true" :value="'application/xml'">xml</el-radio>
        <el-radio :disabled="true" :value="'application/javascript'">javascript</el-radio>
        <el-radio :disabled="true" :value="'application/html'">html</el-radio>
        <el-radio :disabled="true" :value="'binary'">binary</el-radio>
      </el-radio-group>
    </div>
    <div class="rb-payload-content">
      <JsonEditorVue
        style="height: 500px"
        v-model="requestBody"
        v-bind="{
          /* 局部 props & attrs */
        }"
        v-model:mode="editorMode"
        ref="jsonEditorRef"
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import { type GetUserRequestDetailsVo } from "@/api/requestdebug/UserRequestApi.ts";
import { ref, computed, watch } from "vue";
import JsonEditorVue from "json-editor-vue";
import { Mode } from "vanilla-jsoneditor";
import ContentTypeService, { ContentType } from "@/service/ContentTypeService";
import { EventHolder } from "@/store/EventHolder";

const emit = defineEmits<{
  (event: "onRequestBodyChange", requestBody: string): void;
}>();

const props = defineProps<{
  requestDetails: GetUserRequestDetailsVo;
}>();

const editorMode = ref<Mode.text>(Mode.text);
const requestBody = ref<string>(props.requestDetails.requestBody || "");
const requestBodyType = ref<string>(props.requestDetails.requestBodyType || "text/plain");
const jsonEditorRef = ref<InstanceType<typeof JsonEditorVue>>();

watch(
  () => props.requestDetails.requestBody,
  (newVal) => {
    if (newVal) {
      requestBody.value = newVal;
    }
    if (newVal === null) {
      requestBody.value = "";
    }
  },
  { immediate: true, deep: true }
);

watch(
  () => props.requestDetails.requestBodyType,
  (newVal) => {
    const contentType = ContentTypeService.getContentType(newVal);
    requestBodyType.value = contentType.toString();
  },
  { immediate: true, deep: true }
);

//监听载荷变化
watch(requestBody, (newVal) => {
  emit("onRequestBodyChange", newVal);
});

watch(
  () => props.requestDetails.requestBody,
  (newVal) => {
    try {
      if (newVal) {
        //手动格式化
        requestBody.value = JSON.stringify(JSON.parse(requestBody.value), null, 2);
      }
    } catch (e) {
      console.error("手动格式化失败", e);
    }
  }
);
</script>

<style>
.jse-menu {
  background-color: #4ba5ff !important;
  border-radius: 5px 5px 0 0;
}
.jse-status-bar {
  border-radius: 0 0 5px 5px;
}
</style>

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
  margin-bottom: 10px;
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
