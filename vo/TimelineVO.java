package org.example.animalsystem.vo;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class TimelineVO {
    private Long id;
    private String content;
    private String imageUrl;
    private String userName;
    private String userAvatar;
    private LocalDateTime createTime;
    private Integer likeCount;
    private Boolean isLiked;
}