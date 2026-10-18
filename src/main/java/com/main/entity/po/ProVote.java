package com.main.entity.po;

import lombok.Data;

import java.io.Serializable;

@Data
public class ProVote implements Serializable {
    private static final long serialVersionUID = 1L;

    private String nameKey;
    private String name;
    private Integer votes;
}
