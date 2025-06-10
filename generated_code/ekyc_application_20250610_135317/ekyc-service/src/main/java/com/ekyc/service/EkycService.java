```java
package com.ekyc.service;

import com.ekyc.exception.EkycException;
import com.ekyc.exception.ValidationException;
import com.ekyc.model.EkycRequest;
import com.ekyc.model.EkycStatus;
import com.ekyc.repository.EkycRequestRepository;
import com.ekyc.util.MaskingUtil;
import com.ekyc.util.ReferenceGenerator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
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

    @Transactional
    public EkycRequest initiateEkycRequest(EkycRequest request) {
        // Validate input
        validateEkycRequest(request);

        // Create and persist request
        EkycRequest ekycRequest = createEkycRequest(request);

        try {
            // Call UIDAI API
            uidaiService.initiateEkycVerification(ekycRequest);

            // Update status
            ekycRequest.setStatus(EkycStatus.IN_PROGRESS);
            ekycRequestRepository.save(ekycRequest);

            // Log audit trail
            auditService.logEkycInitiation(ekycRequest);

            return ekycRequest;
        } catch (Exception e) {
            // Handle API call failure
            ekycRequest.setStatus(EkycStatus.FAILED);
            ekycRequestRepository.save(ekycRequest);

            LOGGER.severe("eKYC Initiation Failed: " + MaskingUtil.maskSensitiveData(e.getMessage()));
            throw new EkycException("EKYC_INITIATION_FAILED", "Failed to initiate eKYC process");
        }
    }

    private void validateEkycRequest(EkycRequest request) {
        validationService.validateAadhaarOrVid(request.getIdentificationNumber());
        validationService.validateConsentFlags(request);
    }

    private EkycRequest createEkycRequest(EkycRequest request) {
        request.setReferenceNumber(ReferenceGenerator.generateUniqueReference());
        request.setCreatedAt(LocalDateTime.now());
        request.setStatus(EkycStatus.INITIATED);
        return ekycRequestRepository.save(request);
    }
}
```