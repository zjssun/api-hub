package com.main.mappers;

import com.main.entity.po.Comment;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface CommentMapper {

    @Insert("INSERT INTO comments (visitor_uid, nickname, avatar, content, ip, status, created_at, parent_id) " +
            "VALUES (#{visitorUid}, #{nickname}, #{avatar}, #{content}, #{ip}, 1, #{createdAt}, #{parentId})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Comment comment);

    @Select("SELECT id, nickname, avatar, content, created_at AS createdAt " +
            "FROM comments WHERE status = 1 AND parent_id = 0 ORDER BY id DESC LIMIT #{limit}")
    List<Comment> findLatest(@Param("limit") int limit);

    /** 批量取一组顶层评论的回复（按时间正序） */
    @Select("<script>SELECT id, parent_id AS parentId, nickname, avatar, content, created_at AS createdAt " +
            "FROM comments WHERE status = 1 AND parent_id IN " +
            "<foreach collection='ids' item='i' open='(' separator=',' close=')'>#{i}</foreach>" +
            " ORDER BY id ASC</script>")
    List<Comment> findReplies(@Param("ids") List<Long> parentIds);

    @Select("SELECT COUNT(*) FROM comments WHERE id = #{id} AND status = 1 AND parent_id = 0")
    int countTopLevelById(@Param("id") long id);
}
