<template>
  <el-dialog v-model="show" :title="mode === 'add' ? '新增操作' : '编辑操作'" width="500px" align-center>
    <el-form :model="formData" label-width="100px">
      <el-form-item label="名称">
        <el-input v-model="formData.name" placeholder="请输入操作名称" disabled />
      </el-form-item>

      <el-form-item label="类型" required>
        <el-select v-model="formData.kind" placeholder="请选择类型">
          <el-option label="持久化" :value="0" />
          <el-option label="缓存" :value="1" />
          <el-option label="注入缓存" :value="2" />
          <el-option label="注入持久化" :value="3" />
          <el-option label="覆写URL" :value="4" />
          <el-option label="标记请求状态" :value="50" />
          <el-option label="获取请求ID" :value="60" />
        </el-select>
      </el-form-item>

      <el-form-item label="目标" required>
        <el-select v-model="formData.target" placeholder="请选择目标">
          <el-option label="标头" :value="0" v-if="formData.kind !== 4 && formData.kind !== 50" />
          <el-option label="JSON载荷" :value="1" v-if="formData.kind !== 4 && formData.kind !== 50" />
          <el-option label="URL" :value="2" v-if="formData.kind === 4" />
          <el-option label="正常" :value="50" v-if="formData.kind === 50" />
          <el-option label="HTTP失败" :value="51" v-if="formData.kind === 50" />
          <el-option label="业务失败" :value="52" v-if="formData.kind === 50" />
          <el-option label="连接超时" :value="53" v-if="formData.kind === 50" />
        </el-select>
      </el-form-item>

      <el-form-item label="原始键" required v-if="formData.kind !== 4 && formData.kind !== 50">
        <el-input v-model="formData.f" placeholder="请输入原始键" />
      </el-form-item>

      <el-form-item label="目标键" required v-if="formData.kind !== 50 && formData.kind !== 60">
        <el-input v-model="formData.t" placeholder="请输入目标键" />
      </el-form-item>
    </el-form>

    <template #footer>
      <el-button @click="show = false">取消</el-button>
      <el-button type="primary" @click="submit">确定</el-button>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import type { AddSimpleFilterOperationDto, EditSimpleFilterOperationDto } from "@/views/requestdebug/api/SimpleFilterApi.ts";
import { ref, watch } from "vue";

const show = ref(false);
const mode = ref<"add" | "edit">("add");
const dataIndex = ref<number>(-1);
const formData = ref<EditSimpleFilterOperationDto>({
  id: null,
  name: "",
  target: 0,
  kind: 0,
  f: "",
  t: "",
});

const emit = defineEmits<{
  (e: "add", data: AddSimpleFilterOperationDto): void;
  (e: "edit", data: EditSimpleFilterOperationDto, dataIndex: number): void;
}>();

/**
 * 打开新增触发器对话框
 */
const openWithAdd = () => {
  resetFormData();
  mode.value = "add";
  show.value = true;
};

/**
 * 打开编辑触发器对话框
 */
const openWithEdit = (data: EditSimpleFilterOperationDto, idx: number) => {
  dataIndex.value = idx;
  resetFormData();
  formData.value = {
    id: data.id,
    name: data.name,
    target: data.target,
    kind: data.kind,
    f: data.f,
    t: data.t,
  };
  mode.value = "edit";
  show.value = true;
};

const submit = () => {
  //当类型是4:覆写URL时，target变更为2:URL
  if (formData.value.kind === 4) {
    formData.value.target = 2;
    //清除原始键
    formData.value.f = null;
  }

  //当类型是50:标记请求状态时，清除原始键和目标键
  if (formData.value.kind === 50) {
    formData.value.f = null;
    formData.value.t = "";
  }

  //当类型是60:获取请求ID时，清除目标键
  if (formData.value.kind === 60) {
    formData.value.t = "";
  }

  //新增
  if (mode.value === "add") {
    emit("add", {
      name: formData.value.name,
      target: formData.value.target,
      kind: formData.value.kind,
      f: formData.value.f,
      t: formData.value.t,
      seq: 0,
    });
  }

  //编辑
  if (mode.value === "edit") {
    emit(
      "edit",
      {
        id: formData.value.id,
        name: formData.value.name,
        target: formData.value.target,
        kind: formData.value.kind,
        f: formData.value.f,
        t: formData.value.t,
      },
      dataIndex.value
    );
  }

  resetFormData();
  show.value = false;
};

const resetFormData = () => {
  formData.value = {
    id: null,
    name: "",
    target: 0,
    kind: 0,
    f: "",
    t: "",
  };
};

// 监听kind变化，确保target值合法
watch(
  () => formData.value.kind,
  (newKind) => {
    if (newKind === 50) {
      // 如果target不在50-53范围内，重置为50
      if (formData.value.target < 50 || formData.value.target > 53) {
        formData.value.target = 50;
      }
      return;
    } 
    if (newKind === 4) {
      // 覆写URL类型，target必须是2
      formData.value.target = 2;
      return;
    } 
    // 其他类型(0,1,2,3)，如果target不是0或1，重置为0
    if (formData.value.target !== 0 && formData.value.target !== 1) {
      formData.value.target = 0;
    }
  }
);

defineExpose({
  openWithAdd,
  openWithEdit,
});
</script>

<style scoped></style>
