package com.main.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MatchTools {
    private static final Logger logger = LoggerFactory.getLogger(MatchTools.class);

    //计算比赛结果
    public static String calculateMatchResult(String rawMatchScore,String playerTeamScoreStr){
        String matchResultJava = "unknown";
        if (rawMatchScore != null && !rawMatchScore.trim().isEmpty() && playerTeamScoreStr != null && !playerTeamScoreStr.trim().isEmpty()) {
            try {
                String[] scoresStr = rawMatchScore.replace(" ", "").split("/");
                if (scoresStr.length == 2) {
                    int scorePart1 = Integer.parseInt(scoresStr[0]);
                    int scorePart2 = Integer.parseInt(scoresStr[1]);
                    int selfScoreNum = Integer.parseInt(playerTeamScoreStr);
                    int jsTeamScoreEquivalent;
                    if(scorePart1 == scorePart2){
                        matchResultJava = "draw";
                    }
                    if (scorePart1 > scorePart2) {
                        jsTeamScoreEquivalent = scorePart1;
                    }else {
                        jsTeamScoreEquivalent = scorePart2;
                    }
                    if (selfScoreNum == jsTeamScoreEquivalent) {
                        matchResultJava = "win";
                    }
                    else {
                        matchResultJava = "loss";
                    }
                }
            }catch (NumberFormatException e) {
                logger.error("解析比分时出错 i18='{}', c5='{}'", rawMatchScore, playerTeamScoreStr);
            }
        }
        return matchResultJava;
    }

    //计算Rating
    public static String calculateRating(String playerRating){
        String finalRating = "unknown";
        if(playerRating!=null){
            try {
                String[] parts = playerRating.split("\\.");
                if (parts.length == 1) {
                    finalRating = playerRating + ".0";
                } else if (parts.length == 2 && parts[1].length() <= 2) {
                    finalRating = playerRating;
                } else {
                    finalRating = playerRating;
                }
            }catch (NumberFormatException e){
                logger.error("解析玩家的Rating时出错: {}",e.getMessage());
            }
        }
        return finalRating;
    }
}
