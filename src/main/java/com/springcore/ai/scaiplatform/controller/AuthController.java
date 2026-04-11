package com.springcore.ai.scaiplatform.controller;

import com.springcore.ai.scaiplatform.dto.AuthResponse;
import com.springcore.ai.scaiplatform.dto.LoginRequest;
import com.springcore.ai.scaiplatform.dto.PinLoginRequest;
import com.springcore.ai.scaiplatform.dto.RegisterRequest;
import com.springcore.ai.scaiplatform.dto.ResetPasswordRequest;
import com.springcore.ai.scaiplatform.dto.UserResponse;
import com.springcore.ai.scaiplatform.service.api.AuthService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    // -------------------------------------------------------------------------
    // Registration
    // -------------------------------------------------------------------------

    /**
     * Returns a UserResponse DTO — never the User entity.
     * The entity contains password/PIN hashes that must not leave the server.
     */
    @PostMapping("/register")
    public ResponseEntity<UserResponse> registerUser(@RequestBody @Valid RegisterRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.register(request));
    }

    // -------------------------------------------------------------------------
    // Login — password-based
    // -------------------------------------------------------------------------

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody @Valid LoginRequest request,
                                              HttpServletResponse response) {
        AuthResponse authResponse = authService.login(request);
        attachRefreshCookie(response, authResponse);
        return ResponseEntity.ok(authResponse);
        // Note: authResponse.refreshToken is @JsonIgnore — only accessToken is in the JSON body
    }

    // -------------------------------------------------------------------------
    // Login — PIN-based
    // -------------------------------------------------------------------------

    /**
     * Changed from GET /login/pin?email=&pin= to POST /login/pin with a JSON body.
     * <p>
     * Why: GET query parameters appear in server access logs, browser history,
     * and HTTP Referer headers.  A PIN sent as ?pin= is therefore visible to
     * anyone with access to those logs — treating it as a secret is impossible.
     * POST body is not logged by default and is not stored in browser history.
     */
    @PostMapping("/login/pin")
    public ResponseEntity<AuthResponse> loginWithPin(@RequestBody @Valid PinLoginRequest request,
                                                     HttpServletResponse response) {
        AuthResponse authResponse = authService.loginWithPin(request);
        attachRefreshCookie(response, authResponse);
        return ResponseEntity.ok(authResponse);
    }

    // -------------------------------------------------------------------------
    // Password reset
    // -------------------------------------------------------------------------

    /**
     * Resets the user's password to the system default.
     * All exceptions bubble up to GlobalExceptionHandler for a consistent error format.
     * No try-catch here — catching Exception and returning 400 masked legitimate 500 errors.
     */
    @PostMapping("/reset-password-default")
    public ResponseEntity<Void> resetToDefault(@RequestBody @Valid ResetPasswordRequest request) {
        authService.resetPassword(request.getEmail());
        return ResponseEntity.noContent().build();
    }

    // -------------------------------------------------------------------------
    // Email validation
    // -------------------------------------------------------------------------

    @GetMapping("/email/validate")
    public ResponseEntity<Void> validateEmail(@RequestParam String email) {
        boolean exists = authService.validateEmail(email);
        return exists ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
    }

    // -------------------------------------------------------------------------
    // Token refresh — cookie-based (preferred: JS cannot read httpOnly cookies)
    // -------------------------------------------------------------------------

    /**
     * Accepts the refresh token from the httpOnly cookie set at login.
     * JavaScript running in the browser cannot read httpOnly cookies,
     * so this approach protects against XSS token theft.
     * <p>
     * validateToken() returns true = valid.
     * We return 401 only when the token is MISSING or INVALID (not when valid).
     */
    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refreshFromCookie(
            @CookieValue(name = "refreshToken", required = false) String refreshToken) {

        // refreshToken == null  → no cookie present
        // !validateToken(...)   → token is expired / malformed / bad signature
        if (refreshToken == null || !authService.validateToken(refreshToken)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        return ResponseEntity.ok(
                AuthResponse.builder()
                        .accessToken(authService.refreshToken(refreshToken))
                        .build()
        );
    }

    // -------------------------------------------------------------------------
    // Logout
    // -------------------------------------------------------------------------

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletResponse response) {
        // Overwrite the cookie with an empty value and maxAge=0 to instruct the browser to delete it
        ResponseCookie deleteCookie = ResponseCookie.from("refreshToken", "")
                .httpOnly(true)
                .secure(true)
                .path("/api/auth/refresh")
                .maxAge(0)
                .sameSite("Strict")
                .build();
        response.setHeader(HttpHeaders.SET_COOKIE, deleteCookie.toString());
        return ResponseEntity.noContent().build();
    }

    // -------------------------------------------------------------------------
    // Private helpers
    // -------------------------------------------------------------------------

    /**
     * Sets an httpOnly, Secure, SameSite=Strict cookie containing the refresh token.
     * <p>
     * maxAge fix: ResponseCookie.maxAge() expects SECONDS.
     * refreshTokenExpirationMs is in MILLISECONDS → divide by 1000.
     * Previous code passed ms directly, causing the cookie to expire ~1000× too late.
     */
    private void attachRefreshCookie(HttpServletResponse response, AuthResponse authResponse) {
        long maxAgeSeconds = authResponse.getRefreshTokenExpirationMs() / 1000;

        ResponseCookie cookie = ResponseCookie.from("refreshToken", authResponse.getRefreshToken())
                .httpOnly(true)
                .secure(true)
                .path("/api/auth/refresh")
                .maxAge(maxAgeSeconds)                    // seconds — correctly converted
                .sameSite("Strict")
                .build();

        response.setHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }
}