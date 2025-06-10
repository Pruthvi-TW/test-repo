package com.ekyc.controller;

import com.ekyc.dto.EkycRequestDto;
import com.ekyc.dto.EkycResponseDto;
import com.ekyc.dto.OtpVerificationDto;
import com.ekyc.entity.EkycStatus;
import com.ekyc.exception.EkycException;
import com.ekyc.exception.ValidationException;
import com.ekyc.service.EkycService;
import com.ekyc.service.OtpService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class EkycControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private EkycService ekycService;

    @Autowired
    private OtpService otpService;

    private EkycRequestDto validRequestDto;

    @BeforeEach
    void setUp() {
        validRequestDto = new EkycRequestDto();
        validRequestDto.setFullName("John Doe");
        validRequestDto.setEmail("john.doe@example.com");
        validRequestDto.setMobileNumber("9876543210");
        validRequestDto.setAadhaarNumber("123456789012");
        validRequestDto.setPanNumber("ABCDE1234F");
        validRequestDto.setDateOfBirth(LocalDate.of(1990, 1, 1));
        validRequestDto.setAddress("123 Main St, Bangalore, Karnataka, 560001");
    }

    @Test
    @DisplayName("Should create eKYC request successfully")
    void testCreateEkycRequest_Success() throws Exception {
        // When & Then
        mockMvc.perform(post("/api/ekyc")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validRequestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.referenceNumber", notNullValue()))
                .andExpect(jsonPath("$.status", is(EkycStatus.INITIATED.toString())))
                .andExpect(jsonPath("$.maskedAadhaarNumber", notNullValue()))
                .andExpect(jsonPath("$.maskedPanNumber", notNullValue()));
    }

    @Test
    @DisplayName("Should return 400 Bad Request when creating eKYC with invalid data")
    void testCreateEkycRequest_InvalidData() throws Exception {
        // Given
        validRequestDto.setAadhaarNumber("12345"); // Invalid Aadhaar

        // When & Then
        mockMvc.perform(post("/api/ekyc")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validRequestDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("Aadhaar number")))
                .andExpect(jsonPath("$.status", is("BAD_REQUEST")));
    }

    @Test
    @DisplayName("Should generate OTP successfully")
    void testGenerateOtp_Success() throws Exception {
        // Given
        EkycResponseDto createdResponse = ekycService.createEkycRequest(validRequestDto);
        String referenceNumber = createdResponse.getReferenceNumber();

        // When & Then
        mockMvc.perform(post("/api/ekyc/{referenceNumber}/otp", referenceNumber)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validRequestDto.getMobileNumber())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", containsString("OTP sent")))
                .andExpect(jsonPath("$.referenceNumber", is(referenceNumber)));
    }

    @Test
    @DisplayName("Should return 404 Not Found when generating OTP for non-existent reference")
    void testGenerateOtp_NonExistentReference() throws Exception {
        // Given
        String nonExistentReference = "NON_EXISTENT_REF";

        // When & Then
        mockMvc.perform(post("/api/ekyc/{referenceNumber}/otp", nonExistentReference)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString("9876543210")))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", containsString("not found")))
                .andExpect(jsonPath("$.status", is("NOT_FOUND")));
    }

    @Test
    @DisplayName("Should verify OTP successfully")
    void testVerifyOtp_Success() throws Exception {
        // Given
        EkycResponseDto createdResponse = ekycService.createEkycRequest(validRequestDto);
        String referenceNumber = createdResponse.getReferenceNumber();
        String otp = otpService.generateAndSendOtp(referenceNumber, validRequestDto.getMobileNumber());
        
        OtpVerificationDto verificationDto = new OtpVerificationDto();
        verificationDto.setOtp(otp);

        // When & Then
        mockMvc.perform(post("/api/ekyc/{referenceNumber}/verify-otp", referenceNumber)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(verificationDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.referenceNumber", is(referenceNumber)))
                .andExpect(jsonPath("$.status", is(EkycStatus.OTP_VERIFIED.toString())));
    }

    @Test
    @DisplayName("Should return 400 Bad Request when verifying with invalid OTP")
    void testVerifyOtp_InvalidOtp() throws Exception {
        // Given
        EkycResponseDto createdResponse = ekycService.createEkycRequest(validRequestDto);
        String referenceNumber = createdResponse.getReferenceNumber();
        otpService.generateAndSendOtp(referenceNumber, validRequestDto.getMobileNumber());
        
        OtpVerificationDto verificationDto = new OtpVerificationDto();
        verificationDto.setOtp("999999"); // Invalid OTP

        // When & Then
        mockMvc.perform(post("/api/ekyc/{referenceNumber}/verify-otp", referenceNumber)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(verificationDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("Invalid OTP")))
                .andExpect(jsonPath("$.status", is("BAD_REQUEST")));
    }

    @Test
    @DisplayName("Should complete verification successfully")
    void testCompleteVerification_Success() throws Exception {
        // Given
        EkycResponseDto createdResponse = ekycService.createEkycRequest(validRequestDto);
        String referenceNumber = createdResponse.getReferenceNumber();
        String otp = otpService.generateAndSendOtp(referenceNumber, validRequestDto.getMobileNumber());
        ekycService.verifyOtp(referenceNumber, otp);

        // When & Then
        mockMvc.perform(post("/api/ekyc/{referenceNumber}/complete", referenceNumber))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.referenceNumber", is(referenceNumber)))
                .andExpect(jsonPath("$.status", is(EkycStatus.COMPLETED.toString())));
    }

    @Test
    @DisplayName("Should return 400 Bad Request when completing verification without OTP verification")
    void testCompleteVerification_WithoutOtpVerification() throws Exception {
        // Given
        EkycResponseDto createdResponse = ekycService.createEkycRequest(validRequestDto);
        String referenceNumber = createdResponse.getReferenceNumber();

        // When & Then
        mockMvc.perform(post("/api/ekyc/{referenceNumber}/complete", referenceNumber))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("OTP verification")))
                .andExpect(jsonPath("$.status", is("BAD_REQUEST")));
    }

    @Test
    @DisplayName("Should get eKYC request by reference number")
    void testGetEkycRequest_Success() throws Exception {
        // Given
        EkycResponseDto createdResponse = ekycService.createEkycRequest(validRequestDto);
        String referenceNumber = createdResponse.getReferenceNumber();

        // When & Then
        mockMvc.perform(get("/api/ekyc/{referenceNumber}", referenceNumber))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.referenceNumber", is(referenceNumber)))
                .andExpect(jsonPath("$.status", is(EkycStatus.INITIATED.toString())))
                .andExpect(jsonPath("$.maskedAadhaarNumber", notNullValue()))
                .andExpect(jsonPath("$.maskedPanNumber", notNullValue()));
    }

    @Test
    @DisplayName("Should return 404 Not Found when getting non-existent eKYC request")
    void testGetEkycRequest_NonExistent() throws Exception {
        // Given
        String nonExistentReference = "NON_EXISTENT_REF";

        // When & Then
        mockMvc.perform(get("/api/ekyc/{referenceNumber}", nonExistentReference))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", containsString("not found")))
                .andExpect(jsonPath("$.status", is("NOT_FOUND")));
    }

    @Test
    @DisplayName("Should get all eKYC requests")
    void testGetAllEkycRequests() throws Exception {
        // Given
        ekycService.createEkycRequest(validRequestDto);
        
        // Create another request
        EkycRequestDto anotherRequest = new EkycRequestDto();
        anotherRequest.setFullName("Jane Doe");
        anotherRequest.setEmail("jane.doe@example.com");
        anotherRequest.setMobileNumber("9876543211");
        anotherRequest.setAadhaarNumber("123456789013");
        anotherRequest.setPanNumber("ABCDE1234G");
        anotherRequest.setDateOfBirth(LocalDate.of(1992, 2, 2));
        anotherRequest.setAddress("456 Main St, Bangalore, Karnataka, 560001");
        ekycService.createEkycRequest(anotherRequest);

        // When & Then
        MvcResult result = mockMvc.perform(get("/api/ekyc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", isA(List.class)))
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(2))))
                .andReturn();
        
        // Verify response contains masked sensitive data
        String responseContent = result.getResponse().getContentAsString();
        assertFalse(responseContent.contains(validRequestDto.getAadhaarNumber()));
        assertFalse(responseContent.contains(validRequestDto.getPanNumber()));
    }

    @Test
    @DisplayName("Should handle validation exceptions properly")
    void testValidationExceptionHandling() throws Exception {
        // Given
        validRequestDto.setEmail("invalid-email"); // Invalid email

        // When & Then
        mockMvc.perform(post("/api/ekyc")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validRequestDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("email")))
                .andExpect(jsonPath("$.status", is("BAD_REQUEST")));
    }

    @Test
    @DisplayName("Should handle eKYC exceptions properly")
    void testEkycExceptionHandling() throws Exception {
        // Given
        String nonExistentReference = "NON_EXISTENT_REF";
        OtpVerificationDto verificationDto = new OtpVerificationDto();
        verificationDto.setOtp("123456");

        // When & Then
        mockMvc.perform(post("/api/ekyc/{referenceNumber}/verify-otp", nonExistentReference)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(verificationDto)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", containsString("not found")))
                .andExpect(jsonPath("$.status", is("NOT_FOUND")));
    }
}