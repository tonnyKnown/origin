package com.example.aiinterview.repository;

import com.example.aiinterview.entity.UserProfileVersion;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface UserProfileVersionRepository {

    @Select("""
            SELECT id, profile_id, version, profile_summary, weak_points, strength_points,
                   learning_suggestions, evidence_snapshot, evidence_count, model_name, created_at
            FROM user_profile_version
            ORDER BY created_at DESC
            LIMIT #{size}
            """)
    List<UserProfileVersion> findLatest(int size);

    @Insert("""
            INSERT INTO user_profile_version (
                profile_id, version, profile_summary, weak_points, strength_points,
                learning_suggestions, evidence_snapshot, evidence_count, model_name, created_at
            ) VALUES (
                #{profileId}, #{version}, #{profileSummary}, #{weakPoints}, #{strengthPoints},
                #{learningSuggestions}, #{evidenceSnapshot}, #{evidenceCount}, #{modelName}, NOW()
            )
            """)
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(UserProfileVersion version);
}
