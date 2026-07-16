<template>
  <div>
    <div class="page-header">
      <h2>🛒 购物车</h2>
      <button v-if="items.length > 0" class="btn btn-danger btn-sm" @click="clearCart">清空购物车</button>
    </div>
    <div v-if="loading" class="loading"><span class="spinner"></span>加载购物车中...</div>
    <div v-else-if="items.length === 0" class="empty-state">
      <div class="icon">🛒</div>
      <p>购物车是空的</p>
      <router-link to="/products" class="btn btn-primary">去逛逛</router-link>
    </div>
    <div v-else class="section-card">
      <div v-for="(item, i) in items" :key="item.id" class="cart-item">
        <div class="cart-item-cover">
          <img v-if="item.bookCover" :src="item.bookCover" :alt="item.bookTitle" />
          <span v-else>📕</span>
        </div>
        <div class="cart-item-info">
          <h4>{{ item.bookTitle }}</h4>
          <div class="price">¥{{ item.bookPrice }} × {{ item.quantity }}</div>
        </div>
        <div class="cart-item-actions">
          <div class="qty-input">
            <button @click="decQty(i)">−</button>
            <input type="text" :value="item.quantity" readonly style="width:36px" />
            <button @click="incQty(i)">+</button>
          </div>
          <span class="item-total">¥{{ (item.bookPrice * item.quantity).toFixed(2) }}</span>
          <button class="btn-ghost" @click="removeItem(i)" title="删除">✕</button>
        </div>
      </div>
      <div class="cart-summary">
        <span class="total">合计：<span>¥{{ total.toFixed(2) }}</span></span>
        <button class="btn btn-primary" @click="goCheckout">去结算</button>
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
const loading = ref(true)

onMounted(loadCart)

async function loadCart() {
  loading.value = true
  try {
    items.value = await api('/api/cart')
  } catch (e) {
    window.__toast('加载失败：' + e.message, 'error')
  } finally {
    loading.value = false
  }
}

const total = computed(() => items.value.reduce((s, i) => s + i.bookPrice * i.quantity, 0))

async function decQty(idx) {
  const item = items.value[idx]
  if (item.quantity <= 1) return
  try {
    await api('/api/cart/items/' + item.id + '?quantity=' + (item.quantity - 1), { method: 'PUT' })
    item.quantity--
  } catch (e) { window.__toast(e.message, 'error') }
}

async function incQty(idx) {
  const item = items.value[idx]
  try {
    await api('/api/cart/items/' + item.id + '?quantity=' + (item.quantity + 1), { method: 'PUT' })
    item.quantity++
  } catch (e) { window.__toast(e.message, 'error') }
}

async function removeItem(idx) {
  try {
    await api('/api/cart/items/' + items.value[idx].id, { method: 'DELETE' })
    items.value.splice(idx, 1)
  } catch (e) { window.__toast(e.message, 'error') }
}

async function clearCart() {
  if (!confirm('确定清空购物车？')) return
  try {
    await api('/api/cart/clear', { method: 'DELETE' })
    items.value = []
  } catch (e) { window.__toast(e.message, 'error') }
}

function goCheckout() {
  localStorage.setItem('bh_cart', JSON.stringify(items.value))
  router.push('/checkout')
}
</script>

<style scoped>
.cart-item{display:flex;align-items:center;gap:16px;padding:16px 0;border-bottom:1px solid var(--b)}
.cart-item:last-child{border-bottom:none}
.cart-item-cover{width:60px;height:80px;border-radius:6px;background:linear-gradient(135deg,#dfe6e9,#b2bec3);flex-shrink:0;display:flex;align-items:center;justify-content:center;font-size:20px;color:#fff;overflow:hidden}
.cart-item-cover img{width:100%;height:100%;object-fit:cover}
.cart-item-info{flex:1;min-width:0}
.cart-item-info h4{font-size:14px;margin-bottom:2px}
.cart-item-info .price{font-size:13px;color:var(--t2)}
.cart-item-actions{display:flex;align-items:center;gap:12px}
.item-total{font-weight:600;min-width:64px;text-align:right}
.cart-summary{display:flex;justify-content:space-between;align-items:center;padding:20px 0 0}
.cart-summary .total{font-size:18px;font-weight:600}
.cart-summary .total span{color:var(--a);font-size:24px}
.qty-input{display:flex;align-items:center;border:1px solid var(--b);border-radius:var(--rs)}
.qty-input button{width:36px;height:36px;border:none;background:none;cursor:pointer;font-size:18px}
.qty-input button:hover{background:#f5f6fa}
.qty-input input{width:48px;text-align:center;border:none;border-left:1px solid var(--b);border-right:1px solid var(--b);height:36px;font-size:14px;outline:none;background:transparent}
.section-card{background:var(--s);border-radius:var(--r);box-shadow:var(--sh);padding:24px}
.page-header{display:flex;justify-content:space-between;align-items:center;margin-bottom:24px}
.page-header h2{font-size:22px;font-weight:700}
.loading,.empty-state{text-align:center;padding:40px;color:var(--t2)}
.empty-state .icon{font-size:48px;margin-bottom:16px}
.btn{display:inline-flex;align-items:center;justify-content:center;gap:6px;padding:10px 24px;border-radius:var(--rs);border:none;font-size:14px;font-weight:500;cursor:pointer;transition:all .2s;font-family:inherit;text-decoration:none}
.btn-primary{background:var(--a);color:#fff}.btn-primary:hover{background:#b71c1c;transform:translateY(-1px)}
.btn-danger{background:#d63031;color:#fff}.btn-danger:hover{background:#b71c1c}
.btn-sm{padding:6px 14px;font-size:13px}
.btn-ghost{background:none;border:none;color:var(--t2);cursor:pointer;padding:4px 8px;font-size:14px}
.btn-ghost:hover{color:var(--a)}
.spinner{display:inline-block;width:24px;height:24px;border:3px solid var(--b);border-top-color:var(--a);border-radius:50%;animation:spin .6s linear infinite;vertical-align:middle;margin-right:8px}
@keyframes spin{to{transform:rotate(360deg)}}
</style>
