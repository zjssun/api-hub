package com.main.entity.po;

import java.util.List;

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

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public String getRarity() {
        return rarity;
    }

    public void setRarity(String rarity) {
        this.rarity = rarity;
    }

    public String getGunIndex() {
        return gunIndex;
    }

    public void setGunIndex(String gunIndex) {
        this.gunIndex = gunIndex;
    }

    public String getSkinIndex() {
        return skinIndex;
    }

    public void setSkinIndex(String skinIndex) {
        this.skinIndex = skinIndex;
    }

    public String getNameTag() {
        return nameTag;
    }

    public void setNameTag(String nameTag) {
        this.nameTag = nameTag;
    }

    public String getStatTrakCount() {
        return statTrakCount;
    }

    public void setStatTrakCount(String statTrakCount) {
        this.statTrakCount = statTrakCount;
    }

    public String getPattern() {
        return pattern;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    public String getWear() {
        return wear;
    }

    public void setWear(String wear) {
        this.wear = wear;
    }

    public CharmData getCharm() {
        return charm;
    }

    public void setCharm(CharmData charm) {
        this.charm = charm;
    }

    public List<StickerData> getStickers() {
        return stickers;
    }

    public void setStickers(List<StickerData> stickers) {
        this.stickers = stickers;
    }

    public String getMusicIndex() {
        return musicIndex;
    }

    public void setMusicIndex(String musicIndex) {
        this.musicIndex = musicIndex;
    }

    public String getEntindex() {
        return entIndex;
    }

    public void setEntindex(String entIndex) {
        this.entIndex = entIndex;
    }

    public String getPetindex() {
        return petIndex;
    }

    public void setPetindex(String petIndex) {
        this.petIndex = petIndex;
    }
}
