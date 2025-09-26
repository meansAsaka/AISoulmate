<template>
  <div class="layout">
    <!-- 顶栏 -->
    <header class="header">
      <div class="logo">
        <img src="../assets/logo/Logo-light.svg" alt="AISoulmate" />
      </div>

      <!-- 搜索框 -->
      <div class="search-container">
        <input type="text" placeholder="搜索角色..." class="search-input" />
      </div>

      <!-- 右侧按钮组 -->
      <div class="header-buttons">
        <button class="theme-btn" @click="toggleTheme">切换主题</button>
        <button class="auth-btn" @click="goLogin">登录</button>
        <button class="auth-btn" @click="goRegister">注册</button>
      </div>
    </header>

    <!-- 内容区域 -->
    <div class="container">
      <aside class="sidebar">
        <h1>最近聊天</h1>
        <ul class="recent-list">
          <li v-for="s in sessions" :key="s.sessionId" class="recent-item" @click="openSession(s)">
            <img :src="s.avatarUrl" :alt="s.sessionId" />
            <span class="recent-name">{{ shortMessage(s.latestMessageText) }}</span>
          </li>
          <li v-if="sessions.length === 0" class="recent-item">暂无最近会话</li>
        </ul>
      </aside>

      <main class="main">
        <h1>热门角色</h1>
        <div class="image-row">
          <!-- ✅ 使用 v-for 渲染角色卡片 -->
          <div v-for="role in roles" :key="role.name" class="image-card" @click="goChat(role)">
            <img :src="role.avatar" :alt="role.name" />
            <div class="image-info">
              <div class="image-name">名字：{{ role.name }}</div>
              <div class="image-tag">标签：{{ role.tag }}</div>
              <div class="image-desc">简介：{{ role.desc }}</div>
            </div>
          </div>
        </div>
      </main>
    </div>

    <!-- 底部 -->
    <footer class="footer">底部版权信息</footer>
  </div>
</template>

<script setup lang="ts">
import { useRouter } from 'vue-router'

const router = useRouter()

const toggleTheme = () => {
  document.body.classList.toggle('dark-theme')
}

const goLogin = () => {
  router.push({ name: 'login' })
}

const goRegister = () => {
  router.push({ name: 'register' })
}

import { ref, onMounted } from 'vue'

type Role = { id: string; name: string; avatar: string; tag: string; desc: string; popularity?: number }
const roles = ref<Role[]>([])
const loading = ref(false)
const error = ref<string | null>(null)

// 最近会话历史
type SessionSummary = { sessionId: string; avatarUrl?: string; latestMessageText?: string }
const sessions = ref<SessionSummary[]>([])

async function loadHistory() {
  try {
    const base = '/api'
    const headers: Record<string, string> = {}
    const auth = localStorage.getItem('Authorization')
    if (auth) headers['Authorization'] = auth
    const res = await fetch(`${base}/sessions/history`, { headers })
    if (!res.ok) throw new Error(`加载历史会话失败: ${res.status}`)
    const data = await res.json()
    if (Array.isArray(data)) {
      sessions.value = data.map((it: any) => ({ sessionId: it.sessionId, avatarUrl: it.avatarUrl, latestMessageText: it.latestMessageText }))
    }
  } catch (err: any) {
    console.error('loadHistory error', err)
  }
}

function openSession(s: SessionSummary) {
  // 通过 sessionId 打开已存在会话，chat 页面可使用 query.sessionId 恢复会话
  router.push({ name: 'chat', query: { sessionId: s.sessionId, name: '', avatar: s.avatarUrl || '', tag: '', desc: '' } })
}

function shortMessage(text?: string | null) {
  if (!text) return '新会话'
  const t = String(text)
  if (t.length <= 5) return t
  return t.slice(0, 5) + '...'
}

const parseTags = (tagsStr: string | null) => {
  if (!tagsStr) return ''
  try {
    const parsed = JSON.parse(tagsStr)
    if (Array.isArray(parsed) && parsed.length > 0) return String(parsed[0])
    return ''
  } catch (e) {
    // 如果 tags 不是 JSON 字符串，尝试简单分隔
    return String(tagsStr).replace(/\[|\]|\"/g, '')?.split(',')[0] || ''
  }
}

onMounted(async () => {
  loading.value = true
  error.value = null
  // 加载最近会话历史
  loadHistory()
  // 使用相对路径 /api 开发时通过 Vite proxy 转发到后端；如需在生产使用自定义地址，请在代码中替换为 import.meta.env.VITE_API_BASE
  const base = '/api'
  try {
    const res = await fetch(`${base}/characters`)
    if (!res.ok) throw new Error(`请求失败: ${res.status}`)
    const data = await res.json()
    // data 预期为数组
    if (Array.isArray(data)) {
      roles.value = data.map((item: any) => ({
        id: item.id,
        name: item.name,
        avatar: item.avatarUrl || item.avatar || '',
        tag: parseTags(item.tags) || item.brief || '',
        desc: item.brief || '',
        popularity: item.popularity,
      }))
    } else {
      throw new Error('返回数据不是数组')
    }
  } catch (err: any) {
    console.error(err)
    error.value = err?.message || String(err)
  } finally {
    loading.value = false
  }
})

const goChat = (role: Role) => {
  router.push({
    name: 'chat', // ChatPage 的路由命名
    query: {
      characterId: role.id,
      name: role.name,
      avatar: role.avatar,
      tag: role.tag,
      desc: role.desc,
    },
  })
}
</script>

<style scoped>
/* 整体布局 */
.layout {
  display: flex;
  flex-direction: column;
  height: 100dvh;
  box-sizing: border-box;
  overflow: hidden; /* 避免页面级滚动条 */
  gap: 10px; /* 各区域间距 */
  padding: 10px;
}

/* 顶栏布局 */
.header {
  height: 60px;
  background-color: var(--area-bg);
  color: var(--text-color);
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;
  border: 2px solid var(--border-color);
  border-radius: 12px;
  transition: background 0.3s;
  gap: 20px;
}

.logo {
  display: flex;
  align-items: center;
  gap: 8px;
}
.logo img {
  height: 90%;
  width: 100px;
  display: block;
  mix-blend-mode: multiply;
  background: transparent;
}

/* 搜索框 */
.search-container {
  flex: 1;
  display: flex;
  width: 100%;
  height: 80%;
  justify-content: center; /* 水平居中 */
}

.search-input {
  width: 66%;
  max-width: 600px;
  padding: 10px 40px 10px 14px; /* 给右侧图标留空间 */
  border-radius: 10px;
  border: 1px solid rgba(0, 0, 0, 0.12);
  outline: none;
  background: rgba(255, 255, 255, 0.35);
  backdrop-filter: blur(8px);
  -webkit-backdrop-filter: blur(8px);
  color: var(--text-color);
  transition:
    box-shadow 0.2s ease,
    border-color 0.2s ease;
  background-image: url("data:image/svg+xml;utf8,<svg xmlns='http://www.w3.org/2000/svg' width='18' height='18' viewBox='0 0 24 24' fill='none' stroke='%23888' stroke-width='2' stroke-linecap='round' stroke-linejoin='round'><circle cx='11' cy='11' r='8'/><line x1='21' y1='21' x2='16.65' y2='16.65'/></svg>");
  background-repeat: no-repeat;
  background-position: right 14px center;
  background-size: 18px 18px;
}

/* 有内容时不显示放大镜 */
.search-input:not(:placeholder-shown) {
  background-image: none;
}

.search-input::placeholder {
  color: rgba(0, 0, 0, 0.45);
}

.dark-theme .search-input::placeholder {
  color: rgba(255, 255, 255, 0.55);
}

.search-input:focus {
  border-color: rgba(24, 144, 255, 0.6);
  box-shadow: 0 0 0 3px rgba(24, 144, 255, 0.2);
  background: rgba(255, 255, 255, 0.45);
}

.dark-theme .search-input {
  background: rgba(0, 0, 0, 0.25);
  border-color: rgba(255, 255, 255, 0.12);
}

.dark-theme .search-input:not(:placeholder-shown) {
  background-image: none;
}

.dark-theme .search-input:focus {
  border-color: rgba(64, 169, 255, 0.8);
  box-shadow: 0 0 0 3px rgba(64, 169, 255, 0.25);
  background: rgba(0, 0, 0, 0.3);
}

/* 右侧按钮组 */
.header-buttons {
  display: flex;
  gap: 10px;
  align-items: center;
}

/* 按钮 */
.theme-btn {
  padding: 6px 12px;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  background-color: rgba(255, 255, 255, 0.3);
  color: var(--text-color);
  font-weight: bold;
}

/* 登录注册按钮 */
.auth-btn {
  padding: 6px 12px;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  background-color: rgba(24, 144, 255, 0.1);
  color: var(--text-color);
  font-weight: bold;
  transition: background 0.2s;
}

.auth-btn:hover {
  background-color: rgba(24, 144, 255, 0.25);
}

/* 内容区域 */
.container {
  display: flex;
  flex: 1;
  gap: 10px;
  overflow: auto; /* 仅中间区域可滚动 */
}

/* 侧边栏 */
.sidebar {
  width: 200px;
  background-color: var(--area-bg);
  padding: 20px;
  border: 2px solid var(--border-color);
  border-radius: 12px;
  color: var(--text-color);
  transition: background 0.3s;
}

.recent-list {
  list-style: none;
  padding: 0;
  margin: 0;
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.recent-item {
  display: flex;
  align-items: center;
  gap: 10px;
}

.recent-item img {
  width: 40px;
  height: 40px;
  border-radius: 8px;
  object-fit: cover;
}

.recent-name {
  font-size: 14px;
  color: var(--text-color);
}

/* 主内容 */
.main {
  flex: 1;
  padding: 20px;
  background-color: var(--area-bg);
  border: 2px solid var(--border-color);
  border-radius: 12px;
  color: var(--text-color);
  transition: background 0.3s;
}

.image-row {
  display: flex;
  flex-direction: column;
  gap: 20px;
  margin-top: 20px;
}

/* 每个卡片水平排列 */
.image-card {
  display: flex;
  align-items: flex-start;
  gap: 16px;
  padding: 12px;
  border: 1px solid rgba(0, 0, 0, 0.12);
  border-radius: 12px;
  background-color: rgba(255, 255, 255, 0.15);
  transition: background 0.3s;
}

.image-card:hover {
  background-color: rgba(255, 255, 255, 0.25);
}

.image-card img {
  width: 100px;
  height: 140px;
  border-radius: 12px;
  object-fit: cover;
  flex-shrink: 0;
  transition: transform 0.3s;
}

.image-card img:hover {
  transform: scale(1.05);
}

.image-info {
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.image-name {
  font-weight: bold;
  font-size: 16px;
  margin-bottom: 4px;
}

.image-tag {
  font-size: 14px;
  color: #888;
  margin-bottom: 6px;
}

.image-desc {
  font-size: 14px;
  color: #555;
}

/* 底部 */
.footer {
  height: 40px;
  background-color: var(--area-bg);
  color: var(--text-color);
  display: flex;
  align-items: center;
  justify-content: center;
  border: 2px solid var(--border-color);
  border-radius: 12px;
  transition: background 0.3s;
}
</style>
