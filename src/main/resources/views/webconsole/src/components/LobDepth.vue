<template>
  <div class="order-book">

    <div class="order-book-header">
      <div>
        Lob Depth - {{ symbol }}
      </div>
      <div style="display: flex; align-items: center; gap: 10px;">
        <el-select class="precision-select" v-model="currentPrecision" placeholder="precision" size="small" @change="loadOrderBook" clearable disabled>
          <el-option v-for="item in precisionList" :key="item" :value="item">{{ item }}</el-option>
        </el-select>
        <el-button class="order-book-refresh" type="primary" size="small" @click="loadOrderBook">Refresh({{ autoRefreshRemainingTime }})</el-button>
      </div>
    </div>

    <el-empty v-if="!data" description="No data" />

    <div class="order-book-content" v-show="data">

      <div class="order-book-sell">
        <div class="order-book-sell-header">
          ASK1 <span class="order-book-sell-header-price">{{ ask1Price }}</span>
        </div>
        <div class="order-book-sell-list">
          <div class="order-book-sell-item" v-for="item in data?.asks" :key="item.p">
            <div class="order-book-sell-item-price">{{ item.p }}</div>
            <div class="order-book-sell-item-quantity">{{ item.q }}</div>
          </div>
        </div>
      </div>
      <div class="order-book-buy">
        <div class="order-book-buy-header">
          BID1 <span class="order-book-buy-header-price">{{ bid1Price }}</span>
        </div>
        <div class="order-book-buy-list">
          <div class="order-book-buy-item" v-for="item in data?.bids" :key="item.p">
            <div class="order-book-buy-item-price">{{ item.p }}</div>
            <div class="order-book-buy-item-quantity">{{ item.q }}</div>
          </div>
        </div>
      </div>

    </div>

  </div>
</template>

<script setup lang="ts">
import {onMounted, ref, watch, onUnmounted} from 'vue'
import type {GetLobDepthVo} from '@/commons/api/ConsoleLobApi.ts'
import ConsoleLobApi from '@/commons/api/ConsoleLobApi.ts'

//自动刷新剩余时间
const autoRefreshRemainingTime = ref<number>(0)
const autoRefreshTimer = ref<any>(null)

//精度列表
const precisionList = ref<string[]>([])
const currentPrecision = ref<string | null>(null)

const ask1Price = ref<string>('')
const bid1Price = ref<string>('')

const props = defineProps<{
  symbol: string
}>()

watch(() => props.symbol, async () => {
  await loadOrderBook()
  await loadPrecisionList()
})

const data = ref<GetLobDepthVo | null>(null)

const loadOrderBook = async () => {
  if (!props.symbol) {
    return
  }


  data.value = await ConsoleLobApi.getLobDepth({
    symbol: props.symbol,
    limit: 5000,
    precision: currentPrecision.value || null
  })

  //更新ASK1
  ask1Price.value = data.value?.asks[0].p || ''
  //更新BID1
  bid1Price.value = data.value?.bids[0].p || ''
  await loadPrecisionList()
}

const loadPrecisionList = async () => {
  const ret = await ConsoleLobApi.getPrecision({
    symbol: props.symbol,
    limit: 5000
  })
  precisionList.value = ret.bidPrecisionList
}

onMounted(() => {
  loadOrderBook()
  installAutoRefresh()
})

onUnmounted(() => {
  uninstallAutoRefresh()
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
        loadOrderBook()
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

.order-book {
  height: 100%;
  display: flex;
  flex-direction: column;
  border: 1px solid #e4e7ed;
  border-radius: 0px;
  background: #fff;
  overflow: hidden;
}

.order-book-header {
  padding: 8px;
  background: linear-gradient(135deg, #f5f7fa 0%, #e8ecf0 100%);
  border-bottom: 1px solid #e4e7ed;
  font-weight: 600;
  font-size: 15px;
  color: #303133;
  flex-shrink: 0;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.order-book-refresh {
  border-radius: 0px;
}

.order-book-content {
  flex: 1;
  display: flex;
  overflow: hidden;
}

.order-book-sell {
  flex: 1;
  display: flex;
  flex-direction: column;
  border-right: 1px solid #e4e7ed;
}

.order-book-sell-header {
  padding: 6px 12px;
  background: rgba(245, 108, 108, 0.1);
  border-bottom: 1px solid #f56c6c;
  color: #f56c6c;
  font-weight: 500;
  font-size: 14px;
  text-align: center;
  display: flex;
  justify-content: space-between;
}

.order-book-sell-list {
  flex: 1;
  overflow-y: auto;
  padding: 4px;
}

.order-book-sell-item {
  display: flex;
  justify-content: space-between;
  padding: 4px 8px;
  font-size: 12px;
  transition: background 0.2s;
}

.order-book-sell-item:hover {
  background: rgba(245, 108, 108, 0.05);
}

.order-book-sell-item-price {
  color: #f56c6c;
  font-family: 'Courier New', monospace;
}

.order-book-sell-item-quantity {
  color: #606266;
  font-family: 'Courier New', monospace;
}

.order-book-buy {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.order-book-buy-header {
  padding: 6px 12px;
  background: rgba(103, 194, 58, 0.1);
  border-bottom: 1px solid #67c23a;
  color: #67c23a;
  font-weight: 500;
  font-size: 14px;
  text-align: center;
  display: flex;
  justify-content: space-between;
}

.order-book-buy-list {
  flex: 1;
  overflow-y: auto;
  padding: 4px;
}

.order-book-buy-item {
  display: flex;
  justify-content: space-between;
  padding: 4px 8px;
  font-size: 12px;
  transition: background 0.2s;
}

.order-book-buy-item:hover {
  background: rgba(103, 194, 58, 0.05);
}

.order-book-buy-item-price {
  color: #67c23a;
  font-family: 'Courier New', monospace;
}

.order-book-buy-item-quantity {
  color: #606266;
  font-family: 'Courier New', monospace;
}

.order-book-sell-list::-webkit-scrollbar,
.order-book-buy-list::-webkit-scrollbar {
  width: 6px;
}

.order-book-sell-list::-webkit-scrollbar-track,
.order-book-buy-list::-webkit-scrollbar-track {
  background: #f1f1f1;
  border-radius: 3px;
}

.order-book-sell-list::-webkit-scrollbar-thumb,
.order-book-buy-list::-webkit-scrollbar-thumb {
  background: #c1c1c1;
  border-radius: 3px;
}

.order-book-sell-list::-webkit-scrollbar-thumb:hover,
.order-book-buy-list::-webkit-scrollbar-thumb:hover {
  background: #a8a8a8;
}

.precision-select {
  width: 72px;
  border-radius: 0px!important;
  margin-top: 0px!important;
  margin-bottom: 0px!important;
  margin-left: 10px!important;
  margin-right: 0px!important;
  padding-top: 0px!important;
  padding-bottom: 0px!important;
  padding-left: 0px!important;
  padding-right: 0px!important;
}

</style>