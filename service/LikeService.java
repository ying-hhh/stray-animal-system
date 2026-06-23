package org.example.animalsystem.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.example.animalsystem.entity.LikeRecord;
import org.example.animalsystem.mapper.LikeRecordMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LikeService {

    @Autowired
    private LikeRecordMapper likeRecordMapper;

    public int getLikeCount(Long checkInId) {
        return likeRecordMapper.countByCheckInId(checkInId);
    }

    public boolean isLiked(Long checkInId, Long userId) {
        LambdaQueryWrapper<LikeRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(LikeRecord::getCheckInId, checkInId)
                .eq(LikeRecord::getUserId, userId);
        return likeRecordMapper.selectCount(wrapper) > 0;
    }

    @Transactional
    public boolean toggleLike(Long checkInId, Long userId) {
        LambdaQueryWrapper<LikeRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(LikeRecord::getCheckInId, checkInId)
                .eq(LikeRecord::getUserId, userId);

        LikeRecord existing = likeRecordMapper.selectOne(wrapper);
        if (existing != null) {
            // 取消点赞
            return likeRecordMapper.delete(wrapper) > 0;
        } else {
            // 添加点赞
            LikeRecord record = new LikeRecord();
            record.setCheckInId(checkInId);
            record.setUserId(userId);
            return likeRecordMapper.insert(record) > 0;
        }
    }
}