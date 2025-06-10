package com.mockuidai.service;

import com.mockuidai.dto.AdminConfigRequest;
import com.mockuidai.dto.ServiceConfig;
import com.mockuidai.util.TraceLoggerUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MockConfigService {

    private final TraceLoggerUtil logger;
    
    // Default configuration
    private ServiceConfig config = ServiceConfig.builder()
            .simulatedLatencyMs(500)
            .errorProbability(0.0)
            .build();

    public ServiceConfig getConfig() {
        return config;
    }

    public void updateConfig(AdminConfigRequest configRequest) {
        if (configRequest.getSimulatedLatencyMs() != null) {
            config.setSimulatedLatencyMs(configRequest.getSimulatedLatencyMs());
        }
        
        if (configRequest.getErrorProbability() != null) {
            // Ensure error probability is between 0 and 1
            double probability = Math.min(1.0, Math.max(0.0, configRequest.getErrorProbability()));
            config.setErrorProbability(probability);
        }
        
        logger.info("System", "Configuration updated: {}", config);
    }

    public void resetConfig() {
        config = ServiceConfig.builder()
                .simulatedLatencyMs(500)
                .errorProbability(0.0)
                .build();
        
        logger.info("System", "Configuration reset to defaults: {}", config);
    }
}