package com.example.aiinterview.repository;

import com.example.aiinterview.entity.UserProfile;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserProfileRepository {

    UserProfile findActive();

    int insert(UserProfile profile);

    int update(UserProfile profile);
}
