package com.springcore.ai.scaiplatform.service.api;

import com.springcore.ai.scaiplatform.dto.UpdateProfileRequest;
import com.springcore.ai.scaiplatform.entity.User;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface UserService {

    void logout(String token);

    User updateProfile(Long userId, UpdateProfileRequest request);

    User getCurrentUser(Long id);

    List<User> getUsers();
}


