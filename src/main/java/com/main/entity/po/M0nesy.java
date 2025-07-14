package com.main.entity.po;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.io.Serializable;

@Data
public class M0nesy implements PlayerMatchData {


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
