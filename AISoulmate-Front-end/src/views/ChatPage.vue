<template>
  <div class="chat-root">
    <!-- 顶栏 -->
    <header class="chat-header">
      <!-- 左侧 Logo -->
      <img
        class="chat-logo"
        src="../assets/logo/Logo-light.svg"
        alt="AISoulmate"
        @click="goHome"
        style="cursor: pointer"
      />

      <!-- 右侧关闭按钮 -->
      <button class="close-btn" @click="closeChat" title="关闭">
        <div style="display: flex; flex-direction: column; align-items: center">
          <img src="../assets/icon/close.svg" alt="关闭" width="28" height="28" />
        </div>
      </button>
    </header>

    <div class="chat-container">
      <!-- 角色信息 -->
      <div class="role-info">
        <img class="role-avatar" :src="avatar" :alt="name" />
        <div class="role-detail">
          <div class="role-name">{{ name }}</div>
          <div class="role-tag">{{ tag }}</div>
          <div class="role-desc">{{ desc }}</div>
        </div>
      </div>

      <!-- 聊天区域 -->
      <div class="chat-area">
        <div v-for="msg in messages" :key="msg.id" :class="['chat-bubble', msg.from]">
          <img v-if="msg.from === 'ai'" class="bubble-avatar" :src="avatar" :alt="name" />
          <div class="bubble-content">{{ msg.text }}</div>
          <img
            v-if="msg.from === 'user'"
            class="bubble-avatar user-avatar"
            src="/Logo.ico"
            alt="我"
          />
        </div>
      </div>

      <form class="chat-input-bar" @submit.prevent="sendMsg">
        <div class="input-group-chat">
          <select v-model="modelName" class="model-select" :disabled="creatingSession || sendLoading" aria-label="选择模型">
            <option value="tongyi">通义</option>
            <option value="deepseek">DeepSeek</option>
          </select>
          <input v-model="input" class="chat-input" type="text" :placeholder="`和${name}聊天...`" :disabled="creatingSession || sendLoading" />
          <button class="send-btn" type="submit" :title="sendLoading ? '发送中...' : '发送'" :disabled="creatingSession || sendLoading">
            <svg v-if="!sendLoading" width="38" height="38" viewBox="0 0 1024 1024" fill="none">
              <circle cx="512" cy="512" r="512" fill="url(#planeGradient)" />
              <path d="M256 512l512-192-192 512-64-192-192-64z" fill="#fff" />
              <defs>
                <linearGradient
                  id="planeGradient"
                  x1="0"
                  y1="0"
                  x2="1024"
                  y2="1024"
                  gradientUnits="userSpaceOnUse"
                >
                  <stop stop-color="#38bdf8" />
                  <stop offset="1" stop-color="#7c3aed" />
                </linearGradient>
              </defs>
            </svg>
            <svg v-else width="22" height="22" viewBox="0 0 50 50">
              <circle cx="25" cy="25" r="20" stroke="#ccc" stroke-width="4" fill="none" stroke-linecap="round" />
            </svg>
          </button>
        </div>
        <button class="call-btn" type="button" @click="startVoiceCall" title="语音通话">
          <img src="/src/assets/icon/phone.svg" alt="电话" width="44" height="44" />
        </button>
      </form>
    </div>
  </div>
</template>

<script setup lang="ts">
import { useRouter } from 'vue-router'
const router = useRouter()

function goHome() {
  router.push({ name: 'home' })
}

function closeChat() {
  router.push({ name: 'home' })
}
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'

// ✅ 获取路由参数
const route = useRoute()
const name = (route.query.name as string) || 'AI'
const avatar = (route.query.avatar as string) || '/Logo.ico'
const tag = (route.query.tag as string) || ''
const desc = (route.query.desc as string) || ''
const characterId = (route.query.characterId as string) || ''

// ✅ 消息列表
const input = ref('')
const messages = ref([
  {
    id: Date.now(),
    from: 'ai',
    text: `你好，我是${name}`,
  },
])

// session 管理
const sessionId = ref<string | null>(null)
const creatingSession = ref(false)
const sendLoading = ref(false)
// 选择模型名称（tongyi 或 deepseek）
const modelName = ref<'tongyi' | 'deepseek'>('tongyi')

function startVoiceCall() {
  // TODO: WebSocket语音功能实现
  alert('语音通话功能开发中...')
}

// 创建会话
async function createSessionIfNeeded() {
  // 如果已有会话ID，直接返回
  if (sessionId.value) return sessionId.value
  
  // 如果没有角色ID，无法创建会话
  if (!characterId) {
    console.error('缺少 characterId，无法创建会话')
    return null
  }
  
  try {
    creatingSession.value = true
    const base = '/api'
    const url = `${base}/sessions`
    const headers: Record<string, string> = { 'Content-Type': 'application/json' }
    const auth = localStorage.getItem('Authorization')
    if (auth) headers['Authorization'] = auth
    // debug: show which URL will be requested (dev proxy will rewrite to /api/v1/*)
    // console.log('createSession request:', url, { characterId, mode: 'academic' })
    const res = await fetch(url, {
      method: 'POST',
      headers,
      body: JSON.stringify({ characterId, mode: 'academic' }),
    })
    if (!res.ok) throw new Error(`创建会话失败: ${res.status}`)
    const data = await res.json()
    sessionId.value = data.id
    return sessionId.value
  } catch (err: any) {
    console.error('createSessionIfNeeded error', err)
    // 失败时留空，让 sendMsg 使用本地模拟回复
    return null
  } finally {
    creatingSession.value = false
  }
}

// ✅ 发送消息到后端会话
async function sendMsg() {
  if (!input.value.trim()) return
  const text = input.value.trim()
  // 用户消息先添加到本地
  messages.value.push({ id: Date.now(), from: 'user', text })
  input.value = ''

  // 确保有 session
  const sid = await createSessionIfNeeded()
  if (!sid) {
    // 如果无法创建 session，使用本地模拟回复作为回退
    setTimeout(() => {
      messages.value.push({ id: Date.now() + 1, from: 'ai', text: '（本地回退）收到：' + text })
    }, 600)
    return
  }

  // 发送到后端消息接口
  try {
    sendLoading.value = true
    const base = '/api'
    const url = `${base}/sessions/${sid}/messages`
    const headers: Record<string, string> = { 'Content-Type': 'application/json' }
    const auth = localStorage.getItem('Authorization')
    if (auth) headers['Authorization'] = auth
    // console.log('sendMsg request:', url, { text, modelName: modelName.value })
    const res = await fetch(url, {
      method: 'POST',
      headers,
      body: JSON.stringify({ text, modelName: modelName.value }),
    })
    if (!res.ok) throw new Error(`发送消息失败: ${res.status}`)
    // 后端返回一个包含 AI 回复字段的 JSON，例如 { reply: '...' } 或 messages
    const data = await res.json()
    // 根据常见返回结构做兼容处理
    let replyText = ''
    if (typeof data.reply === 'string') replyText = data.reply
    else if (data.message && typeof data.message === 'string') replyText = data.message
    else if (Array.isArray(data.messages) && data.messages.length > 0) {
      // 找到最后一条 ai 回复
      const aiMsg = data.messages.reverse().find((m: any) => m.from === 'ai' || m.role === 'assistant' || m.sender === 'ai')
      replyText = aiMsg ? aiMsg.text || aiMsg.content || JSON.stringify(aiMsg) : ''
    } else if (data.text) replyText = data.text
    else replyText = JSON.stringify(data)

    messages.value.push({ id: Date.now() + 2, from: 'ai', text: replyText })
  } catch (err: any) {
    console.error('sendMsg error', err)
    messages.value.push({ id: Date.now() + 3, from: 'ai', text: '（发送失败，本地回退）收到：' + text })
  } finally {
    sendLoading.value = false
  }
}

// 挂载时尝试创建会话（但不发送消息）
onMounted(async () => {
  // 优先使用 route.query.sessionId 恢复已有会话
  const sidFromQuery = (route.query.sessionId as string) || null
  if (sidFromQuery) {
    // 如果路由里有 sessionId，直接使用
    sessionId.value = sidFromQuery
    // 从会话历史中恢复聊天记录
    try {
      const base = '/api'
      const url = `${base}/sessions/${sidFromQuery}/messages`
      const headers: Record<string, string> = {}
      const auth = localStorage.getItem('Authorization')
      if (auth) headers['Authorization'] = auth
      const res = await fetch(url, { headers })
      if (res.ok) {
        const data = await res.json()
        if (Array.isArray(data)) {
          // 根据你提供的历史消息格式进行处理
          messages.value = data.map((msg: any) => ({
            id: msg.id || Date.now() + Math.random(),
            from: msg.role === 'user' ? 'user' : 'ai', // 根据role字段判断是用户还是AI
            text: msg.text // 直接使用text字段
          }))
          
          // 如果没有消息记录，保留默认欢迎消息
          if (messages.value.length === 0) {
            messages.value = [{
              id: Date.now(),
              from: 'ai',
              text: `你好，我是${name}`
            }]
          }
        }
      }
    } catch (err) {
      console.error('加载历史消息失败', err)
      // 加载失败时保持默认欢迎消息
    }
  } else {
    // fire-and-forget 创建 session，降低第一次发送延迟
    createSessionIfNeeded()
  }
})
</script>

<style scoped>
/* 顶栏样式 */
.chat-root {
  min-height: 100vh;
  background: var(--area-bg, #f8fafc);
}
.chat-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;
  height: 60px;
  background: var(--header-bg, #fff);
  border-bottom: 1px solid #e5e5e5;
}

.chat-logo {
  height: 200%;
  width: 100px;
  display: block;
  mix-blend-mode: multiply;
  background: transparent;
}

.close-btn {
  background: none;
  border: none;
  cursor: pointer;
  padding: 0;
  display: flex;
  align-items: center;
  transition: box-shadow 0.2s;
}
.close-btn:hover svg {
  box-shadow: 0 0 8px #7db7d3;
}

.chat-container {
  max-width: 800px;
  margin: 32px auto;
  padding: 24px 18px 16px 18px;
  background: var(--area-bg);
  border-radius: 16px;
  box-shadow: 0 4px 32px rgba(60, 80, 120, 0.08);
  border: 2px solid var(--border-color);
  display: flex;
  flex-direction: column;
  gap: 18px;
}
.role-info {
  display: flex;
  align-items: center;
  gap: 18px;
  margin-bottom: 8px;
}
.role-avatar {
  width: 64px;
  height: 64px;
  border-radius: 16px;
  object-fit: cover;
  border: 2px solid var(--border-color);
  background: #fff;
}
.role-detail {
  display: flex;
  flex-direction: column;
  gap: 4px;
}
.role-name {
  font-size: 1.3rem;
  font-weight: bold;
  color: var(--text-color);
}
.role-tag {
  font-size: 1rem;
  color: #38bdf8;
}
.role-desc {
  font-size: 0.98rem;
  color: #888;
}
.chat-area {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 12px;
  min-height: 320px;
  margin-bottom: 12px;
}
.chat-bubble {
  display: flex;
  align-items: flex-end;
  gap: 8px;
  margin-bottom: 2px;
}
.chat-bubble.ai {
  flex-direction: row;
  justify-content: flex-start;
}
.chat-bubble.user {
  flex-direction: row-reverse !important;
  justify-content: flex-end !important;
}
.bubble-avatar {
  width: 36px;
  height: 36px;
  border-radius: 10px;
  object-fit: cover;
  background: #fff;
  border: 1.5px solid var(--border-color);
}
.user-avatar {
  border: 1.5px solid #38bdf8;
}
.bubble-content {
  max-width: 60vw;
  padding: 10px 16px;
  border-radius: 14px;
  font-size: 1.08rem;
  background: linear-gradient(90deg, #e0f7fa 0%, #e0e7ff 100%);
  color: var(--text-color);
  box-shadow: 0 2px 12px rgba(60, 80, 120, 0.06);
  word-break: break-word;
  transition:
    background 0.2s,
    color 0.2s;
}
.chat-bubble.user .bubble-content {
  background: linear-gradient(90deg, #38bdf8 0%, #7c3aed 100%);
  color: #fff;
}
/* 暗色主题气泡适配 */
.dark-theme .bubble-content {
  background: linear-gradient(90deg, #23272f 0%, #2c2f36 100%);
  color: #e6f3ff;
}
.dark-theme .chat-bubble.user .bubble-content {
  background: linear-gradient(90deg, #38bdf8 0%, #7c3aed 100%);
  color: #fff;
}
.chat-input-bar {
  display: flex;
  gap: 14px;
  align-items: center;
  margin-top: 8px;
}
.input-group-chat {
  position: relative;
  flex: 1;
  display: flex;
  align-items: center;
}
.model-select {
  height: 44px;
  min-width: 140px;
  max-width: 140px;
  padding: 0 10px;
  margin-right: 10px;
  border-radius: 10px;
  border: 1.5px solid var(--border-color);
  background: #fff;
  color: var(--text-color, #111);
  font-size: 0.95rem;
  outline: none;
  appearance: none;
  -webkit-appearance: none;
  display: inline-flex;
  align-items: center;
}
.chat-input {
  flex: 1;
  padding: 10px 54px 10px 14px;
  border-radius: 10px;
  border: 1.5px solid var(--border-color);
  font-size: 1.18rem;
  background: #f8fafc;
  color: var(--text-color);
  outline: none;
  transition:
    border-color 0.2s,
    box-shadow 0.2s;
}
.chat-input:focus {
  border-color: #38bdf8;
  box-shadow: 0 0 0 2px #c7d2fe;
}
.chat-input::placeholder {
  color: #b0b8c7;
  opacity: 1;
}
.dark-theme .chat-input {
  background: #23272f;
  color: #e6f3ff;
}
.dark-theme .chat-input::placeholder {
  color: #a3aed6;
  opacity: 1;
}
.send-btn {
  position: absolute;
  right: 8px;
  top: 50%;
  transform: translateY(-50%);
  width: 44px;
  height: 44px;
  border: none;
  background: none;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: background 0.2s;
  z-index: 2;
}
.send-btn:hover {
  background: linear-gradient(90deg, #7c3aed 0%, #38bdf8 100%);
}
.call-btn {
  width: 44px;
  height: 44px;
  border: none;
  background: none;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition:
    background 0.2s,
    box-shadow 0.2s;
  box-shadow: none;
}
.call-btn:hover {
  background: rgba(59, 130, 246, 0.08);
  box-shadow:
    0 0 12px 0 #38bdf8,
    0 0 24px 2px #7c3aed;
}
</style>
