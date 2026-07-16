<template>
  <div>
    <div class="page-header"><h2>📋 我的订单</h2></div>
    <div v-if="loading" class="loading"><span class="spinner"></span>加载订单中...</div>
    <div v-else-if="orders.length === 0" class="empty-state">
      <div class="icon">📭</div>
      <p>暂无订单</p>
      <router-link to="/products" class="btn btn-primary">去选购</router-link>
    </div>
    <div v-else class="table-wrap">
      <table>
        <thead>
          <tr>
            <th>订单编号</th>
            <th>金额</th>
            <th>状态</th>
            <th>收件人</th>
            <th>下单时间</th>
            <th>操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="o in orders" :key="o.id">
            <td class="mono">{{ o.orderNo }}</td>
            <td class="price-cell">¥{{ o.totalAmount.toFixed(2) }}</td>
            <td><StatusBadge :status="o.status" /></td>
            <td>{{ o.receiverName }}</td>
            <td class="time-cell">{{ formatDate(o.createTime) }}</td>
            <td><router-link :to="'/orders/' + o.id" class="btn btn-sm btn-outline">查看</router-link></td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { api } from '../api'
import StatusBadge from '../components/StatusBadge.vue'

const orders = ref([])
const loading = ref(true)

onMounted(async () => {
  try {
    orders.value = await api('/api/orders')
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
</script>

<style scoped>
.page-header{display:flex;justify-content:space-between;align-items:center;margin-bottom:24px}
.page-header h2{font-size:22px;font-weight:700}
.table-wrap{background:var(--s);border-radius:var(--r);box-shadow:var(--sh);overflow:auto}
table{width:100%;border-collapse:collapse}
th,td{padding:14px 16px;text-align:left;font-size:14px}
th{background:#f5f6fa;font-weight:600;color:var(--t2);font-size:13px}
tr:not(:last-child) td{border-bottom:1px solid var(--b)}
tr:hover td{background:#fafafa}
.mono{font-family:monospace;font-size:13px}
.price-cell{font-weight:600;color:var(--a)}
.time-cell{font-size:13px;color:var(--t2)}
.loading,.empty-state{text-align:center;padding:40px;color:var(--t2)}
.empty-state .icon{font-size:48px;margin-bottom:16px}
.spinner{display:inline-block;width:24px;height:24px;border:3px solid var(--b);border-top-color:var(--a);border-radius:50%;animation:spin .6s linear infinite;vertical-align:middle;margin-right:8px}
@keyframes spin{to{transform:rotate(360deg)}}
.btn{display:inline-flex;align-items:center;justify-content:center;gap:6px;padding:10px 24px;border-radius:var(--rs);border:none;font-size:14px;font-weight:500;cursor:pointer;transition:all .2s;font-family:inherit;text-decoration:none}
.btn-primary{background:var(--a);color:#fff}.btn-primary:hover{background:#b71c1c;transform:translateY(-1px)}
.btn-outline{background:transparent;border:1px solid var(--b);color:#2d3436;text-decoration:none}
.btn-outline:hover{border-color:var(--a);color:var(--a)}
.btn-sm{padding:6px 14px;font-size:13px}
</style>
