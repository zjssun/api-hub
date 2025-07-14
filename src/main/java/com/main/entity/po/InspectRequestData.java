package com.main.entity.po;

import lombok.Data;

import java.util.List;

@Data
public class InspectRequestData {
    public String itemId;
    public String rarity;
    public String gunIndex;
    public String skinIndex;
    public String nameTag;
    public String statTrakCount;
    public String pattern;
    public String wear;
    public String musicIndex;
    public String entIndex;
    public String petIndex;
    public CharmData charm;
    public List<StickerData> stickers;
}
