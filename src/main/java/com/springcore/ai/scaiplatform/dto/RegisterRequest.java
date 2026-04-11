package com.springcore.ai.scaiplatform.dto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class RegisterRequest {
    private String email;
    private String password;
    private String fullName;
    private String socialProvider; // optional
    private String socialId;       // optional
}
