```java
package com.mockuidai.dto;

import com.mockuidai.enums.UidaiResponseStatus;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class UidaiInitiateResponse {
    private UidaiResponseStatus status;
    private String referenceId;
    private Instant timestamp;
}