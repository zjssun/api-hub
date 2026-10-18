package com.main.mappers;

import com.main.entity.po.ProVote;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface ProVoteMapper {

    @Insert("INSERT INTO pro_votes (name_key, name, votes) VALUES (#{nameKey}, #{name}, 1) " +
            "ON DUPLICATE KEY UPDATE votes = votes + 1")
    int upsertVote(@Param("nameKey") String nameKey, @Param("name") String name);

    @Select("SELECT name, name_key AS nameKey, votes FROM pro_votes ORDER BY votes DESC, name_key")
    List<ProVote> findAllOrderByVotesDesc();
}
