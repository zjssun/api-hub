package com.main.entity.po;


import java.io.Serializable;

public interface  PlayerMatchData extends Serializable {
    void setTime(String time);
    void setNickName(String nickName);
    void setTeam(String team);
    void setMatchMap(String matchMap);
    void setMatchScore(String matchScore);
    void setMatchResult(String matchResult);
    void setMatchId(String matchId);
    void setRoomUrl(String roomUrl);
    void setBestOf(String bestOf);
    void setEffectiveRanking(String effectiveRanking);
    void setTotalKills(String totalKills);
    void setTotalDeaths(String totalDeaths);
    void setTotalAssistsl(String totalAssistsl); // 依然是这个名称
    void setRating(String rating);
    void setTripleKill(String tripleKill);
    void setQuadroKill(String quadroKill);
    void setPentaKill(String pentaKill);
    void setTimestamp(String timestamp);
    void setAdr(String adr);
}