<template>
  <el-dialog
    v-model="visible"
    title="公司成员列表"
    width="800px"
    :close-on-click-modal="false"
    @close="onClose"
    class="modal-centered"
  >
    <!-- 查询表单 -->
    <div class="query-form">
      <el-form :model="queryForm">
        <el-row :gutter="20">
          <el-col :span="8">
            <el-form-item label="用户名称">
              <el-input v-model="queryForm.username" placeholder="请输入用户名称" clearable @clear="loadList" />
            </el-form-item>
          </el-col>
          <el-col v-if="props.role === undefined" :span="8">
            <el-form-item label="职务">
              <el-select v-model="queryForm.role" placeholder="请选择职务" clearable @clear="loadList">
                <el-option label="CEO" :value="0" />
                <el-option label="成员" :value="1" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="props.role !== undefined ? 16 : 8">
            <el-form-item>
              <el-button type="primary" @click="loadList" :disabled="listLoading">查询</el-button>
              <el-button @click="resetQuery" :disabled="listLoading">重置</el-button>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
    </div>

    <!-- 成员列表 -->
    <div class="member-table">
      <el-table
        :data="listData"
        v-loading="listLoading"
        border
        row-key="id"
        @row-click="onRowClick"
        :row-class-name="allowSelect ? 'selectable-row' : ''"
        highlight-current-row
      >
        <el-table-column label="用户名称" prop="username" show-overflow-tooltip />
        <el-table-column label="职务" prop="role" width="100" align="center">
          <template #default="scope">
            <el-tag v-if="scope.row.role === 0" type="danger">CEO</el-tag>
            <el-tag v-else type="info">成员</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="加入时间" prop="joinedTime" width="180" />
        <el-table-column v-if="allowSelect" label="操作" width="100" align="center" fixed="right">
          <template #default="scope">
            <el-button type="primary" size="small" @click.stop="onSelect(scope.row)">选择</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <!-- 分页 -->
    <div class="pagination-container">
      <el-pagination
        v-model:current-page="queryForm.pageNum"
        v-model:page-size="queryForm.pageSize"
        :page-sizes="[10, 20, 50, 100]"
        layout="total, sizes, prev, pager, next, jumper"
        :total="listTotal"
        @size-change="loadList"
        @current-change="loadList"
      />
    </div>

    <template #footer>
      <div class="dialog-footer">
        <el-button @click="onClose">关闭</el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import type { GetCompanyMemberListDto, GetCompanyMemberListVo } from "@/views/core/api/CompanyMemberApi.ts";
import CompanyMemberApi from "@/views/core/api/CompanyMemberApi.ts";
import { Result } from "@/commons/entity/Result.ts";
import { ElMessage } from "element-plus";
import { reactive, ref, watch } from "vue";

interface Props {
  modelValue: boolean;
  companyId: string | null;
  allowSelect?: boolean;
  role?: number | null; // 固定查询的职务 0:CEO 1:成员
}

interface Emits {
  (e: "update:modelValue", value: boolean): void;
  (e: "onMemberSelected", member: GetCompanyMemberListVo): void;
}

const props = withDefaults(defineProps<Props>(), {
  allowSelect: false,
});

const emit = defineEmits<Emits>();

const visible = ref(false);
const listData = ref<GetCompanyMemberListVo[]>([]);
const listTotal = ref(0);
const listLoading = ref(false);

const queryForm = reactive<GetCompanyMemberListDto>({
  companyId: null,
  username: null,
  role: null,
  pageNum: 1,
  pageSize: 20,
});

watch(
  () => props.modelValue,
  (newVal) => {
    visible.value = newVal;
    if (newVal && props.companyId) {
      queryForm.companyId = props.companyId;
      if (props.role !== undefined) {
        queryForm.role = props.role;
      }
      loadList();
    }
  }
);

watch(
  () => props.companyId,
  (newVal) => {
    if (newVal) {
      queryForm.companyId = newVal;
      if (visible.value) {
        loadList();
      }
    }
  }
);

watch(
  () => props.role,
  (newVal) => {
    if (newVal !== undefined) {
      queryForm.role = newVal;
      if (visible.value && queryForm.companyId) {
        loadList();
      }
    }
  },
  { immediate: true }
);

watch(visible, (newVal) => {
  emit("update:modelValue", newVal);
});

const loadList = async () => {
  if (!queryForm.companyId) {
    return;
  }

  listLoading.value = true;

  try {
    const result = await CompanyMemberApi.getCompanyMemberList(queryForm);

    if (Result.isSuccess(result)) {
      listData.value = result.data;
      listTotal.value = result.total;
    }

    if (Result.isError(result)) {
      ElMessage.error(result.message);
    }
  } catch (error: any) {
    ElMessage.error(error.message || "加载失败");
  } finally {
    listLoading.value = false;
  }
};

const resetQuery = () => {
  queryForm.pageNum = 1;
  queryForm.pageSize = 20;
  queryForm.username = null;
  if (props.role === undefined) {
    queryForm.role = null;
  } else {
    queryForm.role = props.role;
  }
  loadList();
};

const onRowClick = (row: GetCompanyMemberListVo) => {
  if (props.allowSelect) {
    onSelect(row);
  }
};

const selecting = ref(false);

const onSelect = (member: GetCompanyMemberListVo) => {
  if (!props.allowSelect) {
    return;
  }
  if (selecting.value) {
    return;
  }
  selecting.value = true;
  emit("onMemberSelected", member);
  onClose();
};

const onClose = () => {
  visible.value = false;
  selecting.value = false;
  resetQuery();
};
</script>

<style scoped>
.query-form {
  margin-bottom: 20px;
}

.member-table {
  margin-bottom: 20px;
  min-height: 300px;
}

.pagination-container {
  display: flex;
  justify-content: flex-end;
  margin-top: 20px;
}

:deep(.selectable-row) {
  cursor: pointer;
}

:deep(.selectable-row:hover) {
  background-color: var(--el-table-row-hover-bg-color);
}

:deep(.modal-centered) {
  margin: 0 auto;
  top: 50%;
  transform: translateY(-50%);
}
</style>
