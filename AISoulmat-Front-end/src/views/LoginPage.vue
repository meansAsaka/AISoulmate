<template>
  <div class="login-layout">
    <section class="illustration">
      <div class="graphic">
        <div class="window"></div>
        <div class="bubble small"></div>
        <div class="bubble big"></div>
      </div>
    </section>

    <section class="panel">
      <div class="card">
        <h2 class="title">AISoulmate</h2>
        <p class="subtitle">欢迎使用AISoulmate</p>

        <form class="form" @submit="handleSubmit">
          <input v-model="username" class="input" type="text" placeholder="请输入用户名" required />
          <input
            v-model="password"
            class="input"
            type="password"
            placeholder="请输入密码"
            required
          />
          <button type="submit" class="btn primary">登 录</button>
        </form>

        <div class="footer-meta"></div>
      </div>
    </section>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()

const username = ref('123')
const password = ref('123')

const handleSubmit = (e: Event) => {
  e.preventDefault()
  if (username.value === '123' && password.value === '123') {
    alert('登录成功')
    router.push({ name: 'home' })
  } else {
    alert('登录失败')
    router.push({ name: 'login' })
  }
}
</script>

<style scoped>
.login-layout {
  height: 100dvh;
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 0;
  padding: 20px;
  box-sizing: border-box;
  background:
    radial-gradient(850px 650px at -10% -20%, rgba(255, 255, 255, 0.55), rgba(255, 255, 255, 0)),
    radial-gradient(850px 650px at 110% 120%, rgba(255, 255, 255, 0.55), rgba(255, 255, 255, 0)),
    linear-gradient(90deg, #e6f3ff 0%, #dbeaff 100%);
}

.illustration {
  position: relative;
  border-radius: 16px;
  border: 2px solid rgba(255, 255, 255, 0.6);
  background: linear-gradient(135deg, rgba(255, 255, 255, 0.6), rgba(255, 255, 255, 0.3));
  overflow: hidden;
}

.graphic {
  position: absolute;
  inset: 0;
  background:
    radial-gradient(60% 80% at 20% 20%, rgba(120, 172, 255, 0.45), transparent),
    radial-gradient(60% 80% at 80% 70%, rgba(88, 197, 247, 0.35), transparent),
    linear-gradient(135deg, #e6f3ff 0%, #dbeaff 100%);
}

.window {
  position: absolute;
  left: 12%;
  top: 20%;
  width: 56%;
  height: 50%;
  border-radius: 16px;
  background: rgba(255, 255, 255, 0.55);
  border: 1px solid rgba(0, 0, 0, 0.08);
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.08);
}

.bubble {
  position: absolute;
  background: rgba(255, 255, 255, 0.5);
  border: 1px solid rgba(0, 0, 0, 0.06);
  border-radius: 12px;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.06);
}
.bubble.small {
  left: 8%;
  bottom: 18%;
  width: 120px;
  height: 90px;
}
.bubble.big {
  right: 14%;
  bottom: 22%;
  width: 140px;
  height: 110px;
}

.panel {
  display: flex;
  align-items: center;
  justify-content: center;
}

.card {
  width: 520px;
  max-width: 90%;
  padding: 28px 28px 20px;
  border-radius: 16px;
  background: rgba(255, 255, 255, 0.65);
  backdrop-filter: blur(8px);
  -webkit-backdrop-filter: blur(8px);
  border: 1px solid rgba(0, 0, 0, 0.06);
  box-shadow: 0 18px 50px rgba(30, 60, 120, 0.15);
  color: var(--text-color);
  animation: card-in 320ms ease-out;
}

.title {
  margin: 0 0 8px 0;
  font-size: 24px;
  text-align: center;
}
.subtitle {
  margin: 0 0 20px 0;
  opacity: 0.8;
  text-align: center;
}

.form {
  display: grid;
  grid-template-columns: 1fr;
  gap: 12px;
}

.input {
  height: 44px;
  padding: 0 14px 0 42px; /* 预留左侧图标空间 */
  border-radius: 10px;
  border: 1px solid rgba(0, 0, 0, 0.12);
  outline: none;
  background: rgba(255, 255, 255, 0.85);
  transition:
    box-shadow 0.2s ease,
    border-color 0.2s ease,
    background 0.2s ease;
  background-repeat: no-repeat;
  background-position: 14px center;
  background-size: 18px 18px;
}

/* 输入框图标：用户名与密码 */
.form .input[type='text'] {
  background-image: url("data:image/svg+xml;utf8,<svg xmlns='http://www.w3.org/2000/svg' width='18' height='18' viewBox='0 0 24 24' fill='none' stroke='%23899' stroke-width='2' stroke-linecap='round' stroke-linejoin='round'><path d='M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2'/><circle cx='12' cy='7' r='4'/></svg>");
}
.form .input[type='password'] {
  background-image: url("data:image/svg+xml;utf8,<svg xmlns='http://www.w3.org/2000/svg' width='18' height='18' viewBox='0 0 24 24' fill='none' stroke='%23899' stroke-width='2' stroke-linecap='round' stroke-linejoin='round'><rect x='3' y='11' width='18' height='11' rx='2' ry='2'/><path d='M7 11V7a5 5 0 0 1 10 0v4'/></svg>");
}

.input:focus {
  border-color: rgba(24, 144, 255, 0.7);
  box-shadow: 0 0 0 3px rgba(24, 144, 255, 0.2);
  background-color: rgba(255, 255, 255, 0.95);
  background-repeat: no-repeat;
}

.input::placeholder {
  color: rgba(0, 0, 0, 0.45);
}

.btn.primary {
  height: 44px;
  border: none;
  border-radius: 10px;
  background: linear-gradient(180deg, #5aa3ff 0%, #3b82f6 100%);
  color: #fff;
  font-weight: 600;
  cursor: pointer;
  box-shadow: 0 8px 18px rgba(59, 130, 246, 0.35);
  transition:
    transform 0.06s ease,
    box-shadow 0.2s ease,
    filter 0.2s ease;
}

.btn.primary:hover {
  filter: brightness(1.05);
  box-shadow: 0 10px 22px rgba(59, 130, 246, 0.42);
}
.btn.primary:active {
  transform: translateY(1px);
  box-shadow: 0 6px 14px rgba(59, 130, 246, 0.32);
}

.footer-meta {
  display: grid;
  justify-items: center;
  gap: 6px;
  margin-top: 18px;
  font-size: 12px;
  opacity: 0.8;
}

/* 暗色主题适配 */
.dark-theme .card {
  background: rgba(0, 0, 0, 0.45);
  border-color: rgba(255, 255, 255, 0.08);
  box-shadow: 0 18px 50px rgba(0, 0, 0, 0.35);
}
.dark-theme .input {
  background-color: rgba(0, 0, 0, 0.35);
  border-color: rgba(255, 255, 255, 0.12);
  color: #fff;
}
.dark-theme .form .input[type='text'] {
  background-image: url("data:image/svg+xml;utf8,<svg xmlns='http://www.w3.org/2000/svg' width='18' height='18' viewBox='0 0 24 24' fill='none' stroke='%23fff' stroke-width='2' stroke-linecap='round' stroke-linejoin='round'><path d='M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2'/><circle cx='12' cy='7' r='4'/></svg>");
}
.dark-theme .form .input[type='password'] {
  background-image: url("data:image/svg+xml;utf8,<svg xmlns='http://www.w3.org/2000/svg' width='18' height='18' viewBox='0 0 24 24' fill='none' stroke='%23fff' stroke-width='2' stroke-linecap='round' stroke-linejoin='round'><rect x='3' y='11' width='18' height='11' rx='2' ry='2'/><path d='M7 11V7a5 5 0 0 1 10 0v4'/></svg>");
}
.dark-theme .input::placeholder {
  color: rgba(255, 255, 255, 0.6);
}

@keyframes card-in {
  from {
    opacity: 0;
    transform: translateY(6px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@media (max-width: 960px) {
  .login-layout {
    grid-template-columns: 1fr;
    gap: 16px;
  }
  .illustration {
    height: 36vh;
  }
}
</style>
