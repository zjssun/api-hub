package com.main.entity.po;

import lombok.Data;

import java.io.Serializable;

@Data
public class Tskr implements Serializable {
    private String id;
    private String title;
    private String url;
}
