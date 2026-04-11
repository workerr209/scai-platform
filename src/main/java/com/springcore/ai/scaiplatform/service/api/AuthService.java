package com.springcore.ai.scaiplatform.service.api;

import com.springcore.ai.scaiplatform.dto.AuthResponse;
import com.springcore.ai.scaiplatform.dto.LoginRequest;
import com.springcore.ai.scaiplatform.dto.PinLoginRequest;
import com.springcore.ai.scaiplatform.dto.RegisterRequest;
import com.springcore.ai.scaiplatform.dto.UserResponse;

public interface AuthService {

    /** Registers a new user and returns a safe projection (no password hash). */
    UserResponse register(RegisterRequest request);

    /** Authenticates with email + password and returns a token pair. */
    AuthResponse login(LoginRequest request);

    /** Resets the user's password to the configured default value. */
    void resetPassword(String email);

    /** Authenticates with email + PIN (POST body — PIN never travels as a query param). */
    AuthResponse loginWithPin(PinLoginRequest request);

    /** Issues a new access token from a valid refresh token. */
    String refreshToken(String refreshToken);

    /** Returns true if an account with the given email exists. */
    boolean validateEmail(String email);

    /**
     * Validates a JWT token.
     *
     * @return true  if the token is structurally valid, correctly signed, and not expired
     *         false otherwise
     */
    boolean validateToken(String token);
}