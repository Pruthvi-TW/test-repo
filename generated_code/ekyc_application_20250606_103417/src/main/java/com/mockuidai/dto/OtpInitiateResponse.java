```java
package com.mockuidai.dto;

import com.mockuidai.enums.OtpStatus;
import lombok.Data;
import java.time.Instant;

@Data
public class OtpInitiateResponse {
    private OtpStatus status;
    private String referenceId;
    private Instant timestamp;
}
```