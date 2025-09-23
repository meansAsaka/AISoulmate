<template>
  <div class="register-container">
    <h1>注册</h1>
    <form>
      <label>
        用户名:
        <input type="text" name="username" required />
      </label>
      <label>
        密码:
        <input type="password" name="password" required />
      </label>
      <button type="submit" @click="handleSubmit">注册</button>
    </form>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'

const username = ref('')
const password = ref('')

const handleSubmit = (e: Event) => {
  e.preventDefault()
  if (username.value === '123' && password.value === '123') {
    alert('注册成功')
  } else {
    alert('注册失败')
  }
}
</script>

<style scoped>
.register-container {
  max-width: 400px;
  margin: 0 auto;
  padding: 20px;
  border: 1px solid #ccc;
  border-radius: 5px;
}
.register-container h1 {
  text-align: center;
}
.register-container form {
  display: flex;
  flex-direction: column;
}
.register-container label {
  margin-bottom: 10px;
}
.register-container input {
  padding: 5px;
  margin-bottom: 10px;
}
.register-container button {
  padding: 10px;
  background-color: #007bff;
  color: #fff;
  border: none;
  border-radius: 5px;
  cursor: pointer;
}
.register-container button:hover {
  background-color: #0056b3;
}
</style>
