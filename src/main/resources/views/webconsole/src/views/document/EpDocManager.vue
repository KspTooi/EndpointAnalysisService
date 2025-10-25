<template>
  <div class="relay-doc-pull-config-manager-container">
    <!-- 查询表单 -->
    <div class="query-form">
      <el-form :model="query" inline>
        <el-form-item>
          <el-button type="primary" @click="loadRelayDocPullConfigList">查询</el-button>
          <el-button @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>
      <el-button type="primary" @click="openAddModal">创建端点文档配置</el-button>
    </div>

    <!-- 列表 -->
    <div class="relay-doc-pull-config-table">
      <el-table
          :data="list"
          stripe
          v-loading="loading"
          border
      >
        <el-table-column label="中继通道" prop="relayServerName" />
        <el-table-column label="文档拉取URL" prop="docPullUrl" />
        <el-table-column label="拉取时间" prop="pullTime" />
        <el-table-column label="创建时间" prop="createTime" />
        <el-table-column label="操作" fixed="right" >
          <template #default="scope">
            <el-button
                link
                type="primary"
                size="small"
                @click="openEditModal(scope.row)"
                :icon="ViewIcon"
            >
              编辑
            </el-button>
            <el-button
                link
                type="danger"
                size="small"
                @click="removeRelayDocPullConfig(scope.row)"
                :icon="DeleteIcon"
            >
              删除
            </el-button>
            <el-button
                link
                type="primary"
                size="small"
                @click="pullDoc(scope.row)"
                :icon="DocumentCopyIcon"
            >
              同步
            </el-button>
            <el-button
                link
                type="primary"
                size="small"
                @click="showSyncLogModal(scope.row)"
                :icon="DocumentCopyIcon"
            >
              同步记录
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination-container">
        <el-pagination
            v-model:current-page="query.pageNum"
            v-model:page-size="query.pageSize"
            :page-sizes="[10, 20, 50, 100]"
            layout="total, sizes, prev, pager, next, jumper"
            :total="total"
            @size-change="(val: number) => {
            query.pageSize = val
            loadRelayDocPullConfigList()
          }"
            @current-change="(val: number) => {
            query.pageNum = val
            loadRelayDocPullConfigList()
          }"
            background
        />
      </div>
    </div>

    <!-- 请求编辑模态框 -->
    <el-dialog
        v-model="dialogVisible"
        :title="modalMode === 'edit' ? '编辑端点文档配置' : '添加端点文档配置'"
        width="550px"
        :close-on-click-modal="false"
        @close="resetForm(); loadRelayDocPullConfigList()"
    >
      <el-form
          v-if="dialogVisible"
          ref="formRef"
          :model="details"
          :rules="rules"
          label-width="140px"
          :validate-on-rule-change="false"
      >
        <el-form-item label="中继通道" prop="relayServerId">
          <el-select v-model="details.relayServerId" placeholder="请选择中继通道">
            <el-option v-for="item in relayServerList" :key="item.id" :label="item.name" :value="item.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="文档拉取URL" prop="docPullUrl">
          <el-input v-model="details.docPullUrl" placeholder="https://www.baidu.com" />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="saveRelayDocPullConfig" :loading="submitLoading">
            {{ modalMode === 'add' ? '创建' : '保存' }}
          </el-button>
        </div>
        </template>
    </el-dialog>

    <!-- 同步记录模态框 -->
    <el-dialog
        v-model="syncLogModalVisible"
        title="同步记录"
        width="80%"
        :close-on-click-modal="false"
    >
      <el-table
        :data="syncLogList"
        stripe
        v-loading="loading"
        border
      >
        <el-table-column label="HASH" prop="hash" />
        <el-table-column label="拉取地址" prop="pullUrl" />
        <el-table-column label="状态" prop="status" width="80">
          <template #default="scope">
            <span v-if="scope.row.status === 0" style="color: green;">成功</span>
            <span v-if="scope.row.status === 1" style="color: red;">失败</span>
          </template>
        </el-table-column>
        <el-table-column label="版本变更" prop="newVersionCreated" width="100">
          <template #default="scope">
            <span v-if="scope.row.newVersionCreated === 0" style="color: red;">否</span>
            <span v-if="scope.row.newVersionCreated === 1" style="color: green;">是</span>
          </template>
        </el-table-column>
        <el-table-column label="版号" prop="newVersionNum" width="80">
          <template #default="scope">
            <span v-if="scope.row.newVersionNum === 0" style="color: red;">未变更</span>
            <span v-if="scope.row.newVersionNum > 0" style="color: green;">{{ scope.row.newVersionNum }}</span>
          </template>
        </el-table-column>
        <el-table-column label="拉取时间" prop="createTime" />
      </el-table>
      <div class="pagination-container">
        <el-pagination
            v-model:current-page="syncLogQuery.pageNum"
            v-model:page-size="syncLogQuery.pageSize"
            :page-sizes="[10, 20, 50, 100]"
            layout="total, sizes, prev, pager, next, jumper"
            :total="syncLogTotal"
            @size-change="(val: number) => {
            syncLogQuery.pageSize = val
            loadSyncLogList()
          }"
          @current-change="(val: number) => {
            syncLogQuery.pageNum = val
            loadSyncLogList()
          }"
            background
        />
      </div>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import {reactive, ref, onMounted} from "vue";
import EpDocApi, { type GetEpDocListDto, type GetEpDocListVo, type GetEpDocDetailsVo, type AddEpDocDto, type EditEpDocDto } from "@/api/document/EpDocApi.ts";
import { ElMessage, ElMessageBox } from 'element-plus';
import { Edit, View, Delete, DocumentCopy } from '@element-plus/icons-vue';
import { markRaw } from 'vue';
import type { FormInstance } from 'element-plus';
import RelayServerApi, { type GetRelayServerListDto, type GetRelayServerListVo, type GetRelayServerDetailsVo, type AddRelayServerDto, type EditRelayServerDto } from "@/api/relay/RelayServerApi.ts";
import type { GetEpDocSyncLogListDto, GetEpDocSyncLogListVo } from "@/api/document/EpDocSyncLogApi.ts";
import EpDocSyncLogApi from "@/api/document/EpDocSyncLogApi.ts";

//查询条件
const query = reactive<GetEpDocListDto>({
  pageNum: 1,
  pageSize: 10
})

//列表
const list = ref<GetEpDocListVo[]>([])
const total = ref(0)

// 加载状态
const loading = ref(false)

// 使用markRaw包装图标组件
const EditIcon = markRaw(Edit);
const ViewIcon = markRaw(View);
const DeleteIcon = markRaw(Delete);
const DocumentCopyIcon = markRaw(DocumentCopy);

// 模态框相关
const dialogVisible = ref(false)
const formRef = ref<FormInstance>()
const submitLoading = ref(false)
const modalMode = ref<("view" | "edit" | "add")>("view") //view:预览,edit:编辑,add:添加

// 同步记录模态框
const syncLogModalVisible = ref(false)
const syncLogQuery = reactive<GetEpDocSyncLogListDto>({
  epDocId: "0",
  pageNum: 1,
  pageSize: 10
})
const syncLogList = ref<GetEpDocSyncLogListVo[]>([])
const syncLogTotal = ref(0)

//中继通道列表
const relayServerList = ref<GetRelayServerListVo[]>([])


//表单数据
  const details = reactive<GetEpDocDetailsVo>({
  id: "0",
  relayServerId: null,
  relayServerName: "",
  docPullUrl: "",
  pullTime: "",
  createTime: "",
})

// 表单校验规则
const rules = {

}

//页面加载时自动加载数据
onMounted(() => {
  loadRelayDocPullConfigList()
  loadRelayServerList()
})


const loadRelayServerList = async () => {

  try {
    const res = await RelayServerApi.getRelayServerList({pageNum: 1, pageSize: 1000, name: null, forwardUrl: null})
    relayServerList.value = res.data
  } catch (error) {
    ElMessage.error('加载中继通道列表失败');
  }
}

const loadRelayDocPullConfigList = async () => {

  loading.value = true
  try {
    const res = await EpDocApi.getEpDocList(query);
    list.value = res.data;
    total.value = res.total;
    console.log(res)
  } catch (e) {
    ElMessage.error('加载中继通道文档拉取配置列表失败');
  } finally {
    loading.value = false
  }
}

const resetQuery = () => {
  query.pageNum = 1
  loadRelayDocPullConfigList()
}

const resetForm = () => {
  details.id = "0"
  details.relayServerId = null
  details.relayServerName = ""
  details.docPullUrl = ""
  details.pullTime = ""
  details.createTime = ""
}


//打开添加中继服务器模态框
const openAddModal = async () => {
  modalMode.value = "add"
  resetForm()
  dialogVisible.value = true
}

//打开编辑中继服务器模态框
const openEditModal = async (row: GetEpDocListVo) => {

  modalMode.value = "edit"
  resetForm()
  try {

    //获取请求数据
    const res = await EpDocApi.getEpDocDetails({id: row.id})
    details.id = res.id
    details.relayServerId = res.relayServerId
    details.relayServerName = res.relayServerName
    details.docPullUrl = res.docPullUrl
    details.pullTime = res.pullTime
    details.createTime = res.createTime
    dialogVisible.value = true
  } catch (error: any) {
    ElMessage.error(error.message)
    console.error('获取请求详情失败', error)
  }
}


const saveRelayDocPullConfig = async () => {

  //验证表单
  if (formRef.value){
    await formRef.value.validate()
  }

  try {

    if(modalMode.value === "add"){
      await EpDocApi.addEpDoc({
        relayServerId: details.relayServerId,
        docPullUrl: details.docPullUrl
      })
      ElMessage.success('添加中继通道成功')
    }

    if(modalMode.value === "edit"){
      await EpDocApi.editEpDoc({
        id: details.id,
        relayServerId: details.relayServerId,
        docPullUrl: details.docPullUrl
      })
      ElMessage.success('编辑中继通道成功')
    }

    dialogVisible.value = false
    loadRelayDocPullConfigList()

  } catch (error: any) {
    ElMessage.error(error.message)
    console.error('操作中继通道文档拉取配置失败', error)
  }



}

const removeRelayDocPullConfig = async (row: GetEpDocListVo) => {

  try {
    await ElMessageBox.confirm('确定删除中继通道文档拉取配置 [' + row.docPullUrl + '] 吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  })
  } catch (error) {
    return
  } 

  try {
    await EpDocApi.removeEpDoc({id: row.id})
    ElMessage.success('删除中继通道文档拉取配置成功');
    loadRelayDocPullConfigList()
  } catch (error: any) {
    ElMessage.error(error.message);
  }

}

const pullDoc = async (row: GetEpDocListVo) => {

  try {
    await EpDocApi.pullEpDoc({id: row.id})
    ElMessage.success('拉取文档成功');
    loadRelayDocPullConfigList()
  } catch (error: any) {
    ElMessage.error(error.message);
  }
}


const showSyncLogModal = async (row: GetEpDocListVo) => {

  syncLogModalVisible.value = true
  syncLogQuery.epDocId = row.id
  loadSyncLogList()
}

const loadSyncLogList = async () => {

  try {
    const res = await EpDocSyncLogApi.getEpSyncLogList(syncLogQuery)
    syncLogList.value = res.data
    syncLogTotal.value = res.total
  } catch (error) {
    ElMessage.error('加载同步记录失败');
  }
}

</script>

<style scoped>
.relay-doc-pull-config-manager-container {
  padding: 20px;
  max-width: 100%;
  overflow-x: auto;
  width: 100%;
}

.query-form {
  margin-bottom: 20px;
}

.relay-doc-pull-config-table {
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

.copy-icon {
  cursor: pointer;
}
</style>