<template>
  <div>
    <router-link to="/cart" class="btn btn-ghost" style="margin-bottom:16px">← 返回购物车</router-link>
    <div class="page-header"><h2>📦 结算</h2></div>
    <div style="display:grid;grid-template-columns:1fr 360px;gap:24px">
      <div class="section-card">
        <h3 style="margin-bottom:16px">收货信息</h3>
        <div class="form-group">
          <label>收件人姓名</label>
          <input type="text" v-model="receiverName" placeholder="请输入收件人" />
        </div>
        <div class="form-group">
          <label>联系电话</label>
          <input type="text" v-model="receiverPhone" placeholder="请输入手机号" />
        </div>
        <div class="form-group">
          <label>收货地址</label>
          <textarea v-model="receiverAddress" placeholder="请输入详细地址"></textarea>
        </div>
        <div class="form-group">
          <label>备注（可选）</label>
          <input type="text" v-model="remark" placeholder="订单备注" />
        </div>
      </div>
      <div class="section-card">
        <h3 style="margin-bottom:16px">订单摘要</h3>
        <div v-for="i in items" :key="i.id" class="summary-item">
          <span class="summary-title">{{ i.bookTitle }}</span>
          <span>×{{ i.quantity }}</span>
          <span class="summary-price">¥{{ (i.bookPrice * i.quantity).toFixed(2) }}</span>
        </div>
        <div class="summary-total">
          <span>合计</span>
          <span style="color:var(--a)">¥{{ total.toFixed(2) }}</span>
        </div>
        <button class="btn btn-primary btn-block" style="margin-top:20px" @click="placeOrder" :disabled="submitting">
          {{ submitting ? '提交中...' : '提交订单' }}
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { api } from '../api'

const router = useRouter()
const items = ref([])
const receiverName = ref('')
const receiverPhone = ref('')
const receiverAddress = ref('')
const remark = ref('')
const submitting = ref(false)

onMounted(() => {
  try {
    const saved = localStorage.getItem('bh_cart')
    items.value = saved ? JSON.parse(saved) : []
  } catch { items.value = [] }
  if (items.value.length === 0) {
    router.push('/cart')
  }
})

const total = computed(() => items.value.reduce((s, i) => s + i.bookPrice * i.quantity, 0))

async function placeOrder() {
  if (!receiverName.value || !receiverPhone.value || !receiverAddress.value) {
    return window.__toast('请填写完整的收货信息', 'error')
  }
  submitting.value = true
  try {
    const params = new URLSearchParams()
    items.value.forEach(i => params.append('cartItemIds', i.id))
    params.append('receiverName', receiverName.value)
    params.append('receiverPhone', receiverPhone.value)
    params.append('receiverAddress', receiverAddress.value)
    if (remark.value) params.append('remark', remark.value)

    const order = await api('/api/orders', {
      method: 'POST',
      headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
      body: params.toString()
    })
    localStorage.removeItem('bh_cart')
    window.__toast('订单创建成功！', 'success')
    router.push('/orders/' + order.id)
  } catch (e) {
    window.__toast(e.message, 'error')
  } finally {
    submitting.value = false
  }
}
</script>

<style scoped>
.section-card{background:var(--s);border-radius:var(--r);box-shadow:var(--sh);padding:24px}
.page-header{display:flex;justify-content:space-between;align-items:center;margin-bottom:24px}
.page-header h2{font-size:22px;font-weight:700}
.form-group{margin-bottom:16px}
.form-group label{display:block;font-size:13px;font-weight:500;margin-bottom:6px;color:var(--t2)}
.form-group input,.form-group textarea{width:100%;padding:10px 14px;border:1px solid var(--b);border-radius:var(--rs);font-size:14px;outline:none;background:var(--s);color:#2d3436;font-family:inherit}
.form-group input:focus,.form-group textarea:focus{border-color:var(--a)}
.form-group textarea{resize:vertical;min-height:60px}
.summary-item{display:flex;justify-content:space-between;font-size:14px;padding:6px 0;border-bottom:1px solid var(--b);gap:8px}
.summary-title{flex:1;overflow:hidden;text-overflow:ellipsis;white-space:nowrap}
.summary-price{font-weight:500;min-width:64px;text-align:right}
.summary-total{display:flex;justify-content:space-between;font-size:18px;font-weight:600;padding:12px 0 0;margin-top:8px}
.btn{display:inline-flex;align-items:center;justify-content:center;gap:6px;padding:10px 24px;border-radius:var(--rs);border:none;font-size:14px;font-weight:500;cursor:pointer;transition:all .2s;font-family:inherit;text-decoration:none}
.btn-primary{background:var(--a);color:#fff}.btn-primary:hover{background:#b71c1c;transform:translateY(-1px)}
.btn-primary:disabled{opacity:.5;cursor:not-allowed;transform:none}
.btn-block{width:100%}
.btn-ghost{background:none;border:none;color:var(--t2);cursor:pointer;padding:4px 8px;font-size:14px;text-decoration:none}
.btn-ghost:hover{color:var(--a)}
@media(max-width:768px){div[style*='grid']{grid-template-columns:1fr!important}}
</style>