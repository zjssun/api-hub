package com.main.entity.po;

import java.io.Serializable;

public class ThePaper implements Serializable {
    private String coutId;
    private String title;
    private String url;

    public String getcoutId() {
        return coutId;
    }

    public void setcoutId(String coutId) {
        this.coutId = coutId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
