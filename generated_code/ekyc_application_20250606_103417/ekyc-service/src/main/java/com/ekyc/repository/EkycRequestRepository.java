```java
package com.ekyc.repository;

import com.ekyc.domain.EkycRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EkycRequestRepository extends JpaRepository<EkycRequest, Long> {
    Optional<EkycRequest> findByReferenceNumber(String referenceNumber);
    Optional<EkycRequest> findBySessionId(String sessionId);
}
```