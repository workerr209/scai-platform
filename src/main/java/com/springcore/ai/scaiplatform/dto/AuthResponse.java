package com.springcore.ai.scaiplatform.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class AuthResponse {

    private String accessToken;

    /**
     * The refresh token is stored in an httpOnly cookie by the controller.
     * It must NOT be serialised into the JSON body — doing so would allow
     * JavaScript to read it, defeating the purpose of httpOnly.
     *
     * The field is kept on the class so the controller can extract it
     * to build the Set-Cookie header before the object is serialised.
     */
    @JsonIgnore
    private String refreshToken;

    /**
     * Refresh token lifetime in milliseconds.
     * Sent to the client so it knows when to trigger a silent refresh.
     */
    private long refreshTokenExpirationMs;
}
