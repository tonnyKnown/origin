package com.example.aiinterview.repository;

import com.example.aiinterview.entity.UserProfileVersion;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface UserProfileVersionRepository {

    List<UserProfileVersion> findLatest(@Param("size") int size);

    int insert(UserProfileVersion version);
}
