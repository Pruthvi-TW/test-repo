package com.ekyc.integration;

import com.ekyc.dto.EkycRequestDto;
import com.ekyc.dto.EkycResponseDto;
import com.ekyc.dto.OtpVerificationDto;
import com.ekyc.entity.EkycRequest;
import com.ekyc.entity.EkycStatus;
import com.ekyc.repository.EkycRequestRepository;
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
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Testcontainers
@Transactional
public class EkycIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:14-alpine")
            .withDatabaseName("ekyc_test")
            .withUsername("test")
            .withPassword("test");

    @DynamicPropertySource
    static void registerPgProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private EkycRequestRepository ekycRequestRepository;

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
    @DisplayName("Should complete full eKYC flow successfully")
    void testFullEkycFlow() throws Exception {
        // Step 1: Create eKYC request
        MvcResult createResult = mockMvc.perform(post("/api/ekyc")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validRequestDto)))
                .andExpect(status().isCreated())
                .andReturn();
        
        EkycResponseDto createResponse = objectMapper.readValue(
                createResult.getResponse().getContentAsString(), EkycResponseDto.class);
        String referenceNumber = createResponse.getReferenceNumber();
        
        // Verify request is saved in database
        Optional<EkycRequest> savedRequest = ekycRequestRepository.findByReferenceNumber(referenceNumber);
        assertTrue(savedRequest.isPresent());
        assertEquals(EkycStatus.INITIATED, savedRequest.get().getStatus());
        
        // Step 2: Generate OTP
        mockMvc.perform(post("/api/ekyc/{referenceNumber}/otp", referenceNumber)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validRequestDto.getMobileNumber())))
                .andExpect(status().isOk());
        
        // Get the generated OTP (in a real scenario, this would be sent to the user's mobile)
        String otp = otpService.getOtpForTesting(referenceNumber);
        assertNotNull(otp);
        
        // Step 3: Verify OTP
        OtpVerificationDto verificationDto = new OtpVerificationDto();
        verificationDto.setOtp(otp);
        
        mockMvc.perform(post("/api/ekyc/{referenceNumber}/verify-otp", referenceNumber)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(verificationDto)))
                .andExpect(status().isOk());
        
        // Verify status is updated in database
        Optional<EkycRequest> afterOtpVerification = ekycRequestRepository.findByReferenceNumber(referenceNumber);
        assertTrue(afterOtpVerification.isPresent());
        assertEquals(EkycStatus.OTP_VERIFIED, afterOtpVerification.get().getStatus());
        
        // Step 4: Complete verification
        mockMvc.perform(post("/api/ekyc/{referenceNumber}/complete", referenceNumber))
                .andExpect(status().isOk());
        
        // Verify status is updated in database