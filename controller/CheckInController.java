//package org.example.animalsystem.controller;
//
//import org.example.animalsystem.entity.CheckIn;
//import org.example.animalsystem.service.CheckInService;
//import org.example.animalsystem.utils.JwtUtil;
//import org.example.animalsystem.vo.TimelineVO;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.bind.annotation.*;
//import org.springframework.web.multipart.MultipartFile;
//import java.io.File;
//import java.io.IOException;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.UUID;
//
//@RestController
//@RequestMapping("/api")
//public class CheckInController {
//
//    @Autowired
//    private CheckInService checkInService;
//
//    @Autowired
//    private JwtUtil jwtUtil;
//
//    // 发布打卡
//    @PostMapping("/checkins")
//    public Map<String, Object> addCheckIn(
//            @RequestParam Long animalId,
//            @RequestParam String content,
//            @RequestParam(required = false) String locationDetail,
//            @RequestParam(required = false) MultipartFile imageFile,
//            @RequestHeader(value = "Authorization", required = false) String authHeader) {
//
//        Map<String, Object> result = new HashMap<>();
//
//        // 获取当前用户ID
//        Long userId = getUserIdFromToken(authHeader);
//        if (userId == null) {
//            result.put("code", 401);
//            result.put("message", "请先登录");
//            return result;
//        }
//
//        String imageUrl = null;
//        if (imageFile != null && !imageFile.isEmpty()) {
//            imageUrl = saveFile(imageFile, "checkin");
//        }
//
//        CheckIn checkIn = new CheckIn();
//        checkIn.setAnimalId(animalId);
//        checkIn.setUserId(userId);
//        checkIn.setContent(content);
//        checkIn.setImageUrl(imageUrl);
//        checkIn.setLocationDetail(locationDetail);
//
//        if (checkInService.addCheckIn(checkIn)) {
//            result.put("code", 200);
//            result.put("message", "打卡成功");
//        } else {
//            result.put("code", 500);
//            result.put("message", "打卡失败");
//        }
//        return result;
//    }
//
//    // 获取动物打卡时间轴
//    @GetMapping("/animals/{animalId}/timeline")
//    public Map<String, Object> getTimeline(
//            @PathVariable Long animalId,
//            @RequestHeader(value = "Authorization", required = false) String authHeader) {
//
//        Long currentUserId = getUserIdFromToken(authHeader);
//        List<TimelineVO> timeline = checkInService.getTimelineByAnimalId(animalId, currentUserId);
//
//        Map<String, Object> result = new HashMap<>();
//        result.put("code", 200);
//        result.put("data", timeline);
//        return result;
//    }
//
//    private Long getUserIdFromToken(String authHeader) {
//        if (authHeader != null && authHeader.startsWith("Bearer ")) {
//            String token = authHeader.substring(7);
//            if (jwtUtil.validateToken(token)) {
//                return jwtUtil.getUserIdFromToken(token);
//            }
//        }
//        return null;
//    }
//
//    private String saveFile(MultipartFile file, String prefix) {
//        try {
//            String uploadDir = "./uploads/";
//            File dir = new File(uploadDir);
//            if (!dir.exists()) {
//                dir.mkdirs();
//            }
//
//            String originalFilename = file.getOriginalFilename();
//            String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
//            String filename = prefix + "_" + UUID.randomUUID().toString() + extension;
//
//            File destFile = new File(uploadDir + filename);
//            file.transferTo(destFile);
//
//            return "/uploads/" + filename;
//        } catch (IOException e) {
//            e.printStackTrace();
//            return null;
//        }
//    }
//}



package org.example.animalsystem.controller;

import org.example.animalsystem.entity.CheckIn;
import org.example.animalsystem.service.CheckInService;
import org.example.animalsystem.service.LikeService;
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
@RequestMapping("/api")
public class CheckInController {

    @Autowired
    private CheckInService checkInService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private LikeService likeService;

    // 发布打卡
    @PostMapping("/checkins")
    public Map<String, Object> addCheckIn(
            @RequestParam Long animalId,
            @RequestParam String content,
            @RequestParam(required = false) String locationDetail,
            @RequestParam(required = false) MultipartFile imageFile,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {

        Map<String, Object> result = new HashMap<>();

        Long userId = getUserIdFromToken(authHeader);
        if (userId == null) {
            result.put("code", 401);
            result.put("message", "请先登录");
            return result;
        }

        String imageUrl = null;
        if (imageFile != null && !imageFile.isEmpty()) {
            imageUrl = saveFile(imageFile, "checkin");
        }

        CheckIn checkIn = new CheckIn();
        checkIn.setAnimalId(animalId);
        checkIn.setUserId(userId);
        checkIn.setContent(content);
        checkIn.setImageUrl(imageUrl);
        checkIn.setLocationDetail(locationDetail);

        if (checkInService.addCheckIn(checkIn)) {
            result.put("code", 200);
            result.put("message", "打卡成功");
        } else {
            result.put("code", 500);
            result.put("message", "打卡失败");
        }
        return result;
    }

    // 获取动物打卡时间轴
    @GetMapping("/animals/{animalId}/timeline")
    public Map<String, Object> getTimeline(@PathVariable Long animalId) {
        List<CheckIn> timeline = checkInService.getTimelineByAnimalId(animalId);
        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("data", timeline);
        return result;
    }

    // 删除打卡
    @DeleteMapping("/checkins/{checkInId}")
    public Map<String, Object> deleteCheckIn(
            @PathVariable Long checkInId,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {

        Map<String, Object> result = new HashMap<>();

        Long userId = getUserIdFromToken(authHeader);
        if (userId == null) {
            result.put("code", 401);
            result.put("message", "请先登录");
            return result;
        }

        if (checkInService.deleteCheckIn(checkInId, userId)) {
            result.put("code", 200);
            result.put("message", "删除成功");
        } else {
            result.put("code", 500);
            result.put("message", "删除失败，只能删除自己的打卡");
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

//    private String saveFile(MultipartFile file, String prefix) {
//        try {
//            String uploadDir = "./uploads/";
//            File dir = new File(uploadDir);
//            if (!dir.exists()) {
//                dir.mkdirs();
//            }
//
//            String originalFilename = file.getOriginalFilename();
//            String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
//            String filename = prefix + "_" + UUID.randomUUID().toString() + extension;
//
//            File destFile = new File(uploadDir + filename);
//            file.transferTo(destFile);
//
//            return "/uploads/" + filename;
//        } catch (IOException e) {
//            e.printStackTrace();
//            return null;
//        }
//    }
    private String saveFile(MultipartFile file, String prefix) {
        try {
            // 获取项目根目录的绝对路径
            String projectPath = System.getProperty("user.dir");
            String uploadDir = projectPath + File.separator + "uploads" + File.separator;

            File dir = new File(uploadDir);
            if (!dir.exists()) {
            dir.mkdirs();
            }

            String originalFilename = file.getOriginalFilename();
            String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            String filename = prefix + "_" + UUID.randomUUID().toString() + extension;

            File destFile = new File(uploadDir + filename);
            file.transferTo(destFile);

            System.out.println("✅ 图片保存成功: " + filename);
            System.out.println("📁 保存路径: " + destFile.getAbsolutePath());
            return "/uploads/" + filename;
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("❌ 图片保存失败: " + e.getMessage());
            return null;
        }
    }

    // ========== 点赞/取消点赞 ==========
    @PostMapping("/checkins/{checkInId}/like")
    public Map<String, Object> toggleLike(
            @PathVariable Long checkInId,
            @RequestHeader("Authorization") String authHeader) {

        Map<String, Object> result = new HashMap<>();
        Long userId = getUserIdFromToken(authHeader);
        if (userId == null) {
            result.put("code", 401);
            result.put("message", "请先登录");
            return result;
        }

        if (likeService.toggleLike(checkInId, userId)) {
            int likeCount = likeService.getLikeCount(checkInId);
            boolean isLiked = likeService.isLiked(checkInId, userId);
            result.put("code", 200);
            result.put("message", "操作成功");
            result.put("likeCount", likeCount);
            result.put("isLiked", isLiked);
        } else {
            result.put("code", 500);
            result.put("message", "操作失败");
        }
        return result;
    }

    // 获取点赞状态
    @GetMapping("/checkins/{checkInId}/like/status")
    public Map<String, Object> getLikeStatus(
            @PathVariable Long checkInId,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {

        Map<String, Object> result = new HashMap<>();
        int likeCount = likeService.getLikeCount(checkInId);
        result.put("code", 200);
        result.put("likeCount", likeCount);

        Long userId = getUserIdFromToken(authHeader);
        if (userId != null) {
            result.put("isLiked", likeService.isLiked(checkInId, userId));
        } else {
            result.put("isLiked", false);
        }
        return result;
    }
}