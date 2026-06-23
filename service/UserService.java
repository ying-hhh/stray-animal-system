//package org.example.animalsystem.service;
//
//import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
//import org.example.animalsystem.entity.User;
//import org.example.animalsystem.mapper.UserMapper;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//@Service
//public class UserService {
//
//    @Autowired
//    private UserMapper userMapper;
//
//    public User login(String username, String password) {
//        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
//        wrapper.eq(User::getUsername, username)
//                .eq(User::getPassword, password);
//        return userMapper.selectOne(wrapper);
//    }
//
//    public User getUserById(Long id) {
//        return userMapper.selectById(id);
//    }
//
//    public boolean register(User user) {
//        user.setRole("user");
//        return userMapper.insert(user) > 0;
//    }
//
//    public boolean checkUsernameExists(String username) {
//        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
//        wrapper.eq(User::getUsername, username);
//        return userMapper.selectCount(wrapper) > 0;
//    }
//}


package org.example.animalsystem.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import org.example.animalsystem.entity.User;
import org.example.animalsystem.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
public class UserService {

    @Autowired
    private UserMapper userMapper;

    public User login(String username, String password) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, username)
                .eq(User::getPassword, password);
        return userMapper.selectOne(wrapper);
    }

    public User getUserById(Long id) {
        return userMapper.selectById(id);
    }

    public boolean register(User user) {
        user.setRole("user");
        return userMapper.insert(user) > 0;
    }

    public boolean checkUsernameExists(String username) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, username);
        return userMapper.selectCount(wrapper) > 0;
    }

    // 修改昵称
    @Transactional
    public boolean updateNickname(Long userId, String nickname) {
        if (!StringUtils.hasText(nickname)) {
            return false;
        }
        User user = new User();
        user.setId(userId);
        user.setNickname(nickname);
        return userMapper.updateById(user) > 0;
    }

    // 修改头像
    @Transactional
    public boolean updateAvatar(Long userId, String avatarUrl) {
        User user = new User();
        user.setId(userId);
        user.setAvatar(avatarUrl);
        return userMapper.updateById(user) > 0;
    }

    public User getUserByUsername(String username) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, username);
        return userMapper.selectOne(wrapper);
    }
}