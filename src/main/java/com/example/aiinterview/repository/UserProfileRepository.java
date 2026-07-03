package com.example.aiinterview.repository;

import com.example.aiinterview.entity.UserProfile;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface UserProfileRepository {

    @Select("""
            SELECT id, profile_summary, weak_points, strength_points, learning_suggestions,
                   evidence_count, version, status, created_at, updated_at
            FROM user_profile
            WHERE status = 'ACTIVE'
            ORDER BY updated_at DESC
            LIMIT 1
            """)
    UserProfile findActive();

    @Insert("""
            INSERT INTO user_profile (
                profile_summary, weak_points, strength_points, learning_suggestions,
                evidence_count, version, status, created_at, updated_at
            ) VALUES (
                #{profileSummary}, #{weakPoints}, #{strengthPoints}, #{learningSuggestions},
                #{evidenceCount}, #{version}, #{status}, NOW(), NOW()
            )
            """)
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(UserProfile profile);

    @Update("""
            UPDATE user_profile
            SET profile_summary = #{profileSummary},
                weak_points = #{weakPoints},
                strength_points = #{strengthPoints},
                learning_suggestions = #{learningSuggestions},
                evidence_count = #{evidenceCount},
                version = #{version},
                updated_at = NOW()
            WHERE id = #{id}
            """)
    int update(UserProfile profile);
}
