package com.main.entity.po;

import lombok.Data;

import java.io.Serializable;

@Data
public class ThePaper implements Serializable {
    private String coutId;
    private String title;
    private String url;
}
