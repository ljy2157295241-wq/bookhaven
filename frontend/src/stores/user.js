import { defineStore } from 'pinia'
import { ref, computed } from 'vue'

export const useUserStore = defineStore('user', () => {
  const token = ref(localStorage.getItem('bh_t') || null)
  const userId = ref(localStorage.getItem('bh_u') || null)
  const username = ref(localStorage.getItem('bh_n') || null)

  const isLoggedIn = computed(() => !!token.value)

  function login(data) {
    token.value = data.token
    userId.value = String(data.userId || data.id)
    username.value = data.username
    localStorage.setItem('bh_t', token.value)
    localStorage.setItem('bh_u', userId.value)
    localStorage.setItem('bh_n', username.value)
  }

  function logout() {
    token.value = null
    userId.value = null
    username.value = null
    localStorage.removeItem('bh_t')
    localStorage.removeItem('bh_u')
    localStorage.removeItem('bh_n')
  }

  return { token, userId, username, isLoggedIn, login, logout }
})
