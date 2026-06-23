// import { createRouter, createWebHistory } from 'vue-router'

// const router = createRouter({
//   history: createWebHistory(import.meta.env.BASE_URL),
//   routes: [],
// })

// export default router


import { createRouter, createWebHistory } from 'vue-router'

const routes = [
    {
        path: '/login',
        name: 'Login',
        component: () => import('../views/Login.vue')
    },
    {
        path: '/',
        name: 'Home',
        component: () => import('../views/Home.vue'),
        meta: { requiresAuth: true }
    },
    {
        path: '/animal/:id',
        name: 'AnimalDetail',
        component: () => import('../views/AnimalDetail.vue'),
        meta: { requiresAuth: true }
    },
    {
        path: '/admin',
        name: 'Admin',
        component: () => import('../views/Admin.vue'),
        meta: { requiresAuth: true, role: 'admin' }
    },
    {
        path: '/usercenter',
        name: 'UserCenter',
        component: () => import('../views/UserCenter.vue'),
        meta: { requiresAuth: true }
    }
]

const router = createRouter({
    history: createWebHistory(),
    routes
})

// 路由守卫
router.beforeEach((to, from, next) => {
    const token = localStorage.getItem('token')
    const userRole = localStorage.getItem('userRole')
    
    if (to.meta.requiresAuth && !token) {
        next('/login')
    } else if (to.meta.role === 'admin' && userRole !== 'admin') {
        next('/')
    } else {
        next()
    }
})

export default router

