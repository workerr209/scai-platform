package com.springcore.ai.scaiplatform.core.controller;

import com.springcore.ai.scaiplatform.core.dto.UpdateProfileRequest;
import com.springcore.ai.scaiplatform.core.dto.UserPrincipal;
import com.springcore.ai.scaiplatform.core.entity.User;
import com.springcore.ai.scaiplatform.core.service.api.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<User> getCurrentUser(@AuthenticationPrincipal UserPrincipal principal) {
        User user = userService.getCurrentUser(principal.getId());
        user.setPassword(null);
        return ResponseEntity.ok(user);
    }

    @GetMapping("/getUsers")
    public ResponseEntity<List<User>> getUsers() {
        return ResponseEntity.ok(userService.getUsers());
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestHeader("Authorization") String token) {
        userService.logout(token);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/profile")
    public ResponseEntity<User> updateProfile(@RequestBody UpdateProfileRequest request, @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(userService.updateProfile(principal.getId(), request));
    }

}

