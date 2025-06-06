```java
package com.ekyc.dto.request;

import com.ekyc.enums.IdType;
import com.ekyc.enums.ConsentType;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

public final class EkycInitiateRequest {
    
    @NotBlank(message = "Customer ID is required")
    @Size(min = 8, max = 20, message = "Customer ID must be between 8 and 20 characters")
    private final String customerId;
    
    @NotNull(message = "ID type is required")
    private final IdType idType;
    
    @NotBlank(message = "ID number is required")
    @Pattern(regexp = "^[A-Z0-9]{8,15}$", message = "Invalid ID number format")
    private final String idNumber;
    
    @NotNull(message = "Consent type is required")
    private final ConsentType consentType;
    
    private final LocalDateTime requestTimestamp;

    public EkycInitiateRequest(String customerId, IdType idType, String idNumber, 
                             ConsentType consentType) {
        this.customerId = customerId;
        this.idType = idType;
        this.idNumber = idNumber;
        this.consentType = consentType;
        this.requestTimestamp = LocalDateTime.now();
    }

    // Getters only - immutable
    public String getCustomerId() { return customerId; }
    public IdType getIdType() { return idType; }
    public String getIdNumber() { return idNumber; }
    public ConsentType getConsentType() { return consentType; }
    public LocalDateTime getRequestTimestamp() { return requestTimestamp; }
}
```