package com.main.entity.po;

import lombok.Data;

@Data
public class Boros implements PlayerMatchData {
    private String time;

    private String nickName;

    private String team;

    private String matchMap;

    private String matchScore;

    private String matchResult;

    private String matchId;

    private String roomUrl;

    private String bestOf;

    private String effectiveRanking;

    private String totalKills;

    private String totalDeaths;

    private String totalAssistsl;

    private String rating;

    private String tripleKill;

    private String quadroKill;

    private String pentaKill;

    private String timestamp;

    private String adr;
}
