package com.main.entity.po;

import lombok.Data;

import java.io.Serializable;

@Data
public class Douban implements Serializable {
    private String Name;
    private String Score;
    private String Url;
}
