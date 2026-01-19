<template>
  <div class="no-active-company" v-if="noActiveCompany" style="width: 100%">
    <el-empty description="暂无激活的团队">
      <template #description>
        <div class="empty-description">
          <h3>您还没有激活或创建团队</h3>
          <p>请先激活或创建一个公司，然后才能管理团队成员</p>
        </div>
      </template>
      <el-button type="primary" @click="goToCompanySetup">前往设置</el-button>
    </el-empty>
  </div>

  <div class="list-container" v-if="!noActiveCompany">
    <!-- 公司信息展示 -->
    <div v-if="companyName" class="company-info">
      <span>当前管理的团队：{{ companyName }}</span>
    </div>

    <!-- 查询表单 -->
    <div class="query-form">
      <el-form :model="listForm">
        <el-row>
          <el-col :span="5" :offset="1">
            <el-form-item label="用户名称">
              <el-input v-model="listForm.username" placeholder="请输入用户名称" clearable />
            </el-form-item>
          </el-col>
          <el-col :span="5" :offset="1">
            <el-form-item label="职务">
              <el-select v-model="listForm.role" placeholder="请选择职务" clearable>
                <el-option label="CEO" :value="0" />
                <el-option label="成员" :value="1" />
              </el-select>
            </el-form-item>
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
      <el-button type="success" @click="openInviteModal">邀请加入团队</el-button>
    </div>

    <!-- 列表 -->
    <div class="list-table">
      <el-table :data="listData" v-loading="listLoading" border row-key="id">
        <el-table-column label="用户名称" prop="username" show-overflow-tooltip />
        <el-table-column label="职务" prop="role" width="100" align="center">
          <template #default="scope">
            <el-tag v-if="scope.row.role === 0" type="danger">CEO</el-tag>
            <el-tag v-else type="info">成员</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="加入时间" prop="joinedTime" width="180" />
        <el-table-column label="操作" width="100" align="center" fixed="right">
          <template #default="scope">
            <el-button
              v-if="scope.row.role !== 0"
              link
              type="danger"
              size="small"
              @click="handleFireMember(scope.row)"
              :icon="DeleteIcon"
            >
              开除
            </el-button>
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

    <!-- 用户选择模态框 -->
    <UserModal v-model="userModalVisible" :allow-select="true" @on-user-selected="handleUserSelected" />
  </div>
</template>

<script setup lang="ts">
import type {
  GetCurrentUserActiveCompanyMemberListDto,
  GetCompanyMemberListVo,
  AddCompanyMemberDto,
  FireCompanyMemberDto,
} from "@/views/core/api/CompanyMemberApi.ts";
import CompanyMemberApi from "@/views/core/api/CompanyMemberApi.ts";
import { Result } from "@/commons/entity/Result";
import { ElMessage, ElMessageBox } from "element-plus";
import { reactive, ref, onMounted } from "vue";
import { useRouter } from "vue-router";
import { Delete as DeleteIcon } from "@element-plus/icons-vue";
import UserModal from "./components/UserModal.vue";
import type { GetUserListVo } from "@/views/core/api/UserApi.ts";

const listForm = reactive<GetCurrentUserActiveCompanyMemberListDto>({
  username: null,
  role: null,
  pageNum: 1,
  pageSize: 10,
});

const listData = ref<GetCompanyMemberListVo[]>([]);
const listTotal = ref(0);
const listLoading = ref(false);
const companyName = ref<string | null>(null);
const companyId = ref<string | null>(null);
const userModalVisible = ref(false);
const router = useRouter();

const noActiveCompany = ref(false); // 没有激活的公司

/**
 * 加载列表
 */
const loadList = async () => {
  listLoading.value = true;

  try {
    const result = await CompanyMemberApi.getCurrentUserActiveCompanyMemberList(listForm);

    if (Result.isSuccess(result)) {
      companyName.value = result.data.companyName || null;
      companyId.value = result.data.companyId || null;
      if (result.data.members) {
        listData.value = result.data.members.data || [];
        listTotal.value = result.data.members.total || 0;
      }
    }

    if (Result.isError(result)) {
      //ElMessage.error(result.message);
      noActiveCompany.value = true;
    }
  } catch (error: any) {
    //ElMessage.error(error.message || "加载失败");
    noActiveCompany.value = true;
  } finally {
    listLoading.value = false;
  }
};

const resetList = () => {
  listForm.pageNum = 1;
  listForm.pageSize = 10;
  listForm.username = null;
  listForm.role = null;
  loadList();
};

const openInviteModal = () => {
  if (!companyId.value) {
    ElMessage.error("公司ID不能为空");
    return;
  }
  userModalVisible.value = true;
};

const handleUserSelected = async (user: GetUserListVo) => {
  if (!companyId.value || !user.id) {
    ElMessage.error("缺少必要参数");
    return;
  }

  try {
    const dto: AddCompanyMemberDto = {
      companyId: companyId.value,
      userId: user.id,
      role: 1, // 默认添加为成员
    };
    const result = await CompanyMemberApi.addCompanyMember(dto);
    if (Result.isSuccess(result)) {
      ElMessage.success("邀请成功");
      await loadList();
    }
    if (Result.isError(result)) {
      ElMessage.error(result.message);
    }
  } catch (error: any) {
    ElMessage.error(error.message || "邀请失败");
  } finally {
    userModalVisible.value = false;
  }
};

const handleFireMember = async (member: GetCompanyMemberListVo) => {
  if (member.role === 0) {
    await ElMessageBox.confirm(
      `CEO作为公司的最高管理者，无法被开除。<br/><br/>如需调整职务，请先进行CEO权限职务交接。`,
      "不符合常理",
      {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning",
        dangerouslyUseHTMLString: true,
      }
    );
    return;
  }

  try {
    await ElMessageBox.confirm(`确定要开除成员 <strong>${member.username}</strong> 吗？`, "确认开除", {
      confirmButtonText: "确定",
      cancelButtonText: "取消",
      type: "warning",
      dangerouslyUseHTMLString: true,
    });

    const dto: FireCompanyMemberDto = {
      companyId: companyId.value,
      userId: member.userId,
    };

    const result = await CompanyMemberApi.fireCompanyMember(dto);
    if (Result.isSuccess(result)) {
      ElMessage.success("开除成功");
      await loadList();
    }
    if (Result.isError(result)) {
      ElMessage.error(result.message);
    }
  } catch (error: any) {
    if (error !== "cancel") {
      ElMessage.error(error.message || "开除失败");
    }
  }
};

const goToCompanySetup = () => {
  router.push({ name: "company-manager" });
};

onMounted(() => {
  loadList();
});
</script>

<style scoped>
.list-container {
  padding: 20px;
  max-width: 100%;
  overflow-x: auto;
  width: 100%;
}

.company-info {
  margin-bottom: 10px;
  font-size: 16px;
  color: #057483;
  font-weight: bold;
}

.company-info span {
  color: #057483;
  font-weight: bold;
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

.action-buttons {
  margin-bottom: 15px;
  border-top: 2px dashed var(--el-border-color);
  padding-top: 15px;
}

.no-active-company {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 400px;
  padding: 20px;
}

.empty-description {
  text-align: center;
  margin-bottom: 20px;
}

.empty-description h3 {
  color: #303133;
  font-size: 18px;
  margin-bottom: 10px;
}

.empty-description p {
  color: #909399;
  font-size: 14px;
}
</style>
