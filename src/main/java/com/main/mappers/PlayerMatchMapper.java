package com.main.mappers;

import com.main.entity.po.PlayerMatch;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface PlayerMatchMapper {
    @Insert({
            "<script>",
            "INSERT INTO ${tableName} (",
            "nick_name, team, match_map, match_score, match_result, match_id, room_url,",
            "best_of, effective_ranking, total_kills, total_deaths, total_assistsL, rating,",
            "triple_kill, quadro_kill, penta_kill, timestamp, adr",
            ")",
            "SELECT",
            "#{match.nickName}, #{match.team}, #{match.matchMap}, #{match.matchScore}, #{match.matchResult}, #{match.matchId}, #{match.roomUrl},",
            "#{match.bestOf}, #{match.effectiveRanking}, #{match.totalKills}, #{match.totalDeaths}, #{match.totalAssists}, #{match.rating},",
            "#{match.tripleKill}, #{match.quadroKill}, #{match.pentaKill}, #{match.timestamp}, #{match.adr}",
            "FROM DUAL",
            "WHERE NOT EXISTS (",
            "SELECT 1 FROM ${tableName} WHERE match_id = #{match.matchId}",
            ")",
            "</script>"
    })
    int insertIfAbsent(@Param("tableName") String tableName, @Param("match") PlayerMatch match);

    @Delete("DELETE FROM ${tableName} WHERE CAST(timestamp AS UNSIGNED) < #{cutoffTimestamp}")
    int deleteOlderThan(@Param("tableName") String tableName, @Param("cutoffTimestamp") long cutoffTimestamp);

    @Select(
            "SELECT " +
                    "nick_name AS nickName, team, match_map AS matchMap, match_score AS matchScore, " +
                    "match_result AS matchResult, match_id AS matchId, room_url AS roomUrl, best_of AS bestOf, " +
                    "effective_ranking AS effectiveRanking, total_kills AS totalKills, total_deaths AS totalDeaths, " +
                    "total_assistsL AS totalAssists, rating, triple_kill AS tripleKill, quadro_kill AS quadroKill, " +
                    "penta_kill AS pentaKill, timestamp, adr " +
                    "FROM ${tableName} ORDER BY CAST(timestamp AS UNSIGNED) DESC"
    )
    List<PlayerMatch> findAllOrderByTimestampDesc(@Param("tableName") String tableName);
}
