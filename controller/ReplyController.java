package org.example.animalsystem.controller;

import org.example.animalsystem.entity.CheckInReply;
import org.example.animalsystem.service.CheckInReplyService;
import org.example.animalsystem.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reply")
public class ReplyController {

    @Autowired
    private CheckInReplyService replyService;

    @Autowired
    private JwtUtil jwtUtil;

    // 添加回复
    @PostMapping
    public Map<String, Object> addReply(
            @RequestBody Map<String, Object> params,
            @RequestHeader("Authorization") String authHeader) {

        Map<String, Object> result = new HashMap<>();
        Long userId = getUserIdFromToken(authHeader);
        if (userId == null) {
            result.put("code", 401);
            result.put("message", "请先登录");
            return result;
        }

        Long checkInId = Long.valueOf(params.get("checkInId").toString());
        String content = params.get("content").toString();

        if (content.trim().isEmpty()) {
            result.put("code", 400);
            result.put("message", "回复内容不能为空");
            return result;
        }

        CheckInReply reply = new CheckInReply();
        reply.setCheckInId(checkInId);
        reply.setUserId(userId);
        reply.setContent(content);

        if (replyService.addReply(reply)) {
            result.put("code", 200);
            result.put("message", "回复成功");
        } else {
            result.put("code", 500);
            result.put("message", "回复失败");
        }
        return result;
    }

    // 获取打卡的所有回复
    @GetMapping("/{checkInId}")
    public Map<String, Object> getReplies(@PathVariable Long checkInId) {
        List<CheckInReply> replies = replyService.getRepliesByCheckInId(checkInId);
        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("data", replies);
        return result;
    }

    // 删除回复
    @DeleteMapping("/{replyId}")
    public Map<String, Object> deleteReply(
            @PathVariable Long replyId,
            @RequestHeader("Authorization") String authHeader) {

        Map<String, Object> result = new HashMap<>();
        Long userId = getUserIdFromToken(authHeader);
        if (userId == null) {
            result.put("code", 401);
            result.put("message", "请先登录");
            return result;
        }

        if (replyService.deleteReply(replyId, userId)) {
            result.put("code", 200);
            result.put("message", "删除成功");
        } else {
            result.put("code", 500);
            result.put("message", "删除失败，只能删除自己的回复");
        }
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
}