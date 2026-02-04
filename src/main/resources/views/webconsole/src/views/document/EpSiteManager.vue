<template>
  <div class="list-container">
    <!-- 查询表单 -->
    <div class="query-form">
      <div class="warning-alert">
        <el-alert
          title="警告: 不要在此录入任何敏感信息，仅限录入测试环境中所使用到的数据"
          type="warning"
          :closable="false"
          show-icon
        />
      </div>
      <el-form :model="listForm">
        <el-row>
          <el-col :span="5" :offset="1">
            <el-form-item label="站点名称">
              <el-input v-model="listForm.name" placeholder="请输入站点名称" clearable />
            </el-form-item>
          </el-col>
          <el-col :span="5" :offset="1">
            <el-form-item label="地址">
              <el-input v-model="listForm.address" placeholder="请输入地址" clearable />
            </el-form-item>
          </el-col>
          <el-col :span="5" :offset="1">
            <el-form-item label="账户">
              <el-input v-model="listForm.username" placeholder="请输入账户" clearable />
            </el-form-item>
          </el-col>
          <el-col :span="4" :offset="2">
            <el-form-item>
              <el-dropdown split-button type="primary" @click="loadList" :disabled="listLoading">
                查询
                <template #dropdown>
                  <el-dropdown-menu>
                    <el-dropdown-item :icon="DownloadIcon" @click="handleExport">导出查询结果</el-dropdown-item>
                  </el-dropdown-menu>
                </template>
              </el-dropdown>
              <el-button @click="resetList" :disabled="listLoading" style="margin-left: 12px">重置</el-button>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
    </div>

    <div class="action-buttons">
      <el-button type="success" @click="openModal('add', null)">新增站点</el-button>
      <el-button
        type="danger"
        @click="() => removeListBatch(listSelected)"
        :disabled="listSelected.length === 0"
        :loading="listLoading"
      >
        删除选中项
      </el-button>
      <el-button type="primary" @click="importWizardRef?.openModal()" :icon="UploadIcon">导入站点</el-button>
    </div>

    <!-- 站点列表 -->
    <div class="list-table">
      <el-table
        :data="listData"
        stripe
        v-loading="listLoading"
        border
        @selection-change="(val: GetEpSiteListVo[]) => (listSelected = val)"
      >
        <el-table-column type="selection" width="40" />
        <el-table-column label="站点名称" min-width="150" show-overflow-tooltip>
          <template #default="scope">
            <div v-if="isEmpty(scope.row.name)" class="empty-cell">
              <span>未设定</span>
            </div>
            <div v-else class="copyable-cell" @click="copyToClipboard(scope.row.name, '站点名称')">
              <el-icon class="copy-icon"><CopyIcon /></el-icon>
              <span>{{ scope.row.name }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="地址" min-width="200" show-overflow-tooltip>
          <template #default="scope">
            <div v-if="isEmpty(scope.row.address)" class="empty-cell">
              <span>未设定</span>
            </div>
            <div v-else class="copyable-cell" @click="copyToClipboard(scope.row.address, '地址')">
              <el-icon class="copy-icon"><CopyIcon /></el-icon>
              <span>{{ scope.row.address }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="账户" min-width="150" show-overflow-tooltip>
          <template #default="scope">
            <div v-if="isEmpty(scope.row.username)" class="empty-cell">
              <span>未设定</span>
            </div>
            <div v-else class="copyable-cell" @click="copyToClipboard(scope.row.username, '账户')">
              <el-icon class="copy-icon"><CopyIcon /></el-icon>
              <span>{{ scope.row.username }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="密码" min-width="150">
          <template #default="scope">
            <div v-if="isEmpty(scope.row.password)" class="empty-cell">
              <span>未设定</span>
            </div>
            <div v-else style="display: flex; align-items: center; gap: 8px">
              <el-button
                link
                type="primary"
                size="small"
                @click="togglePasswordVisibility(scope.row.id)"
                :icon="passwordVisibleMap[scope.row.id] ? HideIcon : ViewIcon"
              />
              <div
                class="copyable-cell"
                @click="copyToClipboard(scope.row.password, '密码')"
                v-if="passwordVisibleMap[scope.row.id]"
              >
                <el-icon class="copy-icon"><CopyIcon /></el-icon>
                <span>{{ scope.row.password }}</span>
              </div>
              <div class="copyable-cell" @click="copyToClipboard(scope.row.password, '密码')" v-else>
                <el-icon class="copy-icon"><CopyIcon /></el-icon>
                <span>••••••••</span>
              </div>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="remark" label="备注" min-width="200" show-overflow-tooltip />
        <el-table-column prop="seq" label="排序" width="80" />
        <el-table-column prop="createTime" label="创建时间" min-width="180" />
        <el-table-column label="操作" fixed="right" min-width="200">
          <template #default="scope">
            <el-button link type="primary" size="small" @click="copySiteInfo(scope.row)" :icon="CopyIcon"> 复制 </el-button>
            <el-button link type="primary" size="small" @click="openModal('edit', scope.row)" :icon="EditIcon">
              编辑
            </el-button>
            <el-button link type="danger" size="small" @click="removeList(scope.row.id)" :icon="DeleteIcon"> 删除 </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
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

    <!-- 站点编辑/新增模态框 -->
    <el-dialog
      v-model="modalVisible"
      :title="modalMode === 'edit' ? '编辑站点' : '新增站点'"
      width="600px"
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
        label-width="100px"
        :validate-on-rule-change="false"
      >
        <el-form-item label="站点名称" prop="name">
          <el-input v-model="modalForm.name" placeholder="请输入站点名称" maxlength="32" show-word-limit />
        </el-form-item>
        <el-form-item label="地址" prop="address">
          <el-input v-model="modalForm.address" placeholder="请输入地址" maxlength="255" show-word-limit />
        </el-form-item>
        <el-form-item label="账户" prop="username">
          <el-input v-model="modalForm.username" placeholder="请输入账户" maxlength="500" show-word-limit />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input
            v-model="modalForm.password"
            type="password"
            placeholder="请输入密码"
            maxlength="500"
            show-word-limit
            show-password
          />
        </el-form-item>
        <el-form-item label="排序" prop="seq">
          <el-input-number v-model="modalForm.seq" :min="0" placeholder="请输入排序" style="width: 100%" />
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input
            v-model="modalForm.remark"
            type="textarea"
            :rows="4"
            placeholder="请输入备注"
            maxlength="1000"
            show-word-limit
          />
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

    <!-- 导入向导 -->
    <ImportWizardModal
      ref="importWizardRef"
      url="/epSite/importEpSite"
      templateCode="ep_site"
      @on-success="loadList"
      @on-close="loadList"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, markRaw } from "vue";
import { Edit, Delete, View, Hide, CopyDocument, Download, Upload } from "@element-plus/icons-vue";
import type { FormInstance } from "element-plus";
import EpSiteService from "@/views/document/service/EpSiteService.ts";
import EpSiteApi, { type GetEpSiteListVo } from "@/views/document/api/EpSiteApi.ts";
import ImportWizardModal from "@/soa/console-framework/ImportWizardModal.vue";

// 使用markRaw包装图标组件
const EditIcon = markRaw(Edit);
const DeleteIcon = markRaw(Delete);
const ViewIcon = markRaw(View);
const HideIcon = markRaw(Hide);
const CopyIcon = markRaw(CopyDocument);
const DownloadIcon = markRaw(Download);
const UploadIcon = markRaw(Upload);

const importWizardRef = ref<InstanceType<typeof ImportWizardModal>>();

// 列表打包
const {
  listForm,
  listData,
  listTotal,
  listLoading,
  loadList,
  resetList,
  removeList,
  removeListBatch,
  passwordVisibleMap,
  togglePasswordVisibility,
} = EpSiteService.useEpSiteList();

// 复制到剪贴板
const copyToClipboard = EpSiteService.copyToClipboard;

// 导出站点
const handleExport = async () => {
  try {
    await EpSiteApi.exportEpSite(listForm.value);
  } catch (e: any) {
    console.error(e);
  }
};

// 判断值是否为空
const isEmpty = (value: string | null | undefined): boolean => {
  return !value || value.trim() === "";
};

// 复制站点信息
const copySiteInfo = (row: GetEpSiteListVo) => {
  const name = isEmpty(row.name) ? "未设定" : row.name;
  const address = isEmpty(row.address) ? "未设定" : row.address;
  const username = isEmpty(row.username) ? "未设定" : row.username;
  const password = isEmpty(row.password) ? "未设定" : row.password;

  const format = `${name}
地址:${address}
账号:${username}
密码:${password}`;
  copyToClipboard(format, "站点信息");
};

// 选中的列表项
const listSelected = ref<GetEpSiteListVo[]>([]);

// 模态框表单引用
const modalFormRef = ref<FormInstance>();

// 模态框打包
const { modalVisible, modalLoading, modalMode, modalForm, modalRules, openModal, resetModal, submitModal } =
  EpSiteService.useEpSiteModal(modalFormRef, loadList);
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

.copyable-cell {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  cursor: pointer;
  user-select: none;
  border-radius: 4px;
  transition: all 0.2s;
}

.copyable-cell:hover {
  color: var(--el-color-primary);
  background-color: var(--el-color-primary-light-9);
}

.copy-icon {
  font-size: 14px;
  opacity: 0.6;
  transition: opacity 0.2s;
}

.copyable-cell:hover .copy-icon {
  opacity: 1;
}

.empty-cell {
  color: var(--el-text-color-placeholder);
}

.warning-alert {
  margin-bottom: 15px;
}
</style>
