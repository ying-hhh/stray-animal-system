<template>
  <div class="admin">
    <div class="navbar">
      <div class="nav-container">
        <h2 class="logo">🔧 管理后台 - 动物图鉴管理</h2>
        <div class="nav-right">
          <el-button @click="router.push('/')">返回首页</el-button>
        </div>
      </div>
    </div>
    
    <div class="container">
      <!-- 添加动物表单 -->
      <div class="add-form">
        <h3>➕ 添加新动物</h3>
        <el-form :model="addForm" label-width="100px">
          <el-form-item label="名字">
            <el-input v-model="addForm.name" placeholder="请输入动物名字" />
          </el-form-item>
          <el-form-item label="类型">
            <el-radio-group v-model="addForm.type">
              <el-radio label="cat">猫咪</el-radio>
              <el-radio label="dog">狗狗</el-radio>
            </el-radio-group>
          </el-form-item>
          <el-form-item label="常驻区域">
            <el-input v-model="addForm.location" placeholder="如：图书馆草坪" />
          </el-form-item>
          <el-form-item label="描述">
            <el-input v-model="addForm.description" type="textarea" :rows="3" />
          </el-form-item>
          <el-form-item label="封面图片">
            <el-upload
              action="#"
              :auto-upload="false"
              :show-file-list="false"
              :on-change="handleCoverChange"
            >
              <el-button>
                <el-icon><Upload /></el-icon> 选择图片
              </el-button>
            </el-upload>
            <span v-if="coverFile" class="file-name">{{ coverFile.name }}</span>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="submitAddAnimal" :loading="adding">添加动物</el-button>
          </el-form-item>
        </el-form>
      </div>
      
      <!-- 动物列表管理 -->
      <div class="animal-list">
        <h3>📋 动物列表管理</h3>
        <el-table :data="animals" stripe>
          <el-table-column prop="id" label="ID" width="80" />
          <el-table-column prop="name" label="名字" width="120" />
          <el-table-column prop="type" label="类型" width="100">
            <template #default="{ row }">
              {{ row.type === 'cat' ? '猫咪' : '狗狗' }}
            </template>
          </el-table-column>
          <el-table-column prop="location" label="常驻区域" />
          <el-table-column prop="createTime" label="创建时间" width="180">
            <template #default="{ row }">
              {{ formatTime(row.createTime) }}
            </template>
          </el-table-column>
          <el-table-column label="操作" width="150">
            <template #default="{ row }">
              <el-button type="primary" size="small" @click="openEditDialog(row)">编辑</el-button>
              <el-button type="danger" size="small" @click="deleteAnimal(row.id)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
        
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
    </div>

    <!-- ========== 编辑对话框 ========== -->
    <el-dialog 
      v-model="editDialogVisible" 
      title="✏️ 编辑动物信息" 
      width="500px"
      :close-on-click-modal="false"
    >
      <el-form :model="editForm" label-width="80px">
        <el-form-item label="名字">
          <el-input v-model="editForm.name" placeholder="请输入动物名字" />
        </el-form-item>
        
        <el-form-item label="类型">
          <el-radio-group v-model="editForm.type">
            <el-radio label="cat">猫咪</el-radio>
            <el-radio label="dog">狗狗</el-radio>
          </el-radio-group>
        </el-form-item>
        
        <el-form-item label="常驻区域">
          <el-input v-model="editForm.location" placeholder="如：图书馆草坪" />
        </el-form-item>
        
        <el-form-item label="描述">
          <el-input 
            v-model="editForm.description" 
            type="textarea" 
            :rows="3" 
            placeholder="请输入动物描述"
          />
        </el-form-item>
        
        <el-form-item label="封面图片">
          <div class="edit-image-preview">
            <img 
              v-if="editForm.coverUrl" 
              :src="getImageUrl(editForm.coverUrl)" 
              class="edit-preview-img"
            />
          </div>
          <el-upload
            action="#"
            :auto-upload="false"
            :show-file-list="false"
            :on-change="handleEditCoverChange"
          >
            <el-button size="small">
              <el-icon><Upload /></el-icon> 更换图片
            </el-button>
          </el-upload>
          <span v-if="editCoverFile" class="file-name">{{ editCoverFile.name }}</span>
          <div v-if="editForm.coverUrl" class="current-image-hint">当前图片已显示</div>
        </el-form-item>
      </el-form>
      
      <template #footer>
        <el-button @click="editDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitEditAnimal" :loading="editing">
          保存修改
        </el-button>
      </template>
    </el-dialog>

  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { animalApi } from '../api/animal'

const router = useRouter()

const animals = ref([])
const page = ref(1)
const size = ref(10)
const total = ref(0)

const addForm = ref({
  name: '',
  type: 'cat',
  location: '',
  description: ''
})
const coverFile = ref(null)
const adding = ref(false)

// ========== 编辑相关 ==========
const editDialogVisible = ref(false)
const editing = ref(false)
const editForm = ref({
  id: null,
  name: '',
  type: 'cat',
  location: '',
  description: '',
  coverUrl: ''
})
const editCoverFile = ref(null)

const loadAnimals = async () => {
  try {
    const res = await animalApi.getAnimals({
      page: page.value,
      size: size.value
    })
    animals.value = res.data.records
    total.value = res.data.total
  } catch (error) {
    console.error('加载动物列表失败', error)
  }
}

const handleCoverChange = (file) => {
  coverFile.value = file.raw
}

const submitAddAnimal = async () => {
  if (!addForm.value.name || !addForm.value.location) {
    ElMessage.warning('请填写完整信息')
    return
  }
  if (!coverFile.value) {
    ElMessage.warning('请选择封面图片')
    return
  }
  
  adding.value = true
  const formData = new FormData()
  formData.append('name', addForm.value.name)
  formData.append('type', addForm.value.type)
  formData.append('location', addForm.value.location)
  formData.append('description', addForm.value.description)
  formData.append('coverFile', coverFile.value)
  
  try {
    await animalApi.addAnimal(formData)
    ElMessage.success('添加成功')
    addForm.value = { name: '', type: 'cat', location: '', description: '' }
    coverFile.value = null
    await loadAnimals()
  } catch (error) {
    ElMessage.error('添加失败')
  } finally {
    adding.value = false
  }
}

const deleteAnimal = async (id) => {
  await ElMessageBox.confirm('确定要删除这只动物吗？删除后相关打卡记录也会被删除！', '警告', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  })
  
  try {
    await animalApi.deleteAnimal(id)
    ElMessage.success('删除成功')
    await loadAnimals()
  } catch (error) {
    ElMessage.error('删除失败')
  }
}

// ========== 编辑动物 ==========
const openEditDialog = (row) => {
  editForm.value = {
    id: row.id,
    name: row.name,
    type: row.type,
    location: row.location,
    description: row.description || '',
    coverUrl: row.coverUrl
  }
  editCoverFile.value = null
  editDialogVisible.value = true
}

const handleEditCoverChange = (file) => {
  editCoverFile.value = file.raw
  // 预览新图片
  const reader = new FileReader()
  reader.onload = (e) => {
    editForm.value.coverUrl = e.target.result
  }
  reader.readAsDataURL(file.raw)
}

const submitEditAnimal = async () => {
  if (!editForm.value.name || !editForm.value.location) {
    ElMessage.warning('请填写完整信息')
    return
  }
  
  editing.value = true
  const formData = new FormData()
  formData.append('name', editForm.value.name)
  formData.append('type', editForm.value.type)
  formData.append('location', editForm.value.location)
  formData.append('description', editForm.value.description)
  if (editCoverFile.value) {
    formData.append('coverFile', editCoverFile.value)
  }
  
  try {
    const res = await animalApi.updateAnimal(editForm.value.id, formData)
    if (res.code === 200) {
      ElMessage.success('修改成功')
      editDialogVisible.value = false
      await loadAnimals()
    } else {
      ElMessage.error(res.message || '修改失败')
    }
  } catch (error) {
    ElMessage.error('修改失败')
  } finally {
    editing.value = false
  }
}

// ========== 工具函数 ==========
const getImageUrl = (url) => {
  if (!url) return ''
  if (url.startsWith('http')) return url
  if (url.startsWith('data:image')) return url
  return `http://localhost:8080${url}`
}

const formatTime = (time) => {
  if (!time) return ''
  return new Date(time).toLocaleString()
}

onMounted(() => {
  loadAnimals()
})
</script>

<style scoped>
.admin {
  min-height: 100vh;
  background: #f5f7fa;
}

.navbar {
  background: white;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
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

.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 30px 20px;
}

.add-form {
  background: white;
  border-radius: 12px;
  padding: 25px;
  margin-bottom: 30px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.add-form h3 {
  margin: 0 0 20px 0;
}

.file-name {
  margin-left: 10px;
  font-size: 12px;
  color: #666;
}

.animal-list {
  background: white;
  border-radius: 12px;
  padding: 25px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.animal-list h3 {
  margin: 0 0 20px 0;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: center;
}

.edit-image-preview {
  margin-bottom: 10px;
}

.edit-preview-img {
  width: 150px;
  height: 100px;
  object-fit: cover;
  border-radius: 8px;
  border: 1px solid #e8e8e8;
}

.current-image-hint {
  font-size: 12px;
  color: #999;
  margin-top: 5px;
}
</style>