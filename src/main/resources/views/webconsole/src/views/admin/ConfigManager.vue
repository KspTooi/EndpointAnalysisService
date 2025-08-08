<template>
  <div class="config-manager-container">
    <!-- 查询表单 -->
    <div class="query-form">
      <el-form :model="query" inline>
        <el-form-item label="Key">
          <el-input 
            v-model="query.key" 
            placeholder="Enter key to search" 
            clearable 
            style="width: 200px"
          />
        </el-form-item>
        <el-form-item label="Value">
          <el-input 
            v-model="query.value" 
            placeholder="Enter value to search" 
            clearable 
            style="width: 200px"
          />
        </el-form-item>
        <el-form-item label="Description">
          <el-input 
            v-model="query.description" 
            placeholder="Enter description to search" 
            clearable 
            style="width: 200px"
          />
        </el-form-item>
        <el-form-item label="Update Time">
          <el-date-picker
            v-model="updateTimeRange"
            type="datetimerange"
            range-separator="-"
            start-placeholder="Start Time"
            end-placeholder="End Time"
            value-format="YYYY-MM-DD HH:mm:ss"
            style="width: 350px"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="loadConfigList">Search</el-button>
          <el-button @click="resetQuery">Reset</el-button>
        </el-form-item>
      </el-form>
    </div>

    <!-- 配置列表 -->
    <div class="config-table">
      <el-table
        :data="list"
        stripe
        v-loading="loading"
      >
        <el-table-column 
          prop="key" 
          label="Key" 
          min-width="210"
          show-overflow-tooltip
        />
        <el-table-column 
          prop="value" 
          label="Value" 
          min-width="150" 
          show-overflow-tooltip
        />
        <el-table-column 
          prop="description" 
          label="Description" 
          min-width="180"
          show-overflow-tooltip
        />
        <el-table-column 
          prop="updateTime" 
          label="Update Time"
          min-width="150"
        />
        <el-table-column label="Actions" fixed="right" min-width="100">
          <template #default="scope">
            <el-button 
              link
              type="primary" 
              size="small" 
              @click="openUpdateModal(scope.row)"
              :icon="EditIcon"
            >
              Edit
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination-container">
        <el-pagination
          v-model:current-page="query.page"
          v-model:page-size="query.pageSize"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          :total="total"
          @size-change="(val: number) => {
            query.pageSize = val
            loadConfigList()
          }"
          @current-change="(val: number) => {
            query.page = val
            loadConfigList()
          }"
          background
        />
      </div>
    </div>

    <!-- 配置编辑模态框 -->
    <el-dialog
      v-model="dialogVisible"
      title="Edit Configuration"
      width="500px"
      :close-on-click-modal="false"
    >
      <el-form
        v-if="dialogVisible"
        ref="formRef"
        :model="details"
        :rules="rules"
        label-width="100px"
        :validate-on-rule-change="false"
      >
        <el-form-item label="Key" prop="key">
          <el-input 
            v-model="details.key" 
            placeholder="Enter key"
            disabled
          />
        </el-form-item>
        <el-form-item label="Value" prop="value">
          <el-input 
            v-model="details.value" 
            type="textarea"
            :rows="3"
            placeholder="Enter value"
          />
        </el-form-item>
        <el-form-item label="Description" prop="description">
          <el-input 
            v-model="details.description" 
            type="textarea" 
            :rows="3"
            placeholder="Enter description"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">Cancel</el-button>
          <el-button type="primary" @click="saveConfig" :loading="submitLoading">
            Confirm
          </el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import {reactive, ref, onMounted} from "vue";
import type { GetConfigListDto, GetConfigListVo, GetConfigDetailVo, EditConfigDto, CommonIdDto } from "@/commons/api/ConsoleConfigApi";
import ConfigApi from "@/commons/api/ConsoleConfigApi";
import { ElMessage } from 'element-plus';
import { Edit } from '@element-plus/icons-vue';
import { markRaw } from 'vue';
import type { FormInstance } from 'element-plus';

const query = reactive<GetConfigListDto>({
  key: null,
  value: null,
  description: null,
  updateTimeStart: null,
  updateTimeEnd: null,
  page: 1,
  pageSize: 10
})

const list = ref<GetConfigListVo[]>([])
const total = ref(0)

// 加载状态
const loading = ref(false)

// 使用markRaw包装图标组件
const EditIcon = markRaw(Edit);

// 模态框相关
const dialogVisible = ref(false)
const formRef = ref<FormInstance>()
const submitLoading = ref(false)

//表单数据
const details = reactive<GetConfigDetailVo>({
  id: 0,
  key: "",
  value: "",
  description: ""
})

// 表单校验规则
const rules = {
  key: [
    { required: true, message: 'Please enter key', trigger: 'blur' }
  ],
  value: [
    { required: true, message: 'Please enter value', trigger: 'blur' }
  ],
  description: [
    { max: 200, message: 'Description cannot exceed 200 characters', trigger: 'blur' }
  ]
}

const updateTimeRange = ref<[string, string] | null>(null)

const loadConfigList = async () => {
  if (updateTimeRange.value) {
    query.updateTimeStart = updateTimeRange.value[0]
    query.updateTimeEnd = updateTimeRange.value[1]
  } else {
    query.updateTimeStart = null
    query.updateTimeEnd = null
  }
  loading.value = true
  try {
    const res = await ConfigApi.getConfigList(query);
    list.value = res.rows;
    total.value = res.count;
  } catch (e) {
    ElMessage.error('Failed to load configuration list');
    console.error("Failed to load configuration list", e);
  } finally {
    loading.value = false
  }
}

const resetQuery = () => {
  query.key = null
  query.value = null
  query.description = null
  updateTimeRange.value = null
  query.page = 1
  loadConfigList()
}

const resetForm = () => {
  details.id = 0
  details.key = ""
  details.value = ""
  details.description = ""
  
  if (formRef.value) {
    formRef.value.resetFields()
  }
}

//页面加载时自动加载数据
onMounted(() => {
  loadConfigList()
})

const openUpdateModal = async (row: GetConfigListVo) => {
  try {
    resetForm()
    
    const res = await ConfigApi.getConfigDetail({ id: row.id } as CommonIdDto)
    Object.assign(details, res)
    
    dialogVisible.value = true
  } catch (error) {
    ElMessage.error('Failed to get configuration details')
    console.error('Failed to get configuration details', error)
  }
}

const saveConfig = async () => {
  if (!formRef.value) return
  
  await formRef.value.validate(async (valid) => {
    if (!valid) return
    
    submitLoading.value = true
    try {
      await ConfigApi.editConfig(details as EditConfigDto)
      
      ElMessage.success('Configuration updated successfully')
      dialogVisible.value = false
      loadConfigList()
    } catch (error) {
      const errorMsg = error instanceof Error ? error.message : 'Operation failed'
      ElMessage.error(errorMsg)
    } finally {
      submitLoading.value = false
    }
  })
}
</script>

<style scoped>
.config-manager-container {
  padding: 20px;
  max-width: 100%;
  overflow-x: auto;
}

.query-form {
  margin-bottom: 20px;
}

.config-table {
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
</style>