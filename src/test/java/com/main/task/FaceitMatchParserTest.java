package com.main.task;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.main.entity.enums.PlayerEnum;
import com.main.entity.po.PlayerMatch;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FaceitMatchParserTest {
    private final FaceitMatchParser parser = new FaceitMatchParser();

    @Test
    void parseHistoryMatchMapsPlayerFieldsFromOfficialHistoryResponse() {
        JSONObject historyItem = JSON.parseObject("""
                {
                  "match_id": "1-match-a",
                  "started_at": 1775508983,
                  "results": {
                    "winner": "faction1",
                    "score": {
                      "faction1": 13,
                      "faction2": 10
                    }
                  },
                  "teams": {
                    "faction1": {
                      "players": [
                        {
                          "player_id": "e5e8e2a6-d716-4493-b949-e16965f41654",
                          "nickname": "donk666"
                        }
                      ]
                    },
                    "faction2": {
                      "players": [
                        {
                          "player_id": "someone-else",
                          "nickname": "other"
                        }
                      ]
                    }
                  }
                }
                """);

        Optional<FaceitMatchParser.HistoryMatchContext> parsedMatch = parser.parseHistoryMatch(PlayerEnum.DONK, historyItem);

        assertTrue(parsedMatch.isPresent());
        PlayerMatch playerMatch = parsedMatch.get().match();
        assertEquals("faction1", parsedMatch.get().teamKey());
        assertEquals("donk666", playerMatch.getNickName());
        assertEquals("faction1", playerMatch.getTeam());
        assertEquals("13/10", playerMatch.getMatchScore());
        assertEquals("win", playerMatch.getMatchResult());
        assertEquals("1-match-a", playerMatch.getMatchId());
        assertEquals("https://www.faceit.com/en/cs2/room/1-match-a/scoreboard", playerMatch.getRoomUrl());
        assertEquals("1775508983000", playerMatch.getTimestamp());
    }

    @Test
    void parseHistoryMatchKeepsPlayerPerspectiveWhenPlayerIsInFaction2() {
        JSONObject historyItem = JSON.parseObject("""
                {
                  "match_id": "1-match-b",
                  "started_at": 1775508423,
                  "results": {
                    "winner": "faction1",
                    "score": {
                      "faction1": 13,
                      "faction2": 4
                    }
                  },
                  "teams": {
                    "faction1": {
                      "players": [
                        {
                          "player_id": "someone-else",
                          "nickname": "other"
                        }
                      ]
                    },
                    "faction2": {
                      "players": [
                        {
                          "player_id": "e5e8e2a6-d716-4493-b949-e16965f41654",
                          "nickname": "donk666"
                        }
                      ]
                    }
                  }
                }
                """);

        Optional<FaceitMatchParser.HistoryMatchContext> parsedMatch = parser.parseHistoryMatch(PlayerEnum.DONK, historyItem);

        assertTrue(parsedMatch.isPresent());
        PlayerMatch playerMatch = parsedMatch.get().match();
        assertEquals("faction2", parsedMatch.get().teamKey());
        assertEquals("4/13", playerMatch.getMatchScore());
        assertEquals("loss", playerMatch.getMatchResult());
    }

    @Test
    void applyMatchStatsMapsPlayerStatsFromOfficialStatsResponse() {
        JSONObject statsResponse = JSON.parseObject("""
                {
                  "rounds": [
                    {
                      "best_of": "1",
                      "round_stats": {
                        "Map": "de_overpass"
                      },
                      "teams": [
                        {
                          "players": [
                            {
                              "player_id": "e5e8e2a6-d716-4493-b949-e16965f41654",
                              "player_stats": {
                                "Kills": "14",
                                "Deaths": "17",
                                "Assists": "6",
                                "K/R Ratio": "0.61",
                                "Triple Kills": "0",
                                "Quadro Kills": "0",
                                "Penta Kills": "0",
                                "ADR": "73.5"
                              }
                            }
                          ]
                        }
                      ]
                    }
                  ]
                }
                """);

        PlayerMatch playerMatch = new PlayerMatch();
        playerMatch.setMatchId("1-match-a");
        FaceitMatchParser.HistoryMatchContext historyMatchContext =
                new FaceitMatchParser.HistoryMatchContext(playerMatch, "faction1");

        Optional<PlayerMatch> parsedMatch = parser.applyMatchStats(PlayerEnum.DONK, historyMatchContext, statsResponse);

        assertTrue(parsedMatch.isPresent());
        PlayerMatch enrichedMatch = parsedMatch.get();
        assertEquals("1", enrichedMatch.getBestOf());
        assertEquals("de_overpass", enrichedMatch.getMatchMap());
        assertEquals("14", enrichedMatch.getTotalKills());
        assertEquals("17", enrichedMatch.getTotalDeaths());
        assertEquals("6", enrichedMatch.getTotalAssists());
        assertEquals("0.61", enrichedMatch.getRating());
        assertEquals("0", enrichedMatch.getTripleKill());
        assertEquals("0", enrichedMatch.getQuadroKill());
        assertEquals("0", enrichedMatch.getPentaKill());
        assertEquals("73.5", enrichedMatch.getAdr());
    }

    @Test
    void applyMatchStatsReturnsEmptyWhenPlayerIsMissingFromStatsResponse() {
        JSONObject statsResponse = JSON.parseObject("""
                {
                  "rounds": [
                    {
                      "teams": [
                        {
                          "players": [
                            {
                              "player_id": "someone-else",
                              "player_stats": {
                                "Kills": "1"
                              }
                            }
                          ]
                        }
                      ]
                    }
                  ]
                }
                """);

        PlayerMatch playerMatch = new PlayerMatch();
        FaceitMatchParser.HistoryMatchContext historyMatchContext =
                new FaceitMatchParser.HistoryMatchContext(playerMatch, "faction1");

        Optional<PlayerMatch> parsedMatch = parser.applyMatchStats(PlayerEnum.DONK, historyMatchContext, statsResponse);

        assertTrue(parsedMatch.isEmpty());
    }

    @Test
    void parseEffectiveRankingUsesMatchingFactionFromOfficialMatchResponse() {
        JSONObject matchResponse = JSON.parseObject("""
                {
                  "teams": {
                    "faction1": {
                      "stats": {
                        "rating": 4272
                      }
                    },
                    "faction2": {
                      "stats": {
                        "rating": 4263
                      }
                    }
                  }
                }
                """);

        String effectiveRanking = parser.parseEffectiveRanking(matchResponse, "faction1");

        assertEquals("4272", effectiveRanking);
    }
}
