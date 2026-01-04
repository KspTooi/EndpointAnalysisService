<template>
  <div class="flex flex-col h-full bg-white select-none">
    <div class="flex bg-white select-none items-center px-4 py-1.5 border-b border-gray-200 flex-shrink-0">
      <div class="text-sm font-semibold text-gray-700 text-[12px]">请求体类型</div>
      <div class="flex justify-between items-center px-4 max-w-65">
        <!-- 0:空 1:form-data 2:raw-text 3:raw-javascription 4:raw-json 5:raw-html 6:raw-xml 7:binary 8:x-www-form-urlencoded -->
        <el-select size="small" placeholder="请选择请求体类型" filterable v-model="reqBody.kind">
          <el-option :value="0" label="空请求体"></el-option>
          <el-option :value="1" label="form-data"></el-option>
          <el-option :value="2" label="text/plain"></el-option>
          <el-option :value="3" label="application/javascript"></el-option>
          <el-option :value="4" label="application/json"></el-option>
          <el-option :value="5" label="text/html"></el-option>
          <el-option :value="6" label="application/xml"></el-option>
          <el-option :value="7" label="二进制(application/octet-stream)"></el-option>
          <el-option :value="8" label="x-www-form-urlencoded"></el-option>
        </el-select>
      </div>
    </div>

    <div class="flex-1 min-h-0">
      <!-- 表单参数编辑器 -->
      <rdbg-param-editor
        v-show="reqBody.kind === 1"
        v-model="reqBody.formData"
        title="表单类型(form-data | multipart/form-data)"
        th
      />
      <rdbg-param-editor v-show="reqBody.kind === 8" v-model="reqBody.formDataUrlEncoded" title="表单类型(URL编码)" th />
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, ref } from "vue";
import type { RequestBodyJson } from "@/views/requestdebug/api/CollectionApi";
import RdbgParamEditor from "@/views/requestdebug/components/RdbgParamEditor.vue";

const reqBody = defineModel<RequestBodyJson>("modelValue", { required: true });
</script>

<style scoped></style>
