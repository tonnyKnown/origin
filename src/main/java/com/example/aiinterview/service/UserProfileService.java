package com.example.aiinterview.service;

import com.example.aiinterview.dto.UserProfileResponse;
import com.example.aiinterview.dto.UserProfileVersionResponse;

import java.util.List;

public interface UserProfileService {

    UserProfileResponse current();

    UserProfileResponse refresh();

    List<UserProfileVersionResponse> versions(int size);
}
