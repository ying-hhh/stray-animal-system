<template>
  <div class="user-center">
    <div class="container">
      <!-- 返回按钮 -->
      <div class="back-btn">
        <el-button @click="router.back()">
          <el-icon><ArrowLeft /></el-icon> 返回
        </el-button>
      </div>
      
      <!-- 用户信息卡片 -->
      <el-card class="user-card">
        <div class="user-header">
          <!-- 头像区域 -->
          <div class="avatar-section">
            <el-avatar 
              :size="100" 
              :src="userInfo?.avatar ? getImageUrl(userInfo.avatar) : ''"
            >
              {{ userInfo?.nickname?.charAt(0) || '?' }}
            </el-avatar>
            <el-upload
              action="#"
              :auto-upload="false"
              :show-file-list="false"
              :on-change="handleAvatarChange"
              class="avatar-upload"
            >
              <el-button size="small" type="primary">
                <el-icon><Upload /></el-icon> 更换头像
              </el-button>
            </el-upload>
          </div>
          
          <!-- 用户信息 -->
          <div class="user-info">
            <div class="info-row">
              <span class="label">用户名：</span>
              <span>{{ userInfo?.username }}</span>
            </div>
            
            <!-- ========== 昵称修改 ========== -->
            <div class="info-row">
              <span class="label">昵称：</span>
              <span v-if="!editingNickname">{{ userInfo?.nickname }}</span>
              <el-input 
                v-else 
                v-model="newNickname" 
                size="small" 
                style="width: 150px"
                @keyup.enter="saveNickname"
              />
              <el-button 
                size="small" 
                :type="editingNickname ? 'primary' : ''"
                @click="toggleEditNickname"
              >
                {{ editingNickname ? '保存' : '修改' }}
              </el-button>
            </div>
            
            <div class="info-row">
              <span class="label">角色：</span>
              <el-tag :type="userInfo?.role === 'admin' ? 'danger' : 'info'">
                {{ userInfo?.role === 'admin' ? '管理员' : '普通用户' }}
              </el-tag>
            </div>
          </div>
        </div>
      </el-card>
      
      <!-- ========== 浏览历史 ========== -->
      <el-card class="history-card">
        <template #header>
          <span>📖 最近浏览</span>
        </template>
        
        <div v-if="historyLoading" class="loading-history">
          <el-skeleton :rows="3" animated />
        </div>
        
        <div v-else-if="history.length === 0" class="empty-history">
          <el-empty description="暂无浏览记录" />
        </div>
        
        <div v-else class="history-list">
          <div 
            v-for="item in history" 
            :key="item.id" 
            class="history-item"
            @click="goToDetail(item.id)"
          >
            <img :src="getImageUrl(item.coverUrl)" class="history-image" />
            <div class="history-info">
              <span class="history-name">{{ item.name }}</span>
              <span class="history-location">
                <el-icon><Location /></el-icon> {{ item.location }}
              </span>
              <el-tag :type="item.type === 'cat' ? 'success' : 'warning'" size="small">
                {{ item.type === 'cat' ? '🐱 猫咪' : '🐶 狗狗' }}
              </el-tag>
            </div>
            <el-icon class="history-arrow"><ArrowRight /></el-icon>
          </div>
        </div>
      </el-card>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import request from '../api/request'

const router = useRouter()

const userInfo = ref(null)
const history = ref([])
const historyLoading = ref(false)

// 昵称编辑
const editingNickname = ref(false)
const newNickname = ref('')

// ========== 获取用户信息 ==========
const getUserInfo = async () => {
  try {
    const res = await request.get('/user/info')
    if (res.code === 200) {
      userInfo.value = res.data
      newNickname.value = res.data.nickname
    }
  } catch (error) {
    ElMessage.error('获取用户信息失败')
  }
}

// ========== 获取浏览历史 ==========
const getHistory = async () => {
  historyLoading.value = true
  try {
    const res = await request.get('/user/history')
    if (res.code === 200) {
      history.value = res.data || []
    }
  } catch (error) {
    console.error('获取浏览历史失败', error)
  } finally {
    historyLoading.value = false
  }
}

// ========== 修改昵称 ==========
const toggleEditNickname = () => {
  if (editingNickname.value) {
    saveNickname()
  } else {
    editingNickname.value = true
  }
}

const saveNickname = async () => {
  if (!newNickname.value.trim()) {
    ElMessage.warning('昵称不能为空')
    return
  }
  
  try {
    const res = await request.put('/user/nickname', { nickname: newNickname.value })
    if (res.code === 200) {
      ElMessage.success('昵称修改成功')
      userInfo.value.nickname = newNickname.value
      editingNickname.value = false
      // 更新本地存储
      localStorage.setItem('nickname', newNickname.value)
    }
  } catch (error) {
    ElMessage.error('修改失败')
  }
}

// ========== 修改头像 ==========
const handleAvatarChange = async (file) => {
  const formData = new FormData()
  formData.append('avatarFile', file.raw)
  
  try {
    const res = await request.post('/user/avatar', formData, {
      headers: { 'Content-Type': 'multipart/form-data' }
    })
    if (res.code === 200) {
      ElMessage.success('头像更新成功')
      userInfo.value.avatar = res.data
      // 刷新页面显示新头像
      await getUserInfo()
    }
  } catch (error) {
    ElMessage.error('头像更新失败')
  }
}

// ========== 工具函数 ==========
const getImageUrl = (url) => {
  if (!url) return ''
  if (url.startsWith('http')) return url
  return `http://localhost:8080${url}`
}

const goToDetail = (id) => {
  router.push(`/animal/${id}`)
}

// ========== 生命周期 ==========
onMounted(() => {
  getUserInfo()
  getHistory()
})
</script>

<style scoped>
.user-center {
  min-height: 100vh;
  background: #f5f7fa;
  padding: 20px 0 50px 0;
}

.container {
  max-width: 800px;
  margin: 0 auto;
  padding: 0 20px;
}

.back-btn {
  margin-bottom: 20px;
}

.user-card {
  margin-bottom: 20px;
}

.user-header {
  display: flex;
  gap: 40px;
  align-items: center;
  padding: 10px 0;
}

.avatar-section {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12px;
}

.avatar-upload {
  display: flex;
  justify-content: center;
}

.user-info {
  flex: 1;
}

.info-row {
  margin-bottom: 14px;
  display: flex;
  align-items: center;
  gap: 10px;
}

.info-row .label {
  font-weight: bold;
  color: #666;
  min-width: 60px;
}

.history-card {
  margin-top: 10px;
}

.history-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.history-item {
  display: flex;
  align-items: center;
  gap: 15px;
  padding: 12px 15px;
  border-radius: 10px;
  cursor: pointer;
  transition: all 0.3s;
  border: 1px solid #f0f0f0;
}

.history-item:hover {
  background: #f5f7fa;
  border-color: #667eea;
  transform: translateX(5px);
}

.history-image {
  width: 70px;
  height: 55px;
  object-fit: cover;
  border-radius: 8px;
  flex-shrink: 0;
}

.history-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.history-name {
  font-weight: bold;
  font-size: 16px;
}

.history-location {
  color: #666;
  font-size: 13px;
  display: flex;
  align-items: center;
  gap: 3px;
}

.history-arrow {
  color: #ccc;
  font-size: 18px;
}

.empty-history {
  padding: 30px 0;
}

.loading-history {
  padding: 20px 0;
}
</style>