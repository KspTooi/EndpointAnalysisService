<template>
  <StdListLayout>
    <template #query>
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
              <el-button type="primary" :disabled="listLoading" @click="loadList">查询</el-button>
              <el-button :disabled="listLoading" @click="resetList">重置</el-button>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
    </template>

    <template #actions>
      <el-button type="success" @click="openModal('add')">创建新团队</el-button>
    </template>

    <template #table>
      <el-table v-loading="listLoading" :data="listData" border row-key="id" height="100%">
        <el-table-column type="index" label="序号" width="60" show-overflow-tooltip align="center" />
        <el-table-column label="团队名称" prop="name" show-overflow-tooltip />
        <el-table-column label="团队描述" prop="description" show-overflow-tooltip />
        <el-table-column label="创始人" prop="founderName" width="120" />
        <el-table-column label="现任CEO" prop="ceoName" width="120" />
        <el-table-column label="成员数量" prop="memberCount" width="100" align="center" />
        <el-table-column label="创建时间" prop="createTime" width="180" />
        <el-table-column label="更新时间" prop="updateTime" width="180" />
        <el-table-column label="操作" fixed="right" width="320">
          <template #default="scope">
            <div style="display: inline-flex; justify-content: flex-end; align-items: center; gap: 2px; width: 100%">
              <el-button
                v-if="scope.row.isActive !== 1"
                link
                type="success"
                size="small"
                :icon="Refresh"
                @click="activateCompany(scope.row.id)"
              >
                激活
              </el-button>
              <el-button link type="primary" size="small" :icon="ViewIcon" @click="openModal('edit', scope.row)">
                编辑
              </el-button>
              <el-button
                v-if="scope.row.isCeo === 1"
                link
                type="danger"
                size="small"
                :icon="SwitchButton"
                @click="openResignCeoModal(scope.row)"
              >
                辞去CEO职位
              </el-button>
              <el-button link type="danger" size="small" :icon="SwitchButton" @click="leaveCompany(scope.row.id)">
                退出
              </el-button>
              <!-- <el-button link type="danger" size="small" @click="removeCompany(scope.row.id)" :icon="DeleteIcon">删除</el-button> -->
            </div>
          </template>
        </el-table-column>
      </el-table>
    </template>

    <template #pagination>
      <el-pagination
        v-model:current-page="listForm.pageNum"
        v-model:page-size="listForm.pageSize"
        :page-sizes="[10, 20, 50, 100]"
        layout="total, sizes, prev, pager, next, jumper"
        :total="listTotal"
        background
        @size-change="
          (val: number) => {
            listForm.pageSize = val;
            loadList();
          }
        "
        @current-change="
          (val: number) => {
            listForm.pageNum = val;
            loadList();
          }
        "
      />
    </template>
  </StdListLayout>

  <!-- 团队编辑模态框 -->
  <el-dialog
    v-model="modalVisible"
    :title="modalMode === 'edit' ? '编辑团队' : '添加团队'"
    width="550px"
    :close-on-click-modal="false"
    @close="resetModal()"
  >
    <el-form
      v-if="modalVisible"
      ref="modalFormRef"
      :model="modalForm"
      :rules="modalRules"
      label-width="80px"
      :validate-on-rule-change="false"
    >
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
        <el-button type="primary" :loading="modalLoading" @click="submitModal">
          {{ modalMode === "add" ? "创建" : "保存" }}
        </el-button>
      </div>
    </template>
  </el-dialog>

  <!-- 成员选择模态框 -->
  <CompanyMemberModal
    v-model="memberModalVisible"
    :company-id="currentResignCompanyId"
    :allow-select="true"
    :role="1"
    @on-member-selected="onMemberSelected"
  />
</template>

<script setup lang="ts">
import type {
  GetCurrentUserCompanyListDto,
  GetCurrentUserCompanyListVo,
  AddCompanyDto,
  EditCompanyDto,
  GetCompanyDetailsVo,
  ResignCeoDto,
} from "@/views/core/api/CompanyApi.ts";
import CompanyApi from "@/views/core/api/CompanyApi.ts";
import { Result } from "@/commons/model/Result.ts";
import { ElMessage, ElMessageBox, type FormInstance } from "element-plus";
import { reactive, ref } from "vue";
import { Delete as DeleteIcon, View as ViewIcon, SwitchButton, Refresh } from "@element-plus/icons-vue";
import CompanyMemberModal from "@/views/core/components/CompanyMemberModal.vue";
import type { GetCompanyMemberListVo } from "@/views/core/api/CompanyMemberApi.ts";
import StdListLayout from "@/soa/std-series/StdListLayout.vue";

const listForm = reactive<GetCurrentUserCompanyListDto>({
  name: "",
  pageNum: 1,
  pageSize: 20,
});

const listData = ref<GetCurrentUserCompanyListVo[]>([]);
const listTotal = ref(0);
const listLoading = ref(false);

const loadList = async (): Promise<void> => {
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

const resetList = (): void => {
  listForm.pageNum = 1;
  listForm.pageSize = 20;
  listForm.name = "";
  loadList();
};

const activateCompany = async (id: string): Promise<void> => {
  try {
    await ElMessageBox.confirm("确定激活该团队吗？", "提示", {
      confirmButtonText: "确定",
      cancelButtonText: "取消",
      type: "warning",
    });
  } catch {
    return;
  }

  try {
    const result = await CompanyApi.activateCompany({ id });
    if (Result.isSuccess(result)) {
      ElMessage.success(result.data || "激活成功");
      await loadList();
    }
  } catch (error: any) {
    ElMessage.error(error.message);
  }
};

const leaveCompany = async (id: string): Promise<void> => {
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
  } catch {
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

const openModal = async (mode: "add" | "edit", currentRow?: GetCurrentUserCompanyListVo): Promise<void> => {
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

const resetModal = (): void => {
  modalForm.id = "";
  modalForm.name = "";
  modalForm.description = "";
};

const submitModal = async (): Promise<void> => {
  // 先校验表单
  try {
    await modalFormRef.value?.validate();
  } catch {
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

// 辞去CEO职位相关
const memberModalVisible = ref(false);
const currentResignCompanyId = ref<string | null>(null);

const openResignCeoModal = async (row: GetCurrentUserCompanyListVo): Promise<void> => {
  if (!row.id) {
    ElMessage.error("公司ID不能为空");
    return;
  }

  if (row.memberCount !== undefined && row.memberCount !== null && row.memberCount <= 1) {
    try {
      await ElMessageBox.alert(
        `<div style="padding: 10px 0;">
          <p style="font-size: 16px; margin-bottom: 15px; line-height: 1.5;">当前团队仅您一人，无法进行CEO职位移交。</p>
          <p style="font-size: 14px; color: #909399; margin-bottom: 10px; line-height: 1.5;">毕竟总不能把职位移交给空气吧？😊</p>
          <p style="font-size: 14px; color: #909399; line-height: 1.5;">如果您想关闭公司，请使用"退出"功能，退出后公司将被注销。</p>
        </div>`,
        "⚠️ 无法辞去CEO职位",
        {
          confirmButtonText: "我知道了",
          type: "warning",
          dangerouslyUseHTMLString: true,
        }
      );
    } catch {
      // 用户点击确认，不需要处理
    }
    return;
  }

  currentResignCompanyId.value = row.id;
  memberModalVisible.value = true;
};

const onMemberSelected = async (member: GetCompanyMemberListVo): Promise<void> => {
  if (!currentResignCompanyId.value || !member.userId) {
    ElMessage.error("缺少必要参数");
    return;
  }

  try {
    await ElMessageBox.confirm(`确定将CEO职位移交给 <strong>${member.username}</strong> 吗？`, "确认辞职", {
      confirmButtonText: "确定",
      cancelButtonText: "取消",
      type: "warning",
      dangerouslyUseHTMLString: true,
    });
  } catch {
    return;
  }

  try {
    const dto: ResignCeoDto = {
      companyId: currentResignCompanyId.value,
      newCeoUserId: member.userId,
    };
    const result = await CompanyApi.resignCEO(dto);
    if (Result.isSuccess(result)) {
      ElMessage.success(result.data || "您已经辞去CEO职位");
      await loadList();
    }
    if (Result.isError(result)) {
      ElMessage.error(result.message);
    }
  } catch (error: any) {
    ElMessage.error(error.message || "操作失败");
  } finally {
    memberModalVisible.value = false;
    currentResignCompanyId.value = null;
  }
};
</script>

<style scoped></style>
