```java
package com.mockuidai.dto;

import com.mockuidai.enums.UidaiResponseStatus;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class UidaiVerifyResponse {
    private UidaiResponseStatus status;
    private KycData kycData;
    private Instant timestamp;
}