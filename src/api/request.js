import axios from 'axios'

const request = axios.create({
    baseURL: 'http://localhost:8080/api',
    timeout: 10000
})

// 请求拦截器 - 自动添加 token
request.interceptors.request.use(
    config => {
        const token = localStorage.getItem('token')
        if (token) {
            config.headers['Authorization'] = `Bearer ${token}`
        }
        return config
    },
    error => {
        return Promise.reject(error)
    }
)

// 响应拦截器 - 统一处理错误
// request.interceptors.response.use(
//     response => {
//         const res = response.data
//         if (res.code !== 200) {
//             console.error('请求错误:', res.message)
//             return Promise.reject(new Error(res.message || '请求失败'))
//         }
//         return res
//     },
//     error => {
//         console.error('网络错误:', error.message)
//         return Promise.reject(error)
//     }
// )
request.interceptors.response.use(
    response => {
        const res = response.data
        if (res.code !== 200) {
            // 如果是401未登录，跳转到登录页
            if (res.code === 401) {
                localStorage.clear()
                window.location.href = '/login'
            }
            ElMessage.error(res.message || '请求失败')
            return Promise.reject(new Error(res.message || '请求失败'))
        }
        return res
    },
    error => {
        if (error.response) {
            const status = error.response.status
            if (status === 401) {
                localStorage.clear()
                window.location.href = '/login'
                ElMessage.error('请重新登录')
            } else if (status === 500) {
                ElMessage.error('服务器错误')
            } else {
                ElMessage.error(error.message || '网络错误')
            }
        } else {
            ElMessage.error('网络连接失败')
        }
        return Promise.reject(error)
    }
)

export default request