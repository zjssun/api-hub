package com.main.mappers;

import com.main.entity.po.ParseHistory;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface ParseHistoryMapper {

    @Insert("INSERT INTO parse_history (subject, video_id, title, platform, thumb, tag, tag_type, size_text, webpage_url, created_at) " +
            "VALUES (#{subject}, #{videoId}, #{title}, #{platform}, #{thumb}, #{tag}, #{tagType}, #{sizeText}, #{webpageUrl}, #{createdAt})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(ParseHistory history);

    /** 同主体同视频只留最新一条 */
    @Delete("DELETE FROM parse_history WHERE subject = #{subject} AND video_id = #{videoId}")
    int deleteBySubjectAndVideo(@Param("subject") String subject, @Param("videoId") String videoId);

    @Select("SELECT id, video_id AS videoId, title, platform, thumb, tag, tag_type AS tagType, " +
            "size_text AS sizeText, webpage_url AS webpageUrl, created_at AS createdAt " +
            "FROM parse_history WHERE subject = #{subject} ORDER BY id DESC LIMIT #{limit}")
    List<ParseHistory> findLatest(@Param("subject") String subject, @Param("limit") int limit);

    /** 每主体最多保留 keep 条，多余的旧记录清除 */
    @Delete("DELETE FROM parse_history WHERE subject = #{subject} AND id NOT IN " +
            "(SELECT id FROM (SELECT id FROM parse_history WHERE subject = #{subject} ORDER BY id DESC LIMIT #{keep}) t)")
    int trim(@Param("subject") String subject, @Param("keep") int keep);
}
