package com.main.mappers;

import com.main.entity.po.Comment;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface CommentMapper {

    @Insert("INSERT INTO comments (visitor_uid, nickname, avatar, content, ip, status, created_at) " +
            "VALUES (#{visitorUid}, #{nickname}, #{avatar}, #{content}, #{ip}, 1, #{createdAt})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Comment comment);

    @Select("SELECT id, nickname, avatar, content, created_at AS createdAt " +
            "FROM comments WHERE status = 1 ORDER BY id DESC LIMIT #{limit}")
    List<Comment> findLatest(@Param("limit") int limit);
}
