//package org.example.animalsystem.controller;
//
//import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
//import org.example.animalsystem.entity.Animal;
//import org.example.animalsystem.service.AnimalService;
//import org.example.animalsystem.service.BrowseHistoryService;
//import org.example.animalsystem.utils.JwtUtil;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.bind.annotation.*;
//import org.springframework.web.multipart.MultipartFile;
//import java.io.File;
//import java.io.IOException;
//import java.util.HashMap;
//import java.util.Map;
//import java.util.UUID;
//
//@RestController
//@RequestMapping("/api")
//public class AnimalController {
//
//    @Autowired
//    private AnimalService animalService;
//    @Autowired
//    private BrowseHistoryService browseHistoryService;
//    @Autowired
//    private JwtUtil jwtUtil;
//
//    // 获取动物列表（分页+筛选）
//    @GetMapping("/animals")
//    public Map<String, Object> getAnimals(
//            @RequestParam(required = false) String name,
//            @RequestParam(required = false) String type,
//            @RequestParam(defaultValue = "1") Integer page,
//            @RequestParam(defaultValue = "10") Integer size) {
//
//        Page<Animal> animals = animalService.searchAnimals(name, type, page, size);
//        Map<String, Object> result = new HashMap<>();
//        result.put("code", 200);
//        result.put("data", animals);
//        return result;
//    }
//
//    // 获取动物详情
//    @GetMapping("/animals/{id}")
//    public Map<String, Object> getAnimalDetail(@PathVariable Long id) {
//        Animal animal = animalService.getAnimalById(id);
//        Map<String, Object> result = new HashMap<>();
//        if (animal != null && animal.getStatus() == 1) {
//            // 记录浏览历史
//            Long userId = getUserIdFromToken(authHeader);
//            if (userId != null) {
//                browseHistoryService.recordBrowse(userId, id);
//            }
//            result.put("code", 200);
//            result.put("data", animal);
//        } else {
//            result.put("code", 404);
//            result.put("message", "动物不存在");
//        }
//        return result;
//    }
//
//    // 管理员：添加动物
//    @PostMapping("/admin/animals")
//    public Map<String, Object> addAnimal(
//            @RequestParam String name,
//            @RequestParam String type,
//            @RequestParam String location,
//            @RequestParam(required = false) String description,
//            @RequestParam(required = false) MultipartFile coverFile) {
//
//        Map<String, Object> result = new HashMap<>();
//
//        String coverUrl = null;
//        if (coverFile != null && !coverFile.isEmpty()) {
//            coverUrl = saveFile(coverFile, "animal");
//        }
//
//        Animal animal = new Animal();
//        animal.setName(name);
//        animal.setType(type);
//        animal.setLocation(location);
//        animal.setDescription(description);
//        animal.setCoverUrl(coverUrl);
//
//        if (animalService.addAnimal(animal)) {
//            result.put("code", 200);
//            result.put("message", "添加成功");
//        } else {
//            result.put("code", 500);
//            result.put("message", "添加失败");
//        }
//        return result;
//    }
//
//    // 管理员：删除动物
//    @DeleteMapping("/admin/animals/{id}")
//    public Map<String, Object> deleteAnimal(@PathVariable Long id) {
//        Map<String, Object> result = new HashMap<>();
//        if (animalService.deleteAnimal(id)) {
//            result.put("code", 200);
//            result.put("message", "删除成功");
//        } else {
//            result.put("code", 500);
//            result.put("message", "删除失败");
//        }
//        return result;
//    }
//
//    // 文件保存方法
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
//
//    // 从Token中获取用户ID
//    private Long getUserIdFromToken(String authHeader) {
//        if (authHeader != null && authHeader.startsWith("Bearer ")) {
//            String token = authHeader.substring(7);
//            if (jwtUtil.validateToken(token)) {
//                return jwtUtil.getUserIdFromToken(token);
//            }
//        }
//        return null;
//    }
//}



package org.example.animalsystem.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.example.animalsystem.entity.Animal;
import org.example.animalsystem.service.AnimalService;
import org.example.animalsystem.service.BrowseHistoryService;
import org.example.animalsystem.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api")
public class AnimalController {

    @Autowired
    private AnimalService animalService;

    @Autowired
    private BrowseHistoryService browseHistoryService;

    @Autowired
    private JwtUtil jwtUtil;

    @GetMapping("/animals")
    public Map<String, Object> getAnimals(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String type,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {

        Page<Animal> animals = animalService.searchAnimals(name, type, page, size);
        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("data", animals);
        return result;
    }

    @GetMapping("/animals/{id}")
    public Map<String, Object> getAnimalDetail(
            @PathVariable Long id,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {

        Animal animal = animalService.getAnimalById(id);
        Map<String, Object> result = new HashMap<>();
        if (animal != null && animal.getStatus() == 1) {
            // 记录浏览历史
            Long userId = getUserIdFromToken(authHeader);
            if (userId != null) {
                browseHistoryService.recordBrowse(userId, id);
            }
            result.put("code", 200);
            result.put("data", animal);
        } else {
            result.put("code", 404);
            result.put("message", "动物不存在");
        }
        return result;
    }

    @PostMapping("/admin/animals")
    public Map<String, Object> addAnimal(
            @RequestParam String name,
            @RequestParam String type,
            @RequestParam String location,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) MultipartFile coverFile) {

        Map<String, Object> result = new HashMap<>();

        String coverUrl = null;
        if (coverFile != null && !coverFile.isEmpty()) {
            coverUrl = saveFile(coverFile, "animal");
        }

        Animal animal = new Animal();
        animal.setName(name);
        animal.setType(type);
        animal.setLocation(location);
        animal.setDescription(description);
        animal.setCoverUrl(coverUrl);

        if (animalService.addAnimal(animal)) {
            result.put("code", 200);
            result.put("message", "添加成功");
        } else {
            result.put("code", 500);
            result.put("message", "添加失败");
        }
        return result;
    }

    @DeleteMapping("/admin/animals/{id}")
    public Map<String, Object> deleteAnimal(@PathVariable Long id) {
        Map<String, Object> result = new HashMap<>();
        if (animalService.deleteAnimal(id)) {
            result.put("code", 200);
            result.put("message", "删除成功");
        } else {
            result.put("code", 500);
            result.put("message", "删除失败");
        }
        return result;
    }

    // ========== 管理员：更新动物信息 ==========
    @PutMapping("/admin/animals/{id}")
    public Map<String, Object> updateAnimal(
            @PathVariable Long id,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) MultipartFile coverFile) {

        Map<String, Object> result = new HashMap<>();

        // 检查动物是否存在
        Animal existing = animalService.getAnimalById(id);
        if (existing == null || existing.getStatus() == 0) {
            result.put("code", 404);
            result.put("message", "动物不存在");
            return result;
        }

        // 更新字段
        if (name != null && !name.trim().isEmpty()) {
            existing.setName(name);
        }
        if (type != null && !type.trim().isEmpty()) {
            existing.setType(type);
        }
        if (location != null && !location.trim().isEmpty()) {
            existing.setLocation(location);
        }
        if (description != null) {
            existing.setDescription(description);
        }

        // 如果上传了新封面图片
        if (coverFile != null && !coverFile.isEmpty()) {
            String coverUrl = saveFile(coverFile, "animal");
            if (coverUrl != null) {
                existing.setCoverUrl(coverUrl);
            }
        }

        if (animalService.updateAnimal(existing)) {
            result.put("code", 200);
            result.put("message", "更新成功");
            result.put("data", existing);
        } else {
            result.put("code", 500);
            result.put("message", "更新失败");
        }
        return result;
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
            return "/uploads/" + filename;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
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