```java
package com.mockuidai.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class KycData {
    private String name;
    private String dob;
    private String gender;
    private String address;
    private String email;
    private String mobile;
    private String photo;
}
```