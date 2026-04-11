package com.springcore.ai.scaiplatform.service.impl;

import com.springcore.ai.scaiplatform.dto.AuthResponse;
import com.springcore.ai.scaiplatform.dto.LoginRequest;
import com.springcore.ai.scaiplatform.dto.PinLoginRequest;
import com.springcore.ai.scaiplatform.dto.RegisterRequest;
import com.springcore.ai.scaiplatform.dto.UserResponse;
import com.springcore.ai.scaiplatform.entity.User;
import com.springcore.ai.scaiplatform.repository.api.UserRepository;
import com.springcore.ai.scaiplatform.security.jwt.JwtTokenProvider;
import com.springcore.ai.scaiplatform.service.api.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;

    /**
     * Default password applied when an admin resets a user's credentials.
     * Loaded from application config — never hardcoded in source code.
     * Set via environment variable: DEFAULT_RESET_PASSWORD
     */
    @Value("${app.security.default-reset-password}")
    private String defaultResetPassword;

    // -------------------------------------------------------------------------
    // Registration
    // -------------------------------------------------------------------------

    @Override
    @Transactional
    public UserResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already registered: " + request.getEmail());
        }

        User user = User.builder()
                .email(request.getEmail())
                .username(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .pin(passwordEncoder.encode("000000"))
                .build();

        User saved = userRepository.save(user);
        log.info("New user registered: id={}", saved.getId());

        // Convert to safe DTO — the User entity (containing password hash) must never leave the server
        return UserResponse.from(saved);
    }

    // -------------------------------------------------------------------------
    // Login — password-based
    // -------------------------------------------------------------------------

    @Override
    public AuthResponse login(LoginRequest request) {
        // AuthenticationManager throws AuthenticationException (→ 401) on bad credentials
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalStateException(
                        "Authenticated user not found in database: " + request.getEmail()));

        return buildAuthResponse(user.getEmail());
    }

    // -------------------------------------------------------------------------
    // Login — PIN-based  (POST /login/pin — PIN travels in request body, never as query param)
    // -------------------------------------------------------------------------

    @Override
    public AuthResponse loginWithPin(PinLoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                // Return a generic message — do not confirm whether the email exists
                .orElseThrow(() -> new BadCredentialsException("Invalid email or PIN"));

        // PIN stored as BCrypt hash — use matches() for constant-time comparison
        if (!passwordEncoder.matches(request.getPin(), user.getPin())) {
            throw new BadCredentialsException("Invalid email or PIN");
        }

        return buildAuthResponse(user.getEmail());
    }

    // -------------------------------------------------------------------------
    // Password reset
    // -------------------------------------------------------------------------

    @Override
    @Transactional
    public void resetPassword(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + email));

        // defaultResetPassword is injected from config — @Value("${app.security.default-reset-password}")
        user.setPassword(passwordEncoder.encode(defaultResetPassword));
        userRepository.save(user);
        log.info("Password reset to default for user: {}", email);
    }

    // -------------------------------------------------------------------------
    // Token operations
    // -------------------------------------------------------------------------

    @Override
    public String refreshToken(String refreshToken) {
        // Guard here in addition to the controller so the service is self-defending
        if (!jwtTokenProvider.validateToken(refreshToken)) {
            throw new BadCredentialsException("Refresh token is expired or invalid");
        }
        String email = jwtTokenProvider.getEmailFromToken(refreshToken);
        return jwtTokenProvider.generateAccessToken(email);
    }

    @Override
    public boolean validateToken(String token) {
        // Delegates to JwtTokenProvider — true = valid, false = invalid/expired
        return jwtTokenProvider.validateToken(token);
    }

    // -------------------------------------------------------------------------
    // Email validation
    // -------------------------------------------------------------------------

    @Override
    @Transactional(readOnly = true)
    public boolean validateEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    // -------------------------------------------------------------------------
    // Private helpers
    // -------------------------------------------------------------------------

    /**
     * Builds a complete token-pair response for a given email address.
     * Centralises token-generation logic so password-login and PIN-login
     * share a single code path.
     */
    private AuthResponse buildAuthResponse(String email) {
        return AuthResponse.builder()
                .accessToken(jwtTokenProvider.generateAccessToken(email))
                .refreshToken(jwtTokenProvider.generateRefreshToken(email))   // @JsonIgnore — for cookie only
                .refreshTokenExpirationMs(jwtTokenProvider.getRefreshTokenExpirationMs())
                .build();
    }
}