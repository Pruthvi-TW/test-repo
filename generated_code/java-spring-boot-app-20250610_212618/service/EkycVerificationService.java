package com.kyc.verification.service;

import com.kyc.verification.domain.*;
import com.kyc.verification.dto.*;
import com.kyc.verification.repository.*;
import com.kyc.verification.exception.*;
import com.kyc.verification.integration.UidaiApiClient;
import com.kyc.verification.util.AuditLogger;
import com.kyc.verification.util.DataMasker;
import com.kyc.verification.util.ValidationUtils;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.Optional;
import java.util.List;

@Service
public class EkycVerificationService {

    private final EkycRequestRepository ekycRequestRepository;
    private final OtpVerificationRepository otpVerificationRepository;
    private final VerificationConsentRepository consentRepository;
    private final AuditLogRepository auditLogRepository;
    private final UidaiApiClient uidaiApiClient;
    private final AuditLogger auditLogger;
    private final DataMasker dataMasker;

    @Autowired
    public EkycVerificationService(
            EkycRequestRepository ekycRequestRepository,
            OtpVerificationRepository otpVerificationRepository,
            VerificationConsentRepository consentRepository,
            AuditLogRepository auditLogRepository,
            UidaiApiClient uidaiApiClient,
            AuditLogger auditLogger,
            DataMasker dataMasker) {
        this.ekycRequestRepository = ekycRequestRepository;
        this.otpVerificationRepository = otpVerificationRepository;
        this.consentRepository = consentRepository;
        this.auditLogRepository = auditLogRepository;
        this.uidaiApiClient = uidaiApiClient;
        this.auditLogger = auditLogger;
        this.dataMasker = dataMasker;
    }

    @Transactional
    public EkycInitiationResponse initiateEkycVerification(EkycInitiationRequest request) {
        String referenceNumber = generateReferenceNumber();
        auditLogger.logRequestInitiation(referenceNumber, dataMasker.maskAadhaarNumber(request.getAadhaarOrVid()));

        validateInitiationRequest(request);

        EkycRequest ekycRequest = new EkycRequest();
        ekycRequest.setReferenceNumber(referenceNumber);
        ekycRequest.setIdType(request.getIdType());
        ekycRequest.setIdNumber(request.getAadhaarOrVid());
        ekycRequest.setStatus(VerificationStatus.INITIATED);
        ekycRequest.setSessionId(request.getSessionId());
        ekycRequest.setParentProcessId(request.getParentProcessId());
        ekycRequest.setCreatedAt(LocalDateTime.now());

        VerificationConsent consent = createConsent(request, ekycRequest);
        consentRepository.save(consent);
        ekycRequest.setConsent(consent);

        ekycRequest = ekycRequestRepository.save(ekycRequest);
        
        try {
            UidaiOtpResponse uidaiResponse = uidaiApiClient.initiateOtp(
                request.getAadhaarOrVid(),
                request.getIdType(),
                referenceNumber
            );

            if (uidaiResponse.isSuccess()) {
                ekycRequest.setStatus(VerificationStatus.IN_PROGRESS);
                ekycRequest.setLastUpdatedAt(LocalDateTime.now());
                ekycRequestRepository.save(ekycRequest);

                auditLogger.logSuccess(referenceNumber, "OTP initiation successful");
                
                return new EkycInitiationResponse(
                    referenceNumber,
                    VerificationStatus.IN_PROGRESS,
                    "OTP sent successfully"
                );
            } else {
                handleFailedInitiation(ekycRequest, "OTP generation failed at UIDAI");
                throw new EkycVerificationException("Failed to generate OTP");
            }
        } catch (Exception e) {
            handleFailedInitiation(ekycRequest, e.getMessage());
            throw new EkycVerificationException("Error during eKYC initiation", e);
        }
    }

    @Transactional
    public OtpVerificationResponse verifyOtp(OtpVerificationRequest request) {
        String maskedReferenceNumber = dataMasker.maskReferenceNumber(request.getReferenceNumber());
        auditLogger.logRequestInitiation(maskedReferenceNumber, "OTP verification initiated");

        validateOtpRequest(request);

        EkycRequest ekycRequest = ekycRequestRepository.findByReferenceNumber(request.getReferenceNumber())
            .orElseThrow(() -> new ResourceNotFoundException("eKYC request not found"));

        if (!VerificationStatus.IN_PROGRESS.equals(ekycRequest.getStatus())) {
            throw new InvalidStateException("eKYC request is not in valid state for OTP verification");
        }

        OtpVerification otpVerification = new OtpVerification();
        otpVerification.setEkycRequest(ekycRequest);
        otpVerification.setOtpValue(request.getOtp());
        otpVerification.setAttemptedAt(LocalDateTime.now());
        
        try {
            UidaiVerificationResponse uidaiResponse = uidaiApiClient.verifyOtp(
                ekycRequest.getIdNumber(),
                request.getOtp(),
                request.getReferenceNumber()
            );

            if (uidaiResponse.isVerified()) {
                otpVerification.setStatus(VerificationStatus.VERIFIED);
                ekycRequest.setStatus(VerificationStatus.VERIFIED);
                ekycRequest.setLastUpdatedAt(LocalDateTime.now());
                
                otpVerificationRepository.save(otpVerification);
                ekycRequestRepository.save(ekycRequest);

                auditLogger.logSuccess(maskedReferenceNumber, "OTP verification successful");
                
                return new OtpVerificationResponse(
                    request.getReferenceNumber(),
                    VerificationStatus.VERIFIED,
                    "OTP verified successfully"
                );
            } else {
                handleFailedOtpVerification(otpVerification, ekycRequest, "Invalid OTP");
                throw new InvalidOtpException("Invalid OTP provided");
            }
        } catch (Exception e) {
            handleFailedOtpVerification(otpVerification, ekycRequest, e.getMessage());
            throw new EkycVerificationException("Error during OTP verification", e);
        }
    }

    public VerificationStatusResponse getVerificationStatus(String referenceNumber) {
        String maskedReference = dataMasker.maskReferenceNumber(referenceNumber);
        auditLogger.logRequestInitiation(maskedReference, "Status check initiated");

        EkycRequest ekycRequest = ekycRequestRepository.findByReferenceNumber(referenceNumber)
            .orElseThrow(() -> new ResourceNotFoundException("eKYC request not found"));

        List<OtpVerification> otpAttempts = otpVerificationRepository.findByEkycRequestOrderByAttemptedAtDesc(ekycRequest);
        
        return new VerificationStatusResponse(
            referenceNumber,
            ekycRequest.getStatus(),
            ekycRequest.getLastUpdatedAt(),
            otpAttempts.size(),
            ekycRequest.getCreatedAt()
        );
    }

    private void validateInitiationRequest(EkycInitiationRequest request) {
        if (!ValidationUtils.isValidAadhaarOrVid(request.getAadhaarOrVid())) {
            throw new ValidationException("Invalid Aadhaar/VID format");
        }
        if (request.getIdType() == null) {
            throw new ValidationException("ID type must be specified");
        }
        if (!StringUtils.hasText(request.getSessionId())) {
            throw new ValidationException("Session ID is required");
        }
        if (request.getIdentityVerificationConsent() == null || !request.getIdentityVerificationConsent()) {
            throw new ValidationException("Identity verification consent is mandatory");
        }
    }

    private void validateOtpRequest(OtpVerificationRequest request) {
        if (!ValidationUtils.isValidOtp(request.getOtp())) {
            throw new ValidationException("Invalid OTP format");
        }
        if (!StringUtils.hasText(request.getReferenceNumber())) {
            throw new ValidationException("Reference number is required");
        }
    }

    private VerificationConsent createConsent(EkycInitiationRequest request, EkycRequest ekycRequest) {
        VerificationConsent consent = new VerificationConsent();
        consent.setEkycRequest(ekycRequest);
        consent.setIdentityVerificationConsent(request.getIdentityVerificationConsent());
        consent.setContactVerificationConsent(request.getContactVerificationConsent());
        consent.setConsentTimestamp(LocalDateTime.now());
        return consent;
    }

    private void handleFailedInitiation(EkycRequest ekycRequest, String reason) {
        ekycRequest.setStatus(VerificationStatus.FAILED);
        ekycRequest.setLastUpdatedAt(LocalDateTime.now());
        ekycRequest.setFailureReason(reason);
        ekycRequestRepository.save(ekycRequest);
        
        auditLogger.logFailure(
            ekycRequest.getReferenceNumber(),
            "eKYC initiation failed",
            reason
        );
    }

    private void handleFailedOtpVerification(OtpVerification otpVerification, EkycRequest ekycRequest, String reason) {
        otpVerification.setStatus(VerificationStatus.FAILED);
        otpVerification.setFailureReason(reason);
        otpVerificationRepository.save(otpVerification);

        if (hasExceededMaxAttempts(ekycRequest)) {
            ekycRequest.setStatus(VerificationStatus.FAILED);
            ekycRequest.setFailureReason("Maximum OTP attempts exceeded");
            ekycRequest.setLastUpdatedAt(LocalDateTime.now());
            ekycRequestRepository.save(ekycRequest);
        }

        auditLogger.logFailure(
            ekycRequest.getReferenceNumber(),
            "OTP verification failed",
            reason
        );
    }

    private boolean hasExceededMaxAttempts(EkycRequest ekycRequest) {
        return otpVerificationRepository.countByEkycRequest(ekycRequest) >= 3;
    }

    private String generateReferenceNumber() {
        return "EKYC-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}