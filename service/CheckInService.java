//package org.example.animalsystem.service;
//
//import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
//import org.example.animalsystem.entity.CheckIn;
//import org.example.animalsystem.mapper.CheckInMapper;
//import org.example.animalsystem.vo.TimelineVO;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//import java.util.List;
//import java.util.stream.Collectors;
//
//@Service
//public class CheckInService {
//
//    @Autowired
//    private CheckInMapper checkInMapper;
//
//    @Autowired
//    private UserService userService;
//
//    public boolean addCheckIn(CheckIn checkIn) {
//        return checkInMapper.insert(checkIn) > 0;
//    }
//
//    public List<TimelineVO> getTimelineByAnimalId(Long animalId, Long currentUserId) {
//        LambdaQueryWrapper<CheckIn> wrapper = new LambdaQueryWrapper<>();
//        wrapper.eq(CheckIn::getAnimalId, animalId)
//                .orderByDesc(CheckIn::getCreateTime);
//
//        List<CheckIn> checkIns = checkInMapper.selectList(wrapper);
//
//        return checkIns.stream().map(checkIn -> {
//            TimelineVO vo = new TimelineVO();
//            vo.setId(checkIn.getId());
//            vo.setContent(checkIn.getContent());
//            vo.setImageUrl(checkIn.getImageUrl());
//            vo.setCreateTime(checkIn.getCreateTime());
//
//            // 获取用户信息
//            var user = userService.getUserById(checkIn.getUserId());
//            if (user != null) {
//                vo.setUserName(user.getNickname());
//                vo.setUserAvatar(user.getAvatar());
//            }
//
//            return vo;
//        }).collect(Collectors.toList());
//    }
//}

package org.example.animalsystem.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.example.animalsystem.entity.CheckIn;
import org.example.animalsystem.entity.User;
import org.example.animalsystem.mapper.CheckInMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class CheckInService {

    @Autowired
    private CheckInMapper checkInMapper;

    @Autowired
    private UserService userService;

    @Autowired
    private LikeService likeService;

    public boolean addCheckIn(CheckIn checkIn) {
        return checkInMapper.insert(checkIn) > 0;
    }

    public List<CheckIn> getTimelineByAnimalId(Long animalId) {
        LambdaQueryWrapper<CheckIn> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CheckIn::getAnimalId, animalId)
                .orderByDesc(CheckIn::getCreateTime);

        List<CheckIn> checkIns = checkInMapper.selectList(wrapper);

        // 填充用户信息
        for (CheckIn checkIn : checkIns) {
            User user = userService.getUserById(checkIn.getUserId());
            if (user != null) {
                checkIn.setUserName(user.getNickname());
                checkIn.setUserAvatar(user.getAvatar());
            }
            // 添加点赞数
            checkIn.setLikeCount(likeService.getLikeCount(checkIn.getId()));
        }

        return checkIns;
    }

    // 删除打卡（只能删除自己的）
    @Transactional
    public boolean deleteCheckIn(Long checkInId, Long userId) {
        LambdaQueryWrapper<CheckIn> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CheckIn::getId, checkInId)
                .eq(CheckIn::getUserId, userId);
        return checkInMapper.delete(wrapper) > 0;
    }
}