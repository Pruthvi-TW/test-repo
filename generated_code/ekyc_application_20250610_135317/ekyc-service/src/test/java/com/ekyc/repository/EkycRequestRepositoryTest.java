package com.ekyc.repository;

import com.ekyc.entity.EkycRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
public class EkycRequestRepositoryTest {

    @Autowired
    private EkycRequestRepository ekycRequestRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    @DisplayName("Save and Retrieve eKYC Request")
    public void testSaveAndRetrieveEkycRequest() {
        // Arrange
        EkycRequest request = createSampleEkycRequest();

        // Act
        EkycRequest savedRequest = ekycRequestRepository.save(request);
        entityManager.flush();
        entityManager.clear();

        // Assert
        EkycRequest retrievedRequest = ekycRequestRepository.findById(savedRequest.getId())
            .orElseThrow(() -> new AssertionError("Request not saved"));
        
        assertEquals(request.getFirstName(), retrievedRequest.getFirstName());
        assertEquals(request.getReferenceNumber(), retrievedRequest.getReferenceNumber());
    }

    @Test
    @DisplayName("Find Request by Reference Number")
    public void testFindByReferenceNumber() {
        // Arrange
        EkycRequest request = createSampleEkycRequest();
        ekycRequestRepository.save(request);
        entityManager.flush();

        // Act
        EkycRequest foundRequest = ekycRequestRepository.findByReferenceNumber(request.getReferenceNumber())
            .orElseThrow(() -> new AssertionError("Request not found"));

        // Assert
        assertNotNull(foundRequest);
        assertEquals(request.getReferenceNumber(), foundRequest.getReferenceNumber());
    }

    @Test
    @DisplayName("Check Unique Constraint on Reference Number")
    public void testUniqueReferenceNumber() {
        // Arrange
        EkycRequest request1 = createSampleEkycRequest();
        EkycRequest request2 = createSampleEkycRequest();
        request2.setReferenceNumber(request1.getReferenceNumber());

        // Act & Assert
        ekycRequestRepository.save(request1);
        assertThrows(Exception.class, 
            () -> ekycRequestRepository.save(request2),
            "Duplicate reference number should not be allowed"
        );
    }

    private EkycRequest createSampleEkycRequest() {
        EkycRequest request = new EkycRequest();
        request.setFirstName("John");
        request.setLastName("Doe");
        request.setEmail("john.doe@example.com");
        request.setPhoneNumber("+911234567890");
        request.setIdType("PASSPORT");
        request.setIdNumber("A1234567");
        request.setReferenceNumber("EKYC-" + System.currentTimeMillis());
        request.setCreatedAt(LocalDateTime.now());
        return request;
    }
}