package com.main.entity.po;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class User implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private String email;
    private String nickname;
    private String passwordHash;
    private String avatar;

    /** 档位预留：free/pro/…，后续付费扩容只改此值 */
    private String plan;

    private boolean admin;

    /** 个性化额度预留（MB），非空优先于档位额度 */
    private Long customQuotaMb;

    private LocalDateTime createdAt;
}
