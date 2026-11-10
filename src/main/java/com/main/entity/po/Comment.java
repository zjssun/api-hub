package com.main.entity.po;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class Comment implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private String visitorUid;
    private String nickname;
    private String avatar;
    private String content;
    private String ip;
    private LocalDateTime createdAt;
}
