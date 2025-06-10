package com.kyc.verification.service;

import com.kyc.verification.entity.EkycRequest;
import com.kyc.verification.entity.OtpVerification;
import com.kyc.verification.entity.AuditLog;
import com.kyc.verification.enums.OtpStatus;
import com.kyc.verification.enums.VerificationStatus;
import com.kyc.verification.exception.InvalidOtpException;
import com.kyc.verification.exception.OtpExpiredException;
import com.kyc.verification.exception.VerificationException;
import com.kyc.verification.repository.OtpVerificationRepository;
import com.kyc.verification.repository.EkycRequestRepository;
import com.kyc.verification.repository.AuditLogRepository;
import com.kyc.verification.client.UidaiClient;
import com.kyc.verification.dto.OtpInitiationRequest;
import com.kyc.verification.dto.OtpVerificationRequest;
import com.kyc.verification.dto.UidaiOtpResponse;
import com.kyc.verification.util.DataMaskingUtil;
import com.kyc.verification.util.ValidationUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class OtpService {

    private static final Logger logger = LoggerFactory.getLogger(OtpService.class);
    private static final int MAX_OTP_ATTEMPTS = 3;
    private static final int OTP_EXPIRY_MINUTES = 10;

    private final OtpVerificationRepository otpVerificationRepository;
    private final EkycRequestRepository ekycRequestRepository;
    private final AuditLogRepository auditLogRepository;
    private final UidaiClient uidaiClient;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public OtpService(
            OtpVerificationRepository otpVerificationRepository,
            EkycRequestRepository ekycRequestRepository,
            AuditLogRepository auditLogRepository,
            UidaiClient uidaiClient,
            PasswordEncoder passwordEncoder) {
        this.otpVerificationRepository = otpVerificationRepository;
        this.ekycRequestRepository = ekycRequestRepository;
        this.auditLogRepository = auditLogRepository;
        this.uidaiClient = uidaiClient;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public String initiateOtp(OtpInitiationRequest request) {
        logger.info("Initiating OTP for eKYC request with session ID: {}", request.getSessionId());

        validateOtpInitiationRequest(request);

        EkycRequest ekycRequest = ekycRequestRepository.findBySessionId(request.getSessionId())
                .orElseThrow(() -> new VerificationException("Invalid session ID"));

        try {
            UidaiOtpResponse uidaiResponse = uidaiClient.initiateOtp(
                    request.getAadhaarOrVid(),
                    request.getTransactionId());

            OtpVerification otpVerification = createOtpVerification(ekycRequest, uidaiResponse);
            otpVerificationRepository.save(otpVerification);

            logAudit("OTP_INITIATED", request.getSessionId(), 
                    DataMaskingUtil.maskAadhaar(request.getAadhaarOrVid()));

            return otpVerification.getReferenceId();

        } catch (Exception e) {
            logger.error("Error initiating OTP: {}", e.getMessage());
            logAudit("OTP_INITIATION_FAILED", request.getSessionId(), 
                    "Error: " + e.getMessage());
            throw new VerificationException("Failed to initiate OTP");
        }
    }

    @Transactional
    public void verifyOtp(OtpVerificationRequest request) {
        logger.info("Verifying OTP for reference ID: {}", request.getReferenceId());

        validateOtpVerificationRequest(request);

        OtpVerification otpVerification = otpVerificationRepository
                .findByReferenceId(request.getReferenceId())
                .orElseThrow(() -> new VerificationException("Invalid reference ID"));

        checkOtpExpiry(otpVerification);
        checkMaxAttempts(otpVerification);

        try {
            boolean isValid = uidaiClient.verifyOtp(
                    request.getReferenceId(),
                    request.getOtp());

            updateVerificationStatus(otpVerification, isValid);
            otpVerificationRepository.save(otpVerification);

            if (isValid) {
                updateEkycRequestStatus(otpVerification.getEkycRequest());
            }

            logAudit(isValid ? "OTP_VERIFIED" : "OTP_VERIFICATION_FAILED",
                    otpVerification.getEkycRequest().getSessionId(),
                    "Attempt: " + otpVerification.getAttempts());

        } catch (Exception e) {
            logger.error("Error verifying OTP: {}", e.getMessage());
            logAudit("OTP_VERIFICATION_ERROR",
                    otpVerification.getEkycRequest().getSessionId(),
                    "Error: " + e.getMessage());
            throw new VerificationException("Failed to verify OTP");
        }
    }

    private void validateOtpInitiationRequest(OtpInitiationRequest request) {
        if (!ValidationUtil.isValidAadhaar(request.getAadhaarOrVid())) {
            throw new VerificationException("Invalid Aadhaar/VID format");
        }
        if (!ValidationUtil.isValidSessionId(request.getSessionId())) {
            throw new VerificationException("Invalid session ID");
        }
    }

    private void validateOtpVerificationRequest(OtpVerificationRequest request) {
        if (!ValidationUtil.isValidOtp(request.getOtp())) {
            throw new InvalidOtpException("Invalid OTP format");
        }
        if (!ValidationUtil.isValidReferenceId(request.getReferenceId())) {
            throw new VerificationException("Invalid reference ID");
        }
    }

    private OtpVerification createOtpVerification(EkycRequest ekycRequest, UidaiOtpResponse uidaiResponse) {
        OtpVerification verification = new OtpVerification();
        verification.setEkycRequest(ekycRequest);
        verification.setReferenceId(UUID.randomUUID().toString());
        verification.setStatus(OtpStatus.INITIATED);
        verification.setAttempts(0);
        verification.setCreatedAt(LocalDateTime.now());
        verification.setUidaiReferenceId(uidaiResponse.getReferenceId());
        return verification;
    }

    private void checkOtpExpiry(OtpVerification otpVerification) {
        if (LocalDateTime.now().isAfter(
                otpVerification.getCreatedAt().plusMinutes(OTP_EXPIRY_MINUTES))) {
            throw new OtpExpiredException("OTP has expired");
        }
    }

    private void checkMaxAttempts(OtpVerification otpVerification) {
        if (otpVerification.getAttempts() >= MAX_OTP_ATTEMPTS) {
            throw new VerificationException("Maximum OTP attempts exceeded");
        }
    }

    private void updateVerificationStatus(OtpVerification otpVerification, boolean isValid) {
        otpVerification.setAttempts(otpVerification.getAttempts() + 1);
        otpVerification.setLastAttemptAt(LocalDateTime.now());
        otpVerification.setStatus(isValid ? OtpStatus.VERIFIED : OtpStatus.FAILED);
    }

    private void updateEkycRequestStatus(EkycRequest ekycRequest) {
        ekycRequest.setStatus(VerificationStatus.VERIFIED);
        ekycRequest.setVerifiedAt(LocalDateTime.now());
        ekycRequestRepository.save(ekycRequest);
    }

    private void logAudit(String action, String sessionId, String details) {
        AuditLog auditLog = new AuditLog();
        auditLog.setAction(action);
        auditLog.setSessionId(sessionId);
        auditLog.setDetails(details);
        auditLog.setTimestamp(LocalDateTime.now());
        auditLogRepository.save(auditLog);
    }
}