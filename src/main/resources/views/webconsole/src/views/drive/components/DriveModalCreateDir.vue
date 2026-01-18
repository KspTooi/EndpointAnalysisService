<template>
  <el-dialog
    v-model="modalVisible"
    title="创建文件夹"
    width="500px"
    :close-on-click-modal="false"
    class="modal-centered"
    @contextmenu.prevent
    @opened="handleDialogOpened"
  >
    <div class="modal-content">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="文件夹名称" prop="name">
          <el-input
            ref="nameInputRef"
            v-model="form.name"
            placeholder="请输入文件夹名称"
            clearable
            maxlength="128"
            show-word-limit
          />
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
import { Result } from "@/commons/entity/Result.ts";
import { ElMessage, type FormInstance, type FormRules } from "element-plus";
import { reactive, ref, watch, nextTick } from "vue";
import type { AddEntryDto, CurrentDirPo } from "@/views/drive/api/DriveTypes.ts";
import DriveApi from "@/views/drive/api/DriveApi.ts";
import GenricHotkeyService from "@/commons/service/GenricHotkeyService";

const props = defineProps<{
  currentDir: CurrentDirPo;
}>();

const emit = defineEmits<{
  (e: "success"): void;
}>();

const modalVisible = ref(false);
const formRef = ref<FormInstance>();
const nameInputRef = ref();
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

const openModal = () => {
  //清空表单
  resetModal();
  modalVisible.value = true;
};

const handleDialogOpened = () => {
  nameInputRef.value?.focus();
};

const resetModal = () => {
  formRef.value?.resetFields();
  form.name = "";
  form.parentId = props.currentDir.id;
  form.kind = 1;
  form.attachId = null;
  submitLoading.value = false;
};

const closeModal = () => {
  modalVisible.value = false;
};

const handleSubmit = async () => {
  console.log("handleSubmit", formRef.value);

  if (!formRef.value) {
    return;
  }

  await formRef.value.validate(async (valid) => {
    if (!valid) {
      return;
    }

    submitLoading.value = true;

    try {
      const result = await DriveApi.addEntry({
        parentId: form.parentId || null,
        name: form.name.trim(),
        kind: 1,
        attachId: null,
      });

      if (Result.isSuccess(result)) {
        ElMessage.success("创建文件夹成功");
        resetModal();
        closeModal();
        emit("success");
      }

      if (Result.isError(result)) {
        ElMessage.error(result.message || "创建文件夹失败");
      }
    } catch (error: any) {
      ElMessage.error(error.message || "创建文件夹失败");
      return;
    } finally {
      submitLoading.value = false;
    }
  });
};

defineExpose({
  openModal,
});

watch(modalVisible, (val) => {
  if (val) {
    nextTick(() => {
      const overlay = document.querySelector(".el-overlay");
      if (overlay) {
        overlay.addEventListener("contextmenu", (e) => {
          e.preventDefault();
          e.stopPropagation();
        });
      }
    });
  }
});

//快捷键功能打包
GenricHotkeyService.useHotkeyFunction(
  {
    enter: handleSubmit,
  },
  modalVisible,
  true
);
</script>

<style scoped>
:deep(.modal-centered) {
  margin: 0 auto;
  top: 50%;
  transform: translateY(-50%);
}

:deep(.el-overlay) {
  user-select: none;
}

:deep(.el-overlay) * {
  user-select: none;
}

.modal-content {
  padding: 20px 0;
}
</style>
