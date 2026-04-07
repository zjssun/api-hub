package com.main.task;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.main.entity.enums.PlayerEnum;
import com.main.entity.po.PlayerMatch;
import com.main.utils.MatchTools;

import java.util.Map;
import java.util.Optional;

final class FaceitMatchParser {
    private static final String UNKNOWN = "unknown";

    Optional<HistoryMatchContext> parseHistoryMatch(PlayerEnum playerEnum, JSONObject historyItem) {
        if (historyItem == null) {
            return Optional.empty();
        }

        String matchId = historyItem.getString("match_id");
        if (matchId == null || matchId.isBlank()) {
            return Optional.empty();
        }

        JSONObject teamsObject = historyItem.getJSONObject("teams");
        Optional<PlayerHistoryInfo> playerHistoryInfo = findPlayerInHistoryTeams(teamsObject, playerEnum.getUuid());
        if (playerHistoryInfo.isEmpty()) {
            return Optional.empty();
        }

        PlayerMatch playerMatch = new PlayerMatch();
        playerMatch.setNickName(playerHistoryInfo.get().nickname());
        playerMatch.setTeam(playerHistoryInfo.get().teamKey());
        playerMatch.setMatchId(matchId);
        playerMatch.setRoomUrl("https://www.faceit.com/en/cs2/room/" + matchId + "/scoreboard");
        playerMatch.setTimestamp(toTimestampString(historyItem.getLong("started_at")));
        playerMatch.setMatchScore(buildMatchScore(historyItem.getJSONObject("results"), playerHistoryInfo.get().teamKey()));
        playerMatch.setMatchResult(resolveMatchResult(historyItem.getJSONObject("results"), playerHistoryInfo.get().teamKey()));
        return Optional.of(new HistoryMatchContext(playerMatch, playerHistoryInfo.get().teamKey()));
    }

    Optional<PlayerMatch> applyMatchStats(PlayerEnum playerEnum, HistoryMatchContext historyMatchContext, JSONObject statsResponse) {
        if (historyMatchContext == null || statsResponse == null) {
            return Optional.empty();
        }

        JSONArray rounds = statsResponse.getJSONArray("rounds");
        if (rounds == null || rounds.isEmpty()) {
            return Optional.empty();
        }

        JSONObject firstRound = rounds.getJSONObject(0);
        if (firstRound == null) {
            return Optional.empty();
        }

        PlayerMatch playerMatch = historyMatchContext.match();
        playerMatch.setBestOf(firstRound.getString("best_of"));

        JSONObject roundStats = firstRound.getJSONObject("round_stats");
        if (roundStats != null) {
            playerMatch.setMatchMap(roundStats.getString("Map"));
        }

        Optional<JSONObject> playerStats = findPlayerStats(firstRound.getJSONArray("teams"), playerEnum.getUuid());
        if (playerStats.isEmpty()) {
            return Optional.empty();
        }

        JSONObject playerStatsObject = playerStats.get();
        playerMatch.setTotalKills(playerStatsObject.getString("Kills"));
        playerMatch.setTotalDeaths(playerStatsObject.getString("Deaths"));
        playerMatch.setTotalAssists(playerStatsObject.getString("Assists"));
        playerMatch.setRating(MatchTools.calculateRating(playerStatsObject.getString("K/R Ratio")));
        playerMatch.setTripleKill(playerStatsObject.getString("Triple Kills"));
        playerMatch.setQuadroKill(playerStatsObject.getString("Quadro Kills"));
        playerMatch.setPentaKill(playerStatsObject.getString("Penta Kills"));
        playerMatch.setAdr(playerStatsObject.getString("ADR"));
        return Optional.of(playerMatch);
    }

    String parseEffectiveRanking(JSONObject matchResponse, String teamKey) {
        if (matchResponse == null || teamKey == null || teamKey.isBlank()) {
            return UNKNOWN;
        }

        JSONObject teamsObject = matchResponse.getJSONObject("teams");
        if (teamsObject == null) {
            return UNKNOWN;
        }

        JSONObject teamObject = teamsObject.getJSONObject(teamKey);
        if (teamObject == null) {
            return UNKNOWN;
        }

        JSONObject statsObject = teamObject.getJSONObject("stats");
        if (statsObject == null) {
            return UNKNOWN;
        }

        Object rating = statsObject.get("rating");
        return rating == null ? UNKNOWN : String.valueOf(rating);
    }

    record HistoryMatchContext(PlayerMatch match, String teamKey) {
    }

    private Optional<PlayerHistoryInfo> findPlayerInHistoryTeams(JSONObject teamsObject, String playerId) {
        if (teamsObject == null || playerId == null || playerId.isBlank()) {
            return Optional.empty();
        }

        for (Map.Entry<String, Object> entry : teamsObject.entrySet()) {
            JSONObject teamObject = teamsObject.getJSONObject(entry.getKey());
            if (teamObject == null) {
                continue;
            }

            JSONArray players = teamObject.getJSONArray("players");
            if (players == null) {
                continue;
            }

            for (Object playerObject : players) {
                if (!(playerObject instanceof JSONObject playerJson)) {
                    continue;
                }
                if (playerId.equals(playerJson.getString("player_id"))) {
                    return Optional.of(new PlayerHistoryInfo(entry.getKey(), playerJson.getString("nickname")));
                }
            }
        }

        return Optional.empty();
    }

    private Optional<JSONObject> findPlayerStats(JSONArray teamsArray, String playerId) {
        if (teamsArray == null || playerId == null || playerId.isBlank()) {
            return Optional.empty();
        }

        for (Object teamObject : teamsArray) {
            if (!(teamObject instanceof JSONObject teamJson)) {
                continue;
            }

            JSONArray players = teamJson.getJSONArray("players");
            if (players == null) {
                continue;
            }

            for (Object playerObject : players) {
                if (!(playerObject instanceof JSONObject playerJson)) {
                    continue;
                }
                if (playerId.equals(playerJson.getString("player_id"))) {
                    return Optional.ofNullable(playerJson.getJSONObject("player_stats"));
                }
            }
        }

        return Optional.empty();
    }

    private String buildMatchScore(JSONObject resultsObject, String playerTeamKey) {
        if (resultsObject == null || playerTeamKey == null || playerTeamKey.isBlank()) {
            return UNKNOWN;
        }

        JSONObject scoreObject = resultsObject.getJSONObject("score");
        if (scoreObject == null || !scoreObject.containsKey(playerTeamKey)) {
            return UNKNOWN;
        }

        String opponentTeamKey = findOpponentTeamKey(scoreObject, playerTeamKey);
        if (opponentTeamKey == null) {
            return UNKNOWN;
        }

        Integer playerTeamScore = scoreObject.getInteger(playerTeamKey);
        Integer opponentScore = scoreObject.getInteger(opponentTeamKey);
        if (playerTeamScore == null || opponentScore == null) {
            return UNKNOWN;
        }

        return playerTeamScore + "/" + opponentScore;
    }

    private String resolveMatchResult(JSONObject resultsObject, String playerTeamKey) {
        if (resultsObject == null || playerTeamKey == null || playerTeamKey.isBlank()) {
            return UNKNOWN;
        }

        String winner = resultsObject.getString("winner");
        if (winner == null || winner.isBlank()) {
            return UNKNOWN;
        }

        return playerTeamKey.equals(winner) ? "win" : "loss";
    }

    private String findOpponentTeamKey(JSONObject scoreObject, String playerTeamKey) {
        for (String teamKey : scoreObject.keySet()) {
            if (!playerTeamKey.equals(teamKey)) {
                return teamKey;
            }
        }
        return null;
    }

    private String toTimestampString(Long startedAt) {
        if (startedAt == null) {
            return null;
        }
        long normalizedTimestamp = startedAt < 100_000_000_000L ? startedAt * 1000 : startedAt;
        return String.valueOf(normalizedTimestamp);
    }

    private record PlayerHistoryInfo(String teamKey, String nickname) {
    }
}
