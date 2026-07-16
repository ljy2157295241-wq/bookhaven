<template>
  <div v-if="!userStore.isLoggedIn" class="full-page">
    <router-view />
  </div>
  <div v-else class="app-layout">
    <button class="hamburger" @click="sidebarOpen = !sidebarOpen">☰</button>
    <div class="overlay" :class="{ open: sidebarOpen }" @click="sidebarOpen = false"></div>
    <aside class="sidebar" :class="{ open: sidebarOpen }">
      <div class="sidebar-brand">
        <h1>📚 BookHaven</h1>
        <small>欢迎, {{ userStore.username }}</small>
      </div>
      <nav>
        <router-link to="/products" @click="sidebarOpen = false">
          <span class="icon">📖</span>图书浏览
        </router-link>
        <router-link to="/cart" @click="sidebarOpen = false">
          <span class="icon">🛒</span>购物车
        </router-link>
        <router-link to="/orders" @click="sidebarOpen = false">
          <span class="icon">📋</span>我的订单
        </router-link>
      </nav>
      <div class="sidebar-footer">
        <a href="#" @click.prevent="logout">🚪 退出登录</a>
      </div>
    </aside>
    <main class="main">
      <router-view />
    </main>
    
  </div>
  <div v-for="(item, idx) in toasts" :key="idx" class="toast" :class="'toast-' + item.type">{{ item.msg }}</div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from './stores/user'

const userStore = useUserStore()
const router = useRouter()
const sidebarOpen = ref(false)

const toasts = ref([])
function toast(msg, type) {
  if (!type) type = 'info'
  toasts.value.push({ msg, type })
  setTimeout(() => toasts.value.shift(), 2500)
}
window.__toast = toast

function logout() {
  userStore.logout()
  router.push('/login')
}
</script>

<style scoped>
.full-page{min-height:100vh;background:linear-gradient(135deg,#2d3436,#636e72)}
.app-layout{display:flex;min-height:100vh}
.sidebar{width:var(--sw);background:var(--p);color:#fff;padding:24px 0;position:fixed;top:0;left:0;height:100vh;display:flex;flex-direction:column;z-index:100;transition:transform .2s}
.sidebar-brand{padding:0 20px 24px;border-bottom:1px solid rgba(255,255,255,.1)}
.sidebar-brand h1{font-size:20px;font-weight:700}
.sidebar-brand small{font-size:12px;opacity:.6;display:block;margin-top:2px}
.sidebar nav{flex:1}
.sidebar nav a{display:block;padding:10px 20px;color:rgba(255,255,255,.7);text-decoration:none;font-size:14px;cursor:pointer;transition:all .2s}
.sidebar nav a:hover,.sidebar nav a.router-link-active{background:rgba(255,255,255,.1);color:#fff}
.sidebar nav a .icon{margin-right:10px;width:20px;text-align:center;display:inline-block}
.sidebar-footer{padding:16px 20px;border-top:1px solid rgba(255,255,255,.1)}
.sidebar-footer a{color:rgba(255,255,255,.7);text-decoration:none;font-size:14px;cursor:pointer}
.sidebar-footer a:hover{color:#fff}
.main{margin-left:var(--sw);flex:1;padding:32px;max-width:calc(100vw - var(--sw))}
.toast{position:fixed;top:20px;right:20px;padding:12px 20px;border-radius:var(--rs);color:#fff;font-size:14px;z-index:9999;animation:slideIn .3s;max-width:360px}
.toast-success{background:#00b894}.toast-error{background:#d63031}.toast-info{background:#2d3436}
@keyframes slideIn{from{opacity:0;transform:translateX(100%)}to{opacity:1;transform:translateX(0)}}
.hamburger{display:none}.overlay{display:none}
@media(max-width:768px){
.sidebar{transform:translateX(-100%)}.sidebar.open{transform:translateX(0)}.main{margin-left:0;padding:16px;max-width:100vw}
.hamburger{display:block!important;position:fixed;top:16px;left:16px;z-index:200;background:#2d3436;color:#fff;border:none;padding:10px 14px;border-radius:var(--rs);font-size:20px;cursor:pointer}
.overlay.open{display:block;position:fixed;inset:0;background:rgba(0,0,0,.4);z-index:99}}
</style>
