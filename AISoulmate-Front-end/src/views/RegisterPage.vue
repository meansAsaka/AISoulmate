<template>
  <div class="register-layout">
    <section class="register-illustration">
      <div class="register-graphic">
        <div class="register-window"></div>
        <div class="register-bubble small"></div>
        <div class="register-bubble big"></div>
      </div>
    </section>

    <section class="register-panel">
      <div class="register-card">
        <h2 class="register-title">创建新账号</h2>
        <form @submit.prevent="handleSubmit">
          <!-- 邮箱 -->
          <div class="input-group">
            <span class="input-icon">
              <svg
                width="20"
                height="20"
                fill="none"
                stroke="#888"
                stroke-width="2"
                viewBox="0 0 24 24"
              >
                <path d="M4 4h16v16H4z" stroke="none" />
                <path d="M4 4l8 6 8-6" />
              </svg>
            </span>
            <input type="email" v-model="email" placeholder="邮箱（唯一）" required />
          </div>

          <!-- 密码 -->
          <div class="input-group">
            <span class="input-icon">
              <svg
                width="20"
                height="20"
                fill="none"
                stroke="#888"
                stroke-width="2"
                viewBox="0 0 24 24"
              >
                <rect x="6" y="10" width="12" height="8" rx="2" />
                <path d="M12 14v2" />
                <circle cx="12" cy="12" r="1" />
              </svg>
            </span>
            <input type="password" v-model="password" placeholder="密码" required />
          </div>

          <!-- 确认密码 -->
          <div class="input-group">
            <span class="input-icon">
              <svg
                width="20"
                height="20"
                fill="none"
                stroke="#888"
                stroke-width="2"
                viewBox="0 0 24 24"
              >
                <rect x="6" y="10" width="12" height="8" rx="2" />
                <path d="M12 14v2" />
                <circle cx="12" cy="12" r="1" />
              </svg>
            </span>
            <input type="password" v-model="confirmPassword" placeholder="确认密码" required />
          </div>

          <!-- 昵称 -->
          <div class="input-group">
            <span class="input-icon">
              <svg
                width="20"
                height="20"
                fill="none"
                stroke="#888"
                stroke-width="2"
                viewBox="0 0 24 24"
              >
                <circle cx="12" cy="8" r="4" />
                <path d="M4 20c0-4 8-4 8-4s8 0 8 4" />
              </svg>
            </span>
            <input type="text" v-model="nickname" placeholder="用户名 (昵称)" required />
          </div>

          <!-- 上传头像 -->
          <div class="input-group avatar-upload-group">
            <input
              type="file"
              accept="image/*"
              @change="handleAvatarUpload"
              style="display: none"
              ref="avatarInputRef"
            />
            <div class="avatar-upload-box" @click="triggerAvatarInput">
              <template v-if="avatarUrl">
                <img :src="avatarUrl" alt="头像预览" class="avatar-preview" />
              </template>
              <template v-else>
                <span class="avatar-plus">
                  <svg width="36" height="36" viewBox="0 0 36 36" fill="none">
                    <circle cx="18" cy="18" r="16" fill="#e0e7ff" />
                    <path
                      d="M18 12v12M12 18h12"
                      stroke="#7c3aed"
                      stroke-width="2"
                      stroke-linecap="round"
                    />
                  </svg>
                </span>
              </template>
            </div>
          </div>

          <button type="submit" class="register-btn">注册</button>
        </form>
        <div class="register-tip">已有账号？<a href="/login">去登录</a></div>
      </div>
    </section>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'

const email = ref('')
const password = ref('')
const confirmPassword = ref('')
const nickname = ref('')
const avatarFile = ref<File | null>(null)
const router = useRouter()

const avatarUrl = ref<string | null>(null)
const avatarInputRef = ref<HTMLInputElement | null>(null)

const triggerAvatarInput = () => {
  if (avatarInputRef.value) {
    avatarInputRef.value.click()
  } else {
    // 兼容 label input
    const input = document.querySelector(
      '.avatar-upload-label input[type=\"file\"]',
    ) as HTMLInputElement
    input?.click()
  }
}

const handleAvatarUpload = (event: Event) => {
  const target = event.target as HTMLInputElement
  if (target.files && target.files[0]) {
    avatarFile.value = target.files[0]
    const reader = new FileReader()
    reader.onload = (e) => {
      avatarUrl.value = e.target?.result as string
    }
    reader.readAsDataURL(target.files[0])
  }
}

const handleSubmit = async () => {
  if (password.value !== confirmPassword.value) {
    alert('两次输入的密码不一致')
    return
  }

  if (email.value && password.value && nickname.value) {
    try {
      const res = await fetch('/api/user/register', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          email: email.value,
          password: password.value,
          nickname: nickname.value,
          avatar: avatarUrl.value || '', // base64字符串或空
        }),
      })
      const data = await res.json()
      if (data !== null) {
        alert('注册成功')
        router.push('/login')
      } else {
        alert(data.message || '注册失败，邮箱被使用')
      }
    } catch (e) {
      alert('网络错误或服务器异常')
    }
  } else {
    alert('请完整填写注册信息')
  }
}
</script>

<style scoped>
/* 保留原有样式，仅复用，无需修改 */
.register-layout {
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
.register-illustration {
  position: relative;
  border-radius: 16px;
  border: 2px solid rgba(255, 255, 255, 0.6);
  background: linear-gradient(135deg, rgba(255, 255, 255, 0.6), rgba(255, 255, 255, 0.3));
  overflow: hidden;
}
.register-graphic {
  position: absolute;
  inset: 0;
  background:
    radial-gradient(60% 80% at 20% 20%, rgba(120, 172, 255, 0.45), transparent),
    radial-gradient(60% 80% at 80% 70%, rgba(88, 197, 247, 0.35), transparent),
    linear-gradient(135deg, #e6f3ff 0%, #dbeaff 100%);
}
.register-window {
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
.register-bubble {
  position: absolute;
  background: rgba(255, 255, 255, 0.5);
  border: 1px solid rgba(0, 0, 0, 0.06);
  border-radius: 12px;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.06);
}
.register-bubble.small {
  left: 8%;
  bottom: 18%;
  width: 120px;
  height: 90px;
}
.register-bubble.big {
  right: 14%;
  bottom: 22%;
  width: 140px;
  height: 110px;
}
.register-panel {
  display: flex;
  align-items: center;
  justify-content: center;
}
.register-card {
  background: #fff;
  box-shadow: 0 4px 32px rgba(60, 80, 120, 0.12);
  border-radius: 18px;
  padding: 40px 32px 32px 32px;
  min-width: 340px;
  max-width: 480px;
  width: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
}
@media (max-width: 900px) {
  .register-layout {
    grid-template-columns: 1fr;
    gap: 16px;
  }
  .register-illustration {
    height: 36vh;
  }
}
.register-title {
  font-size: 2rem;
  font-weight: 700;
  color: #3a4a6b;
  margin-bottom: 28px;
  letter-spacing: 1px;
}
.register-card form {
  width: 100%;
  display: flex;
  flex-direction: column;
  gap: 18px;
}
.input-group {
  position: relative;
  display: flex;
  align-items: center;
}
.input-icon {
  position: absolute;
  left: 12px;
  top: 50%;
  transform: translateY(-50%);
  pointer-events: none;
}
.input-group input {
  width: 100%;
  padding: 10px 12px 10px 38px;
  border-radius: 10px;
  border: 1px solid #dbeafe;
  background: #f8fafc;
  font-size: 1rem;
  color: #3a4a6b;
  outline: none;
  transition:
    border-color 0.2s,
    box-shadow 0.2s;
  box-shadow: 0 1px 4px rgba(60, 80, 120, 0.04);
}
.input-group input:focus {
  border-color: #7c3aed;
  box-shadow: 0 0 0 2px #c7d2fe;
}
.register-btn {
  width: 100%;
  padding: 12px 0;
  border: none;
  border-radius: 10px;
  background: linear-gradient(90deg, #7c3aed 0%, #38bdf8 100%);
  color: #fff;
  font-size: 1.1rem;
  font-weight: 600;
  cursor: pointer;
  box-shadow: 0 2px 8px rgba(60, 80, 120, 0.08);
  transition:
    background 0.2s,
    transform 0.2s;
}
.register-btn:hover {
  background: linear-gradient(90deg, #38bdf8 0%, #7c3aed 100%);
  transform: translateY(-2px) scale(1.03);
}
.register-tip {
  margin-top: 18px;
  font-size: 0.98rem;
  color: #888;
}
.register-tip a {
  color: #7c3aed;
  text-decoration: underline;
  margin-left: 4px;
  transition: color 0.2s;
}
.register-tip a:hover {
  color: #38bdf8;
}
@media (max-width: 500px) {
  .register-card {
    padding: 24px 8px;
    min-width: unset;
    max-width: 98vw;
  }
}

/* 表单美化增强 */
.input-group {
  margin-bottom: 10px;
  transition: box-shadow 0.2s;
}

.input-group input {
  border-radius: 16px;
  background: linear-gradient(90deg, #f3f8ff 0%, #f8faff 100%);
  border: 1.5px solid #e0e7ff;
  box-shadow: 0 2px 12px rgba(120, 172, 255, 0.08);
  font-size: 1.08rem;
  padding: 12px 16px 12px 44px;
  transition:
    border-color 0.25s,
    box-shadow 0.25s,
    background 0.25s;
}

.input-group input:focus {
  border-color: #38bdf8;
  background: linear-gradient(90deg, #e0f2fe 0%, #f3f8ff 100%);
  box-shadow: 0 0 0 3px #bae6fd;
}

.input-group input:hover {
  border-color: #7c3aed;
  background: linear-gradient(90deg, #ede9fe 0%, #f3f8ff 100%);
}

.input-icon svg {
  stroke: #38bdf8;
  transition: stroke 0.2s;
}

.input-group input:focus + .input-icon svg,
.input-group input:hover + .input-icon svg {
  stroke: #7c3aed;
}

.input-group label {
  font-weight: 500;
  color: #3a4a6b;
  letter-spacing: 0.5px;
}

.input-group input[type='file'] {
  padding-left: 0;
  background: none;
  border: none;
  box-shadow: none;
}

@media (max-width: 500px) {
  .input-group input {
    font-size: 1rem;
    padding: 10px 12px 10px 38px;
  }
}
.avatar-upload-group {
  display: flex;
  justify-content: center;
  align-items: center;
  margin-bottom: 18px;
}
.avatar-upload-box {
  width: 72px;
  height: 72px;
  border-radius: 50%;
  background: linear-gradient(135deg, #f3f8ff 0%, #e0e7ff 100%);
  box-shadow: 0 2px 12px rgba(120, 172, 255, 0.1);
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  transition:
    box-shadow 0.2s,
    border 0.2s;
  border: 2px dashed #c7d2fe;
  margin-right: 16px;
  overflow: hidden;
}
.avatar-upload-box:hover {
  box-shadow: 0 0 0 4px #bae6fd;
  border-color: #7c3aed;
}
.avatar-plus {
  display: flex;
  align-items: center;
  justify-content: center;
}
.avatar-preview {
  width: 100%;
  height: 100%;
  object-fit: cover;
  border-radius: 50%;
  border: none;
}
</style>
