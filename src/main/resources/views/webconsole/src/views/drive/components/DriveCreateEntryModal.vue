<template>
  <el-dialog :model-value="props.visible" title="创建文件夹" width="500px" :close-on-click-modal="false" class="modal-centered" @close="closeModal">
    <div class="modal-content">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="文件夹名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入文件夹名称" clearable maxlength="128" show-word-limit />
        </el-form-item>
        <el-form-item label="父级ID" prop="parentId">
          <el-input v-model="form.parentId" placeholder="留空表示创建在根目录" clearable />
        </el-form-item>
      </el-form>
    </div>
    <template #footer>
      <div class="dialog-footer">
        <el-button @click="closeModal">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitLoading">确定</el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import DriveApi, { type AddEntryDto } from "@/api/drive/DriveApi.ts";
import { Result } from "@/commons/entity/Result.ts";
import { ElMessage, type FormInstance, type FormRules } from "element-plus";
import { reactive, ref, watch } from "vue";

const props = defineProps<{
  visible: boolean;
  parentId?: string | null;
}>();

const emit = defineEmits<{
  (e: "close"): void;
  (e: "success"): void;
}>();

const formRef = ref<FormInstance>();
const submitLoading = ref(false);

const form = reactive<AddEntryDto>({
  parentId: null,
  name: "",
  kind: 1,
  attachId: null,
});

const rules = reactive<FormRules>({
  name: [
    { required: true, message: "文件夹名称不能为空", trigger: "blur" },
    { min: 1, max: 128, message: "文件夹名称长度必须在1-128个字符之间", trigger: "blur" },
  ],
});

const handleVisibleChange = (val: boolean) => {
  if (!val) {
    resetModal();
  }
};

const resetModal = () => {
  formRef.value?.resetFields();
  form.name = "";
  form.parentId = props.parentId || null;
  form.kind = 1;
  form.attachId = null;
  submitLoading.value = false;
};

const closeModal = () => {
  emit("close");
};

const handleSubmit = async () => {
  if (!formRef.value) {
    return;
  }

  await formRef.value.validate(async (valid) => {
    if (!valid) {
      return;
    }

    submitLoading.value = true;
    const result = await DriveApi.addEntry({
      parentId: form.parentId || null,
      name: form.name.trim(),
      kind: 1,
      attachId: null,
    });

    if (Result.isSuccess(result)) {
      ElMessage.success("创建文件夹成功");
      resetModal();
      emit("success");
    }
    if (Result.isError(result)) {
      ElMessage.error(result.message || "创建文件夹失败");
    }
    submitLoading.value = false;
  });
};

watch(
  () => props.visible,
  (newVal) => {
    if (newVal) {
      form.parentId = props.parentId || null;
    }
  }
);

watch(
  () => props.parentId,
  (newVal) => {
    if (props.visible) {
      form.parentId = newVal || null;
    }
  }
);
</script>

<style scoped>
:deep(.modal-centered) {
  margin: 0 auto;
  top: 50%;
  transform: translateY(-50%);
}

.modal-content {
  padding: 20px 0;
}
</style>
