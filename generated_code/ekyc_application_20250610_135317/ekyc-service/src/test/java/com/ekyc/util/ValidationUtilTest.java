package com.ekyc.util;

import com.ekyc.dto.EkycRequestDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class ValidationUtilTest {

    @Autowired
    private ValidationUtil validationUtil;

    private EkycRequestDto validRequest;

    @BeforeEach
    public void setup() {
        validRequest = createValidEkycRequest();
    }

    @Test
    @DisplayName("Validate Valid Email")
    public void testValidEmail() {
        assertTrue(validationUtil.isValidEmail(validRequest.getEmail()));
    }

    @Test
    @DisplayName("Validate Invalid Email")
    public void testInvalidEmail() {
        assertFalse(validationUtil.isValidEmail("invalid-email"));
    }

    @Test
    @DisplayName("Validate Valid Phone Number")
    public void testValidPhoneNumber() {
        assertTrue(validationUtil.isValidPhoneNumber(validRequest.getPhoneNumber()));
    }

    @Test
    @DisplayName("Validate Invalid Phone Number")
    public void testInvalidPhoneNumber() {
        assertFalse(validationUtil.isValidPhoneNumber("123"));
    }

    @Test
    @DisplayName("Validate Complete Valid Request")
    public void testValidRequest() {
        assertDoesNotThrow(() -> validationUtil.validateEkycRequest(validRequest));
    }

    @Test
    @DisplayName("Validate Request with Missing Fields")
    public void testInvalidRequestWithMissingFields() {
        EkycRequestDto invalidRequest = new EkycRequestDto();
        assertThrows(ValidationException.class, 
            () -> validationUtil.validateEkycRequest(invalidRequest)
        );
    }

    private EkycRequestDto createValidEkycRequest() {
        EkycRequestDto dto = new EkycRequestDto();
        dto.setFirstName("John");
        dto.setLastName("Doe");
        dto.setEmail("john.doe@example.com");
        dto.setPhoneNumber("+911234567890");
        dto.setIdType("PASSPORT");
        dto.setIdNumber("A1234567");
        return dto;
    }
}