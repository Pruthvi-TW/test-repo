package com.mockuidai.service;

import com.mockuidai.dto.AdminConfigRequest;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicReference;

@Service
@Slf4j
public class MockConfigService {

    @Getter
    private final AtomicReference<Double> latencyMultiplier = new AtomicReference<>(1.0);
    
    @Getter
    private final AtomicReference<Boolean> forceSystemError = new AtomicReference<>(false);
    
    @Getter
    private final AtomicReference<Boolean> forceOtpFailure = new AtomicReference<>(false);
    
    @Getter
    private final AtomicReference<Boolean> forceExpiredOtp = new AtomicReference<>(false);
    
    @Getter
    private final AtomicReference<String> customOtpValue = new AtomicReference<>("123456");

    public void updateConfig(AdminConfigRequest configRequest) {
        if (configRequest.getLatencyMultiplier() != null) {
            latencyMultiplier.set(configRequest.getLatencyMultiplier());
            log.info("Updated latency multiplier to: {}", configRequest.getLatencyMultiplier());
        }
        
        if (configRequest.getForceSystemError() != null) {
            forceSystemError.set(configRequest.getForceSystemError());
            log.info("Updated force system error to: {}", configRequest.getForceSystemError());
        }
        
        if (configRequest.getForceOtpFailure() != null) {
            forceOtpFailure.set(configRequest.getForceOtpFailure());
            log.info("Updated force OTP failure to: {}", configRequest.getForceOtpFailure());
        }
        
        if (configRequest.getForceExpiredOtp() != null) {
            forceExpiredOtp.set(configRequest.getForceExpiredOtp());
            log.info("Updated force expired OTP to: {}", configRequest.getForceExpiredOtp());
        }
        
        if (configRequest.getCustomOtpValue() != null) {
            customOtpValue.set(configRequest.getCustomOtpValue());
            log.info("Updated custom OTP value to: {}", configRequest.getCustomOtpValue());
        }
    }

    public void resetConfig() {
        latencyMultiplier.set(1.0);
        forceSystemError.set(false);
        forceOtpFailure.set(false);
        forceExpiredOtp.set(false);
        customOtpValue.set("123456");
        log.info("Reset all configuration to default values");
    }

    public double getLatencyMultiplier() {
        return latencyMultiplier.get();
    }

    public boolean isForceSystemError() {
        return forceSystemError.get();
    }

    public boolean isForceOtpFailure() {
        return forceOtpFailure.get();
    }

    public boolean isForceExpiredOtp() {
        return forceExpiredOtp.get();
    }

    public String getCustomOtpValue() {
        return customOtpValue.get();
    }
}