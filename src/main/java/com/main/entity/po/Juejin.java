package com.main.entity.po;

import lombok.Data;

import java.io.Serializable;

@Data
public class Juejin implements Serializable {
    private String title;
    private String content_id;
    private String url;
}
