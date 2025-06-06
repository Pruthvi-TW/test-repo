package com.mockuidai.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Response for admin operations")
public class AdminResponse {

    @Schema(description = "Message describing the result of the operation", example = "Configuration updated successfully")
    private String message;

    @Schema(description = "Timestamp of the response")
    private Instant timestamp;

    public AdminResponse(String message) {
        this.message = message;
        this.timestamp = Instant.now();
    }
}