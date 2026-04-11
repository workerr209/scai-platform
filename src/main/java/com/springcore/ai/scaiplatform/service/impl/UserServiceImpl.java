package com.springcore.ai.scaiplatform.service.impl;

import com.springcore.ai.scaiplatform.dto.UpdateProfileRequest;
import com.springcore.ai.scaiplatform.entity.User;
import com.springcore.ai.scaiplatform.repository.api.UserRepository;
import com.springcore.ai.scaiplatform.service.api.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public void logout(String token) {
        // Placeholder: ใช้ blacklist หรือ token revocation ตามระบบที่ใช้
        System.out.println("Token revoked: " + token);
    }

    @Override
    public User updateProfile(Long userId, UpdateProfileRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        /*user.setFullName(request.getFullName());
        user.setPhone(request.getPhone());
        user.setAddress(request.getAddress());
        user.setProfilePictureUrl(request.getProfilePictureUrl());*/

        return userRepository.save(user);
    }

    @Override
    public User getCurrentUser(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @Override
    public List<User> getUsers() {
        return userRepository.findAll();
    }

}

