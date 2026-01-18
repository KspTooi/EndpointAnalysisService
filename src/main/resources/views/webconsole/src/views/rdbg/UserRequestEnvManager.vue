<template>
  <div class="user-request-env-manager-container">
    <RequestEnvSideList
      ref="requestEnvSideListRef"
      @onAdd="openModal('add', null)"
      @onEdit="openModal('edit', $event)"
      @onSelect="handleEnvSelect"
    />

    <div class="env-storage-container">
      <RequestEnvStorageList :request-env-id="selectedRequestEnvId" />
    </div>

    <!-- 用户请求环境编辑模态框 -->
    <el-dialog
      v-model="modalVisible"
      :title="modalMode === 'edit' ? '编辑用户请求环境' : '添加用户请求环境'"
      width="550px"
      :close-on-click-modal="false"
      @close="
        resetModal();
        loadList();
      "
    >
      <el-form
        v-if="modalVisible"
        ref="modalFormRef"
        :model="modalForm"
        :rules="modalRules"
        label-width="95px"
        :validate-on-rule-change="false"
      >
        <el-form-item label="环境名称" prop="name">
          <el-input v-model="modalForm.name" placeholder="请输入环境名称" />
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="modalForm.remark" placeholder="请输入备注" />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="modalVisible = false">取消</el-button>
          <el-button type="primary" @click="submitModal" :loading="modalLoading">
            {{ modalMode === "add" ? "创建" : "保存" }}
          </el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { Result } from "@/commons/entity/Result.ts";
import { ElMessage, ElMessageBox, type FormInstance } from "element-plus";
import { reactive, ref } from "vue";
import { Delete as DeleteIcon, View as ViewIcon } from "@element-plus/icons-vue";
import UserRequestEnvApi, {
  type GetUserRequestEnvDetailsVo,
  type GetUserRequestEnvListDto,
  type GetUserRequestEnvListVo,
} from "@/views/rdbg/api/UserRequestEnvApi.ts";
import RequestEnvSideList from "@/components/user-request/RequestEnvSideList.vue";
import RequestEnvStorageList from "@/components/user-request/RequestEnvStorageList.vue";

const loadList = () => {
  requestEnvSideListRef?.value?.loadList();
};

const selectedRequestEnvId = ref<string | null>(null);

const handleEnvSelect = (item: GetUserRequestEnvListVo) => {
  selectedRequestEnvId.value = item.id;
};

//模态框内容
const modalVisible = ref(false);
const modalFormRef = ref<FormInstance>();
const requestEnvSideListRef = ref<InstanceType<typeof RequestEnvSideList>>();
const modalLoading = ref(false);
const modalMode = ref<"add" | "edit">("add"); //add:添加,edit:编辑
const modalForm = reactive<GetUserRequestEnvDetailsVo>({
  id: null,
  name: "",
  remark: "",
  updateTime: "",
});

const modalRules = {
  name: [
    { required: true, message: "请输入环境名称", trigger: "blur" },
    { max: 32, message: "环境名称长度不能超过32个字符", trigger: "blur" },
  ],
  remark: [
    { required: false, message: "请输入备注", trigger: "blur" },
    { max: 5000, message: "备注长度不能超过5000个字符", trigger: "blur" },
  ],
};

const openModal = async (mode: "add" | "edit", row: GetUserRequestEnvListVo | null) => {
  modalMode.value = mode;
  resetModal();

  //如果是编辑模式则需要加载详情数据
  if (mode === "edit" && row) {
    const ret = await UserRequestEnvApi.getUserRequestEnvDetails({ id: row.id });

    if (Result.isSuccess(ret)) {
      modalForm.id = ret.data.id;
      modalForm.name = ret.data.name;
      modalForm.remark = ret.data.remark;
      modalForm.updateTime = ret.data.updateTime;
    }

    if (Result.isError(ret)) {
      ElMessage.error(ret.message);
      return;
    }
  }

  modalVisible.value = true;
};

const resetModal = () => {
  modalForm.id = null;
  modalForm.name = "";
  modalForm.remark = "";
  modalForm.updateTime = "";
};

const submitModal = async () => {
  //先校验表单
  try {
    await modalFormRef?.value?.validate();
  } catch (error) {
    return;
  }

  modalLoading.value = true;

  //提交表单
  try {
    if (modalMode.value === "add") {
      await UserRequestEnvApi.addUserRequestEnv(modalForm);
      ElMessage.success("新增成功");
      resetModal();
    }

    if (modalMode.value === "edit") {
      await UserRequestEnvApi.editUserRequestEnv(modalForm);
      ElMessage.success("操作成功");
    }
  } catch (error: any) {
    ElMessage.error(error.message);
    return;
  } finally {
    modalLoading.value = false;
  }

  //modalVisible.value = false;
  loadList();
};
</script>

<style scoped>
.user-request-env-manager-container {
  display: flex;
  height: 100%;
  width: 100%;
}

.env-storage-container {
  flex: 1;
  min-width: 0;
}

/* .list-container {
  padding: 20px;
  max-width: 100%;
  overflow-x: auto;
  width: 100%;
} */

.query-form {
  margin-bottom: 20px;
}

.list-table {
  margin-bottom: 20px;
  width: 100%;
  overflow-x: auto;
}

.pagination-container {
  display: flex;
  justify-content: flex-end;
  margin-top: 20px;
  width: 100%;
}
</style>
