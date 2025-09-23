import { createRouter, createWebHistory } from 'vue-router'
import HomeView from '../views/HomePage.vue'
import LoginView from '../views/LoginPage.vue'
import RegisterView from '../views/RegisterPage.vue'
import ChatView from '../views/ChatPage.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'home',
      component: HomeView,
    },
    {
      path: '/login',
      name: 'login',
      component: LoginView,
    },
    {
      path: '/register',
      name: 'register',
      component: RegisterView,
    },
    {
      path: '/chat',
      name: 'chat',
      component: ChatView,
    },
  ],
})

export default router
