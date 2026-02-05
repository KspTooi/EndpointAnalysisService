<template>
  <div class="list-container">
    <div class="right-content">
      <!-- 查询条件区域 -->
      <div class="query-form">
        <el-form :model="listForm" inline class="flex justify-between">
          <div>
            <el-form-item label="用户名">
              <el-input v-model="listForm.username" placeholder="输入用户名" clearable />
            </el-form-item>
            <el-form-item label="状态">
              <el-select v-model="listForm.status" placeholder="选择状态" clearable style="width: 180px">
                <el-option label="成功" value="0" />
                <el-option label="失败" value="1" />
              </el-select>
            </el-form-item>
          </div>
          <el-form-item>
            <el-button type="primary" @click="loadList" :disabled="listLoading">查询</el-button>
            <el-button @click="resetList" :disabled="listLoading">重置</el-button>
          </el-form-item>
        </el-form>
      </div>

      <!-- 列表表格区域 -->
      <div class="list-table">
        <el-table :data="listData" stripe v-loading="listLoading" border>
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
              <el-tag :type="scope.row.status === '0' ? 'success' : 'danger'" size="small">
                {{ scope.row.status === "0" ? "成功" : "失败" }}
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

        <div class="pagination-container">
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
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { markRaw } from "vue";
import { Delete } from "@element-plus/icons-vue";
import AuditLoginRcdService from "@/views/audit/service/AuditLoginRcdService.ts";

const DeleteIcon = markRaw(Delete);

const { listForm, listData, listTotal, listLoading, loadList, resetList, removeList } =
  AuditLoginRcdService.useAuditLoginList();
</script>

<style scoped>
.list-container {
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
  background-color: var(--el-bg-color);
}

.right-content {
  height: 100%;
  display: flex;
  flex-direction: column;
  padding: 15px;
  box-sizing: border-box;
}

.query-form {
  margin-bottom: 10px;
  background-color: var(--el-fill-color-blank);
  border-bottom: 1px dashed var(--el-border-color-light);
}

.list-table {
  flex: 1;
  width: 100%;
  box-sizing: border-box;
  overflow-y: auto;
}

.pagination-container {
  display: flex;
  justify-content: flex-end;
  margin-top: 15px;
  padding-bottom: 15px;
}
</style>
