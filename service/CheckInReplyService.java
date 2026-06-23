package org.example.animalsystem.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.example.animalsystem.entity.CheckInReply;
import org.example.animalsystem.entity.User;
import org.example.animalsystem.mapper.CheckInReplyMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class CheckInReplyService {

    @Autowired
    private CheckInReplyMapper replyMapper;

    @Autowired
    private UserService userService;

    public boolean addReply(CheckInReply reply) {
        return replyMapper.insert(reply) > 0;
    }

    public List<CheckInReply> getRepliesByCheckInId(Long checkInId) {
        LambdaQueryWrapper<CheckInReply> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CheckInReply::getCheckInId, checkInId)
                .orderByAsc(CheckInReply::getCreateTime);

        List<CheckInReply> replies = replyMapper.selectList(wrapper);

        // 填充用户信息
        for (CheckInReply reply : replies) {
            User user = userService.getUserById(reply.getUserId());
            if (user != null) {
                reply.setUserName(user.getNickname());
                reply.setUserAvatar(user.getAvatar());
            }
        }
        return replies;
    }

    public boolean deleteReply(Long replyId, Long userId) {
        LambdaQueryWrapper<CheckInReply> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CheckInReply::getId, replyId)
                .eq(CheckInReply::getUserId, userId);
        return replyMapper.delete(wrapper) > 0;
    }
}