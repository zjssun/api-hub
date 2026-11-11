package com.main.mappers;

import com.main.entity.po.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

public interface UserMapper {

    @Insert("INSERT INTO users (email, nickname, password_hash, avatar, plan, custom_quota_mb, created_at) " +
            "VALUES (#{email}, #{nickname}, #{passwordHash}, #{avatar}, #{plan}, #{customQuotaMb}, #{createdAt})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(User user);

    @Select("SELECT id, email, nickname, password_hash AS passwordHash, avatar, plan, " +
            "is_admin AS admin, custom_quota_mb AS customQuotaMb, created_at AS createdAt FROM users WHERE email = #{email}")
    User findByEmail(@Param("email") String email);

    @Select("SELECT id, email, nickname, password_hash AS passwordHash, avatar, plan, " +
            "is_admin AS admin, custom_quota_mb AS customQuotaMb, created_at AS createdAt FROM users WHERE id = #{id}")
    User findById(@Param("id") long id);

    @Update("UPDATE users SET password_hash = #{passwordHash} WHERE id = #{id}")
    int updatePassword(@Param("id") long id, @Param("passwordHash") String passwordHash);

    @Update("UPDATE users SET nickname = #{nickname} WHERE id = #{id}")
    int updateNickname(@Param("id") long id, @Param("nickname") String nickname);
}
