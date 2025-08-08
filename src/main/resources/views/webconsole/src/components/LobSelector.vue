<template>
    <div class="lob-selector">
        <div class="lob-header">
            Available Symbols({{ filteredLobList.length }})
        </div>
        <div class="lob-search-container">
            <input class="lob-search" v-model="search" placeholder="Search lob" />
            <el-button class="lob-search-refresh" type="primary" @click="loadAvailableLobList" size="small">Refresh({{ autoRefreshRemainingTime }})</el-button>
        </div>
        <div class="lob-list" style="margin-top: 10px;">
            <div class="lob-item" v-for="item in filteredLobList" :key="item.symbol" @click="onSelect(item.symbol)" :class="{ 'lob-item-active': item.symbol === selectedSymbol }">
                <div class="lob-item-symbol">{{ item.symbol }}</div>
                <div class="lob-item-status">
                    <el-tag class="lob-item-status-tag" :type="item.status === 'READY' ? 'success' : 'danger'" size="small" >{{ item.status }}</el-tag>
                </div>
            </div>
        </div>
    </div>
</template>

<script setup lang="ts">
import { onMounted, ref, computed, onUnmounted, watch } from 'vue';
import ConsoleLobApi from '@/commons/api/ConsoleLobApi.ts'
import type { GetAvailableLobListVo } from '@/commons/api/ConsoleLobApi.ts'


const availableLobList = ref<GetAvailableLobListVo[]>([])
const search = ref('')
const selectedSymbol = ref<string | null>(null)

//自动刷新剩余时间
const autoRefreshRemainingTime = ref<number>(0)
const autoRefreshTimer = ref<any>(null)

const emit = defineEmits<{
  select: [symbol: string]  // 选择lob
  close: [symbol: string]   // lob被关闭时触发
  persist: [symbol: string] // 持久化lob
}>()

const filteredLobList = computed(() => {
    if (!search.value) {
        return availableLobList.value;
    }
    return availableLobList.value.filter(item =>
        item.symbol.toLowerCase().includes(search.value.toLowerCase())
    );
});


// 加载可用的lob列表
const loadAvailableLobList = async () => {
    const ret = await ConsoleLobApi.getAvailableLobList()

    //若lob之前是ready状态则需要抛出close事件
    const readyLobList = availableLobList.value.filter(item => item.status === 'READY')
    readyLobList.forEach(item => {
        emit('close', item.symbol)
    })

    //更新lob列表
    availableLobList.value = ret
}

onMounted(() => {
    loadAvailableLobList()
    installAutoRefresh()
    const savedSymbol = localStorage.getItem('selectedTradingSymbol')
    if (savedSymbol) {
        selectedSymbol.value = savedSymbol
        emit('select', savedSymbol)
    }
})

onUnmounted(() => {
    uninstallAutoRefresh()
})


const onSelect = (symbol: string) => {
    selectedSymbol.value = symbol
    emit('select', symbol)
    localStorage.setItem('selectedTradingSymbol', symbol)
}


watch(autoRefreshRemainingTime, (newVal) => {
    if (newVal === 0) {
        loadAvailableLobList()
    }
})


const installAutoRefresh = () => {
    //如果已经安装了自动刷新，则先卸载
    if (autoRefreshTimer.value) {
        uninstallAutoRefresh()
        return
    }
    //安装自动刷新
    autoRefreshRemainingTime.value = 3
    autoRefreshTimer.value = setInterval(() => {
        if (autoRefreshRemainingTime.value > 1) {
            autoRefreshRemainingTime.value--
            return
        }
        loadAvailableLobList()
        autoRefreshRemainingTime.value = 3
    }, 1000)
}


const uninstallAutoRefresh = () => {
    if (autoRefreshTimer.value) {
        clearInterval(autoRefreshTimer.value)
    }
}


</script>

<style scoped>
.lob-selector {
    height: 100%;
    background-color: #fff;
    padding: 10px;
    border: 1px solid #e4e7ed;
}

.lob-header {
    font-size: 16px;
    font-weight: bold;
    margin-bottom: 10px;
}

.lob-search-refresh {
    margin-left: 10px;
    border-radius: 0px;
}



.lob-search-refresh {
    height: 25px;
}

.lob-search {
    height: 25px;
    border: 1px solid #e4e7ed;
    padding: 0 10px;
    border-radius: 0px;
    outline: none;
}

.lob-search-container {
    display: flex;
    justify-content: space-between;
    align-items: center;
}


.lob-list {
    height: 100%;
    overflow-y: auto;
}

.lob-item {
    height: 16px;
    padding: 8px 12px;
    cursor: pointer;
    display: flex;
    justify-content: space-between;
    align-items: center;
    background-color: #ffffff;
    border-bottom: 1px solid #f0f0f0;
    font-size: 12px;
    border-top: 2px solid transparent;
}

.lob-item:hover {
    transition: all 0.2s ease;
    background-color: #f5f5f5;
    border-top: 2px solid #67C23A;
}

.lob-item-active {
    background-color: #f0f9ff;
    color: #16a085;
    border-top: 2px solid #67C23A;
    font-weight: 500;
}

.lob-item-active:hover {
    background-color: #f0f9ff;
}

.lob-item-status-tag {
    margin-left: 10px;
    margin-right: 20px;
    font-size: 10px;
    font-weight: 500;
    border-radius: 0px;
    color: #606266;
}
</style>