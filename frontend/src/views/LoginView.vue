<template>
  <div class="auth-page">
    <div class="auth-card">
      <h2>📚 BookHaven</h2>
      <p class="subtitle">在线图书商城</p>
      <div class="auth-tabs">
        <button :class="{ active: tab === 'login' }" @click="tab = 'login'">登录</button>
        <button :class="{ active: tab === 'register' }" @click="tab = 'register'">注册</button>
      </div>

      <div v-if="tab === 'login'" class="auth-form active">
        <div class="form-group">
          <label>用户名</label>
          <input type="text" v-model="loginForm.username" placeholder="请输入用户名" @keydown.enter="doLogin" />
        </div>
        <div class="form-group">
          <label>密码</label>
          <input type="password" v-model="loginForm.password" placeholder="请输入密码" @keydown.enter="doLogin" />
        </div>
        <button class="btn btn-primary btn-block" @click="doLogin" :disabled="loading">{{ loading ? '登录中...' : '登录' }}</button>
      </div>

      <div v-if="tab === 'register'" class="auth-form active">
        <div class="form-group">
          <label>用户名</label>
          <input type="text" v-model="regForm.username" placeholder="请设置用户名" />
        </div>
        <div class="form-group">
          <label>密码</label>
          <input type="password" v-model="regForm.password" placeholder="请设置密码" />
        </div>
        <div class="form-group">
          <label>邮箱（可选）</label>
          <input type="email" v-model="regForm.email" placeholder="example@bookhaven.com" />
        </div>
        <button class="btn btn-primary btn-block" @click="doRegister" :disabled="loading">{{ loading ? '注册中...' : '注册' }}</button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '../stores/user'
import { api } from '../api'

const userStore = useUserStore()
const router = useRouter()
const tab = ref('login')
const loading = ref(false)

const loginForm = reactive({ username: '', password: '' })
const regForm = reactive({ username: '', password: '', email: '' })

async function doLogin() {
  if (!loginForm.username || !loginForm.password) {
    return window.__toast('请填写用户名和密码', 'error')
  }
  loading.value = true
  try {
    const data = await api('/api/users/login?username=' + encodeURIComponent(loginForm.username) + '&password=' + encodeURIComponent(loginForm.password), { method: 'POST' })
    userStore.login(data)
    window.__toast('登录成功！', 'success')
    router.push('/products')
  } catch (e) {
    window.__toast(e.message, 'error')
  } finally {
    loading.value = false
  }
}

async function doRegister() {
  if (!regForm.username || !regForm.password) {
    return window.__toast('请填写用户名和密码', 'error')
  }
  loading.value = true
  try {
    let url = '/api/users/register?username=' + encodeURIComponent(regForm.username) + '&password=' + encodeURIComponent(regForm.password)
    if (regForm.email) url += '&email=' + encodeURIComponent(regForm.email)
    await api(url, { method: 'POST' })
    window.__toast('注册成功，请登录！', 'success')
    tab.value = 'login'
    loginForm.username = regForm.username
  } catch (e) {
    window.__toast(e.message, 'error')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.auth-page{display:flex;align-items:center;justify-content:center;min-height:100vh;background:linear-gradient(135deg,#2d3436,#636e72)}
.auth-card{background:var(--s);border-radius:var(--r);padding:40px;width:400px;max-width:90vw;box-shadow:0 20px 60px rgba(0,0,0,.3)}
.auth-card h2{text-align:center;margin-bottom:8px;font-size:24px}
.subtitle{text-align:center;color:var(--t2);margin-bottom:28px;font-size:14px}
.auth-tabs{display:flex;margin-bottom:24px;border-bottom:2px solid var(--b)}
.auth-tabs button{flex:1;padding:10px;background:none;border:none;font-size:15px;cursor:pointer;color:var(--t2);font-weight:500;position:relative}
.auth-tabs button.active{color:var(--a)}
.auth-tabs button.active::after{content:'';position:absolute;bottom:-2px;left:20%;right:20%;height:2px;background:var(--a)}
.auth-form{display:none}.auth-form.active{display:block}
.form-group{margin-bottom:16px}
.form-group label{display:block;font-size:13px;font-weight:500;margin-bottom:6px;color:var(--t2)}
.form-group input{width:100%;padding:10px 14px;border:1px solid var(--b);border-radius:var(--rs);font-size:14px;outline:none;background:var(--s);color:#2d3436;font-family:inherit}
.form-group input:focus{border-color:var(--a)}
.btn{display:inline-flex;align-items:center;justify-content:center;gap:6px;padding:10px 24px;border-radius:var(--rs);border:none;font-size:14px;font-weight:500;cursor:pointer;transition:all .2s;font-family:inherit}
.btn-primary{background:var(--a);color:#fff}.btn-primary:hover{background:#b71c1c;transform:translateY(-1px)}
.btn-primary:disabled{opacity:.5;cursor:not-allowed;transform:none}
.btn-block{width:100%}
</style>
