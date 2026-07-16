<template>
  <div>
    <div class="page-header"><h2>📖 图书浏览</h2></div>
    <div class="toolbar">
      <div class="search-box">
        <input type="text" v-model="keyword" placeholder="搜索书名、作者..." @input="onSearch" />
      </div>
      <div class="filter-group">
        <button class="btn btn-sm" :class="selectedCat === '' ? 'btn-primary' : 'btn-outline'" @click="filterBy('')">全部</button>
        <button v-for="c in categories" :key="c.id" class="btn btn-sm"
          :class="selectedCat === String(c.id) ? 'btn-primary' : 'btn-outline'"
          @click="filterBy(String(c.id))">{{ c.name }}</button>
      </div>
    </div>
    <div v-if="loading" class="loading"><span class="spinner"></span>加载图书中...</div>
    <div v-else class="cards-grid">
      <div v-for="book in books" :key="book.id" class="book-card" @click="$router.push('/products/' + book.id)">
        <div class="book-card-cover">
          <img v-if="book.coverImage" :src="book.coverImage" :alt="book.title" />
          <span v-else>📕</span>
        </div>
        <div class="book-card-body">
          <h3>{{ book.title }}</h3>
          <div class="author">{{ book.author || '未知作者' }}</div>
          <div class="meta">
            <span class="price">¥{{ book.price }}<small v-if="book.sales"> / 已售{{ book.sales }}</small></span>
            <span class="stock">{{ book.stock > 0 ? '库存' + book.stock : '暂无库存' }}</span>
          </div>
        </div>
      </div>
    </div>
    <div v-if="!loading && books.length === 0" class="empty-state"><div class="icon">📭</div><p>没有找到图书</p></div>
    <div v-if="hasMore" style="text-align:center;margin-top:24px">
      <button class="btn btn-outline btn-sm" @click="loadMore" :disabled="loadingMore">加载更多</button>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { api } from '../api'

const books = ref([])
const categories = ref([])
const keyword = ref('')
const selectedCat = ref('')
const page = ref(1)
const hasMore = ref(false)
const loading = ref(true)
const loadingMore = ref(false)
let searchTimer = null

onMounted(async () => {
  try {
    categories.value = await api('/api/categories')
    await loadBooks()
  } catch (e) {
    window.__toast('加载失败：' + e.message, 'error')
    loading.value = false
  }
})

async function loadBooks() {
  let url = '/api/products?page=' + page.value + '&size=20'
  if (keyword.value) url += '&keyword=' + encodeURIComponent(keyword.value)
  if (selectedCat.value) url += '&categoryId=' + selectedCat.value
  const result = await api(url)
  if (page.value === 1) books.value = result.records || []
  else books.value = books.value.concat(result.records || [])
  hasMore.value = result.total > page.value * result.size
}

function onSearch() {
  clearTimeout(searchTimer)
  searchTimer = setTimeout(() => {
    page.value = 1
    loading.value = true
    loadBooks().finally(() => loading.value = false)
  }, 400)
}

function filterBy(catId) {
  selectedCat.value = catId
  page.value = 1
  loading.value = true
  loadBooks().finally(() => loading.value = false)
}

async function loadMore() {
  loadingMore.value = true
  page.value++
  await loadBooks()
  loadingMore.value = false
}
</script>

<style scoped>
.toolbar{display:flex;gap:12px;align-items:center;flex-wrap:wrap;margin-bottom:24px}
.search-box{flex:1;min-width:200px;position:relative}
.search-box input{width:100%;padding:10px 14px 10px 40px;border:1px solid var(--b);border-radius:var(--rs);font-size:14px;outline:none;background:var(--s)}
.search-box input:focus{border-color:var(--a)}
.search-box::before{content:'🔍';position:absolute;left:14px;top:50%;transform:translateY(-50%);font-size:14px;pointer-events:none}
.filter-group{display:flex;gap:8px;flex-wrap:wrap}
.cards-grid{display:grid;grid-template-columns:repeat(auto-fill,minmax(200px,1fr));gap:20px}
.book-card{background:var(--s);border-radius:var(--r);overflow:hidden;box-shadow:var(--sh);cursor:pointer;transition:all .2s}
.book-card:hover{transform:translateY(-4px);box-shadow:0 8px 30px rgba(0,0,0,.1)}
.book-card-cover{height:200px;background:linear-gradient(135deg,#dfe6e9,#b2bec3);display:flex;align-items:center;justify-content:center;font-size:48px;color:#fff;overflow:hidden}
.book-card-cover img{width:100%;height:100%;object-fit:cover}
.book-card-body{padding:14px}
.book-card-body h3{font-size:15px;font-weight:600;margin-bottom:4px}
.book-card-body .author{font-size:12px;color:var(--t2);margin-bottom:8px}
.book-card-body .meta{display:flex;justify-content:space-between;align-items:center}
.book-card-body .price{font-size:18px;font-weight:700;color:var(--a)}
.book-card-body .stock{font-size:12px;color:var(--t2)}
.btn{display:inline-flex;align-items:center;justify-content:center;gap:6px;padding:10px 24px;border-radius:var(--rs);border:none;font-size:14px;font-weight:500;cursor:pointer;transition:all .2s;font-family:inherit}
.btn-primary{background:var(--a);color:#fff}.btn-primary:hover{background:#b71c1c;transform:translateY(-1px)}
.btn-outline{background:transparent;border:1px solid var(--b);color:#2d3436}
.btn-outline:hover{border-color:var(--a);color:var(--a)}
.btn-sm{padding:6px 14px;font-size:13px}
.btn-block{width:100%}
.loading,.empty-state{text-align:center;padding:40px;color:var(--t2)}
.empty-state .icon{font-size:48px;margin-bottom:16px}
.spinner{display:inline-block;width:24px;height:24px;border:3px solid var(--b);border-top-color:var(--a);border-radius:50%;animation:spin .6s linear infinite;vertical-align:middle;margin-right:8px}
@keyframes spin{to{transform:rotate(360deg)}}
.page-header{display:flex;justify-content:space-between;align-items:center;margin-bottom:24px}
.page-header h2{font-size:22px;font-weight:700}
</style>
