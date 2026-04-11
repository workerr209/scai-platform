package com.springcore.ai.scaiplatform.dto;

import com.springcore.ai.scaiplatform.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Safe projection of the {@link User} entity for API responses.
 *
 * Intentionally omits:
 *   - password  (BCrypt hash — must never leave the server)
 *   - pin       (BCrypt hash — must never leave the server)
 *   - euniteToken, atworkId, valstr, and other internal/legacy fields
 *
 * Only expose fields that the client legitimately needs to display or operate on.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {

    private Long id;
    private String email;
    private String username;
    private String name;

    /** Factory method — converts a User entity to a safe response object. */
    public static UserResponse from(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .username(user.getUsername())
                .name(user.getName())
                .build();
    }
}