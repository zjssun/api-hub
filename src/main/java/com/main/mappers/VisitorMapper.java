package com.main.mappers;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;

public interface VisitorMapper {

    @Insert("INSERT INTO visitors (uid, first_seen, last_seen, last_ip, visit_count) " +
            "VALUES (#{uid}, #{now}, #{now}, #{ip}, 1) " +
            "ON DUPLICATE KEY UPDATE last_seen = #{now}, last_ip = #{ip}, visit_count = visit_count + 1")
    int upsertVisit(@Param("uid") String uid, @Param("ip") String ip, @Param("now") LocalDateTime now);
}
