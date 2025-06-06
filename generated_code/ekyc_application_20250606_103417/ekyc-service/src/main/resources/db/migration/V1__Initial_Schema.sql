```sql
CREATE TABLE ekyc_requests (
    id BIGSERIAL PRIMARY KEY,
    identity_number VARCHAR(12) NOT NULL,
    identity_type VARCHAR(10) NOT NULL,
    identity_verification_consent BOOLEAN NOT NULL,
    contact_info_consent BOOLEAN NOT NULL,
    session_id VARCHAR(50) NOT NULL UNIQUE,
    parent_process_id VARCHAR(50),
    status VARCHAR(20) NOT NULL,
    reference_number VARCHAR(50) UNIQUE,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

CREATE INDEX idx_ekyc_requests_reference_number ON ekyc_requests(reference_number);
CREATE INDEX idx_ekyc_requests_session_id ON ekyc_requests(session_id);
```

This provides the core infrastructure for the eKYC application. The setup includes:

1. Maven project structure with parent and child POMs
2. Main application class with JPA auditing enabled
3. Core domain entity for eKYC requests
4. Enums for identity type and verification status
5. JPA repository interface
6. Application configuration with PostgreSQL settings
7. Initial Flyway migration script

Next steps would include:
1. Adding OTP verification entity and repository
2. Creating service layer classes
3. Implementing REST controllers
4. Adding validation and error handling
5. Implementing security measures
6. Creating integration tests

Would you like me to continue with any of these aspects?