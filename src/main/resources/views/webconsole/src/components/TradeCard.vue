
<template>
    <div :class="['trade-card', side === 'buy' ? 'buy-card' : 'sell-card']">
        <div class="trade-card-header"> 
            <span class="trade-card-header-text">{{ side === 'buy' ? 'Buy' : 'Sell' }} {{ symbol }}</span>
            <el-tag :type="side === 'buy' ? 'success' : 'danger'">{{ side.toUpperCase() }}</el-tag>
        </div>
        <div class="trade-card-content">
            <el-form :model="details" :rules="rules" label-position="top" size="small" ref="formRef">
                <el-form-item prop="kind" style="text-align: center;margin-bottom: 0px;padding-bottom: 0px;"> 
                    <el-select v-model="details.kind" placeholder="Select Kind">
                        <el-option label="Limit Order" value="limit" />
                        <el-option label="Market Order" value="market" />
                    </el-select>
                </el-form-item>
                <el-form-item prop="price" v-if="details.kind === 'limit'" style="text-align: center;margin-bottom: 0px;padding-bottom: 8px;">
                    <div class="trade-card-content-label">Price</div>
                    <el-input v-model="details.price" :formatter="formatDecimal" :parser="parseDecimal" placeholder="Price"/>
                </el-form-item>
                <el-form-item prop="quantity" style="text-align: center;margin-bottom: 15px;padding-bottom: 8px;">
                    <div class="trade-card-content-label">Qty</div>
                    <el-input v-model="details.quantity" :formatter="formatDecimal" :parser="parseDecimal" placeholder="qty" />
                </el-form-item>
                <el-form-item>
                    <el-button type="primary" @click="createOrder" :class="side === 'buy' ? 'buy-button' : 'sell-button'">Take Order</el-button>
                </el-form-item>
            </el-form>
        </div>
    </div>
</template>

<script setup lang="ts">
import { ref } from 'vue';
import type { FormInstance } from 'element-plus';
import ConsoleOrderApi from '@/commons/api/ConsoleOrderApi';
import type { CreateOrderDto } from '@/commons/api/ConsoleOrderApi';
import { ElMessage } from 'element-plus';

//表单ref
const formRef = ref<FormInstance>()

const props = defineProps<{
    symbol: string
    side: 'buy' | 'sell'
}>()

//表单数据
const details = ref<any>({
    kind: 'limit',
    price: '',
    quantity: ''
})

//表单校验规则
const rules = {
    price: [
        { required: true, message: 'Please input price', trigger: 'blur' }
    ],
    quantity: [
        { required: true, message: 'Please input qty', trigger: 'blur' }
    ]
}

//下单
const createOrder = async () => {

    const valid = await formRef.value?.validate()

    if (!valid) {
        return
    }

    var data: CreateOrderDto = {
        symbol: props.symbol,
        side: props.side.toUpperCase(),
        price: details.value.kind === 'limit' ? details.value.price.toString() : null,
        quantity: details.value.quantity.toString(),
        quantityQuote: details.value.kind === 'market' && props.side === 'buy' ? details.value.quantity.toString() : '',
        kind: details.value.kind.toUpperCase(),
        timeInForce: 'GTC'
    }

    try {
        const ret = await ConsoleOrderApi.createOrder(data)
        ElMessage.success(ret.message)
    } catch (error: any) {
        ElMessage.error(error.message)
    }
    
  
}

const parseDecimal = (value: string) => {
    if (!value) {
        return '';
    }

    let cleanedValue = String(value).replace(/[^0-9.]/g, '');

    const parts = cleanedValue.split('.');
    if (parts.length > 2) {
        cleanedValue = parts[0] + '.' + parts.slice(1).join('');
    }

    return cleanedValue;
}

//处理用户输入的数字，限制最多16位小数
const formatDecimal = (value: string): string => {
    const valueStr = parseDecimal(String(value));

    if (valueStr === '') {
        return '';
    }

    if (isNaN(parseFloat(valueStr)) && !valueStr.endsWith('.')) {
        return value;
    }

    let [integer, decimal] = valueStr.split('.');

    integer = integer.replace(/\B(?=(\d{3})+(?!\d))/g, ',');

    if (decimal === undefined) {
        return integer;
    }

    if (valueStr.endsWith('.')) {
        return integer + '.';
    }

    const formattedDecimal = decimal.replace(/0+$/, '');

    if (formattedDecimal) {
        return integer + '.' + formattedDecimal;
    }

    return integer;
}


</script>

<style scoped>
.trade-card {
  border-radius: 8px;
  border: 1px solid #e4e7ed;
  background: #fff;
  overflow: hidden;
  border-radius: 0px;
  display: flex;
  flex-direction: column;
}

.trade-card-header {
  padding: 16px;
  background: #f8f9fa;
  border-bottom: 1px solid #e4e7ed;
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-shrink: 0;
}

.trade-card-header-text {
  font-weight: 600;
  font-size: 16px;
  color: #303133;
}

.trade-card-content {
  flex: 1;
  padding: 20px;
  overflow-y: auto;
}

.trade-card-content :deep(.el-form-item) {
  margin-bottom: 20px;
}

.trade-card-content :deep(.el-form-item__label) {
  font-weight: 500;
  color: #606266;
  font-size: 14px;
}

.trade-card-content :deep(.el-select) {
  width: 100%;
}

.trade-card-content :deep(.el-button) {
  width: 100%;
  height: 40px;
  font-weight: 600;
  font-size: 14px;
}

/* 买入卡片样式 */
.trade-card.buy-card {
  border-color: #67c23a;
}

.trade-card.buy-card .trade-card-header {
  background: rgba(103, 194, 58, 0.1);
  border-bottom-color: #67c23a;
}

.trade-card.buy-card .trade-card-header-text {
  color: #67c23a;
}

/* 卖出卡片样式 */
.trade-card.sell-card {
  border-color: #f56c6c;
}

.trade-card.sell-card .trade-card-header {
  background: rgba(245, 108, 108, 0.1);
  border-bottom-color: #f56c6c;
}

.trade-card.sell-card .trade-card-header-text {
  color: #f56c6c;
}

/* 输入框样式优化 */
.trade-card-content :deep(.el-input-number) {
  width: 100%;
}

.trade-card-content :deep(.el-input-number .el-input__inner) {
  text-align: left;
}

/* 按钮hover效果 */
.trade-card-content :deep(.el-button--success:hover) {
  background-color: #5daf34;
  border-color: #5daf34;
}

.trade-card-content :deep(.el-button--danger:hover) {
  background-color: #f45656;
  border-color: #f45656;
}

.buy-button {
    color: #67c23a;
    background-color: #f0f9eb;
    border-color: #67c23a;
}

.sell-button {
    color: #f56c6c;
    background-color: #fef0f0;
    border-color: #f56c6c;
}

.trade-card-content-label {
    font-size: 12px;
    color: #999;
    text-align: left;
    width: 100%;
    z-index: 1000;
    pointer-events: none;
}
</style>