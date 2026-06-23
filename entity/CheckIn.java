package org.example.animalsystem.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("check_in")
public class CheckIn {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long animalId;
    private Long userId;
    private String content;
    private String imageUrl;
    private String locationDetail;
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    // 用于展示时携带用户信息，但数据库中没有这些字段
    @TableField(exist = false)
    private String userName;
    @TableField(exist = false)
    private String userAvatar;

    // 添加以下字段
    @TableField(exist = false)
    private Integer likeCount;
    @TableField(exist = false)
    private Boolean isLiked;
}