package org.example.animalsystem.controller;

import org.example.animalsystem.entity.Animal;
import org.example.animalsystem.entity.User;
import org.example.animalsystem.service.BrowseHistoryService;
import org.example.animalsystem.service.UserService;
import org.example.animalsystem.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private BrowseHistoryService browseHistoryService;

    // 获取当前用户信息
    @GetMapping("/info")
    public Map<String, Object> getUserInfo(@RequestHeader("Authorization") String authHeader) {
        Map<String, Object> result = new HashMap<>();
        Long userId = getUserIdFromToken(authHeader);
        if (userId == null) {
            result.put("code", 401);
            result.put("message", "未登录");
            return result;
        }

        User user = userService.getUserById(userId);
        if (user != null) {
            user.setPassword(null); // 不返回密码
            result.put("code", 200);
            result.put("data", user);
        } else {
            result.put("code", 404);
            result.put("message", "用户不存在");
        }
        return result;
    }

    // 修改昵称
    @PutMapping("/nickname")
    public Map<String, Object> updateNickname(
            @RequestBody Map<String, String> params,
            @RequestHeader("Authorization") String authHeader) {

        Map<String, Object> result = new HashMap<>();
        Long userId = getUserIdFromToken(authHeader);
        if (userId == null) {
            result.put("code", 401);
            result.put("message", "未登录");
            return result;
        }

        String nickname = params.get("nickname");
        if (userService.updateNickname(userId, nickname)) {
            result.put("code", 200);
            result.put("message", "昵称修改成功");
        } else {
            result.put("code", 500);
            result.put("message", "修改失败");
        }
        return result;
    }

    // 修改头像
    @PostMapping("/avatar")
    public Map<String, Object> updateAvatar(
            @RequestParam("avatarFile") MultipartFile avatarFile,
            @RequestHeader("Authorization") String authHeader) {

        Map<String, Object> result = new HashMap<>();
        Long userId = getUserIdFromToken(authHeader);
        if (userId == null) {
            result.put("code", 401);
            result.put("message", "未登录");
            return result;
        }

        try {
            String avatarUrl = saveAvatar(avatarFile);
            if (userService.updateAvatar(userId, avatarUrl)) {
                result.put("code", 200);
                result.put("message", "头像更新成功");
                result.put("data", avatarUrl);
            } else {
                result.put("code", 500);
                result.put("message", "头像更新失败");
            }
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", "头像上传失败");
        }
        return result;
    }

    @GetMapping("/history")
    public Map<String, Object> getHistory(@RequestHeader("Authorization") String authHeader) {
        Map<String, Object> result = new HashMap<>();
        Long userId = getUserIdFromToken(authHeader);
        if (userId == null) {
            result.put("code", 401);
            result.put("message", "未登录");
            return result;
        }

        List<Animal> history = browseHistoryService.getBrowseHistory(userId);
        result.put("code", 200);
        result.put("data", history);
        return result;
    }

    private Long getUserIdFromToken(String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            if (jwtUtil.validateToken(token)) {
                return jwtUtil.getUserIdFromToken(token);
            }
        }
        return null;
    }

    private String saveAvatar(MultipartFile file) {
        try {
            String uploadDir = "./uploads/avatar/";
            File dir = new File(uploadDir);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            String originalFilename = file.getOriginalFilename();
            String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            String filename = "avatar_" + System.currentTimeMillis() + extension;

            File destFile = new File(uploadDir + filename);
            file.transferTo(destFile);

            return "/uploads/avatar/" + filename;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}