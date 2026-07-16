<template>
  <div>
    <router-link to="/products" class="btn btn-ghost" style="margin-bottom:16px">← 返回图书列表</router-link>
    <div v-if="loading" class="loading"><span class="spinner"></span>加载中...</div>
    <div v-else class="book-detail">
      <div class="book-detail-cover">
        <img v-if="book.coverImage" :src="book.coverImage" :alt="book.title" />
        <span v-else>📕</span>
      </div>
      <div class="book-detail-info">
        <h2>{{ book.title }}</h2>
        <div class="meta-row">
          <template v-if="book.author">作者：{{ book.author }} | </template>
          <template v-if="book.publisher">出版社：{{ book.publisher }} | </template>
          <template v-if="book.isbn">ISBN：{{ book.isbn }}</template>
        </div>
        <div class="description">{{ book.description || '暂无简介' }}</div>
        <div class="price-row">
          <span class="price">¥{{ book.price }}</span>
          <span class="stock">{{ book.stock > 0 ? '库存 ' + book.stock + ' 件' : '暂时缺货' }}</span>
          <span v-if="book.sales" class="stock">已售 {{ book.sales }} 件</span>
        </div>
        <div class="actions">
          <div class="qty-input">
            <button @click="decrement">−</button>
            <input type="text" v-model.number="quantity" readonly />
            <button @click="increment">+</button>
          </div>
          <button class="btn btn-primary" @click="addToCart" :disabled="book.stock <= 0">🛒 加入购物车</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { api } from '../api'

const route = useRoute()
const book = ref({})
const loading = ref(true)
const quantity = ref(1)

onMounted(async () => {
  try {
    book.value = await api('/api/products/' + route.params.id)
  } catch (e) {
    window.__toast('加载失败：' + e.message, 'error')
  } finally {
    loading.value = false
  }
})

function decrement() {
  if (quantity.value > 1) quantity.value--
}
function increment() {
  if (quantity.value < (book.value.stock || 99)) quantity.value++
}

async function addToCart() {
  try {
    await api('/api/cart/items', {
      method: 'POST',
      body: JSON.stringify({
        bookId: book.value.id,
        bookTitle: book.value.title,
        bookCover: book.value.coverImage || '',
        bookPrice: book.value.price,
        quantity: quantity.value
      })
    })
    window.__toast('已加入购物车', 'success')
  } catch (e) {
    window.__toast(e.message, 'error')
  }
}
</script>

<style scoped>
.book-detail{display:flex;gap:32px;background:var(--s);border-radius:var(--r);padding:32px;box-shadow:var(--sh)}
.book-detail-cover{width:280px;min-height:360px;border-radius:var(--rs);background:linear-gradient(135deg,#dfe6e9,#b2bec3);display:flex;align-items:center;justify-content:center;font-size:72px;color:#fff;flex-shrink:0;overflow:hidden}
.book-detail-cover img{width:100%;height:100%;object-fit:cover}
.book-detail-info{flex:1}
.book-detail-info h2{font-size:24px;margin-bottom:4px}
.meta-row{color:var(--t2);font-size:14px;margin-bottom:16px}
.description{font-size:14px;line-height:1.8;margin-bottom:20px}
.price-row{display:flex;align-items:baseline;gap:16px;margin-bottom:24px}
.price{font-size:32px;font-weight:700;color:var(--a)}
.stock{font-size:14px;color:var(--t2)}
.actions{display:flex;gap:12px;align-items:center}
.qty-input{display:flex;align-items:center;border:1px solid var(--b);border-radius:var(--rs)}
.qty-input button{width:36px;height:36px;border:none;background:none;cursor:pointer;font-size:18px}
.qty-input button:hover{background:#f5f6fa}
.qty-input input{width:48px;text-align:center;border:none;border-left:1px solid var(--b);border-right:1px solid var(--b);height:36px;font-size:14px;outline:none;background:transparent}
.btn{display:inline-flex;align-items:center;justify-content:center;gap:6px;padding:10px 24px;border-radius:var(--rs);border:none;font-size:14px;font-weight:500;cursor:pointer;transition:all .2s;font-family:inherit}
.btn-primary{background:var(--a);color:#fff}.btn-primary:hover{background:#b71c1c;transform:translateY(-1px)}
.btn-primary:disabled{opacity:.5;cursor:not-allowed}
.btn-ghost{background:none;border:none;color:var(--t2);cursor:pointer;padding:4px 8px;font-size:14px;text-decoration:none}
.btn-ghost:hover{color:var(--a)}
.loading{text-align:center;padding:40px;color:var(--t2)}
.spinner{display:inline-block;width:24px;height:24px;border:3px solid var(--b);border-top-color:var(--a);border-radius:50%;animation:spin .6s linear infinite;vertical-align:middle;margin-right:8px}
@keyframes spin{to{transform:rotate(360deg)}}
@media(max-width:768px){.book-detail{flex-direction:column}.book-detail-cover{width:100%;min-height:240px}}
</style>
