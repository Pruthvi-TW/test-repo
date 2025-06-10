```java
package com.ekyc.service;

import com.ekyc.exception.EkycException;
import com.ekyc.exception.ValidationException;
import com.ekyc.model.EkycRequest;
import com.ekyc.model.EkycStatus;
import com.ekyc.repository.EkycRequestRepository;
import com.ekyc.util.MaskingUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Logger;

@Service
public class EkycService {
    private static final Logger LOGGER = Logger.getLogger(EkycService.class.getName());

    @Autowired
    private ValidationService validationService;

    @Autowired
    private UidaiIntegrationService uidaiService;

    @Autowired
    private AuditService auditService;

    @Autowired
    private EkycRequestRepository ekycRequestRepository;

    /**
     * Initiate eKYC request with comprehensive validation and processing
     * 
     * @param request eKYC initiation request
     * @return EkycRequest with reference number
     * @throws ValidationException if input validation fails
     * @throws EkycException for processing errors
     */
    @Transactional
    public EkycRequest initiateEkycRequest(EkycRequest request) {
        // Validate input
        validationService.validateEkycInitiationRequest(request);

        // Generate unique reference number
        String referenceNumber = generateReferenceNumber();
        request.setReferenceNumber(referenceNumber);
        request.setStatus(EkycStatus.INITIATED);
        request.setCreatedAt(LocalDateTime.now());

        try {
            // Call UIDAI API for initial verification
            boolean apiResponse = uidaiService.initiateEkycVerification(request);

            if (apiResponse) {
                request.setStatus(EkycStatus.IN_PROGRESS);
                
                // Audit log with masked data
                auditService.logEkycInitiation(
                    MaskingUtil.maskAadhaar(request.getIdentificationNumber()), 
                    referenceNumber
                );
            } else {
                request.setStatus(EkycStatus.FAILED);
                throw new EkycException("UIDAI_INITIATION_FAILED", 
                    "Unable to initiate eKYC with UIDAI");
            }

            // Persist request
            return ekycRequestRepository.save(request);

        } catch (Exception e) {
            LOGGER.severe("eKYC Initiation Error: " + e.getMessage());
            request.setStatus(EkycStatus.FAILED);
            throw new EkycException("EKYC_INITIATION_ERROR", e.getMessage());
        }
    }

    /**
     * Retrieve eKYC request by reference number
     * 
     * @param referenceNumber Unique reference number
     * @return Optional EkycRequest
     */
    public Optional<EkycRequest> getEkycRequestByReference(String referenceNumber) {
        return ekycRequestRepository.findByReferenceNumber(referenceNumber);
    }

    /**
     * Generate a unique reference number for tracking
     * 
     * @return Unique reference number
     */
    private String generateReferenceNumber() {
        return UUID.randomUUID().toString().replace("-", "").toUpperCase();
    }
}
```