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
    public EkycRequest initiateEkycRequest(String identityNumber, String idType, 
                                           boolean identityConsent, 
                                           boolean contactConsent, 
                                           String sessionId) {
        // Validate input
        validationService.validateIdentityNumber(identityNumber, idType);
        validationService.validateConsent(identityConsent, contactConsent);

        // Create eKYC request
        EkycRequest ekycRequest = new EkycRequest();
        ekycRequest.setIdentityNumber(MaskingUtil.maskIdentityNumber(identityNumber));
        ekycRequest.setIdType(idType);
        ekycRequest.setIdentityConsent(identityConsent);
        ekycRequest.setContactConsent(contactConsent);
        ekycRequest.setSessionId(sessionId);
        ekycRequest.setReferenceNumber(ReferenceGenerator.generateReferenceNumber());
        ekycRequest.setStatus(EkycStatus.INITIATED);
        ekycRequest.setCreatedAt(LocalDateTime.now());

        try {
            // Call UIDAI API
            uidaiService.initiateEkycProcess(ekycRequest);

            // Update status
            ekycRequest.setStatus(EkycStatus.IN_PROGRESS);
            
            // Audit log
            auditService.logEkycInitiation(ekycRequest);

            // Persist request
            return ekycRequestRepository.save(ekycRequest);
        } catch (Exception e) {
            // Handle API call failure
            ekycRequest.setStatus(EkycStatus.FAILED);
            ekycRequestRepository.save(ekycRequest);

            LOGGER.severe("eKYC Initiation Failed: " + MaskingUtil.maskLog(e.getMessage()));
            throw new EkycException("EKYC_INITIATION_FAILED", "Failed to initiate eKYC process");
        }
    }

    @Transactional(readOnly = true)
    public EkycRequest getEkycRequestByReferenceNumber(String referenceNumber) {
        return ekycRequestRepository.findByReferenceNumber(referenceNumber)
            .orElseThrow(() -> new ValidationException("INVALID_REFERENCE", "Invalid eKYC reference number"));
    }
}
```