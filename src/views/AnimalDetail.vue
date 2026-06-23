<template>
  <div class="animal-detail">
    <div class="container">
      <!-- 返回按钮 -->
      <div class="back-btn">
        <el-button @click="router.back()">
          <el-icon><ArrowLeft /></el-icon> 返回
        </el-button>
      </div>
      
      <!-- 动物基本信息 -->
      <div class="animal-info-card" v-if="animal">
        <div class="info-header">
          <img :src="getImageUrl(animal.coverUrl)" class="animal-cover-large" />
          <div class="info-content">
            <h1>{{ animal.name }}</h1>
            <div class="tags">
              <el-tag :type="animal.type === 'cat' ? 'success' : 'warning'">
                {{ animal.type === 'cat' ? '🐱 猫咪' : '🐶 狗狗' }}
              </el-tag>
              <el-tag type="info">
                <el-icon><Location /></el-icon> {{ animal.location }}
              </el-tag>
            </div>
            <p class="description">{{ animal.description || '暂无描述' }}</p>
          </div>
        </div>
      </div>
      
      <!-- 打卡表单 -->
      <div class="checkin-form">
        <h3>📝 发布近况打卡</h3>
        <el-input 
          v-model="checkinContent" 
          type="textarea" 
          :rows="4" 
          placeholder="写下你遇到这只小动物的故事..."
        />
        <div class="form-actions">
          <el-upload
            action="#"
            :auto-upload="false"
            :show-file-list="false"
            :on-change="handleImageChange"
          >
            <el-button>
              <el-icon><Picture /></el-icon> 上传图片
            </el-button>
          </el-upload>
          <el-button type="primary" @click="submitCheckin" :loading="submitting">
            发布打卡
          </el-button>
        </div>
        <span v-if="uploadImage" class="image-name">{{ uploadImage.name }}</span>
      </div>
      
      <!-- ========== 时间轴 ========== -->
      <div class="timeline">
        <h3>📅 历史足迹时间轴</h3>
        <div v-if="timeline.length === 0" class="empty-timeline">
          <el-empty description="还没有打卡记录，快去发布第一条吧~" />
        </div>
        <div v-else class="timeline-list">
          <div v-for="item in timeline" :key="item.id" class="timeline-item">
            <div class="timeline-avatar">
              <el-avatar :size="40" :src="item.userAvatar ? getImageUrl(item.userAvatar) : ''">
                {{ item.userName?.charAt(0) || '?' }}
              </el-avatar>
            </div>
            
            <div class="timeline-content">
              <div class="timeline-header">
                <span class="user-name">{{ item.userName || '未知用户' }}</span>
                <span class="time">⏰ {{ formatTime(item.createTime) }}</span>
              </div>
              
              <p class="content">{{ item.content }}</p>
              <img v-if="item.imageUrl" :src="getImageUrl(item.imageUrl)" class="timeline-image" />
              
              <!-- ========== 点赞和回复按钮 ========== -->
              <div class="timeline-actions">
                <el-button 
                  size="small" 
                  :type="item.isLiked ? 'primary' : ''"
                  @click="handleLike(item)"
                >
                  <el-icon><Star /></el-icon> 
                  {{ item.likeCount || 0 }}
                </el-button>
                
                <el-button size="small" @click="showReplyInput(item.id)">
                  <el-icon><ChatLineRound /></el-icon> 回复
                </el-button>
                
                <el-button 
                  v-if="item.userId == currentUserId" 
                  type="danger" 
                  size="small" 
                  link
                  @click="deleteCheckin(item.id)"
                >
                  <el-icon><Delete /></el-icon> 删除
                </el-button>
              </div>
              
              <!-- ========== 回复输入框 ========== -->
              <div v-if="replyInputVisible === item.id" class="reply-input">
                <el-input 
                  v-model="replyContent" 
                  placeholder="输入回复内容..." 
                  size="small"
                  @keyup.enter="submitReply(item.id)"
                />
                <el-button size="small" type="primary" @click="submitReply(item.id)">发送</el-button>
                <el-button size="small" @click="replyInputVisible = null">取消</el-button>
              </div>
              
              <!-- ========== 回复列表 ========== -->
              <div v-if="replies[item.id] && replies[item.id].length > 0" class="reply-list">
                <div v-for="reply in replies[item.id]" :key="reply.id" class="reply-item">
                  <span class="reply-user">{{ reply.userName || '未知' }}</span>
                  <span class="reply-content">{{ reply.content }}</span>
                  <span class="reply-time">{{ formatTime(reply.createTime) }}</span>
                  <el-button 
                    v-if="reply.userId == currentUserId" 
                    type="danger" 
                    size="small" 
                    link
                    @click="deleteReply(reply.id, item.id)"
                  >
                    删除
                  </el-button>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { animalApi } from '../api/animal'

const route = useRoute()
const router = useRouter()
const animalId = route.params.id

const currentUserId = computed(() => localStorage.getItem('userId'))

const animal = ref(null)
const timeline = ref([])
const checkinContent = ref('')
const uploadImage = ref(null)
const submitting = ref(false)

// 回复相关
const replyContent = ref('')
const replyInputVisible = ref(null)
const replies = ref({})

// ========== 加载数据 ==========
const loadAnimalDetail = async () => {
  try {
    const res = await animalApi.getAnimalDetail(animalId)
    animal.value = res.data
  } catch (error) {
    ElMessage.error('加载动物信息失败')
    router.back()
  }
}

const loadTimeline = async () => {
  try {
    const res = await animalApi.getTimeline(animalId)
    timeline.value = res.data || []
    // 加载每个打卡的点赞状态和回复
    for (const item of timeline.value) {
      await loadLikeStatus(item)
      await loadReplies(item.id)
    }
  } catch (error) {
    console.error('加载时间轴失败', error)
  }
}

// ========== 点赞 ==========
const loadLikeStatus = async (item) => {
  try {
    const res = await animalApi.getLikeStatus(item.id)
    item.likeCount = res.likeCount || 0
    item.isLiked = res.isLiked || false
  } catch (error) {
    console.error('加载点赞状态失败', error)
  }
}

const handleLike = async (item) => {
  try {
    const res = await animalApi.toggleLike(item.id)
    item.likeCount = res.likeCount || 0
    item.isLiked = res.isLiked || false
  } catch (error) {
    ElMessage.error('操作失败')
  }
}

// ========== 回复 ==========
const showReplyInput = (checkInId) => {
  replyInputVisible.value = replyInputVisible.value === checkInId ? null : checkInId
  replyContent.value = ''
}

const loadReplies = async (checkInId) => {
  try {
    const res = await animalApi.getReplies(checkInId)
    replies.value[checkInId] = res.data || []
  } catch (error) {
    console.error('加载回复失败', error)
  }
}

const submitReply = async (checkInId) => {
  if (!replyContent.value.trim()) {
    ElMessage.warning('请输入回复内容')
    return
  }
  
  try {
    await animalApi.addReply({
      checkInId: checkInId,
      content: replyContent.value
    })
    ElMessage.success('回复成功')
    replyContent.value = ''
    replyInputVisible.value = null
    await loadReplies(checkInId)
  } catch (error) {
    ElMessage.error('回复失败')
  }
}

const deleteReply = async (replyId, checkInId) => {
  try {
    await ElMessageBox.confirm('确定要删除这条回复吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await animalApi.deleteReply(replyId)
    ElMessage.success('删除成功')
    await loadReplies(checkInId)
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

// ========== 打卡 ==========
const handleImageChange = (file) => {
  uploadImage.value = file.raw
}

const submitCheckin = async () => {
  if (!checkinContent.value.trim()) {
    ElMessage.warning('请输入打卡内容')
    return
  }
  
  submitting.value = true
  const formData = new FormData()
  formData.append('animalId', animalId)
  formData.append('content', checkinContent.value)
  if (uploadImage.value) {
    formData.append('imageFile', uploadImage.value)
  }
  
  try {
    await animalApi.addCheckIn(formData)
    ElMessage.success('打卡成功')
    checkinContent.value = ''
    uploadImage.value = null
    await loadTimeline()
  } catch (error) {
    ElMessage.error('打卡失败')
  } finally {
    submitting.value = false
  }
}

const deleteCheckin = async (checkinId) => {
  try {
    await ElMessageBox.confirm('确定要删除这条打卡记录吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await animalApi.deleteCheckIn(checkinId)
    ElMessage.success('删除成功')
    await loadTimeline()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

// ========== 工具函数 ==========
const getImageUrl = (url) => {
  if (!url) return ''
  if (url.startsWith('http')) return url
  return `http://localhost:8080${url}`
}

const formatTime = (time) => {
  if (!time) return '刚刚'
  try {
    const date = new Date(time)
    if (isNaN(date.getTime())) return '刚刚'
    return date.toLocaleString('zh-CN', {
      year: 'numeric',
      month: '2-digit',
      day: '2-digit',
      hour: '2-digit',
      minute: '2-digit'
    })
  } catch (e) {
    return '刚刚'
  }
}

// ========== 生命周期 ==========
onMounted(() => {
  loadAnimalDetail()
  loadTimeline()
})
</script>

<style scoped>
/* ... 原有样式 ... */
.animal-detail {
  min-height: 100vh;
  background: #f5f7fa;
  padding-bottom: 50px;
}

.container {
  max-width: 1000px;
  margin: 0 auto;
  padding: 20px;
}

.back-btn {
  margin-bottom: 20px;
}

.animal-info-card {
  background: white;
  border-radius: 16px;
  padding: 30px;
  margin-bottom: 30px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.info-header {
  display: flex;
  gap: 30px;
}

.animal-cover-large {
  width: 300px;
  height: 250px;
  object-fit: cover;
  border-radius: 12px;
}

.info-content {
  flex: 1;
}

.info-content h1 {
  margin: 0 0 15px 0;
  font-size: 28px;
}

.tags {
  display: flex;
  gap: 10px;
  margin-bottom: 20px;
}

.description {
  color: #666;
  line-height: 1.6;
}

.checkin-form {
  background: white;
  border-radius: 16px;
  padding: 25px;
  margin-bottom: 30px;
}

.checkin-form h3 {
  margin: 0 0 15px 0;
}

.form-actions {
  margin-top: 15px;
  display: flex;
  gap: 15px;
}

.image-name {
  display: inline-block;
  margin-top: 10px;
  font-size: 12px;
  color: #666;
}

.timeline {
  background: white;
  border-radius: 16px;
  padding: 25px;
}

.timeline h3 {
  margin: 0 0 20px 0;
}

.timeline-list {
  display: flex;
  flex-direction: column;
  gap: 25px;
}

.timeline-item {
  display: flex;
  gap: 15px;
  padding-bottom: 20px;
  border-bottom: 1px solid #eee;
}

.timeline-content {
  flex: 1;
}

.timeline-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.user-name {
  font-weight: bold;
  color: #333;
}

.time {
  font-size: 12px;
  color: #999;
}

.content {
  margin: 0 0 10px 0;
  line-height: 1.5;
}

.timeline-image {
  max-width: 200px;
  border-radius: 8px;
  margin-top: 10px;
}

/* ========== 新增：删除按钮样式 ========== */
.timeline-actions {
  margin-top: 10px;
}

.empty-timeline {
  padding: 40px;
}

/* ========== 新增样式 ========== */
.timeline-actions {
  margin-top: 10px;
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
}

.reply-input {
  margin-top: 10px;
  display: flex;
  gap: 10px;
  align-items: center;
}

.reply-input .el-input {
  flex: 1;
}

.reply-list {
  margin-top: 10px;
  padding-left: 20px;
  border-left: 2px solid #e8e8e8;
}

.reply-item {
  padding: 6px 0;
  font-size: 14px;
  display: flex;
  align-items: center;
  gap: 8px;
  flex-wrap: wrap;
}

.reply-user {
  font-weight: bold;
  color: #409EFF;
}

.reply-content {
  color: #333;
}

.reply-time {
  font-size: 12px;
  color: #999;
}
</style>