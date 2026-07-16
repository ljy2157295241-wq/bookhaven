const API_BASE = 'http://localhost:8088'

export async function api(path, opts = {}) {
  const token = localStorage.getItem('bh_t')
  const userId = localStorage.getItem('bh_u')
  const headers = { 'Content-Type': 'application/json', ...opts.headers }
  if (userId) headers['userId'] = userId
  if (token) headers['Authorization'] = 'Bearer ' + token

  const controller = new AbortController()
  const timer = setTimeout(() => controller.abort(), 8000)
  try {
    var res = await fetch(API_BASE + path, { ...opts, headers, signal: controller.signal })
  } finally {
    clearTimeout(timer)
  }
  const json = await res.json()
  if (json.code !== 200) throw new Error(json.message || '请求失败')
  return json.data
}
