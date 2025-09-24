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
          <input v-model="input" class="chat-input" type="text" :placeholder="`和${name}聊天...`" />
          <button class="send-btn" type="submit" title="发送">
            <svg width="38" height="38" viewBox="0 0 1024 1024" fill="none">
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
import { ref } from 'vue'
import { useRoute } from 'vue-router'

// ✅ 获取路由参数
const route = useRoute()
const name = (route.query.name as string) || 'AI'
const avatar = (route.query.avatar as string) || '/Logo.ico'
const tag = (route.query.tag as string) || ''
const desc = (route.query.desc as string) || ''

// ✅ 消息列表
const input = ref('')
const messages = ref([
  {
    id: Date.now(),
    from: 'ai',
    text: `你好，我是${name}`,
  },
])

function startVoiceCall() {
  // TODO: WebSocket语音功能实现
  alert('语音通话功能开发中...')
}

// ✅ 发送消息
function sendMsg() {
  if (!input.value.trim()) return
  // 用户消息
  messages.value.push({
    id: Date.now(),
    from: 'user',
    text: input.value,
  })
  const userText = input.value
  input.value = ''
  // 模拟 AI 回复
  setTimeout(() => {
    messages.value.push({
      id: Date.now() + 1,
      from: 'ai',
      text: '收到：' + userText,
    })
  }, 600)
}
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
