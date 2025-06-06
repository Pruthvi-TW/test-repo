package com.mockuidai.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class TraceLoggerUtil {

    private static final Logger log = LoggerFactory.getLogger(TraceLoggerUtil.class);

    /**
     * Logs an info message with trace ID
     */
    public void info(String traceId, String message, Object... args) {
        log.info("[TraceID: {}] {}", traceId, formatMessage(message, args));
    }

    /**
     * Logs an error message with trace ID
     */
    public void error(String traceId, String message, Object... args) {
        log.error("[TraceID: {}] {}", traceId, formatMessage(message, args));
    }

    /**
     * Logs a warning message with trace ID
     */
    public void warn(String traceId, String message, Object... args) {
        log.warn("[TraceID: {}] {}", traceId, formatMessage(message, args));
    }

    /**
     * Masks PII data for logging
     * - Aadhaar/VID: Shows only first 2 and last 2 digits
     * - OTP: Completely masked
     */
    public String maskPii(String data) {
        if (data == null || data.isEmpty()) {
            return "";
        }

        // For Aadhaar/VID (12 digits)
        if (data.length() == 12 && data.matches("\\d+")) {
            return data.substring(0, 2) + "XXXXXXXX" + data.substring(10);
        }

        // For OTP (6 digits)
        if (data.length() == 6 && data.matches("\\d+")) {
            return "******";
        }

        // For other PII data
        if (data.length() > 4) {
            return data.substring(0, 2) + "****" + data.substring(data.length() - 2);
        }

        return "****";
    }

    private String formatMessage(String message, Object... args) {
        if (args == null || args.length == 0) {
            return message;
        }

        // Simple string formatting
        String result = message;
        for (Object arg : args) {
            result = result.replaceFirst("\\{\\}", arg != null ? arg.toString() : "null");
        }
        return result;
    }
}