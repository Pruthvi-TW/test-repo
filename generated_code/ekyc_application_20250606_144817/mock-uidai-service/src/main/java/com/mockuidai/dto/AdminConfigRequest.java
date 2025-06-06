package com.mockuidai.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request to update mock service configuration")
public class AdminConfigRequest {

    @Schema(description = "Simulated latency in milliseconds", example = "500")
    private Integer simulatedLatencyMs;

    @Schema(description = "Probability of generating random errors (0.0 to 1.0)", example = "0.1")
    private Double errorProbability;
}