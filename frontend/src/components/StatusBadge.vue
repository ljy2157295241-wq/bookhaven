<template>
  <span class="badge" :class="'badge-' + cls">{{ label }}</span>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({ status: String })

const map = {
  PENDING_PAY: ['warning', '待支付'],
  PAID: ['info', '已支付'],
  SHIPPED: ['primary', '已发货'],
  DELIVERED: ['success', '已送达'],
  CANCELLED: ['secondary', '已取消'],
  REFUNDING: ['danger', '退款中'],
  REFUNDED: ['danger', '已退款'],
  ON_SHELF: ['success', '在售'],
  OFF_SHELF: ['secondary', '下架'],
  PENDING: ['warning', '待处理'],
  SUCCESS: ['success', '成功'],
  FAILED: ['danger', '失败'],
}

const m = computed(() => map[props.status] || ['secondary', props.status])
const cls = computed(() => m.value[0])
const label = computed(() => m.value[1])
</script>

<style scoped>
.badge{display:inline-block;padding:2px 10px;border-radius:20px;font-size:12px;font-weight:500}
.badge-success{background:#d4edda;color:#155724}
.badge-danger{background:#f8d7da;color:#721c24}
.badge-warning{background:#fff3cd;color:#856404}
.badge-info{background:#d1ecf1;color:#0c5460}
.badge-secondary{background:#e2e3e5;color:#383d41}
.badge-primary{background:#cce5ff;color:#004085}
</style>
