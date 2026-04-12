<template>
  <StdListContainer>
    <!-- 查询条件区域 -->
    <StdListAreaQuery>
      <el-form :model="listForm" inline class="flex justify-between">
        <div>
          <el-form-item label="名称">
            <el-input v-model="listForm.name" placeholder="输入名称" clearable />
          </el-form-item>
          <el-form-item label="标签(CTJ)">
            <el-input v-model="listForm.tags" placeholder="输入标签(CTJ)" clearable />
          </el-form-item>
        </div>
        <el-form-item>
          <el-button type="primary" @click="loadList" :disabled="listLoading">查询</el-button>
          <el-button @click="resetList" :disabled="listLoading">重置</el-button>
        </el-form-item>
      </el-form>
    </StdListAreaQuery>

    <!-- 操作按钮区域 -->
    <StdListAreaAction class="flex gap-2">
      <el-button type="success" @click="openModal('add', null)">新增提示词</el-button>
    </StdListAreaAction>

    <!-- 列表表格区域 -->
    <StdListAreaTable>
      <el-table :data="listData" stripe v-loading="listLoading" border height="100%">
        <el-table-column type="index" label="序号" width="60" show-overflow-tooltip align="center" />
        <el-table-column prop="name" label="名称" min-width="120" show-overflow-tooltip />
        <el-table-column prop="charCount" label="字符数" min-width="90" show-overflow-tooltip />
        <el-table-column prop="tags" label="标签" min-width="120" show-overflow-tooltip>
          <template #default="scope">
            <el-tag v-for="tag in scope.row.tags" :key="tag.n" type="info" size="small" class="mr-1">
              {{ tag.n }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="paramCount" label="参数数量" min-width="120" show-overflow-tooltip />
        <el-table-column prop="version" label="版本号" min-width="120" show-overflow-tooltip>
          <template #default="scope">
            <span class="text-cyan-600">V{{ scope.row.version }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" min-width="120" show-overflow-tooltip />
        <el-table-column label="操作" fixed="right" min-width="240">
          <template #default="scope">
            <el-button link type="success" size="small" @click="openCompileModal(scope.row)" :icon="MagicIcon">
              编译
            </el-button>
            <el-button link type="primary" size="small" @click="openModal('edit', scope.row)" :icon="EditIcon">
              编辑
            </el-button>
            <el-button link type="danger" size="small" @click="removeList(scope.row)" :icon="DeleteIcon"> 删除 </el-button>
          </template>
        </el-table-column>
      </el-table>

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
    </StdListAreaTable>

    <!-- 编译模态框 -->
    <el-dialog v-model="compileVisible" title="编译提示词" width="640px" :close-on-click-modal="false" @close="resetCompile">
      <div v-if="compileLoading && compileSlots.length === 0" class="flex justify-center py-8">
        <el-icon class="is-loading text-2xl"><Loading /></el-icon>
      </div>
      <el-form v-else label-width="100px">
        <el-form-item v-for="slot in compileSlots" :key="slot" :label="slot">
          <el-input v-model="compileParams[slot]" :placeholder="`请输入 ${slot}`" clearable type="textarea" :rows="3" maxlength="10000" show-word-limit />
        </el-form-item>
        <el-form-item v-if="compileSlots.length === 0" label="无参数">
          <span class="text-gray-400">该提示词没有参数槽位</span>
        </el-form-item>
      </el-form>
      <div v-if="compileResult !== null">
        <el-divider>编译结果</el-divider>
        <div class="relative">
          <el-input v-model="compileResult" type="textarea" :rows="8" readonly />
          <el-button class="absolute top-2 right-2" size="small" :icon="CopyIcon" @click="copyResult">复制</el-button>
        </div>
      </div>
      <template #footer>
        <el-button @click="compileVisible = false">关闭</el-button>
        <el-button type="primary" :disabled="compileSlots.length === 0" @click="doCompile" :loading="compileLoading">编译</el-button>
      </template>
    </el-dialog>

    <!-- 新增/编辑模态框 -->
    <el-dialog
      v-model="modalVisible"
      :title="modalMode === 'edit' ? '编辑提示词' : '新增提示词'"
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
        <el-form-item label="名称" prop="name">
          <el-input
            v-model="modalForm.name"
            placeholder="请输入名称"
            clearable
            show-word-limit
            maxlength="80"
            :disabled="modalMode === 'edit'"
          />
        </el-form-item>
        <el-form-item label="标签" prop="tags">
          <StdCustomizeTagSelect
            v-model="modalForm.tags"
            :tags="[{ n: 'Prompt' }, { n: 'Skill' }, { n: 'Command' }]"
            allow-create
            filterable
            default-first-option
            placeholder="回车创建新标签"
            multiple-limit="6"
            :reserve-keyword="false"
          >
          </StdCustomizeTagSelect>
        </el-form-item>
        <el-form-item label="内容" prop="content">
          <el-input
            v-model="modalForm.content"
            placeholder="请输入内容"
            clearable
            type="textarea"
            :rows="10"
            show-word-limit
            maxlength="10000"
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
  </StdListContainer>
</template>

<script setup lang="ts">
import { ref, markRaw, reactive } from "vue";
import { Edit, Delete, MagicStick, CopyDocument, Loading } from "@element-plus/icons-vue";
import { ElMessage } from "element-plus";
import type { FormInstance } from "element-plus";
import PromptService from "@/views/document/service/PromptService.ts";
import PromptApi from "@/views/document/api/PromptApi.ts";
import type { GetPromptListVo } from "@/views/document/api/PromptApi.ts";
import StdListContainer from "@/soa/std-series/StdListContainer.vue";
import StdListAreaQuery from "@/soa/std-series/StdListAreaQuery.vue";
import StdListAreaAction from "@/soa/std-series/StdListAreaAction.vue";
import StdListAreaTable from "@/soa/std-series/StdListAreaTable.vue";
import StdCustomizeTagSelect from "@/soa/std-series/StdCustomizeTagSelect.vue";

// 使用markRaw包装图标组件，防止被Vue响应式系统处理
const EditIcon = markRaw(Edit);
const DeleteIcon = markRaw(Delete);
const MagicIcon = markRaw(MagicStick);
const CopyIcon = markRaw(CopyDocument);

// 列表管理打包
const { listForm, listData, listTotal, listLoading, loadList, resetList, removeList } = PromptService.usePromptList();

// 模态框表单引用
const modalFormRef = ref<FormInstance>();

// 模态框打包
const { modalVisible, modalLoading, modalMode, modalForm, modalRules, openModal, resetModal, submitModal } =
  PromptService.usePromptModal(modalFormRef, loadList);

// 编译模态框
const compileVisible = ref(false);
const compileLoading = ref(false);
const compileSlots = ref<string[]>([]);
const compileParams = reactive<Record<string, string>>({});
const compileResult = ref<string | null>(null);
const compileId = ref<string>("");

const resetCompile = (): void => {
  compileSlots.value = [];
  compileResult.value = null;
  compileId.value = "";
  Object.keys(compileParams).forEach((k) => delete compileParams[k]);
};

const openCompileModal = async (row: GetPromptListVo): Promise<void> => {
  resetCompile();
  compileVisible.value = true;
  compileLoading.value = true;
  try {
    const details = await PromptApi.getPromptDetails({ id: row.id });
    compileId.value = details.id;
    compileSlots.value = details.paramSlots ?? [];
    compileSlots.value.forEach((slot) => {
      compileParams[slot] = "";
    });
  } catch (e: any) {
    ElMessage.error(e.message ?? "获取详情失败");
    compileVisible.value = false;
  } finally {
    compileLoading.value = false;
  }
};

const doCompile = async (): Promise<void> => {
  compileLoading.value = true;
  try {
    const result = await PromptApi.compilePrompt({ id: compileId.value, params: { ...compileParams } });
    compileResult.value = result;
  } catch (e: any) {
    ElMessage.error(e.message ?? "编译失败");
  } finally {
    compileLoading.value = false;
  }
};

const copyResult = (): void => {
  if (compileResult.value === null) {
    return;
  }
  navigator.clipboard.writeText(compileResult.value).then(() => {
    ElMessage.success("已复制到剪贴板");
  });
};
</script>

<style scoped></style>
