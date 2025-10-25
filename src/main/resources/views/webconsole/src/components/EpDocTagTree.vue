<template>
    <div class="tag-tree-container">
        <div class="tag-tree-header">
            <el-select v-model="selectedDocId" placeholder="选择端点文档" style="min-width: 180px" @change="loadTagTree" size="small" clearable>
                <el-option v-for="doc in docList" :key="doc.id" :label="doc.relayServerName" :value="doc.id" />
            </el-select>
            <el-button type="primary" @click="loadTagTree" size="small">加载</el-button>
        </div>

        <div class="tag-tree-search" v-if="false">
            <el-input v-model="searchValue" placeholder="输入任意字符查询" size="small" />
        </div>

        <div class="tag-tree-body">
            <div class="tag-tree-group" v-for="item in listData" :key="item.tag">
                <div class="tag-tree-item" :class="{ 'tag-item-active': activeTags.includes(item.tag) }" @click="handleSelectTag(item.tag)">
                    <div class="tag-tree-item-tag">
                        <el-icon class="expand-icon" @click.stop="handleSelectTag(item.tag)">
                            <ArrowRight v-if="!activeTags.includes(item.tag)" />
                            <ArrowDown v-else />
                        </el-icon>
                        <el-icon>
                            <Folder />
                        </el-icon>
                        {{ item.tag }}
                    </div>
                    <div class="tag-tree-item-count">
                        {{ item.apiCount }}
                    </div>
                </div>
                
                <div class="operation-list" v-if="activeTags.includes(item.tag) && item.operationDefineList && item.operationDefineList.length > 0">
                    <div class="operation-item" v-for="operation in item.operationDefineList" :key="operation.id" @click="handleOperationClick(operation.id)" :class="{ 'operation-item-active': activeOperationId == operation.id }">
                        <div class="operation-method" :class="`method-${operation.method.toLowerCase()}`">
                            {{ operation.method }}
                        </div>
                        <div class="operation-name">
                            {{ operation.name }}
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</template>


<script setup lang="ts">
import { onMounted, ref } from 'vue'
import EpDocOperationApi from '@/api/document/EpDocOperationApi.ts'
import type { GetEpDocOperationTagListVo } from '@/api/document/EpDocOperationApi.ts'
import EpDocApi from '@/api/document/EpDocApi.ts'
import type { GetEpDocListVo } from '@/api/document/EpDocApi.ts'
import { Folder, ArrowDown, ArrowRight } from '@element-plus/icons-vue'

const emit = defineEmits<{
  (e: 'select-tag', tagId: string): void
  (e: 'select-operation', operationId: string): void
}>()

const selectedDocId = ref('')
const listData = ref<GetEpDocOperationTagListVo[]>([])
const docList = ref<GetEpDocListVo[]>([])
const searchValue = ref('')
const activeTags = ref<string[]>([])

//选中的接口ID
const activeOperationId = ref<string | null>(null)


const loadDocList = async () => {
    const res = await EpDocApi.getEpDocList({
        pageNum: 1,
        pageSize: 1000,
    })
    docList.value = res.data
    if (docList.value.length > 0) {
        if (selectedDocId.value == '') {
            selectedDocId.value = docList.value[0].id
            loadTagTree()
        }
    }
}

const loadTagTree = async () => {
    if (!selectedDocId.value) {
        return
    }

    const res = await EpDocOperationApi.getEpDocOperationTagList({
        epDocId: selectedDocId.value,
        epDocVersionId: null,
    })

    listData.value = res
}

const handleSelectTag = (tag: string) => {
    //已存在则删除
    if (activeTags.value.includes(tag)) {
        activeTags.value = activeTags.value.filter(t => t !== tag)
        return
    }
    //不存在则添加
    activeTags.value.push(tag)
}


const handleOperationClick = (operationId: string) => {
    activeOperationId.value = operationId
    emit('select-operation', operationId)
}

onMounted(() => {
    loadDocList()
})


defineExpose({
    loadTagTree
})
</script>

<style scoped>

.tag-tree-item-tag {
    display: flex;
    flex-direction: row;
    align-items: center;
    gap: 6px;
    font-weight: 500;
    color: #2c3e50;
}

.tag-tree-item-tag .el-icon {
    color: #409eff;
    font-size: 16px;
}

.tag-tree-item {
    display: flex;
    flex-direction: row;
    align-items: center;
    justify-content: space-between;
    padding: 6px 12px;
    font-size: 14px;
    background: linear-gradient(135deg, #ffffff 0%, #f8f9fa 100%);
    border: 1px solid #e9ecef;
    border-radius: 4px;
    margin: 4px 8px;
    cursor: pointer;
    transition: all 0.3s ease;
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
    user-select: none;
}

.tag-item-active {
    background: linear-gradient(135deg, #f0f8ff 0%, #e6f3ff 100%) !important;
    border-color: #40b9ff !important;
    box-shadow: 0 4px 12px rgba(64, 158, 255, 0.15) !important;
    transform: translateY(-1px) !important;
}

.tag-tree-item:hover {
    background: linear-gradient(135deg, #f0f8ff 0%, #e6f3ff 100%);
    border-color: #409eff;
    box-shadow: 0 4px 12px rgba(64, 158, 255, 0.15);
    transform: translateY(-1px);
}

.tag-tree-item:active {
    background: linear-gradient(135deg, #e0f2ff 0%, #d0ebff 100%);
    transform: translateY(0);
    box-shadow: 0 2px 6px rgba(64, 158, 255, 0.2);
}

.tag-tree-item-count {
    background: linear-gradient(135deg, #409eff 0%, #66b3ff 100%);
    color: white;
    padding: 4px 8px;
    border-radius: 12px;
    font-size: 12px;
    font-weight: 600;
    min-width: 24px;
    text-align: center;
    box-shadow: 0 2px 4px rgba(64, 158, 255, 0.3);
}


.tag-tree-header {
    display: flex;
    flex-direction: row;
    align-items: center;
    justify-content: space-between;
    gap: 10px;
    padding: 10px;
    padding-bottom: 0;
}

.tag-tree-container {
    background: linear-gradient(135deg, #f8f9fa 0%, #ffffff 100%);
    box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
    border: 1px solid #e9ecef;
}

.tag-tree-body {
    border-top: 1px solid rgba(64, 158, 255, 0.2);
    margin-top: 10px;
    overflow-y: auto;
    height: calc(100vh - 105px);
    padding-top: 4px;
}

.tag-tree-body::-webkit-scrollbar {
    width: 4px;
}

.tag-tree-body::-webkit-scrollbar-track {
    background: transparent;
}

.tag-tree-body::-webkit-scrollbar-thumb {
    background: rgba(144, 147, 153, 0.3);
    border-radius: 2px;
}

.tag-tree-body::-webkit-scrollbar-thumb:hover {
    background: rgba(144, 147, 153, 0.6);
}

.tag-tree-group {
    margin-bottom: 4px;
}

.expand-icon {
    cursor: pointer;
    transition: transform 0.3s ease;
    color: #909399;
    margin-right: 4px;
}

.expand-icon:hover {
    color: #409eff;
}

.operation-list {
    margin-left: 20px;
    margin-top: 4px;
    animation: slideDown 0.3s ease-out;
}

@keyframes slideDown {
    from {
        opacity: 0;
        transform: translateY(-10px);
    }
    to {
        opacity: 1;
        transform: translateY(0);
    }
}

.operation-item {
    display: flex;
    align-items: center;
    gap: 8px;
    padding: 6px 12px;
    margin: 2px 8px;
    background: linear-gradient(135deg, #f8f9fa 0%, #ffffff 100%);
    border: 1px solid #e9ecef;
    border-radius: 4px;
    cursor: pointer;
    transition: all 0.2s ease;
    font-size: 13px;
}

.operation-item:hover {
    background: linear-gradient(135deg, #e8f4fd 0%, #f0f9ff 100%);
    border-color: #409eff;
}

.operation-item-active {
    background: linear-gradient(135deg, #e6f8ff 0%, #d0f0ff 100%);
    border-color: #409eff;
}

.operation-method {
    padding: 2px 6px;
    border-radius: 3px;
    font-size: 11px;
    font-weight: 600;
    text-transform: uppercase;
    min-width: 45px;
    text-align: center;
    color: white;
}

.method-get {
    background: linear-gradient(135deg, #67c23a 0%, #85ce61 100%);
}

.method-post {
    background: linear-gradient(135deg, #409eff 0%, #66b3ff 100%);
}

.method-put {
    background: linear-gradient(135deg, #e6a23c 0%, #f0c78a 100%);
}

.method-delete {
    background: linear-gradient(135deg, #f56c6c 0%, #f78989 100%);
}

.method-patch {
    background: linear-gradient(135deg, #909399 0%, #b1b3b8 100%);
}

.operation-name {
    flex: 1;
    color: #606266;
    font-weight: 500;
}


</style>