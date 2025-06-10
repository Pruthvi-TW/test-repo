package com.kyc.verification.service;

import com.kyc.verification.dto.ConsentRequest;
import com.kyc.verification.dto.ConsentResponse;
import com.kyc.verification.entity.EkycRequest;
import com.kyc.verification.entity.VerificationConsent;
import com.kyc.verification.enums.ConsentStatus;
import com.kyc.verification.enums.ConsentType;
import com.kyc.verification.exception.ConsentValidationException;
import com.kyc.verification.exception.ResourceNotFoundException;
import com.kyc.verification.repository.EkycRequestRepository;
import com.kyc.verification.repository.VerificationConsentRepository;
import com.kyc.verification.util.AuditLogger;
import com.kyc.verification.util.DataMasker;
import com.kyc.verification.validation.ConsentValidator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class ConsentService {

    private static final Logger logger = LoggerFactory.getLogger(ConsentService.class);

    private final VerificationConsentRepository consentRepository;
    private final EkycRequestRepository ekycRequestRepository;
    private final ConsentValidator consentValidator;
    private final AuditLogger auditLogger;
    private final DataMasker dataMasker;

    @Autowired
    public ConsentService(VerificationConsentRepository consentRepository,
                         EkycRequestRepository ekycRequestRepository,
                         ConsentValidator consentValidator,
                         AuditLogger auditLogger,
                         DataMasker dataMasker) {
        this.consentRepository = consentRepository;
        this.ekycRequestRepository = ekycRequestRepository;
        this.consentValidator = consentValidator;
        this.auditLogger = auditLogger;
        this.dataMasker = dataMasker;
    }

    @Transactional
    public ConsentResponse createConsent(ConsentRequest consentRequest, String sessionId) {
        logger.info("Processing consent creation request for session: {}", sessionId);
        
        validateConsentRequest(consentRequest);

        EkycRequest ekycRequest = ekycRequestRepository.findBySessionId(sessionId)
                .orElseThrow(() -> new ResourceNotFoundException("EkycRequest not found for session: " + sessionId));

        VerificationConsent consent = buildConsentEntity(consentRequest, ekycRequest);
        
        try {
            VerificationConsent savedConsent = consentRepository.save(consent);
            auditLogger.logConsentCreation(savedConsent, sessionId);
            
            return buildConsentResponse(savedConsent);
        } catch (Exception e) {
            logger.error("Error creating consent for session {}: {}", sessionId, e.getMessage());
            auditLogger.logConsentError(sessionId, e.getMessage());
            throw new RuntimeException("Failed to create consent record", e);
        }
    }

    @Transactional(readOnly = true)
    public List<ConsentResponse> getConsentsByEkycRequestId(String ekycRequestId) {
        logger.debug("Fetching consents for eKYC request: {}", ekycRequestId);
        
        List<VerificationConsent> consents = consentRepository.findByEkycRequestId(ekycRequestId);
        return consents.stream()
                .map(this::buildConsentResponse)
                .toList();
    }

    @Transactional
    public ConsentResponse updateConsentStatus(String consentId, ConsentStatus newStatus, String sessionId) {
        logger.info("Updating consent status for ID: {} to {}", consentId, newStatus);
        
        VerificationConsent consent = consentRepository.findById(consentId)
                .orElseThrow(() -> new ResourceNotFoundException("Consent not found: " + consentId));

        consent.setStatus(newStatus);
        consent.setUpdatedAt(LocalDateTime.now());
        
        VerificationConsent updatedConsent = consentRepository.save(consent);
        auditLogger.logConsentStatusUpdate(updatedConsent, sessionId);
        
        return buildConsentResponse(updatedConsent);
    }

    @Transactional(readOnly = true)
    public boolean verifyConsentStatus(String ekycRequestId, ConsentType consentType) {
        logger.debug("Verifying consent status for request: {} and type: {}", ekycRequestId, consentType);
        
        return consentRepository.findByEkycRequestIdAndType(ekycRequestId, consentType)
                .map(consent -> ConsentStatus.APPROVED.equals(consent.getStatus()))
                .orElse(false);
    }

    private void validateConsentRequest(ConsentRequest request) {
        if (!consentValidator.isValidConsentRequest(request)) {
            String errorMessage = "Invalid consent request";
            logger.error(errorMessage);
            throw new ConsentValidationException(errorMessage);
        }

        if (ConsentType.IDENTITY_VERIFICATION.equals(request.getType()) && !request.isApproved()) {
            String errorMessage = "Identity verification consent is mandatory";
            logger.error(errorMessage);
            throw new ConsentValidationException(errorMessage);
        }
    }

    private VerificationConsent buildConsentEntity(ConsentRequest request, EkycRequest ekycRequest) {
        VerificationConsent consent = new VerificationConsent();
        consent.setId(UUID.randomUUID().toString());
        consent.setEkycRequest(ekycRequest);
        consent.setType(request.getType());
        consent.setStatus(request.isApproved() ? ConsentStatus.APPROVED : ConsentStatus.REJECTED);
        consent.setCreatedAt(LocalDateTime.now());
        consent.setUpdatedAt(LocalDateTime.now());
        consent.setConsentText(request.getConsentText());
        consent.setIpAddress(request.getIpAddress());
        consent.setDeviceInfo(request.getDeviceInfo());
        return consent;
    }

    private ConsentResponse buildConsentResponse(VerificationConsent consent) {
        return ConsentResponse.builder()
                .id(consent.getId())
                .ekycRequestId(consent.getEkycRequest().getId())
                .type(consent.getType())
                .status(consent.getStatus())
                .createdAt(consent.getCreatedAt())
                .updatedAt(consent.getUpdatedAt())
                .build();
    }

    @Transactional
    public void revokeConsent(String consentId, String sessionId) {
        logger.info("Revoking consent for ID: {}", consentId);
        
        VerificationConsent consent = consentRepository.findById(consentId)
                .orElseThrow(() -> new ResourceNotFoundException("Consent not found: " + consentId));

        if (ConsentType.IDENTITY_VERIFICATION.equals(consent.getType())) {
            String errorMessage = "Identity verification consent cannot be revoked";
            logger.error(errorMessage);
            throw new ConsentValidationException(errorMessage);
        }

        consent.setStatus(ConsentStatus.REVOKED);
        consent.setUpdatedAt(LocalDateTime.now());
        
        consentRepository.save(consent);
        auditLogger.logConsentRevocation(consent, sessionId);
    }

    @Transactional(readOnly = true)
    public ConsentResponse getConsentById(String consentId) {
        logger.debug("Fetching consent details for ID: {}", consentId);
        
        VerificationConsent consent = consentRepository.findById(consentId)
                .orElseThrow(() -> new ResourceNotFoundException("Consent not found: " + consentId));
        
        return buildConsentResponse(consent);
    }

    @Transactional
    public void deleteExpiredConsents() {
        logger.info("Starting expired consents cleanup process");
        
        LocalDateTime expiryThreshold = LocalDateTime.now().minusDays(90);
        List<VerificationConsent> expiredConsents = consentRepository.findByCreatedAtBefore(expiryThreshold);
        
        for (VerificationConsent consent : expiredConsents) {
            if (!ConsentType.IDENTITY_VERIFICATION.equals(consent.getType())) {
                consentRepository.delete(consent);
                auditLogger.logConsentDeletion(consent);
            }
        }
        
        logger.info("Completed expired consents cleanup. Processed {} records", expiredConsents.size());
    }
}