<template>
  <div>
    <router-link to="/orders" class="btn btn-ghost" style="margin-bottom:16px">← 返回订单列表</router-link>
    <div v-if="loading" class="loading"><span class="spinner"></span>加载订单详情...</div>
    <div v-else style="display:grid;grid-template-columns:1fr 320px;gap:24px">
      <div>
        <div class="section-card" style="margin-bottom:16px">
          <h3 style="margin-bottom:12px">订单信息</h3>
          <div class="info-grid">
            <span class="info-label">订单编号</span>
            <span class="mono">{{ order.orderNo }}</span>
            <span class="info-label">下单时间</span>
            <span>{{ formatDate(order.createTime) }}</span>
            <span class="info-label">订单状态</span>
            <span><StatusBadge :status="order.status" /></span>
            <span class="info-label">总金额</span>
            <span class="total-amount">¥{{ order.totalAmount.toFixed(2) }}</span>
          </div>
        </div>
        <div class="section-card">
          <h3 style="margin-bottom:12px">商品明细</h3>
          <div v-for="i in items" :key="i.id" class="order-item">
            <div class="order-item-cover">
              <img v-if="i.bookCover" :src="i.bookCover" />
              <span v-else>📕</span>
            </div>
            <div class="order-item-info">
              <div class="order-item-title">{{ i.bookTitle }}</div>
              <div class="order-item-meta">¥{{ i.bookPrice }} × {{ i.quantity }}</div>
            </div>
            <span class="order-item-total">¥{{ (i.bookPrice * i.quantity).toFixed(2) }}</span>
          </div>
        </div>
      </div>
      <div>
        <div class="section-card" style="margin-bottom:16px">
          <h3 style="margin-bottom:12px">收货信息</h3>
          <div style="font-size:14px;line-height:2">
            <div><strong>{{ order.receiverName }}</strong> {{ order.receiverPhone }}</div>
            <div style="color:var(--t2)">{{ order.receiverAddress }}</div>
            <div v-if="order.remark" style="color:var(--t2);margin-top:4px">备注：{{ order.remark }}</div>
          </div>
        </div>
        <div style="display:flex;flex-direction:column;gap:8px">
          <template v-if="order.status === 'PENDING_PAY'">
            <button class="btn btn-success btn-block" @click="pay" :disabled="paying">
              {{ paying ? '支付中...' : '💳 立即支付' }}
            </button>
            <button class="btn btn-danger btn-block" @click="cancel">取消订单</button>
          </template>
          <span v-if="order.status === 'PAID'" class="status-msg">✅ 已支付，等待发货</span>
          <span v-if="order.status === 'CANCELLED'" class="status-msg">订单已取消</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { api } from '../api'
import StatusBadge from '../components/StatusBadge.vue'

const route = useRoute()
const order = ref({})
const items = ref([])
const loading = ref(true)
const paying = ref(false)

onMounted(async () => {
  try {
    const id = route.params.id
    order.value = await api('/api/orders/' + id)
    items.value = await api('/api/orders/' + id + '/items')
  } catch (e) {
    window.__toast('加载失败：' + e.message, 'error')
  } finally {
    loading.value = false
  }
})

function formatDate(dt) {
  if (!dt) return ''
  try { return new Date(dt).toLocaleString('zh-CN', { year: 'numeric', month: '2-digit', day: '2-digit', hour: '2-digit', minute: '2-digit' }) }
  catch { return dt }
}

async function pay() {
  paying.value = true
  try {
    const payment = await api('/api/payments', {
      method: 'POST',
      headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
      body: 'orderNo=' + order.value.orderNo + '&orderId=' + order.value.id + '&amount=' + order.value.totalAmount
    })
    await api('/api/payments/' + payment.paymentNo + '/pay', { method: 'POST' })
    window.__toast('支付成功！', 'success')
    // Reload order
    order.value = await api('/api/orders/' + route.params.id)
  } catch (e) {
    window.__toast(e.message, 'error')
  } finally {
    paying.value = false
  }
}

async function cancel() {
  if (!confirm('确定取消此订单？')) return
  try {
    await api('/api/orders/' + order.value.id + '/cancel', { method: 'POST' })
    window.__toast('订单已取消', 'info')
    order.value = await api('/api/orders/' + route.params.id)
  } catch (e) {
    window.__toast(e.message, 'error')
  }
}
</script>

<style scoped>
.section-card{background:var(--s);border-radius:var(--r);box-shadow:var(--sh);padding:24px}
.info-grid{display:grid;grid-template-columns:120px 1fr;gap:8px;font-size:14px}
.info-label{color:var(--t2)}
.mono{font-family:monospace}
.total-amount{font-weight:600;color:var(--a);font-size:18px}
.order-item{display:flex;align-items:center;gap:12px;padding:10px 0;border-bottom:1px solid var(--b)}
.order-item:last-child{border-bottom:none}
.order-item-cover{width:48px;height:64px;border-radius:4px;background:linear-gradient(135deg,#dfe6e9,#b2bec3);display:flex;align-items:center;justify-content:center;font-size:20px;color:#fff;flex-shrink:0;overflow:hidden}
.order-item-cover img{width:100%;height:100%;object-fit:cover}
.order-item-info{flex:1}
.order-item-title{font-weight:500;font-size:14px}
.order-item-meta{font-size:13px;color:var(--t2)}
.order-item-total{font-weight:500}
.status-msg{text-align:center;font-size:14px;color:var(--t2);padding:20px}
.btn{display:inline-flex;align-items:center;justify-content:center;gap:6px;padding:10px 24px;border-radius:var(--rs);border:none;font-size:14px;font-weight:500;cursor:pointer;transition:all .2s;font-family:inherit;text-decoration:none}
.btn-success{background:#00b894;color:#fff}.btn-success:hover{background:#00a381}
.btn-danger{background:#d63031;color:#fff}.btn-danger:hover{background:#b71c1c}
.btn-block{width:100%}
.btn-ghost{background:none;border:none;color:var(--t2);cursor:pointer;padding:4px 8px;font-size:14px;text-decoration:none}
.btn-ghost:hover{color:var(--a)}
.btn:disabled{opacity:.5;cursor:not-allowed}
.loading{text-align:center;padding:40px;color:var(--t2)}
.spinner{display:inline-block;width:24px;height:24px;border:3px solid var(--b);border-top-color:var(--a);border-radius:50%;animation:spin .6s linear infinite;vertical-align:middle;margin-right:8px}
@keyframes spin{to{transform:rotate(360deg)}}
@media(max-width:768px){div[style*='grid']{grid-template-columns:1fr!important}}
</style>
