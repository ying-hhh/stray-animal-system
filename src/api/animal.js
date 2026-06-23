import request from './request'

export const animalApi = {
    // 获取动物列表
    getAnimals(params) {
        return request.get('/animals', { params })
    },
    
    // 获取动物详情
    getAnimalDetail(id) {
        return request.get(`/animals/${id}`)
    },
    
    // 获取打卡时间轴
    getTimeline(animalId) {
        return request.get(`/animals/${animalId}/timeline`)
    },
    
    // 管理员：添加动物
    addAnimal(formData) {
        return request.post('/admin/animals', formData, {
            headers: { 'Content-Type': 'multipart/form-data' }
        })
    },
    
    // 管理员：删除动物
    deleteAnimal(id) {
        return request.delete(`/admin/animals/${id}`)
    },

    // 管理员：更新动物信息
    updateAnimal(id, formData) {
        return request.put(`/admin/animals/${id}`, formData, {
            headers: { 'Content-Type': 'multipart/form-data' }
        })
    },
    
    // 发布打卡
    addCheckIn(formData) {
        return request.post('/checkins', formData, {
            headers: { 'Content-Type': 'multipart/form-data' }
        })
    },

    // 删除打卡
    deleteCheckIn(checkInId) {
        return request.delete(`/checkins/${checkInId}`)
    },

    // 点赞
    toggleLike(checkInId) {
        return request.post(`/checkins/${checkInId}/like`)
    },
    
    getLikeStatus(checkInId) {
        return request.get(`/checkins/${checkInId}/like/status`)
    },
    
    // 回复
    addReply(data) {
        return request.post('/reply', data)
    },
    
    getReplies(checkInId) {
        return request.get(`/reply/${checkInId}`)
    },
    
    deleteReply(replyId) {
        return request.delete(`/reply/${replyId}`)
    }
}