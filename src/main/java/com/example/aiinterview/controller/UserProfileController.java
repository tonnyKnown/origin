package com.example.aiinterview.controller;

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
    public UserProfileResponse current() {
        return userProfileService.current();
    }

    // 首页按钮触发画像维护；复盘页只读取画像，不主动刷新。
    @PostMapping("/refresh")
    public UserProfileResponse refresh() {
        return userProfileService.refresh();
    }

    @GetMapping("/versions")
    public List<UserProfileVersionResponse> versions(@RequestParam(defaultValue = "10") int size) {
        return userProfileService.versions(size);
    }
}
