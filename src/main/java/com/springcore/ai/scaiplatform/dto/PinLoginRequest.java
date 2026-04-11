package com.springcore.ai.scaiplatform.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request body for PIN-based authentication.
 *
 * Replaces the previous GET /login/pin?email=&pin= approach.
 * Sending a PIN as a query parameter exposed it in:
 *   - Server access logs
 *   - Browser history
 *   - HTTP Referer headers
 * Using POST + JSON body avoids all of the above.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PinLoginRequest {

    @NotBlank(message = "Email must not be blank")
    @Email(message = "Email must be a valid email address")
    private String email;

    @NotBlank(message = "PIN must not be blank")
    @Size(min = 4, max = 12, message = "PIN must be between 4 and 12 characters")
    private String pin;
}