<template>
  <StdListLayout>
    <template #query>
      <el-form :model="listForm" inline class="flex justify-between">
        <div>
          <el-form-item label="用户名">
            <el-input v-model="listForm.username" placeholder="输入用户名" clearable />
          </el-form-item>
          <el-form-item label="状态">
            <el-select v-model="listForm.status" placeholder="选择状态" clearable style="width: 180px">
              <el-option label="成功" :value="0" />
              <el-option label="失败" :value="1" />
            </el-select>
          </el-form-item>
        </div>
        <el-form-item>
          <el-button type="primary" @click="loadList" :disabled="listLoading">查询</el-button>
          <el-button @click="resetList" :disabled="listLoading">重置</el-button>
        </el-form-item>
      </el-form>
    </template>

    <template #actions>
      <el-button
        type="danger"
        @click="() => removeListBatch(listSelected)"
        :disabled="listSelected.length === 0"
        :loading="listLoading"
      >
        删除选中项
      </el-button>
    </template>

    <template #table>
      <el-table
        :data="listData"
        stripe
        v-loading="listLoading"
        border
        height="100%"
        @selection-change="(val: GetAuditLoginListVo[]) => (listSelected = val)"
      >
        <el-table-column type="selection" width="40" />
        <el-table-column prop="username" label="用户名" min-width="120" />
        <el-table-column label="登录方式" min-width="120">
          <template #default="scope">
            <span v-if="scope.row.loginKind === 0">用户名密码</span>
            <span v-else>未知</span>
          </template>
        </el-table-column>
        <el-table-column prop="ipAddr" label="IP地址" min-width="140" />
        <el-table-column prop="browser" label="浏览器" min-width="150" show-overflow-tooltip />
        <el-table-column prop="os" label="操作系统" min-width="120" />
        <el-table-column label="状态" min-width="100">
          <template #default="scope">
            <el-tag :type="scope.row.status === 0 ? 'success' : 'danger'" size="small">
              {{ scope.row.status === 0 ? "成功" : "失败" }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="message" label="提示消息" min-width="150" show-overflow-tooltip />
        <el-table-column prop="createTime" label="登录时间" min-width="180" />
        <el-table-column label="操作" fixed="right" min-width="100">
          <template #default="scope">
            <el-button link type="danger" size="small" @click="removeList(scope.row)" :icon="DeleteIcon"> 删除 </el-button>
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
        background
      />
    </template>
  </StdListLayout>
</template>

<script setup lang="ts">
import { markRaw, ref } from "vue";
import { Delete } from "@element-plus/icons-vue";
import type { GetAuditLoginListVo } from "@/views/audit/api/AuditLoginApi.ts";
import AuditLoginRcdService from "@/views/audit/service/AuditLoginRcdService.ts";
import StdListLayout from "@/soa/std-series/StdListLayout.vue";

const DeleteIcon = markRaw(Delete);

// 选中的列表项
const listSelected = ref<GetAuditLoginListVo[]>([]);

const { listForm, listData, listTotal, listLoading, loadList, resetList, removeList, removeListBatch } =
  AuditLoginRcdService.useAuditLoginList();
</script>

<style scoped></style>
