<template>
    <div class="order-book">

        <div class="order-book-header">


        </div>

        <el-empty v-if="!data" description="No data" />

        <div class="order-book-content" v-show="data">

            <div class="order-book-sell">
            <div class="order-book-sell-header">
                ASK 
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
                BID
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
import { ref, onMounted, watch } from 'vue'
import type { GetLobDepthVo } from '@/commons/api/ConsoleLobApi.ts'
import ConsoleLobApi from '@/commons/api/ConsoleLobApi.ts'

const props = defineProps<{
    symbol: string
}>()

watch(() => props.symbol, async () => {
    await loadOrderBook()
})

const data = ref<GetLobDepthVo | null>(null)

const loadOrderBook = async () => {
    if (!props.symbol) {
        return
    }
    const ret = await ConsoleLobApi.getLobDepth({ 
            symbol: props.symbol, 
            limit: 5000 
    })
    data.value = ret
}

onMounted(() => {
    loadOrderBook()
})

</script>

<style scoped>

</style>