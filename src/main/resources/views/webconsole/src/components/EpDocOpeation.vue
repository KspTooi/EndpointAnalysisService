<template>
    <div class="operation-container">

        <div class="operation-header">
            <div class="operation-header-title">
                {{ operationDetails?.summary }}
            </div>
            <div class="operation-header-description">
                {{ operationDetails?.description }}
            </div>
            <div class="operation-header-path-content">
                <div class="oh-path-method" :data-method="operationDetails?.method">
                    {{ operationDetails?.method }}
                </div>
                <div class="oh-path-path">
                    {{ operationDetails?.path }}
                </div>
            </div>
        </div>

        <div class="operation-body">
            <div class="operation-tab">
                <div class="operation-tab-item" :class="{ active: activeTab === 'params' }" @click="activeTab = 'params'">
                    请求参数
                </div>
                <div class="operation-tab-item" :class="{ active: activeTab === 'body' }" @click="activeTab = 'body'">
                    载荷
                </div>
            </div>

            <div class="operation-content">
                <div v-if="activeTab === 'params'" class="tab-content">
                    <div class="content-section">
                        <h4 class="section-title">请求参数</h4>
                        <div class="param-table" v-if="operationDetails?.reqBody?.params?.length">
                            <div class="param-header">
                                <div class="param-col-name">参数名</div>
                                <div class="param-col-type">类型</div>
                                <div class="param-col-required">必填</div>
                                <div class="param-col-desc">描述</div>
                            </div>
                            <div class="param-row" v-for="param in operationDetails.reqBody.params" :key="param.name">
                                <div class="param-col-name">
                                    <span class="param-name">{{ param.name }}</span>
                                </div>
                                <div class="param-col-type">
                                    <span class="param-type">{{ param.type }}{{ param.isArray ? '[]' : '' }}</span>
                                </div>
                                <div class="param-col-required">
                                    <span class="param-required" :class="{ required: param.required }">
                                        {{ param.required ? '是' : '否' }}
                                    </span>
                                </div>
                                <div class="param-col-desc">
                                    <span class="param-desc">{{ param.description || '-' }}</span>
                                </div>
                            </div>
                        </div>
                        <div v-else class="empty-content">
                            暂无请求参数
                        </div>
                    </div>

                    <div class="content-section">
                        <h4 class="section-title">响应参数</h4>
                        <div class="param-table" v-if="operationDetails?.resBody?.params?.length">
                            <div class="param-header">
                                <div class="param-col-name">参数名</div>
                                <div class="param-col-type">类型</div>
                                <div class="param-col-required">必填</div>
                                <div class="param-col-desc">描述</div>
                            </div>
                            <div class="param-row" v-for="param in operationDetails.resBody.params" :key="param.name">
                                <div class="param-col-name">
                                    <span class="param-name">{{ param.name }}</span>
                                </div>
                                <div class="param-col-type">
                                    <span class="param-type">{{ param.type }}{{ param.isArray ? '[]' : '' }}</span>
                                </div>
                                <div class="param-col-required">
                                    <span class="param-required" :class="{ required: param.required }">
                                        {{ param.required ? '是' : '否' }}
                                    </span>
                                </div>
                                <div class="param-col-desc">
                                    <span class="param-desc">{{ param.description || '-' }}</span>
                                </div>
                            </div>
                        </div>
                        <div v-else class="empty-content">
                            暂无响应参数
                        </div>
                    </div>
                </div>

                <div v-if="activeTab === 'body'" class="tab-content">
                    <div class="content-section">
                        <h4 class="section-title">请求载荷结构</h4>
                        <div class="body-schema" v-if="operationDetails?.reqBody?.schemaName">
                            <div class="schema-name">{{ operationDetails.reqBody.schemaName }}</div>
                            <div class="schema-content">
                                <pre>{{ formatBodySchema(operationDetails.reqBody) }}</pre>
                            </div>
                        </div>
                        <div v-else class="empty-content">
                            暂无请求载荷
                        </div>
                    </div>

                    <div class="content-section">
                        <h4 class="section-title">响应载荷结构</h4>
                        <div class="body-schema" v-if="operationDetails?.resBody?.schemaName">
                            <div class="schema-name">{{ operationDetails.resBody.schemaName }}</div>
                            <div class="schema-content">
                                <pre>{{ formatBodySchema(operationDetails.resBody) }}</pre>
                            </div>
                        </div>
                        <div v-else class="empty-content">
                            暂无响应载荷
                        </div>
                    </div>
                </div>
            </div>
        </div>

    </div>
</template>

<script setup lang="ts">
import EpDocOperationApi, { type GetEpDocOperationDetailsVo, type BodySchema } from '@/api/EpDocOperationApi';
import { onMounted, ref, watch } from 'vue'

const props = defineProps<{
    operationId: string | undefined
}>()

const operationDetails = ref<GetEpDocOperationDetailsVo>()
const activeTab = ref<'params' | 'body'>('params')


const loadOperation = async () => {
    if (!props.operationId) {
        return
    }
    const res = await EpDocOperationApi.getEpDocOperationDetails({
        id: props.operationId
    })
    operationDetails.value = res
}

const formatBodySchema = (schema: BodySchema): string => {
    if (!schema || !schema.params) {
        return '{}'
    }
    
    const formatParams = (params: any[], indent = 0): string => {
        const spaces = '  '.repeat(indent)
        let result = ''
        
        params.forEach((param, index) => {
            const isLast = index === params.length - 1
            const paramType = param.isArray ? `${param.type}[]` : param.type
            
            if (param.schema && param.schema.params) {
                result += `${spaces}  "${param.name}": {\n`
                result += formatParams(param.schema.params, indent + 2)
                result += `${spaces}  }${isLast ? '' : ','}\n`
            } else {
                const comment = param.description ? ` // ${param.description}` : ''
                result += `${spaces}  "${param.name}": ${paramType}${isLast ? '' : ','}${comment}\n`
            }
        })
        
        return result
    }
    
    return `{\n${formatParams(schema.params)}}`
}


//监听operationId的变化
watch(() => props.operationId, (newVal) => {
    loadOperation()
})

onMounted(() => {
    loadOperation()
})

</script>

<style scoped>
.operation-container {
    background: #fafafa;
    padding: 24px;
    transition: all 0.3s ease;
    border-left: 1px solid #e6e6e6;
}

.operation-container:hover {
    box-shadow: 0 4px 16px rgba(0, 0, 0, 0.15);
}

.operation-header {
    display: flex;
    flex-direction: column;
    gap: 6px;
}

.operation-header-title {
    font-size: 20px;
    font-weight: 600;
    color: #444444;
    margin: 0;
}

.operation-header-description {
    font-size: 16px;
    color: #666666;
    line-height: 1.5;
    margin: 0;
    white-space: pre-wrap;
}

.operation-header-path-content {
    display: flex;
    align-items: center;
    gap: 12px;
    padding: 12px 16px;
    background: #f8f9fa;
    border-radius: 5px;
    border: 1px solid #dbdbdb;
    font-family: 'Consolas', 'Monaco', 'Courier New', monospace;
}

.oh-path-method {
    font-size: 14px;
    font-weight: 700;
    padding: 4px 12px;
    border-radius: 6px;
    text-transform: uppercase;
    letter-spacing: 0.5px;
    min-width: 60px;
    text-align: center;
    color: #ffffff;
    background: #6c757d;
}

.oh-path-method[data-method="GET"] {
    background: #28a745;
}

.oh-path-method[data-method="POST"] {
    background: #007bff;
}

.oh-path-method[data-method="PUT"] {
    background: #ffc107;
    color: #212529;
}

.oh-path-method[data-method="DELETE"] {
    background: #dc3545;
}

.oh-path-method[data-method="PATCH"] {
    background: #6f42c1;
}

.oh-path-path {
    font-size: 16px;
    color: #495057;
    font-weight: 500;
    word-break: break-all;
    flex: 1;
}

.operation-body {
    margin-top: 20px;
}

.operation-tab {
    display: flex;
    border-bottom: 1px solid #e0e0e0;
    margin-bottom: 20px;
}

.operation-tab-item {
    padding: 12px 24px;
    cursor: pointer;
    border-bottom: 2px solid transparent;
    color: #666666;
    font-weight: 500;
    transition: all 0.3s ease;
}

.operation-tab-item:hover {
    color: #1976d2;
    background-color: #f5f5f5;
}

.operation-tab-item.active {
    color: #1976d2;
    border-bottom-color: #1976d2;
    background-color: #f8f9ff;
}

.operation-content {
    min-height: 300px;
}

.tab-content {
    display: flex;
    flex-direction: column;
    gap: 24px;
}

.content-section {
    background: #ffffff;
    border: 1px solid #e0e0e0;
    border-radius: 6px;
    overflow: hidden;
}

.section-title {
    background: #f8f9fa;
    color: #333333;
    font-size: 16px;
    font-weight: 600;
    margin: 0;
    padding: 12px 16px;
    border-bottom: 1px solid #e0e0e0;
}

.param-table {
    display: flex;
    flex-direction: column;
}

.param-header {
    display: grid;
    grid-template-columns: 200px 120px 80px 1fr;
    background: #fafafa;
    border-bottom: 1px solid #e0e0e0;
    font-weight: 600;
    color: #333333;
}

.param-header > div {
    padding: 12px 16px;
    border-right: 1px solid #e0e0e0;
}

.param-header > div:last-child {
    border-right: none;
}

.param-row {
    display: grid;
    grid-template-columns: 200px 120px 80px 1fr;
    border-bottom: 1px solid #f0f0f0;
}

.param-row:last-child {
    border-bottom: none;
}

.param-row:hover {
    background-color: #f9f9f9;
}

.param-row > div {
    padding: 12px 16px;
    border-right: 1px solid #f0f0f0;
    display: flex;
    align-items: center;
}

.param-row > div:last-child {
    border-right: none;
}

.param-name {
    font-family: 'Consolas', 'Monaco', 'Courier New', monospace;
    font-weight: 500;
    color: #333333;
}

.param-type {
    background: #e3f2fd;
    color: #1976d2;
    padding: 2px 8px;
    border-radius: 4px;
    font-size: 12px;
    font-weight: 500;
    font-family: 'Consolas', 'Monaco', 'Courier New', monospace;
}

.param-required {
    font-size: 12px;
    font-weight: 500;
}

.param-required.required {
    color: #d32f2f;
}

.param-desc {
    color: #666666;
    font-size: 14px;
}

.body-schema {
    padding: 16px;
}

.schema-name {
    font-size: 14px;
    font-weight: 600;
    color: #333333;
    margin-bottom: 12px;
    font-family: 'Consolas', 'Monaco', 'Courier New', monospace;
}

.schema-content {
    background: #f8f9fa;
    border: 1px solid #e0e0e0;
    border-radius: 4px;
    padding: 16px;
}

.schema-content pre {
    margin: 0;
    font-family: 'Consolas', 'Monaco', 'Courier New', monospace;
    font-size: 13px;
    line-height: 1.5;
    color: #333333;
    white-space: pre-wrap;
    word-wrap: break-word;
}

.empty-content {
    padding: 40px;
    text-align: center;
    color: #999999;
    font-size: 14px;
}


</style>