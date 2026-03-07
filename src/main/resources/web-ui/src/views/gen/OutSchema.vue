<template>
  <StdListContainer>
    <!-- 查询条件区域 -->
    <StdListAreaQuery>
      <el-form :model="listForm" inline class="flex justify-between">
        <div>
          <el-form-item label="输出方案名称">
            <el-input v-model="listForm.name" placeholder="输入输出方案名称" clearable />
          </el-form-item>
          <el-form-item label="模型名称">
            <el-input v-model="listForm.modelName" placeholder="输入模型名称" clearable />
          </el-form-item>
          <el-form-item label="数据源表名">
            <el-input v-model="listForm.tableName" placeholder="输入数据源表名" clearable />
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
      <el-button type="success" @click="openModal('add', null)">新增输出方案</el-button>
    </StdListAreaAction>

    <!-- 列表表格区域 -->
    <StdListAreaTable>
      <el-table :data="listData" stripe v-loading="listLoading" border height="100%">
        <el-table-column prop="name" label="输出方案名称" min-width="120" show-overflow-tooltip />
        <el-table-column prop="modelName" label="模型名称" min-width="180" show-overflow-tooltip />
        <el-table-column prop="tableName" label="数据源表名" min-width="120" show-overflow-tooltip />
        <el-table-column prop="fieldCountOrigin" label="字段数(原始)" min-width="110" show-overflow-tooltip />
        <el-table-column prop="fieldCountPoly" label="字段数(聚合)" min-width="110" show-overflow-tooltip />
        <el-table-column label="操作" fixed="right" min-width="180">
          <template #default="scope">
            <el-button link type="primary" size="small" @click="" :icon="ManagementIcon"> 模型管理 </el-button>
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

    <!-- 新增/编辑模态框 -->
    <el-dialog
      v-model="modalVisible"
      :title="modalMode === 'edit' ? '编辑输出方案' : '新增输出方案'"
      width="900px"
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
        label-width="120px"
        :validate-on-rule-change="false"
      >
        <el-divider content-position="left">方案基本配置</el-divider>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="方案名称" prop="name">
              <el-input v-model="modalForm.name" placeholder="请输入输出方案名称" clearable maxlength="32" show-word-limit />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="模型名称" prop="modelName">
              <el-input v-model="modalForm.modelName" placeholder="请输入模型名称" clearable maxlength="255" show-word-limit>
                <template #append>
                  <el-button @click="modalForm.modelName = modalForm.name">推断</el-button>
                </template>
              </el-input>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="类型映射方案" prop="typeSchemaId">
              <el-select
                v-model="modalForm.typeSchemaId"
                placeholder="请选择类型映射方案"
                filterable
                clearable
                style="width: 100%"
              >
                <el-option
                  v-for="item in modalTypeSchema"
                  :key="item.id"
                  :value="item.id"
                  :label="`${item.name}（${item.code}）`"
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="权限码前缀" prop="permCodePrefix">
              <el-input
                v-model="modalForm.permCodePrefix"
                placeholder="请输入权限码前缀"
                clearable
                maxlength="32"
                show-word-limit
              />
            </el-form-item>
          </el-col>
        </el-row>

        <el-divider content-position="left">输入配置</el-divider>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="输入数据源" prop="dataSourceId">
              <el-select
                v-model="modalForm.dataSourceId"
                placeholder="请选择输入数据源"
                filterable
                clearable
                style="width: 100%"
              >
                <el-option
                  v-for="item in modalDataSource"
                  :key="item.id"
                  :value="item.id"
                  :label="`${item.name}（${item.code}）`"
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="数据源表名" prop="tableName">
              <DataSourceTableBrowser v-model="modalForm.tableName" :data-source-id="modalForm.dataSourceId" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="从SCM读取" prop="inputScmId">
              <el-select v-model="modalForm.inputScmId" placeholder="请选择输入SCM" filterable clearable style="width: 100%">
                <el-option v-for="item in modalScm" :key="item.id" :value="item.id" :label="`${item.name}（${item.code}）`" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="SCM基准路径" prop="baseInput">
              <el-input
                v-model="modalForm.baseInput"
                placeholder="请输入SCM基准路径"
                clearable
                maxlength="320"
                show-word-limit
              />
            </el-form-item>
          </el-col>
        </el-row>

        <el-divider content-position="left">输出配置</el-divider>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="输出到SCM" prop="outputScmId">
              <el-select v-model="modalForm.outputScmId" placeholder="请选择输出SCM" filterable clearable style="width: 100%">
                <el-option v-for="item in modalScm" :key="item.id" :value="item.id" :label="`${item.name}（${item.code}）`" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="SCM基准路径" prop="baseOutput">
              <el-input
                v-model="modalForm.baseOutput"
                placeholder="请输入SCM基准路径"
                clearable
                maxlength="320"
                show-word-limit
              />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="写出策略" prop="policyOverride">
              <el-select v-model="modalForm.policyOverride" placeholder="请选择写出策略" style="width: 100%">
                <el-option :value="0" label="不覆盖" />
                <el-option :value="1" label="覆盖" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="移除表前缀" prop="removeTablePrefix">
              <el-input
                v-model="modalForm.removeTablePrefix"
                placeholder="请输入移除表前缀"
                clearable
                maxlength="80"
                show-word-limit
              >
                <template #append>
                  <el-button
                    @click="
                      () => {
                        const parts = modalForm.tableName.split('_');
                        modalForm.removeTablePrefix = parts.length > 1 ? parts[0] + '_' : '';
                      }
                    "
                    :disabled="!modalForm.tableName"
                  >推断</el-button>
                </template>
              </el-input>
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="方案备注" prop="remark">
          <el-input
            v-model="modalForm.remark"
            type="textarea"
            :rows="2"
            placeholder="请输入方案备注"
            clearable
            show-word-limit
            maxlength="1000"
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
import { ref, markRaw } from "vue";
import { Edit, Delete, Management } from "@element-plus/icons-vue";
import type { FormInstance } from "element-plus";
import OutSchemaService from "@/views/gen/service/OutSchemaService";
import StdListContainer from "@/soa/std-series/StdListContainer.vue";
import StdListAreaQuery from "@/soa/std-series/StdListAreaQuery.vue";
import StdListAreaAction from "@/soa/std-series/StdListAreaAction.vue";
import StdListAreaTable from "@/soa/std-series/StdListAreaTable.vue";
import DataSourceTableBrowser from "@/views/gen/components/DataSourceTableBrowser.vue";

const EditIcon = markRaw(Edit);
const DeleteIcon = markRaw(Delete);
const ManagementIcon = markRaw(Management);

//列表管理打包
const { listForm, listData, listTotal, listLoading, loadList, resetList, removeList } = OutSchemaService.useOutSchemaList();

const modalFormRef = ref<FormInstance>();

//模态框打包
const {
  modalVisible,
  modalLoading,
  modalMode,
  modalForm,
  modalRules,
  modalDataSource,
  modalTypeSchema,
  modalScm,
  openModal,
  resetModal,
  submitModal,
} = OutSchemaService.useOutSchemaModal(modalFormRef, loadList);
</script>

<style scoped></style>
