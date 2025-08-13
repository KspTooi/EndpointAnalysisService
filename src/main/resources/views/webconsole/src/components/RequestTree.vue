<template>
    <div class="tag-tree-container">

        <div class="tag-tree-search">
          <el-input v-model="searchValue" placeholder="输入任意字符查询" size="small" @input="handleSearch" clearable />
          <el-button type="primary" @click="loadUserRequestTree" size="small">加载用户请求</el-button>
        </div>

        <div class="tag-tree-body">
            <template v-for="item in treeData" :key="item.id">
                <div class="tree-node">
                    <!-- 节点本身 -->
                    <div 
                        class="tag-tree-item" 
                        :class="{ 
                            'tag-item-active': isGroup(item) ? isExpanded(item.id) : isActive(item.id),
                            'request-item': !isGroup(item)
                        }" 
                        @click="handleNodeClick(item)"
                    >
                        <div class="tag-tree-item-tag">
                            <el-icon 
                                v-if="isGroup(item) && hasChildren(item)" 
                                class="expand-icon" 
                                @click.stop="handleToggleNode(item.id)"
                            >
                                <ArrowRight v-if="!isExpanded(item.id)" />
                                <ArrowDown v-else />
                            </el-icon>
                            <el-icon v-if="isGroup(item)" class="folder-icon">
                                <Folder />
                            </el-icon>
                            <el-icon v-if="!isGroup(item)" class="document-icon">
                                <Document />
                            </el-icon>
                            
                            <span class="node-name">{{ item.name }}</span>
                            
                            <div v-if="!isGroup(item) && item.method" 
                                 class="operation-method" 
                                 :class="`method-${item.method.toLowerCase()}`">
                                {{ item.method }}
                            </div>
                        </div>
                        
                        <div v-if="isGroup(item) && hasChildren(item)" class="tag-tree-item-count">
                            {{ item.children?.length }}
                        </div>
                    </div>
                    
                    <!-- 子节点 -->
                    <div 
                        v-if="isGroup(item) && hasChildren(item) && isExpanded(item.id)" 
                        class="operation-list"
                    >
                        <template v-for="child in item.children" :key="child.id">
                            <div 
                                class="tag-tree-item request-item" 
                                :class="{ 
                                    'tag-item-active': isGroup(child) ? isExpanded(child.id) : isActive(child.id)
                                }" 
                                @click="handleNodeClick(child)"
                            >
                                <div class="tag-tree-item-tag">
                                    <el-icon v-if="isGroup(child)" class="folder-icon">
                                        <Folder />
                                    </el-icon>
                                    <el-icon v-else class="document-icon">
                                        <Document />
                                    </el-icon>
                                    
                                    <span class="node-name">{{ child.name }}</span>
                                    
                                    <div v-if="!isGroup(child) && child.method" 
                                         class="operation-method" 
                                         :class="`method-${child.method.toLowerCase()}`">
                                        {{ child.method }}
                                    </div>
                                </div>
                            </div>
                        </template>
                    </div>
                </div>
            </template>
        </div>
    </div>
</template>


<script setup lang="ts">
import { onMounted, ref, defineComponent } from 'vue'
import UserRequestTreeApi from '@/api/UserRequestTreeApi'
import type { GetUserRequestTreeVo, GetUserRequestTreeDto } from '@/api/UserRequestTreeApi'
import { Folder, ArrowDown, ArrowRight, Document } from '@element-plus/icons-vue'

const emit = defineEmits<{
  (e: 'select-group', groupId: string): void
  (e: 'select-request', requestId: string): void
}>()

const treeData = ref<GetUserRequestTreeVo[]>([])
const searchValue = ref('')
const activeNodes = ref<string[]>([])
const activeRequestId = ref<string | null>(null)

let searchTimer: ReturnType<typeof setTimeout> | null = null

const loadUserRequestTree = async () => {
    try {
        const dto: GetUserRequestTreeDto = {
            keyword: searchValue.value || null
        }
        const res = await UserRequestTreeApi.getUserRequestTree(dto)
        treeData.value = res
    } catch (error) {
        console.error('加载用户请求树失败:', error)
    }
}

const handleSearch = () => {
    if (searchTimer) {
        clearTimeout(searchTimer)
    }
    searchTimer = setTimeout(() => {
        loadUserRequestTree()
    }, 500)
}

const handleToggleNode = (nodeId: string) => {
    if (activeNodes.value.includes(nodeId)) {
        activeNodes.value = activeNodes.value.filter(id => id !== nodeId)
    } else {
        activeNodes.value.push(nodeId)
    }
}

const handleSelectRequest = (requestId: string) => {
    activeRequestId.value = requestId
    emit('select-request', requestId)
}

// 递归渲染函数
const isGroup = (node: GetUserRequestTreeVo) => node.type === 0
const hasChildren = (node: GetUserRequestTreeVo) => node.children && node.children.length > 0
const isExpanded = (nodeId: string) => activeNodes.value.includes(nodeId)
const isActive = (nodeId: string) => activeRequestId.value === nodeId

const handleNodeClick = (node: GetUserRequestTreeVo) => {
    if (isGroup(node)) {
        if (hasChildren(node)) {
            handleToggleNode(node.id)
        }
    } else {
        handleSelectRequest(node.id)
    }
}

onMounted(() => {
    loadUserRequestTree()
})

defineExpose({
    loadUserRequestTree
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
    flex: 1;
}

.tag-tree-item-tag .el-icon {
    color: #409eff;
    font-size: 16px;
}

.folder-icon {
    color: #409eff !important;
}

.document-icon {
    color: #67c23a !important;
}

.node-name {
    flex: 1;
    color: #2c3e50;
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
    min-height: 20px;
}

.request-item {
    /*margin-left: 20px;*/
    background: linear-gradient(135deg, #f8f9fa 0%, #ffffff 100%);
    border: 1px solid #dee2e6;
}

.request-item:hover {
    background: linear-gradient(135deg, #e8f4fd 0%, #f0f9ff 100%);
    border-color: #409eff;
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
    padding: 2px 4px;
    border-radius: 16px;
    font-size: 11px;
    font-weight: 600;
    min-width: 20px;
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
    margin-left: auto;
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

.tree-node {
    margin-bottom: 2px;
}

.tag-tree-search {
    padding: 10px;
    border-bottom: 1px solid rgba(64, 158, 255, 0.2);
    display: flex;
    flex-direction: row;
    align-items: center;
    justify-content: space-between;
    gap: 15px;
}




</style>