<template>
  <StdListLayout>
    <template #query>
      <el-form :model="listForm">
        <el-row>
          <el-col :span="5" :offset="1">
            <el-form-item label="标题">
              <el-input v-model="listForm.title" placeholder="请输入标题" clearable />
            </el-form-item>
          </el-col>
          <el-col :span="5" :offset="1">
            <el-form-item label="种类">
              <el-select v-model="listForm.kind" placeholder="请选择种类" clearable style="width: 100%">
                <el-option label="公告" :value="0" />
                <el-option label="业务提醒" :value="1" />
                <el-option label="私信" :value="2" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="5" :offset="1">
            <el-form-item label="消息内容">
              <el-input v-model="listForm.content" placeholder="请输入消息内容" clearable />
            </el-form-item>
          </el-col>
          <el-col :span="5" :offset="1">
            <el-form-item>
              <el-button type="primary" @click="loadList()" :disabled="listLoading">查询</el-button>
              <el-button @click="onReset" :disabled="listLoading">重置</el-button>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
    </template>

    <template #actions> </template>

    <template #table>
      <el-table :data="listData" stripe v-loading="listLoading" border height="100%">
        <el-table-column prop="title" label="标题" min-width="200" show-overflow-tooltip />
        <el-table-column prop="kind" label="种类" width="100">
          <template #default="scope">
            <el-tag v-if="scope.row.kind === 0" type="primary">公告</el-tag>
            <el-tag v-if="scope.row.kind === 1" type="warning">业务提醒</el-tag>
            <el-tag v-if="scope.row.kind === 2" type="success">私信</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="priority" label="优先级" width="80">
          <template #default="scope">
            <el-tag v-if="scope.row.priority === 0" type="info">低</el-tag>
            <el-tag v-if="scope.row.priority === 1" type="warning">中</el-tag>
            <el-tag v-if="scope.row.priority === 2" type="danger">高</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="category" label="业务类型" width="120" show-overflow-tooltip>
          <template #default="scope">
            <span v-if="scope.row.category">{{ scope.row.category }}</span>
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column prop="senderName" label="发送人" width="120" show-overflow-tooltip>
          <template #default="scope">
            <span>{{ scope.row.senderName || "系统" }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="接收时间" width="180" />
        <el-table-column label="操作" fixed="right" width="150" align="center">
          <template #default="scope">
            <el-button link type="primary" size="small" @click="openModal(scope.row.id)" :icon="ViewIcon"> 查看 </el-button>
            <el-button link type="danger" size="small" @click="remove(scope.row.id)" :icon="DeleteIcon"> 删除 </el-button>
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

    <template #modal>
      <!-- 消息详情模态框 -->
      <el-dialog v-model="modalVisible" title="消息详情" width="600px" @close="closeModal" :close-on-click-modal="false">
        <div v-loading="modalLoading" class="min-h-[200px] select-text">
          <template v-if="detailsData">
            <!-- 标题和标签 -->
            <div class="flex items-center gap-3 mb-5 pb-4 border-b border-gray-200">
              <h3 class="flex-1 m-0 text-lg font-bold text-gray-900">{{ detailsData.title }}</h3>
              <el-tag v-if="detailsData.kind === 0" type="primary">公告</el-tag>
              <el-tag v-if="detailsData.kind === 1" type="warning">业务提醒</el-tag>
              <el-tag v-if="detailsData.kind === 2" type="success">私信</el-tag>
            </div>

            <!-- 元信息 -->
            <div class="flex flex-col gap-2 mb-5">
              <div class="flex text-sm">
                <span class="text-gray-500 min-w-[80px]">发送人：</span>
                <span class="text-gray-900">{{ detailsData.senderName || "系统" }}</span>
              </div>
              <div class="flex text-sm">
                <span class="text-gray-500 min-w-[80px]">发送时间：</span>
                <span class="text-gray-900">{{ detailsData.createTime }}</span>
              </div>
              <div v-if="detailsData.category" class="flex text-sm">
                <span class="text-gray-500 min-w-[80px]">分类：</span>
                <span class="text-gray-900">{{ detailsData.category }}</span>
              </div>
              <div class="flex text-sm">
                <span class="text-gray-500 min-w-[80px]">优先级：</span>
                <el-tag v-if="detailsData.priority === 0" type="info" size="small">低</el-tag>
                <el-tag v-if="detailsData.priority === 1" type="warning" size="small">中</el-tag>
                <el-tag v-if="detailsData.priority === 2" type="danger" size="small">高</el-tag>
              </div>
            </div>

            <!-- 通知内容 -->
            <div>
              <div class="text-sm font-bold text-gray-900 mb-3">消息内容：</div>
              <div
                class="text-sm leading-relaxed text-gray-700 whitespace-pre-wrap break-words"
                v-html="detailsData.content"
              ></div>
            </div>
          </template>
        </div>

        <template #footer>
          <el-button @click="closeModal">关闭</el-button>
          <el-button v-if="detailsData?.forward" type="primary" @click="onForward"> 前往查看 </el-button>
        </template>
      </el-dialog>
    </template>
  </StdListLayout>
</template>

<script setup lang="ts">
import { markRaw } from "vue";
import { View, Delete } from "@element-plus/icons-vue";
import NoticeRcdService from "@/views/core/service/NoticeRcdService.ts";
import StdListLayout from "@/soa/std-series/StdListLayout.vue";
import { ElMessage } from "element-plus";

// 使用markRaw包装图标组件，防止被Vue响应式系统处理
const ViewIcon = markRaw(View);
const DeleteIcon = markRaw(Delete);

// 列表管理打包
const { listForm, listData, listTotal, listLoading, loadList } = NoticeRcdService.useNoticeRcdList();

// 模态框打包
const { modalVisible, modalLoading, detailsData, openModal, closeModal } = NoticeRcdService.useNoticeRcdModal();

// CRUD打包
const { remove } = NoticeRcdService.useNoticeRcdCrud({
  onRemoved: () => {
    // 删除后重新加载当前页
    loadList();
  },
});

/**
 * 重置查询表单
 */
const onReset = () => {
  listForm.value.title = undefined;
  listForm.value.kind = undefined;
  listForm.value.content = undefined;
  loadList();
};

/**
 * 跳转到关联页面
 */
const onForward = () => {
  if (!detailsData.value?.forward) {
    return;
  }
  // router.push(detailsData.value.forward);
  closeModal();
  ElMessage.info("功能开发中");
};
</script>

<style scoped></style>
