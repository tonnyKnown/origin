package com.example.aiinterview.controller;

import com.example.aiinterview.common.Result;
import com.example.aiinterview.dto.UserProfileResponse;
import com.example.aiinterview.dto.UserProfileVersionResponse;
import com.example.aiinterview.service.UserProfileService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/user-profile")
public class UserProfileController {

    private final UserProfileService userProfileService;

    public UserProfileController(UserProfileService userProfileService) {
        this.userProfileService = userProfileService;
    }

    @GetMapping("/current")
    public Result<UserProfileResponse> current() {
        return Result.ok(userProfileService.current());
    }

    @PostMapping("/refresh")
    public Result<UserProfileResponse> refresh() {
        return Result.ok(userProfileService.refresh());
    }

    @GetMapping("/versions")
    public Result<List<UserProfileVersionResponse>> versions(@RequestParam(defaultValue = "10") int size) {
        return Result.ok(userProfileService.versions(size));
    }
}