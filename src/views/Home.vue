<template>
  <div class="home">
    <!-- 头部导航栏 -->
    <div class="navbar">
      <div class="nav-container">
        <h2 class="logo">🐾 校园流浪动物图鉴</h2>
        <div class="nav-right">
          <span class="welcome">欢迎，{{ nickname }}</span>
          
          <!-- 用户下拉菜单 -->
          <el-dropdown @command="handleCommand" trigger="click" class="user-dropdown">
            <div class="avatar-wrapper">
              <el-avatar 
                :size="42" 
                :src="userAvatar ? getImageUrl(userAvatar) : ''"
                class="user-avatar"
              >
                {{ nickname?.charAt(0) || '?' }}
              </el-avatar>
              <!-- <el-icon class="dropdown-arrow"><ArrowDown /></el-icon> -->
            </div>
    
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="usercenter">
                  <el-icon><User /></el-icon> 个人中心
                </el-dropdown-item>
                <el-dropdown-item v-if="isAdmin" command="admin" divided>
                  <el-icon><Setting /></el-icon> 管理后台
                </el-dropdown-item>
                <el-dropdown-item command="logout" divided>
                  <el-icon><SwitchButton /></el-icon> 退出登录
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </div>
    </div>
    
    <!-- 搜索栏 -->
    <div class="search-bar">
      <el-input 
        v-model="searchName" 
        placeholder="搜索动物名字" 
        clearable 
        style="width: 300px; margin-right: 15px"
        @clear="loadAnimals"
        @keyup.enter="loadAnimals"
      >
        <template #prefix>
          <el-icon><Search /></el-icon>
        </template>
      </el-input>
      
      <el-select v-model="searchType" placeholder="选择类型" clearable style="width: 150px; margin-right: 15px" @change="loadAnimals">
        <el-option label="猫咪" value="cat" />
        <el-option label="狗狗" value="dog" />
      </el-select>
      
      <el-button type="primary" @click="loadAnimals">搜索</el-button>
    </div>
    
    <!-- 动物卡片列表 -->
    <div class="animal-grid">
      <el-card 
        v-for="animal in animals" 
        :key="animal.id" 
        class="animal-card"
        shadow="hover"
        @click="goToDetail(animal.id)"
      >
        <img :src="getImageUrl(animal.coverUrl)" class="animal-cover" :alt="animal.name" />
        <div class="animal-info">
          <h3>{{ animal.name }}</h3>
          <p class="type">
            <el-tag :type="animal.type === 'cat' ? 'success' : 'warning'" size="small">
              {{ animal.type === 'cat' ? '🐱 猫咪' : '🐶 狗狗' }}
            </el-tag>
          </p>
          <p class="location"><el-icon><Location /></el-icon> {{ animal.location }}</p>
        </div>
      </el-card>
    </div>
    
    <!-- 分页 -->
    <div class="pagination">
      <el-pagination 
        v-model:current-page="page" 
        :page-size="size" 
        :total="total" 
        layout="prev, pager, next"
        @current-change="loadAnimals"
      />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessageBox } from 'element-plus'
import { animalApi } from '../api/animal'
import request from '../api/request'

const router = useRouter()
const nickname = localStorage.getItem('nickname')
const userRole = localStorage.getItem('userRole')
const isAdmin = computed(() => userRole === 'admin')

const userAvatar = ref('')
const animals = ref([])
const page = ref(1)
const size = ref(12)
const total = ref(0)
const searchName = ref('')
const searchType = ref('')

// ========== 获取用户头像 ==========
const getUserInfo = async () => {
  try {
    const res = await request.get('/user/info')
    if (res.code === 200) {
      userAvatar.value = res.data.avatar || ''
    }
  } catch (error) {
    console.error('获取用户信息失败', error)
  }
}

// ========== 下拉菜单命令处理 ==========
const handleCommand = (command) => {
  switch (command) {
    case 'usercenter':
      router.push('/usercenter')
      break
    case 'admin':
      router.push('/admin')
      break
    case 'logout':
      handleLogout()
      break
    default:
      break
  }
}

// ========== 退出登录 ==========
const handleLogout = () => {
  ElMessageBox.confirm('确定要退出登录吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消'
  }).then(() => {
    localStorage.clear()
    router.push('/login')
  })
}

// ========== 加载动物列表 ==========
const loadAnimals = async () => {
  try {
    const res = await animalApi.getAnimals({
      name: searchName.value || undefined,
      type: searchType.value || undefined,
      page: page.value,
      size: size.value
    })
    animals.value = res.data.records
    total.value = res.data.total
  } catch (error) {
    console.error('加载动物列表失败', error)
  }
}

// ========== 工具函数 ==========
const getImageUrl = (url) => {
  if (!url) return 'https://via.placeholder.com/300x200/667eea/ffffff?text=🐾'
  if (url.startsWith('http')) return url
  return `http://localhost:8080${url}`
}

const goToDetail = (id) => {
  router.push(`/animal/${id}`)
}

// ========== 生命周期 ==========
onMounted(() => {
  loadAnimals()
  getUserInfo()
})
</script>

<style scoped>
.home {
  min-height: 100vh;
  background: #f5f7fa;
}

.navbar {
  background: white;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  position: sticky;
  top: 0;
  z-index: 100;
}

.nav-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 15px 20px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.logo {
  margin: 0;
  color: #667eea;
}

.nav-right {
  display: flex;
  gap: 15px;
  align-items: center;
  flex-wrap: wrap;
}

.welcome {
  color: #666;
}

/* ========== 用户下拉菜单 ========== */
.user-dropdown {
  cursor: pointer;
}

.avatar-wrapper {
  display: flex;
  align-items: center;
  gap: 4px;
  padding: 4px 8px 4px 4px;
  border-radius: 24px;
  transition: background 0.3s ease;
}

.avatar-wrapper:hover {
  background: rgba(102, 126, 234, 0.08);
}

.user-avatar {
  transition: all 0.3s ease;
  border: 2px solid #667eea;
  box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.15);
  flex-shrink: 0;
}

.user-avatar:hover {
  transform: scale(1.05);
  box-shadow: 0 0 0 4px rgba(102, 126, 234, 0.25), 0 0 20px rgba(102, 126, 234, 0.15);
}

.dropdown-arrow {
  color: #999;
  font-size: 14px;
  transition: transform 0.3s ease;
}

.user-dropdown:hover .dropdown-arrow {
  color: #667eea;
}

/* 下拉菜单项样式 */
.el-dropdown-menu .el-dropdown-item {
  padding: 10px 20px;
  font-size: 14px;
}

.el-dropdown-menu .el-dropdown-item .el-icon {
  margin-right: 10px;
  font-size: 16px;
  vertical-align: middle;
}

.search-bar {
  max-width: 1200px;
  margin: 30px auto;
  padding: 0 20px;
  display: flex;
  align-items: center;
}

.animal-grid {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 25px;
}

.animal-card {
  cursor: pointer;
  transition: transform 0.3s, box-shadow 0.3s;
}

.animal-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.15);
}

.animal-cover {
  width: 100%;
  height: 200px;
  object-fit: contain;
  border-radius: 8px 8px 0 0;
}

.animal-info {
  padding: 15px;
}

.animal-info h3 {
  margin: 0 0 10px 0;
  font-size: 18px;
  color: #333;
}

.type {
  margin: 0 0 8px 0;
}

.location {
  margin: 0;
  color: #666;
  font-size: 14px;
  display: flex;
  align-items: center;
  gap: 5px;
}

.pagination {
  display: flex;
  justify-content: center;
  padding: 30px 0;
}
</style>