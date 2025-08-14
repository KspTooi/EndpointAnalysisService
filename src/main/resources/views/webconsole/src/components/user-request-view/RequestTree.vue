<template>
    <div class="tag-tree-container">

        <div class="tag-tree-search">
          <el-input v-model="searchValue" placeholder="输入任意字符查询" size="small" @input="handleSearch" clearable  />
          <el-button type="primary" @click="loadUserRequestTree" size="small">加载数据</el-button>
          <el-button type="primary" @click="showCreateGroupDialog" size="small">新建组</el-button>
          <div v-if="isRootDragOver" class="root-drop-hint">拖拽到此处以移动到根级别</div>
        </div>

        <!-- 创建组对话框 -->
        <el-dialog v-model="createGroupDialogVisible" title="创建请求组" width="400px">
            <el-form ref="createGroupFormRef" :model="createGroupForm" :rules="createGroupRules" label-width="80px">
                <el-form-item label="组名称" prop="name">
                    <el-input v-model="createGroupForm.name" placeholder="请输入组名称" />
                </el-form-item>
            </el-form>
            <template #footer>
                <span class="dialog-footer" style="gap: 10px; display: flex; justify-content: right;">
                    <el-button @click="createGroupDialogVisible = false">取消</el-button>
                    <el-button type="primary" @click="handleCreateGroup" :loading="createGroupLoading">确定</el-button>
                </span>
            </template>
        </el-dialog>

        <div class="tag-tree-body" 
             ref="tagTreeBodyRef"
             @dragenter="handleRootDragEnter"
             @dragover.prevent="handleRootDragOver"
             @drop.prevent="handleRootDrop"
             @dragleave="handleRootDragLeave"
             >
            <RequestTreeItem
                v-for="(item, index) in treeData" 
                :key="item.id"
                :node="item" 
                :active-nodes="activeNodes" 
                :active-request-id="activeRequestId"
                :child-index="index"
                @toggle-node="handleToggleNode"
                @select-request="handleSelectRequest"
                @right-click="handleRightClick"
                @refresh-tree="loadUserRequestTree"
            />
        </div>

        <!-- 右键菜单 -->
        <RequestTreeItemRightMenu
            :visible="rightMenuVisible"
            :x="rightMenuX"
            :y="rightMenuY"
            :node="rightMenuNode"
            @close="handleRightMenuClose"
            @refresh="loadUserRequestTree"
        />
    </div>
</template>


<script setup lang="ts">
import { onMounted, onUnmounted, ref } from 'vue'
import UserRequestTreeApi from '@/api/UserRequestTreeApi.ts'
import type { GetUserRequestTreeVo, GetUserRequestTreeDto } from '@/api/UserRequestTreeApi.ts'
import UserRequestGroupApi from '@/api/UserRequestGroupApi.ts'
import type { AddUserRequestGroupDto } from '@/api/UserRequestGroupApi.ts'
import RequestTreeItem from './RequestTreeItem.vue'
import RequestTreeItemRightMenu from './RequestTreeItemRightMenu.vue'
import { ElMessage, type FormInstance } from 'element-plus'

const emit = defineEmits<{
  (e: 'select-group', groupId: string): void
  (e: 'select-request', requestId: string): void
}>()

const treeData = ref<GetUserRequestTreeVo[]>([])
const searchValue = ref('')
const activeNodes = ref<string[]>([])
const activeRequestId = ref<string | null>(null)

// 右键菜单状态
const rightMenuVisible = ref(false)
const rightMenuX = ref(0)
const rightMenuY = ref(0)
const rightMenuNode = ref<GetUserRequestTreeVo | null>(null)

// 根级拖拽状态
const isRootDragOver = ref(false)
let rootDragCounter = 0
const tagTreeBodyRef = ref<HTMLElement | null>(null)

// 创建组相关数据
const createGroupDialogVisible = ref(false)
const createGroupLoading = ref(false)
const createGroupFormRef = ref<FormInstance>()
const createGroupForm = ref<AddUserRequestGroupDto>({
    parentId: null,
    name: ''
})

const createGroupRules = {
    name: [
        { required: true, message: '请输入组名称', trigger: 'blur' },
        { min: 1, max: 64, message: '组名称长度应在 1 到 64 个字符之间', trigger: 'blur' }
    ]
}

let searchTimer: ReturnType<typeof setTimeout> | null = null

const loadUserRequestTree = async () => {
    try {
        const dto: GetUserRequestTreeDto = {
            keyword: searchValue.value || null
        }
        const res = await UserRequestTreeApi.getUserRequestTree(dto)
        treeData.value = res
        
        // 清理没有子节点的分组展开状态
        cleanupActiveNodes()
    } catch (error) {
        console.error('加载用户请求树失败:', error)
    }
}

// 递归收集所有没有子节点的分组ID
const collectEmptyGroupIds = (nodes: GetUserRequestTreeVo[]): Set<string> => {
    const emptyGroupIds = new Set<string>()
    
    const traverse = (nodeList: GetUserRequestTreeVo[]) => {
        for (const node of nodeList) {
            if (node.type === 0) { // 分组类型
                if (!node.children || node.children.length === 0) {
                    // 没有子节点的分组
                    emptyGroupIds.add(node.id)
                } else {
                    // 递归检查子节点
                    traverse(node.children)
                }
            }
        }
    }
    
    traverse(nodes)
    return emptyGroupIds
}

// 清理activeNodes中没有子节点的分组
const cleanupActiveNodes = () => {
    const emptyGroupIds = collectEmptyGroupIds(treeData.value)
    activeNodes.value = activeNodes.value.filter(nodeId => !emptyGroupIds.has(nodeId))
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

// 右键菜单处理
const handleRightClick = (event: { node: GetUserRequestTreeVo, x: number, y: number }) => {
    rightMenuNode.value = event.node
    rightMenuX.value = event.x
    rightMenuY.value = event.y
    rightMenuVisible.value = true
}

const handleRightMenuClose = () => {
    rightMenuVisible.value = false
}

// 创建组相关方法
const showCreateGroupDialog = () => {
    createGroupForm.value = {
        parentId: null,
        name: ''
    }
    createGroupDialogVisible.value = true
}

// 根级拖拽处理
const handleRootDragEnter = (event: DragEvent) => {
    if (!event.dataTransfer) return
    event.preventDefault()
    event.stopPropagation()
    rootDragCounter++
    if (rootDragCounter === 1) {
        isRootDragOver.value = true
    }
}

const handleRootDragOver = (event: DragEvent) => {
    if (!event.dataTransfer) return
    event.preventDefault()
    event.stopPropagation()
    event.dataTransfer.dropEffect = 'move'
}

const handleRootDragLeave = () => {
    rootDragCounter--
    if (rootDragCounter <= 0) {
        isRootDragOver.value = false
        rootDragCounter = 0
    }
}

const handleRootDrop = async (event: DragEvent) => {
    if (!event.dataTransfer) return
    event.preventDefault()
    event.stopPropagation()
    isRootDragOver.value = false
    rootDragCounter = 0

    try {
        const jsonData = event.dataTransfer.getData('application/json')
        if (!jsonData) return
        const dragData = JSON.parse(jsonData) as { id: string, parentId: string | null, type: number, name: string }
        // 已在根级则忽略
        if (dragData.parentId === null) return

        // 计算根级新序号
        const rootItems = treeData.value.filter(item => !item.parentId)
        const newSeq = rootItems.length + 1

        const editDto = {
            id: dragData.id,
            parentId: null,
            type: dragData.type,
            name: dragData.name,
            seq: newSeq
        }
        await UserRequestTreeApi.editUserRequestTree(editDto)
        ElMessage.success('已移动到根级别')
        await loadUserRequestTree()
    } catch (error:any) {
        ElMessage.error(error?.message || '移动失败')
    }
}

// 全局拖拽：允许在 tag-tree-body 之外任意位置放入根
const onDocumentDragOver = (event: DragEvent) => {
    if (!event.dataTransfer) return
    const bodyEl = tagTreeBodyRef.value
    const target = event.target as Node | null
    const outside = bodyEl ? (target ? !bodyEl.contains(target) : true) : true
    if (outside) {
        event.preventDefault()
        event.stopPropagation()
        event.dataTransfer.dropEffect = 'move'
        if (!isRootDragOver.value) {
            isRootDragOver.value = true
        }
    }
}

const onDocumentDrop = async (event: DragEvent) => {
    const bodyEl = tagTreeBodyRef.value
    const target = event.target as Node | null
    const outside = bodyEl ? (target ? !bodyEl.contains(target) : true) : true
    if (!outside) return
    if (!event.dataTransfer) return
    event.preventDefault()
    event.stopPropagation()
    isRootDragOver.value = false
    rootDragCounter = 0
    try {
        const jsonData = event.dataTransfer.getData('application/json')
        if (!jsonData) return
        const dragData = JSON.parse(jsonData) as { id: string, parentId: string | null, type: number, name: string }
        if (dragData.parentId === null) return
        const rootItems = treeData.value.filter(item => !item.parentId)
        const newSeq = rootItems.length + 1
        const editDto = {
            id: dragData.id,
            parentId: null,
            type: dragData.type,
            name: dragData.name,
            seq: newSeq
        }
        await UserRequestTreeApi.editUserRequestTree(editDto)
        ElMessage.success('已移动到根级别')
        await loadUserRequestTree()
    } catch (error:any) {
        ElMessage.error(error?.message || '移动失败')
    }
}

const handleCreateGroup = async () => {
    if (!createGroupFormRef.value) return
    
    try {
        const valid = await createGroupFormRef.value.validate()
        if (!valid) return
        
        createGroupLoading.value = true
        await UserRequestGroupApi.addUserRequestGroup(createGroupForm.value)
        
        ElMessage.success('创建组成功')
        createGroupDialogVisible.value = false
        
        // 重新加载数据
        await loadUserRequestTree()
        
    } catch (error: any) {
        ElMessage.error(error.message || '创建组失败')
    } finally {
        createGroupLoading.value = false
    }
}



onMounted(() => {
    loadUserRequestTree()
    document.addEventListener('dragover', onDocumentDragOver)
    document.addEventListener('drop', onDocumentDrop)
})

onUnmounted(() => {
    document.removeEventListener('dragover', onDocumentDragOver)
    document.removeEventListener('drop', onDocumentDrop)
})
defineExpose({
    loadUserRequestTree
})
</script>

<style scoped>
.tag-tree-container {
    height: 100%;
    background: linear-gradient(135deg, #f8f9fa 0%, #ffffff 100%);
    box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
    border: 1px solid #e9ecef;
}

.tag-tree-body {
    border-top: 1px solid rgba(64, 158, 255, 0.2);
    overflow-y: auto;
    height: 100%;
    padding-top: 4px;
    position: relative;
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

.tag-tree-search {
    padding: 10px;
    display: flex;
    flex-direction: row;
    align-items: center;
    justify-content: space-around;
    gap: 5px;
    position: sticky;
    top: 0;
    background: linear-gradient(135deg, #f8f9fa 0%, #ffffff 100%);
    z-index: 1000;
}
.el-button+.el-button{
    margin-left: 0;
}

/* 根级拖拽样式 */
.root-drag-over {
    background: linear-gradient(135deg, #e8f4fd 0%, #d4edda 100%) !important;
    border: 2px dashed #28a745 !important;
    border-radius: 8px !important;
    position: relative;
}

.root-drop-hint {
    position: absolute;
    top: 25px;
    left: 50%;
    transform: translateX(-50%);
    background: #28a745;
    color: white;
    text-align: center;
    padding: 6px 12px;
    border-radius: 6px;
    font-size: 14px;
    font-weight: 500;
    z-index: 99999;
    box-shadow: 0 2px 8px rgba(40, 167, 69, 0.3);
    pointer-events: none;
    white-space: nowrap;
}
</style>