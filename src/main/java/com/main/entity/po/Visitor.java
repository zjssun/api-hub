package com.main.entity.po;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class Visitor implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;

    /** 小程序端生成的访客标识（visitor_info.uid） */
    private String uid;

    private LocalDateTime firstSeen;
    private LocalDateTime lastSeen;
    private String lastIp;
    private Integer visitCount;
}
