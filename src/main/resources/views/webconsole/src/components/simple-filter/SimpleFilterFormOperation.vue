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
        </el-select>
      </el-form-item>

      <el-form-item label="目标" required>
        <el-select v-model="formData.target" placeholder="请选择目标">
          <el-option label="标头" :value="0" />
          <el-option label="JSON载荷" :value="1" />
          <el-option label="URL" :value="2" v-if="formData.kind === 4" />
        </el-select>
      </el-form-item>

      <el-form-item label="原始键" required>
        <el-input v-model="formData.f" placeholder="请输入原始键" />
      </el-form-item>

      <el-form-item label="目标键" required>
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
import type { AddSimpleFilterOperationDto, EditSimpleFilterOperationDto } from "@/api/SimpleFilterApi";
import { ref } from "vue";

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

defineExpose({
  openWithAdd,
  openWithEdit,
});
</script>

<style scoped></style>
