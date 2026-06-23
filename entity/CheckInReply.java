package org.example.animalsystem.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("check_in_reply")
public class CheckInReply {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long checkInId;
    private Long userId;
    private String content;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    // 非数据库字段，用于展示
    @TableField(exist = false)
    private String userName;

    @TableField(exist = false)
    private String userAvatar;
}