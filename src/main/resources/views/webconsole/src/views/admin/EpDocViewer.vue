<template>
  <div class="epdoc-viewer-container">
    <div class="toolbar">
      <el-space wrap>
        <el-select v-model="selectedDocId" filterable clearable placeholder="选择端点文档" style="min-width: 280px" @change="onDocChanged">
          <el-option v-for="doc in epDocOptions" :key="doc.id" :label="formatDocLabel(doc)" :value="doc.id" />
        </el-select>
        <el-button type="primary" @click="reloadTags" :loading="loadingTags">加载接口</el-button>
      </el-space>
    </div>

    <div class="content">
      <div class="sidebar">
        <el-input v-model="keyword" placeholder="搜索标签/接口" clearable @input="filterTags" />
        <el-collapse v-model="activeTagPanels" class="tag-collapse">
          <el-collapse-item v-for="tag in filteredTags" :key="tag.tag" :name="tag.tag">
            <template #title>
              <div class="tag-title">
                <span class="tag-text">{{ tag.tag }}</span>
                <el-tag size="small" effect="plain">{{ tag.apiCount }}</el-tag>
              </div>
            </template>
            <div class="operation-list">
              <div
                v-for="op in tag.operationDefineList"
                :key="op.id"
                class="operation-item"
                :class="{ active: currentOperationId === op.id }"
                @click="loadOperation(op.id)"
              >
                <el-tag :type="methodTagType(op.method)" size="small" class="method-tag">{{ op.method }}</el-tag>
                <span class="operation-name">{{ op.name }}</span>
              </div>
            </div>
          </el-collapse-item>
        </el-collapse>
      </div>

      <div class="details">
        <el-empty v-if="!details" description="请选择左侧接口查看详情" />
        <div v-else class="details-content">
          <el-card shadow="hover" class="basic-card">
            <div class="basic-header">
              <el-tag :type="methodTagType(details.method)" size="large" class="method-badge">{{ details.method }}</el-tag>
              <span class="path">{{ details.path }}</span>
            </div>
            <div class="basic-meta">
              <div class="meta-row" v-if="details.summary">
                <span class="meta-label">摘要</span>
                <span class="meta-value">{{ details.summary }}</span>
              </div>
              <div class="meta-row" v-if="details.description">
                <span class="meta-label">描述</span>
                <span class="meta-value">{{ details.description }}</span>
              </div>
              <div class="meta-row">
                <span class="meta-label">OperationId</span>
                <span class="meta-value">{{ details.operationId }}</span>
              </div>
            </div>
          </el-card>

          <div class="schema-grid">
            <el-card shadow="never" class="schema-card">
              <template #header>
                <div class="card-header">请求体</div>
              </template>
              <SchemaViewer :schema="details.reqBody" />
            </el-card>
            <el-card shadow="never" class="schema-card">
              <template #header>
                <div class="card-header">响应体</div>
              </template>
              <SchemaViewer :schema="details.resBody" />
            </el-card>
          </div>
        </div>
      </div>
    </div>
  </div>
  
</template>

<script setup lang="ts">
import { defineComponent, h, onMounted, ref, watch } from 'vue'
import type { PropType } from 'vue'
import { ElMessage, ElTree } from 'element-plus'
import EpDocApi from '@/api/EpDocApi.ts'
import type { GetEpDocListDto, GetEpDocListVo } from '@/api/EpDocApi.ts'
import EpDocOperationApi from '@/api/EpDocOperationApi.ts'
import type { GetEpDocOperationTagListDto, GetEpDocOperationTagListVo, GetEpDocOperationDetailsVo, BodySchema, BodySchemaParam } from '@/api/EpDocOperationApi.ts'

// 顶部：文档选择
const epDocOptions = ref<GetEpDocListVo[]>([])
const selectedDocId = ref<string | null>(null)
const loadingDocs = ref(false)

const loadEpDocOptions = async () => {
  loadingDocs.value = true
  try {
    const dto: GetEpDocListDto = { pageNum: 1, pageSize: 1000 }
    const res = await EpDocApi.getEpDocList(dto)
    epDocOptions.value = res.data || []
    if (!selectedDocId.value && epDocOptions.value.length > 0) {
      selectedDocId.value = epDocOptions.value[0].id
    }
  } catch (e) {
    ElMessage.error('加载端点文档失败')
  } finally {
    loadingDocs.value = false
  }
}

const formatDocLabel = (doc: GetEpDocListVo): string => {
  const server = doc.relayServerName ? `【${doc.relayServerName}】` : ''
  return `${server} 文档ID:${doc.id}`
}

// 左侧：标签 + 接口列表
const tags = ref<GetEpDocOperationTagListVo[]>([])
const filteredTags = ref<GetEpDocOperationTagListVo[]>([])
const activeTagPanels = ref<string[]>([])
const loadingTags = ref(false)
const keyword = ref('')

const reloadTags = async () => {
  tags.value = []
  filteredTags.value = []
  activeTagPanels.value = []
  const epDocIdNum = Number(selectedDocId.value)
  if (!selectedDocId.value || Number.isNaN(epDocIdNum)) {
    ElMessage.warning('请选择有效的端点文档')
    return
  }
  loadingTags.value = true
  try {
    const dto: GetEpDocOperationTagListDto = { epDocId: epDocIdNum, epDocVersionId: null }
    const list = await EpDocOperationApi.getEpDocOperationTagList(dto)
    tags.value = list || []
    filteredTags.value = tags.value
    activeTagPanels.value = tags.value.map(t => t.tag)
  } catch (e) {
    ElMessage.error('加载接口标签失败')
  } finally {
    loadingTags.value = false
  }
}

const filterTags = () => {
  const kw = keyword.value.trim().toLowerCase()
  if (!kw) {
    filteredTags.value = tags.value
    return
  }
  filteredTags.value = tags.value.map(t => ({
    ...t,
    operationDefineList: t.operationDefineList.filter(op =>
      (t.tag && t.tag.toLowerCase().includes(kw)) ||
      (op.name && op.name.toLowerCase().includes(kw)) ||
      (op.method && op.method.toLowerCase().includes(kw))
    )
  })).filter(t => t.operationDefineList.length > 0 || (t.tag && t.tag.toLowerCase().includes(kw)))
}

watch(keyword, filterTags)

const onDocChanged = () => {
  currentOperationId.value = null
  details.value = null
  reloadTags()
}

// 右侧：接口详情
const currentOperationId = ref<number | null>(null)
const details = ref<GetEpDocOperationDetailsVo | null>(null)
const loadingDetails = ref(false)

const loadOperation = async (opId: number) => {
  if (!opId) return
  loadingDetails.value = true
  try {
    currentOperationId.value = opId
    const res = await EpDocOperationApi.getEpDocOperationDetails({ id: String(opId) })
    details.value = res
  } catch (e) {
    ElMessage.error('加载接口详情失败')
  } finally {
    loadingDetails.value = false
  }
}

const methodTagType = (method: string): 'success' | 'warning' | 'info' | 'danger' => {
  const m = (method || '').toUpperCase()
  if (m === 'GET') return 'success'
  if (m === 'POST') return 'warning'
  if (m === 'PUT') return 'info'
  return 'danger'
}

onMounted(async () => {
  await loadEpDocOptions()
  await reloadTags()
})

// Schema Viewer 子组件
type TreeNode = { label: string; children?: TreeNode[] }

const buildTreeFromSchema = (schema: BodySchema | null | undefined): TreeNode[] => {
  if (!schema || !schema.params || schema.params.length === 0) return []
  return schema.params.map((p) => buildNodeFromParam(p))
}

const buildNodeFromParam = (p: BodySchemaParam): TreeNode => {
  const requiredMark = p.required ? '*' : ''
  const arrayMark = p.isArray ? '[]' : ''
  const typeText = p.type ? `${p.type}${arrayMark}` : ''
  const descText = p.description ? ` - ${p.description}` : ''
  const defText = p.defaultValue !== undefined && p.defaultValue !== null && String(p.defaultValue).length > 0 ? ` = ${p.defaultValue}` : ''
  const label = `${p.name}${requiredMark}: ${typeText}${descText}${defText}`
  const hasChild = !!p.schema && !!p.schema.params && p.schema.params.length > 0
  if (!hasChild) {
    return { label }
  }
  return {
    label,
    children: p.schema!.params.map((child) => buildNodeFromParam(child))
  }
}

const SchemaViewer = defineComponent({
  name: 'SchemaViewer',
  props: {
    schema: {
      type: Object as PropType<BodySchema | null | undefined>,
      required: false
    }
  },
  setup(props) {
    return () => {
      const nodes = buildTreeFromSchema(props.schema)
      if (nodes.length === 0) {
        return h('div', { class: 'empty' }, '无')
      }
      return h('div', { class: 'schema-tree' }, [
        h(ElTree as any, { data: nodes, props: { label: 'label', children: 'children' }, defaultExpandAll: true })
      ])
    }
  }
})
</script>

<style scoped>
.epdoc-viewer-container {
  padding: 16px;
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.toolbar {
  display: flex;
  align-items: center;
}

.content {
  display: grid;
  grid-template-columns: 360px 1fr;
  gap: 16px;
  min-height: 70vh;
}

.sidebar {
  border: 1px solid #ebeef5;
  border-radius: 6px;
  padding: 12px;
  background: #fff;
  display: flex;
  flex-direction: column;
  gap: 8px;
  overflow: auto;
}

.tag-collapse {
  margin-top: 4px;
}

.tag-title {
  display: flex;
  align-items: center;
  gap: 8px;
}

.operation-list {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.operation-item {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 6px 8px;
  border-radius: 4px;
  cursor: pointer;
}

.operation-item:hover {
  background: #f5f7fa;
}

.operation-item.active {
  background: #ecf5ff;
}

.method-tag {
  width: 56px;
  text-align: center;
}

.operation-name {
  flex: 1;
  color: #333;
}

.details {
  overflow: auto;
}

.basic-card {
  margin-bottom: 12px;
}

.basic-header {
  display: flex;
  align-items: center;
  gap: 12px;
  font-size: 14px;
}

.method-badge {
  width: 70px;
  text-align: center;
}

.path {
  font-family: monospace;
  color: #409eff;
}

.basic-meta {
  margin-top: 8px;
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.meta-row {
  display: flex;
  gap: 8px;
}

.meta-label {
  color: #909399;
  width: 100px;
  flex: 0 0 auto;
}

.meta-value {
  color: #303133;
}

.schema-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 12px;
}

.schema-card {
  min-height: 280px;
}

.schema-tree {
  max-height: 50vh;
  overflow: auto;
}

.empty {
  color: #909399;
}

@media (max-width: 1200px) {
  .content {
    grid-template-columns: 1fr;
  }
}
</style>