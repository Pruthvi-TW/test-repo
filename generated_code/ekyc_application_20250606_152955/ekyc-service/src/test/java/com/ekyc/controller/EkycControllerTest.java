package com.ekyc.controller;

import com.ekyc.dto.EkycRequestDto;
import com.ekyc.dto.EkycResponseDto;
import com.ekyc.dto.OtpRequestDto;
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
    void testCreateEkycRequest() throws Exception {
        // When & Then
        mockMvc.perform(post("/api/ekyc")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validRequestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.referenceNumber", notNullValue()))
                .andExpect(jsonPath("$.status", is(EkycStatus.INITIATED.toString())))
                .andExpect(jsonPath("$.fullName", is(validRequestDto.getFullName())))
                .andExpect(jsonPath("$.email", is(validRequestDto.getEmail())))
                // Verify PII is masked
                .andExpect(jsonPath("$.mobileNumber", not(validRequestDto.getMobileNumber())))
                .andExpect(jsonPath("$.aadhaarNumber", not(validRequestDto.getAadhaarNumber())))
                .andExpect(jsonPath("$.panNumber", not(validRequestDto.getPanNumber())));
    }

    @Test
    @DisplayName("Should return 400 for invalid request data")
    void testCreateEkycRequestWithInvalidData() throws Exception {
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
    void testGenerateOtp() throws Exception {
        // Given
        EkycResponseDto createdResponse = ekycService.createEkycRequest(validRequestDto);
        String referenceNumber = createdResponse.getReferenceNumber();
        
        OtpRequestDto otpRequestDto = new OtpRequestDto();
        otpRequestDto.setReferenceNumber(referenceNumber);
        otpRequestDto.setMobileNumber(validRequestDto.getMobileNumber());

        // When & Then
        mockMvc.perform(post("/api/ekyc/otp/generate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(otpRequestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", containsString("OTP sent")))
                .andExpect(jsonPath("$.referenceNumber", is(referenceNumber)));
    }

    @Test
    @DisplayName("Should return 400 for OTP generation with invalid reference number")
    void testGenerateOtpWithInvalidReferenceNumber() throws Exception {
        // Given
        OtpRequestDto otpRequestDto = new OtpRequestDto();
        otpRequestDto.setReferenceNumber("INVALID_REF");
        otpRequestDto.setMobileNumber(validRequestDto.getMobileNumber());

        // When & Then
        mockMvc.perform(post("/api/ekyc/otp/generate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(otpRequestDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("not found")))
                .andExpect(jsonPath("$.status", is("BAD_REQUEST")));
    }

    @Test
    @DisplayName("Should verify OTP successfully")
    void testVerifyOtp() throws Exception {
        // Given
        EkycResponseDto createdResponse = ekycService.createEkycRequest(validRequestDto);
        String referenceNumber = createdResponse.getReferenceNumber();
        
        String otp = otpService.generateAndSendOtp(referenceNumber, validRequestDto.getMobileNumber());
        
        // When & Then
        mockMvc.perform(post("/api/ekyc/otp/verify")
                .param("referenceNumber", referenceNumber)
                .param("otp", otp))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.referenceNumber", is(referenceNumber)))
                .andExpect(jsonPath("$.status", is(EkycStatus.OTP_VERIFIED.toString())));
    }

    @Test
    @DisplayName("Should return 400 for OTP verification with invalid OTP")
    void testVerifyOtpWithInvalidOtp() throws Exception {
        // Given
        EkycResponseDto createdResponse = ekycService.createEkycRequest(validRequestDto);
        String referenceNumber = createdResponse.getReferenceNumber();
        
        otpService.generateAndSendOtp(referenceNumber, validRequestDto.getMobileNumber());
        String invalidOtp = "999999";
        
        // When & Then
        mockMvc.perform(post("/api/ekyc/otp/verify")
                .param("referenceNumber", referenceNumber)
                .param("otp", invalidOtp))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("Invalid OTP")))
                .andExpect(jsonPath("$.status", is("BAD_REQUEST")));
    }

    @Test
    @DisplayName("Should complete verification successfully")
    void testCompleteVerification() throws Exception {
        // Given
        EkycResponseDto createdResponse = ekycService.createEkycRequest(validRequestDto);
        String referenceNumber = createdResponse.getReferenceNumber();
        
        String otp = otpService.generateAndSendOtp(referenceNumber, validRequestDto.getMobileNumber());
        ekycService.verifyOtp(referenceNumber, otp);
        
        // When & Then
        mockMvc.perform(post("/api/ekyc/complete")
                .param("referenceNumber", referenceNumber))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.referenceNumber", is(referenceNumber)))
                .andExpect(jsonPath("$.status", is(EkycStatus.COMPLETED.toString())));
    }

    @Test
    @DisplayName("Should return 400 for completion without OTP verification")
    void testCompleteVerificationWithoutOtpVerification() throws Exception {
        // Given
        EkycResponseDto createdResponse = ekycService.createEkycRequest(validRequestDto);
        String referenceNumber = createdResponse.getReferenceNumber();
        
        // When & Then
        mockMvc.perform(post("/api/ekyc/complete")
                .param("referenceNumber", referenceNumber))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("OTP verification required")))
                .andExpect(jsonPath("$.status", is("BAD_REQUEST")));
    }

    @Test
    @DisplayName("Should get eKYC request by reference number")
    void testGetEkycRequestByReferenceNumber() throws Exception {
        // Given
        EkycResponseDto createdResponse = ekycService.createEkycRequest(validRequestDto);
        String referenceNumber = createdResponse.getReferenceNumber();
        
        // When & Then
        mockMvc.perform(get("/api/ekyc/{referenceNumber}", referenceNumber))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.referenceNumber", is(referenceNumber)))
                .andExpect(jsonPath("$.status", is(EkycStatus.INITIATED.toString())))
                .andExpect(jsonPath("$.fullName", is(validRequestDto.getFullName())))
                // Verify PII is masked
                .andExpect(jsonPath("$.mobileNumber", not(validRequestDto.getMobileNumber())))
                .andExpect(jsonPath("$.aadhaarNumber", not(validRequestDto.getAadhaarNumber())))
                .andExpect(jsonPath("$.panNumber", not(validRequestDto.getPanNumber())));
    }

    @Test
    @DisplayName("Should return 404 for non-existent reference number")
    void testGetEkycRequestByNonExistentReferenceNumber() throws Exception {
        // Given
        String nonExistentReferenceNumber = "NON_EXISTENT_REF";
        
        // When & Then
        mockMvc.perform(get("/api/ekyc/{referenceNumber}", nonExistentReferenceNumber))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", containsString("not found")))
                .andExpect(jsonPath("$.status", is("NOT_FOUND")));
    }

    @Test
    @DisplayName("Should get all eKYC requests by status")
    void testGetAllEkycRequestsByStatus() throws Exception {
        // Given
        ekycService.createEkycRequest(validRequestDto);
        
        // When & Then
        mockMvc.perform(get("/api/ekyc/status/{status}", EkycStatus.INITIATED))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$[0].status", is(EkycStatus.INITIATED.toString())));
    }

    @Test
    @DisplayName("Should handle validation errors properly")
    void testValidationErrorHandling() throws Exception {
        // Given
        validRequestDto.setEmail("invalid-email");
        validRequestDto.setMobileNumber("123");
        
        // When & Then
        mockMvc.perform(post("/api/ekyc")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validRequestDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", anyOf(
                    containsString("Email"),
                    containsString("Mobile number")
                )))
                .andExpect(jsonPath("$.status", is("BAD_REQUEST")));
    }

    @Test
    @DisplayName("Should handle method argument type mismatch")
    void testMethodArgumentTypeMismatch() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/ekyc/status/{status}", "INVALID_STATUS"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("Invalid status")))
                .andExpect(jsonPath("$.status", is("BAD_REQUEST")));
    }

    @Test
    @DisplayName("Should handle missing request parameters")
    void testMissingRequestParameters() throws Exception {
        // Given
        EkycResponseDto createdResponse = ekycService.createEkycRequest(validRequestDto);
        String referenceNumber = createdResponse.getReferenceNumber();
        
        // When & Then - Missing OTP parameter
        mockMvc.perform(post("/api/ekyc/otp/verify")
                .param("referenceNumber", referenceNumber))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("Required parameter")))
                .andExpect(jsonPath("$.status", is("BAD_REQUEST")));
    }

    @Test
    @DisplayName("Should handle request body conversion errors")
    void testRequestBodyConversionErrors() throws Exception {
        // Given - Invalid JSON
        String invalidJson = "{\"fullName\":\"John Doe\", \"email\":\"invalid-json";
        
        // When & Then
        mockMvc.perform(post("/api/ekyc")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("JSON parse error")))
                .andExpect(jsonPath("$.status", is("BAD_REQUEST")));
    }

    @Test
    @DisplayName("Should return correct content type")
    void testContentTypeHeader() throws Exception {
        // Given
        EkycResponseDto createdResponse = ekycService.createEkycRequest(validRequestDto);
        String referenceNumber = createdResponse.getReferenceNumber();
        
        // When & Then
        mockMvc.perform(