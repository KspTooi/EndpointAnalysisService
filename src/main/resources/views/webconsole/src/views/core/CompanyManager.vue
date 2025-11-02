<template>
  <div class="list-container">
    <!-- 查询表单 -->
    <div class="query-form">
      <el-form :model="listForm">
        <el-row>
          <el-col :span="5" :offset="1">
            <el-form-item label="团队名称">
              <el-input v-model="listForm.name" placeholder="请输入团队名称" clearable />
            </el-form-item>
          </el-col>
          <el-col :span="5" :offset="1">
            <!-- 占位，保持布局一致性 -->
          </el-col>
          <el-col :span="5" :offset="1">
            <!-- 占位，保持布局一致性 -->
          </el-col>
          <el-col :span="3" :offset="3">
            <el-form-item>
              <el-button type="primary" @click="loadList" :disabled="listLoading">查询</el-button>
              <el-button @click="resetList" :disabled="listLoading">重置</el-button>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
    </div>

    <div class="action-buttons">
      <el-button type="success" @click="openModal('add')">创建团队</el-button>
    </div>

    <!-- 列表 -->
    <div class="list-table">
      <el-table :data="listData" v-loading="listLoading" border row-key="id">
        <el-table-column label="团队名称" prop="name" show-overflow-tooltip />
        <el-table-column label="团队描述" prop="description" show-overflow-tooltip />
        <el-table-column label="创始人" prop="founderName" width="120" />
        <el-table-column label="现任CEO" prop="ceoName" width="120" />
        <el-table-column label="成员数量" prop="memberCount" width="100" align="center" />
        <el-table-column label="创建时间" prop="createTime" width="180" />
        <el-table-column label="更新时间" prop="updateTime" width="180" />
        <el-table-column label="操作" fixed="right" width="280">
          <template #default="scope">
            <div style="display: inline-flex; justify-content: flex-end; align-items: center; gap: 2px; width: 100%">
              <el-button link type="primary" size="small" @click="openModal('edit', scope.row)" :icon="ViewIcon">编辑</el-button>
              <el-button link type="danger" size="small" @click="leaveCompany(scope.row.id)" :icon="SwitchButton">退出</el-button>
              <!-- <el-button link type="danger" size="small" @click="removeCompany(scope.row.id)" :icon="DeleteIcon">删除</el-button> -->
            </div>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <!-- 分页 -->
    <div class="pagination-container">
      <el-pagination
        v-model:current-page="listForm.pageNum"
        v-model:page-size="listForm.pageSize"
        :page-sizes="[10, 20, 50, 100]"
        layout="total, sizes, prev, pager, next, jumper"
        :total="listTotal"
        @size-change="
          () => {
            loadList();
          }
        "
        @current-change="
          () => {
            loadList();
          }
        "
      />
    </div>

    <!-- 团队编辑模态框 -->
    <el-dialog
      v-model="modalVisible"
      :title="modalMode === 'edit' ? '编辑团队' : '添加团队'"
      width="550px"
      :close-on-click-modal="false"
      @close="resetModal()"
      class="modal-centered"
    >
      <el-form v-if="modalVisible" ref="modalFormRef" :model="modalForm" :rules="modalRules" label-width="80px" :validate-on-rule-change="false">
        <el-form-item label="团队名称" prop="name">
          <el-input v-model="modalForm.name" placeholder="请输入团队名称" clearable />
        </el-form-item>
        <el-form-item label="团队描述" prop="description">
          <el-input v-model="modalForm.description" placeholder="请输入团队描述" clearable type="textarea" :rows="3" />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="modalVisible = false">关闭</el-button>
          <el-button type="primary" @click="submitModal" :loading="modalLoading">
            {{ modalMode === "add" ? "创建" : "保存" }}
          </el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import type { GetCurrentUserCompanyListDto, GetCurrentUserCompanyListVo, AddCompanyDto, EditCompanyDto, GetCompanyDetailsVo } from "@/api/core/CompanyApi";
import CompanyApi from "@/api/core/CompanyApi";
import { Result } from "@/commons/entity/Result";
import { ElMessage, ElMessageBox, type FormInstance } from "element-plus";
import { reactive, ref } from "vue";
import { Delete as DeleteIcon, View as ViewIcon, SwitchButton } from "@element-plus/icons-vue";

const listForm = reactive<GetCurrentUserCompanyListDto>({
  name: "",
  pageNum: 1,
  pageSize: 10,
});

const listData = ref<GetCurrentUserCompanyListVo[]>([]);
const listTotal = ref(0);
const listLoading = ref(false);

const loadList = async () => {
  listLoading.value = true;
  const result = await CompanyApi.getCurrentUserCompanyList(listForm);

  if (Result.isSuccess(result)) {
    listData.value = result.data;
    listTotal.value = result.total;
  }

  if (Result.isError(result)) {
    ElMessage.error(result.message);
  }

  listLoading.value = false;
};

const resetList = () => {
  listForm.pageNum = 1;
  listForm.pageSize = 10;
  listForm.name = "";
  loadList();
};

const removeCompany = async (id: string) => {
  try {
    await ElMessageBox.confirm("确定删除该团队吗？", "提示", {
      confirmButtonText: "确定",
      cancelButtonText: "取消",
      type: "warning",
    });
  } catch (error) {
    return;
  }

  try {
    const result = await CompanyApi.removeCompany({ id });
    if (Result.isSuccess(result)) {
      ElMessage.success("删除成功");
      await loadList();
    }
  } catch (error: any) {
    ElMessage.error(error.message);
  }
};

const leaveCompany = async (id: string) => {
  try {
    await ElMessageBox.confirm(
      `<strong>您确定要退出该团队吗？</strong><br/>
      <ol style="text-align: left; margin: 10px 0;">
        <li>如果您是团队中的最后一位成员，您的退出将会导致团队被注销</li>
        <li>如果您是CEO，并且团队中还有其他成员时，您将无法退出团队</li>
        <li>退出后将无法访问团队资源</li>
      </ol>
      <p>确定要退出该团队吗？</p>`,
      "退出团队",
      {
        confirmButtonText: "确定退出",
        cancelButtonText: "取消",
        type: "warning",
        dangerouslyUseHTMLString: true,
        closeOnClickModal: false,
      }
    );
  } catch (error) {
    return;
  }

  try {
    const result = await CompanyApi.leaveCompany({ id });
    if (Result.isSuccess(result)) {
      ElMessage.success(result.data || "退出成功");
      await loadList();
    }
  } catch (error: any) {
    ElMessage.error(error.message);
  }
};

// 初始加载
loadList();

// 模态框内容
const modalVisible = ref(false);
const modalFormRef = ref<FormInstance>();
const modalLoading = ref(false);
const modalMode = ref<"add" | "edit">("add");
const modalForm = reactive<GetCompanyDetailsVo>({
  id: null,
  name: "",
  description: "",
});

const modalRules = {
  name: [
    { required: true, message: "请输入团队名称", trigger: "blur" },
    { min: 2, max: 50, message: "团队名称长度必须在2-50个字符之间", trigger: "blur" },
  ],
  description: [{ max: 200, message: "团队描述长度不能超过200个字符", trigger: "blur" }],
};

const openModal = async (mode: "add" | "edit", currentRow?: GetCurrentUserCompanyListVo) => {
  modalMode.value = mode;
  resetModal();

  if (mode === "edit" && currentRow) {
    try {
      const ret = await CompanyApi.getCompanyDetails({ id: currentRow.id });
      if (Result.isSuccess(ret)) {
        modalForm.id = ret.data.id;
        modalForm.name = ret.data.name;
        modalForm.description = ret.data.description;
      }
      if (Result.isError(ret)) {
        ElMessage.error(ret.message);
        return;
      }
    } catch (error: any) {
      ElMessage.error(error.message);
      return;
    }
  }

  modalVisible.value = true;
};

const resetModal = () => {
  modalForm.id = "";
  modalForm.name = "";
  modalForm.description = "";
};

const submitModal = async () => {
  // 先校验表单
  try {
    await modalFormRef.value?.validate();
  } catch (error) {
    return;
  }

  modalLoading.value = true;

  try {
    if (modalMode.value === "add") {
      await CompanyApi.addCompany(modalForm as AddCompanyDto);
      ElMessage.success("创建成功");
    }

    if (modalMode.value === "edit") {
      await CompanyApi.editCompany(modalForm as EditCompanyDto);
      ElMessage.success("修改成功");
    }

    modalVisible.value = false;
    await loadList();
  } catch (error: any) {
    ElMessage.error(error.message);
  } finally {
    modalLoading.value = false;
  }
};
</script>

<style scoped>
.list-container {
  padding: 20px;
  max-width: 100%;
  overflow-x: auto;
  width: 100%;
}

.list-table {
  margin-bottom: 20px;
  width: 100%;
  overflow-x: auto;
}

.action-buttons {
  margin-bottom: 15px;
  border-top: 2px dashed var(--el-border-color);
  padding-top: 15px;
}

.pagination-container {
  display: flex;
  justify-content: flex-end;
  margin-top: 20px;
  width: 100%;
}

:deep(.modal-centered) {
  margin: 0 auto;
  top: 50%;
  transform: translateY(-50%);
}
</style>
