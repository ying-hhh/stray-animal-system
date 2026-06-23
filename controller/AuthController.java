package org.example.animalsystem.controller;

import org.example.animalsystem.entity.User;
import org.example.animalsystem.service.UserService;
import org.example.animalsystem.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody Map<String, String> params) {
        String username = params.get("username");
        String password = params.get("password");

        User user = userService.login(username, password);
        Map<String, Object> result = new HashMap<>();

        if (user != null) {
            String token = jwtUtil.generateToken(user.getId(), user.getUsername(), user.getRole());
            result.put("code", 200);
            result.put("message", "登录成功");
            Map<String, Object> data = new HashMap<>();
            data.put("token", token);
            data.put("user", user);
            result.put("data", data);
        } else {
            result.put("code", 401);
            result.put("message", "用户名或密码错误");
        }
        return result;
    }

    @PostMapping("/register")
    public Map<String, Object> register(@RequestBody User user) {
        Map<String, Object> result = new HashMap<>();

        if (userService.checkUsernameExists(user.getUsername())) {
            result.put("code", 400);
            result.put("message", "用户名已存在");
        } else if (userService.register(user)) {
            result.put("code", 200);
            result.put("message", "注册成功");
        } else {
            result.put("code", 500);
            result.put("message", "注册失败");
        }
        return result;
    }
}