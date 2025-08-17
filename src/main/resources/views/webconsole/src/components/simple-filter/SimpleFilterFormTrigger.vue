<template>
  <div>
    <!-- 触发器编辑对话框 -->
    <el-dialog v-model="show" :title="mode === 'add' ? '新增触发器' : '编辑触发器'" width="500px" align-center>
      <el-form :model="formData" label-width="100px">
        <el-form-item label="名称">
          <el-input v-model="formData.name" placeholder="请输入触发器名称" disabled />
        </el-form-item>

        <el-form-item label="目标" required>
          <el-select v-model="formData.target" placeholder="请选择目标">
            <el-option label="标头" :value="0" />
            <el-option label="JSON载荷" :value="1" />
            <el-option label="URL" :value="2" />
            <el-option label="HTTP方法" :value="3" />
            <el-option label="总是触发" :value="4" />
          </el-select>
        </el-form-item>

        <el-form-item label="条件" required v-if="formData.target !== 4">
          <el-select v-model="formData.kind" placeholder="请选择条件">
            <el-option label="包含" :value="0" />
            <el-option label="不包含" :value="1" />
            <el-option label="等于" :value="2" />
            <el-option label="不等于" :value="3" />
            <el-option label="总是触发" :value="4" />
          </el-select>
        </el-form-item>

        <el-form-item label="目标键" required v-if="formData.target !== 4">
          <el-input v-model="formData.tk" placeholder="请输入目标键" />
        </el-form-item>

        <el-form-item label="比较值" required v-if="formData.target !== 4">
          <el-input v-model="formData.tv" placeholder="请输入比较值" />
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="show = false">取消</el-button>
        <el-button type="primary" @click="submit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import type { AddSimpleFilterTriggerDto, EditSimpleFilterTriggerDto } from "@/api/SimpleFilterApi";
import { nextTick, ref, watch } from "vue";

const show = ref(false);
const mode = ref<"add" | "edit">("add");
const dataIndex = ref<number>(-1);
const formData = ref<EditSimpleFilterTriggerDto>({
  id: null,
  name: "",
  target: 0,
  kind: 0,
  tk: "",
  tv: "",
});

const emit = defineEmits<{
  (e: "add", data: AddSimpleFilterTriggerDto): void;
  (e: "edit", data: EditSimpleFilterTriggerDto, dataIndex: number): void;
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
const openWithEdit = (data: EditSimpleFilterTriggerDto, idx: number) => {
  dataIndex.value = idx;
  resetFormData();
  formData.value = {
    id: data.id,
    name: data.name,
    target: data.target,
    kind: data.kind,
    tk: data.tk,
    tv: data.tv,
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
      tk: formData.value.tk,
      tv: formData.value.tv,
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
        tk: formData.value.tk,
        tv: formData.value.tv,
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
    tk: "",
    tv: "",
  };
};

defineExpose({
  openWithAdd,
  openWithEdit,
});
</script>

<style scoped></style>
