<template>
  <el-dialog
    v-model="modalVisible"
    title="重命名"
    width="500px"
    :close-on-click-modal="false"
    @contextmenu.prevent
    @opened="onDialogOpened"
  >
    <div class="modal-content">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="名称" prop="name">
          <el-input ref="nameInputRef" v-model="form.name" placeholder="请输入名称" clearable maxlength="128" show-word-limit />
        </el-form-item>
      </el-form>
    </div>
    <template #footer>
      <div class="dialog-footer">
        <el-button @click="closeModal">取消</el-button>
        <el-button type="primary" @click="onSubmit" :loading="submitLoading">确定</el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import DriveApi from "@/views/drive/api/DriveApi.ts";
import { Result } from "@/commons/entity/Result.ts";
import { ElMessage, type FormInstance, type FormRules } from "element-plus";
import { reactive, ref, watch, nextTick } from "vue";
import type { GetEntryListItemVo, RenameEntryDto } from "@/views/drive/api/DriveTypes.ts";
import GenricHotkeyService from "@/service/GenricHotkeyService";

const emit = defineEmits<{
  (e: "success"): void;
}>();

const modalVisible = ref(false);
const formRef = ref<FormInstance>();
const nameInputRef = ref();
const submitLoading = ref(false);
const currentEntry = ref<GetEntryListItemVo | null>(null);

const form = reactive<RenameEntryDto>({
  entryId: "",
  name: "",
});

const rules = reactive<FormRules>({
  name: [
    { required: true, message: "名称不能为空", trigger: "blur" },
    { min: 1, max: 128, message: "名称长度必须在1-128个字符之间", trigger: "blur" },
  ],
});

const openModal = (entry: GetEntryListItemVo) => {
  if (!entry) {
    return;
  }

  currentEntry.value = entry;
  resetModal();
  form.entryId = entry.id;
  form.name = entry.name;
  modalVisible.value = true;
};

const onDialogOpened = () => {
  nameInputRef.value?.focus();
  nextTick(() => {
    nameInputRef.value?.select();
  });
};

const resetModal = () => {
  formRef.value?.resetFields();
  form.name = "";
  form.entryId = "";
  submitLoading.value = false;
};

const closeModal = () => {
  modalVisible.value = false;
};

const onSubmit = async () => {
  if (!formRef.value) {
    return;
  }

  await formRef.value.validate(async (valid) => {
    if (!valid) {
      return;
    }

    const trimmedName = form.name.trim();

    if (!trimmedName) {
      ElMessage.error("名称不能为空");
      return;
    }

    submitLoading.value = true;

    try {
      const result = await DriveApi.renameEntry({
        entryId: form.entryId,
        name: trimmedName,
      });

      if (Result.isSuccess(result)) {
        ElMessage.success("重命名成功");
        resetModal();
        closeModal();
        emit("success");
        return;
      }

      if (Result.isError(result)) {
        ElMessage.error(result.message || "重命名失败");
      }
    } catch (error: any) {
      ElMessage.error(error.message || "重命名失败");
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
    enter: onSubmit,
  },
  modalVisible,
  true
);
</script>

<style scoped>
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
