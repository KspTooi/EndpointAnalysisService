<template>
  <el-dialog
    v-model="modalVisible"
    title="创建集合"
    width="500px"
    :close-on-click-modal="false"
    class="modal-centered"
    @contextmenu.prevent
    @opened="handleDialogOpened"
  >
    <div class="modal-content">
      <el-form :model="modalForm" ref="modalFormRef" label-width="100px">
        <el-form-item label="名称" prop="name">
          <el-input
            ref="modalRef"
            v-model="modalForm.name"
            placeholder="请输入名称"
            clearable
            maxlength="128"
            show-word-limit
          />
        </el-form-item>
        <el-form-item label="类型" prop="kind">
          <el-radio-group v-model="modalForm.kind">
            <el-radio :label="0">请求</el-radio>
            <el-radio :label="1">请求组</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
    </div>
    <template #footer>
      <div class="dialog-footer">
        <el-button @click="closeModal">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="modalLoading">确定</el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { nextTick, reactive, ref } from "vue";
import type { AddCollectionDto } from "../api/CollectionApi";
import { ElMessage, type FormInstance } from "element-plus";
import CollectionApi from "../api/CollectionApi";
import { Result } from "@/commons/entity/Result";
import GenricHotkeyService from "@/service/GenricHotkeyService";

const emit = defineEmits<{
  (e: "on-success"): void;
}>();

const modalVisible = ref(false);
const modalFormRef = ref<FormInstance>();
const modalRef = ref();
const modalLoading = ref(false);

const modalForm = reactive<AddCollectionDto>({
  parentId: null,
  name: "",
  kind: 0,
  reqUrl: "",
  reqUrlParamsJson: "",
  reqMethod: 0,
  reqHeaderJson: "",
  reqBodyKind: 0,
  reqBodyJson: "",
});

/**
 * 打开创建集合弹窗
 * @param parentId 父级ID
 */
const openModal = (parentId: string) => {
  modalForm.parentId = parentId;
  modalForm.name = "未命名请求";
  modalVisible.value = true;
};

const closeModal = () => {
  modalVisible.value = false;
};

const handleDialogOpened = () => {
  modalRef.value?.focus();
  nextTick(() => {
    modalRef.value?.select();
  });
};

const handleSubmit = async () => {
  const result = await CollectionApi.addCollection(modalForm);
  if (Result.isSuccess(result)) {
    ElMessage.success("已创建:" + modalForm.name);
    closeModal();
    emit("on-success");
  }
};

defineExpose({
  openModal,
  closeModal,
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
