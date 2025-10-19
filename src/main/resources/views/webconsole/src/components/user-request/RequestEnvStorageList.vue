<template>
  <div class="list-container">
    <!-- 查询表单 -->
    <div class="query-form">
      <el-form :model="query" inline>
        <el-form-item>
          <el-form-item label="变量名" prop="name">
            <el-input v-model="query.name" placeholder="请输入变量名" />
          </el-form-item>
          <el-form-item label="状态" prop="status">
            <el-select v-model="query.status" placeholder="请选择状态" style="width: 200px">
              <el-option label="全部" :value="null" />
              <el-option label="启用" :value="0" />
              <el-option label="禁用" :value="1" />
            </el-select>
          </el-form-item>
          <el-button type="primary" @click="loadList">查询</el-button>
          <el-button @click="resetList">重置</el-button>
        </el-form-item>
      </el-form>
      <el-button type="success" @click="openModal('add', null)">创建共享存储变量</el-button>
    </div>

    <!-- 列表 -->
    <div class="list-table">
      <el-table :data="list" v-loading="loading" border row-key="id" default-expand-all>
        <el-table-column label="变量名" prop="name" min-width="180" show-overflow-tooltip />
        <el-table-column label="初始值" prop="initValue" min-width="180" show-overflow-tooltip />
        <el-table-column label="当前值" prop="value" min-width="180" show-overflow-tooltip>
          <template #default="scope">
            <span v-if="scope.row.value === null">--</span>
            <span v-else style="color: #7b0087">{{ scope.row.value }}</span>
          </template>
        </el-table-column>
        <el-table-column label="状态" prop="status" width="90" show-overflow-tooltip>
          <template #default="scope">
            <el-tag :type="scope.row.status === 0 ? 'success' : 'danger'">{{ scope.row.status === 0 ? "启用" : "禁用" }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="创建时间" prop="createTime" width="180" show-overflow-tooltip />
        <el-table-column label="操作" fixed="right" width="200">
          <template #default="scope">
            <el-button link type="primary" size="small" @click="openModal('edit', scope.row)" :icon="ViewIcon"> 编辑 </el-button>
            <el-button link type="danger" size="small" @click="removeList(scope.row.id)" :icon="DeleteIcon"> 删除 </el-button>
          </template>
        </el-table-column>
      </el-table>
      <!-- 分页 -->
      <div class="pagination-container">
        <el-pagination
          v-model:current-page="query.pageNum"
          v-model:page-size="query.pageSize"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          :total="total"
          @size-change="
            (val: number) => {
              query.pageSize = val;
              loadList();
            }
          "
          @current-change="
            (val: number) => {
              query.pageNum = val;
              loadList();
            }
          "
          background
        />
      </div>
    </div>

    <!-- 共享存储编辑模态框 -->
    <el-dialog
      v-model="modalVisible"
      :title="modalMode === 'edit' ? '编辑共享存储变量' : '添加共享存储变量'"
      width="550px"
      class="modal-centered"
      :close-on-click-modal="false"
      @close="
        resetModal();
        loadList();
      "
    >
      <el-form v-if="modalVisible" ref="modalFormRef" :model="modalForm" :rules="modalRules" label-width="95px" :validate-on-rule-change="false">
        <el-form-item label="变量名" prop="name">
          <el-input v-model="modalForm.name" placeholder="请输入变量名" />
        </el-form-item>
        <el-form-item label="初始值" prop="initValue">
          <el-input v-model="modalForm.initValue" placeholder="请输入初始值" />
        </el-form-item>
        <el-form-item label="当前值" prop="value">
          <el-input v-model="modalForm.value" placeholder="请输入当前值" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-select v-model="modalForm.status" placeholder="请选择状态">
            <el-option label="启用" :value="0" />
            <el-option label="禁用" :value="1" />
          </el-select>
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
import { Result } from "@/commons/entity/Result";
import { ElMessage, ElMessageBox, type FormInstance } from "element-plus";
import { reactive, ref, watch } from "vue";
import { Delete as DeleteIcon, View as ViewIcon } from "@element-plus/icons-vue";
import UserRequestEnvStorageApi, {
  type GetUserRequestEnvStorageDetailsVo,
  type GetUserRequestEnvStorageListDto,
  type GetUserRequestEnvStorageListVo,
} from "@/api/userrequest/UserRequestEnvStorageApi.ts";

const props = defineProps<{
  requestEnvId: string | null;
}>();

//列表内容
const query = reactive<GetUserRequestEnvStorageListDto>({
  requestEnvId: props.requestEnvId,
  name: null,
  status: null,
  pageNum: 1,
  pageSize: 10,
});

const list = ref<GetUserRequestEnvStorageListVo[]>([]);
const total = ref(0);
const loading = ref(false);

const loadList = async () => {
  if (props.requestEnvId === null) {
    list.value = [];
    total.value = 0;
    return;
  }
  loading.value = true;
  query.requestEnvId = props.requestEnvId;
  const result = await UserRequestEnvStorageApi.getUserRequestEnvStorageList(query);

  if (Result.isSuccess(result)) {
    list.value = result.data;
    total.value = result.total;
  }

  if (Result.isError(result)) {
    ElMessage.error(result.message);
  }

  loading.value = false;
};

const resetList = () => {
  query.name = null;
  query.status = null;
  query.pageNum = 1;
  query.pageSize = 10;
  loadList();
};

const removeList = async (id: string) => {
  try {
    await ElMessageBox.confirm("确定删除该共享存储变量吗？", "提示", {
      confirmButtonText: "确定",
      cancelButtonText: "取消",
      type: "warning",
    });
  } catch (error) {
    return;
  }

  try {
    const result = await UserRequestEnvStorageApi.removeUserRequestEnvStorage({ id });
    if (Result.isSuccess(result)) {
      ElMessage.success("删除成功");
    }
    if (Result.isError(result)) {
      ElMessage.error(result.message);
      return;
    }
  } catch (error: any) {
    ElMessage.error(error.message);
    return;
  }
  loadList();
};

watch(
  () => props.requestEnvId,
  () => {
    loadList();
  }
);

loadList();

//模态框内容
const modalVisible = ref(false);
const modalFormRef = ref<FormInstance>();
const modalLoading = ref(false);
const modalMode = ref<"add" | "edit">("add"); //add:添加,edit:编辑
const modalForm = reactive<GetUserRequestEnvStorageDetailsVo>({
  id: null,
  envId: null,
  name: "",
  initValue: "",
  value: "",
  status: 0,
  createTime: "",
  updateTime: "",
});

const modalRules = {
  name: [
    { required: true, message: "请输入变量名", trigger: "blur" },
    { max: 32, message: "变量名长度不能超过32个字符", trigger: "blur" },
  ],
  initValue: [
    { required: true, message: "请输入初始值", trigger: "blur" },
    { max: 500, message: "初始值长度不能超过500个字符", trigger: "blur" },
  ],
  value: [
    { required: true, message: "请输入当前值", trigger: "blur" },
    { max: 500, message: "当前值长度不能超过500个字符", trigger: "blur" },
  ],
  status: [{ required: true, message: "请选择状态", trigger: "change" }],
};

const openModal = async (mode: "add" | "edit", row: GetUserRequestEnvStorageListVo | null) => {
  modalMode.value = mode;
  resetModal();

  //如果是编辑模式则需要加载详情数据
  if (mode === "edit" && row) {
    const ret = await UserRequestEnvStorageApi.getUserRequestEnvStorageDetails({ id: row.id });

    if (Result.isSuccess(ret)) {
      modalForm.id = ret.data.id;
      modalForm.envId = ret.data.envId;
      modalForm.name = ret.data.name;
      modalForm.initValue = ret.data.initValue;
      modalForm.value = ret.data.value;
      modalForm.status = ret.data.status;
      modalForm.createTime = ret.data.createTime;
      modalForm.updateTime = ret.data.updateTime;
    }

    if (Result.isError(ret)) {
      ElMessage.error(ret.message);
      return;
    }
  } else {
    modalForm.envId = props.requestEnvId;
  }

  modalVisible.value = true;
};

const resetModal = () => {
  modalForm.id = null;
  modalForm.envId = null;
  modalForm.name = "";
  modalForm.initValue = "";
  modalForm.value = "";
  modalForm.status = 0;
  modalForm.createTime = "";
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
      await UserRequestEnvStorageApi.addUserRequestEnvStorage({
        requestEnvId: props.requestEnvId,
        name: modalForm.name,
        initValue: modalForm.initValue,
        value: modalForm.value,
        status: modalForm.status,
      });
      ElMessage.success("新增成功");
      resetModal();
    }

    if (modalMode.value === "edit") {
      await UserRequestEnvStorageApi.editUserRequestEnvStorage({
        id: modalForm.id,
        name: modalForm.name,
        initValue: modalForm.initValue,
        value: modalForm.value,
        status: modalForm.status,
      });
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
.list-container {
  padding: 20px;
  width: 100%;
  box-sizing: border-box;
  height: 100%;
  display: flex;
  flex-direction: column;
}

.query-form {
  margin-bottom: 20px;
  flex-shrink: 0;
}

.list-table {
  margin-bottom: 20px;
  width: 100%;
  overflow-x: auto;
  flex: 1;
  min-height: 0;
}

.pagination-container {
  display: flex;
  justify-content: flex-end;
  margin-top: 20px;
  width: 100%;
  flex-shrink: 0;
}

:deep(.modal-centered) {
  margin: 0 auto;
  top: 50%;
  transform: translateY(-50%);
}
</style>
