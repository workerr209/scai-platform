package com.springcore.ai.scai_platform.controller;

import com.springcore.ai.scai_platform.dto.ManuByUserDTO;
import com.springcore.ai.scai_platform.dto.UpdateProfileRequest;
import com.springcore.ai.scai_platform.dto.UserPrincipal;
import com.springcore.ai.scai_platform.entity.GroupMenu;
import com.springcore.ai.scai_platform.entity.User;
import com.springcore.ai.scai_platform.service.api.AdminService;
import com.springcore.ai.scai_platform.service.api.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final AdminService adminService;

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

    @GetMapping("/getMenuByUsername")
    public ResponseEntity<List<ManuByUserDTO>> listMenuByUsername() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return ResponseEntity.ok(adminService.listMenuByUsername(username));
    }

    @GetMapping("/getAllAllowMenu")
    public @ResponseBody List<GroupMenu> getAllAllowMenu(String username) {
        return adminService.findAllAllowMenuByUsername(username);
    }

}

