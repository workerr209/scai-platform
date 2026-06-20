package com.springcore.ai.scaiplatform.core.service.api;

import com.springcore.ai.scaiplatform.core.dto.UpdateProfileRequest;
import com.springcore.ai.scaiplatform.core.entity.User;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface UserService {

    void logout(String token);

    User updateProfile(Long userId, UpdateProfileRequest request);

    User getCurrentUser(Long id);

    List<User> getUsers();
}


