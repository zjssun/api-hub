package com.main.entity.po;

import java.io.Serializable;

public class Douban implements Serializable {
    private String Name;
    private String Score;
    private String Url;

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getScore() {
        return Score;
    }

    public void setScore(String score) {
        Score = score;
    }

    public String getUrl() {
        return Url;
    }

    public void setUrl(String url) {
        Url = url;
    }
}
