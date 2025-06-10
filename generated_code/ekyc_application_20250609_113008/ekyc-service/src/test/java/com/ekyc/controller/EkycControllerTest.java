package com.ekyc.controller;

import com.ekyc.dto.EkycRequestDTO;
import com.ekyc.service.EkycService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("test")
public class EkycControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    public void testCreateEkycRequest_ValidInput_Success() throws Exception {
        EkycRequestDTO validRequest = createValidEkycRequest();
        
        mockMvc.perform(post("/api/ekyc/request")
            .contentType(MediaType.APPLICATION_JSON)
            .content(asJsonString(validRequest)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.referenceNumber").exists());
    }

    @Test
    public void testCreateEkycRequest_InvalidInput_BadRequest() throws Exception {
        EkycRequestDTO invalidRequest = new EkycRequestDTO();
        
        mockMvc.perform(post("/api/ekyc/request")
            .contentType(MediaType.APPLICATION_JSON)
            .content(asJsonString(invalidRequest)))
            .andExpect(status().isBadRequest());
    }

    @Test
    public void testGetEkycRequest_ExistingReference_Success() throws Exception {
        EkycRequestDTO validRequest = createValidEkycRequest();
        String referenceNumber = createEkycRequest(validRequest);
        
        mockMvc.perform(get("/api/ekyc/request/{reference}", referenceNumber))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.referenceNumber").value(referenceNumber));
    }

    private EkycRequestDTO createValidEkycRequest() {
        EkycRequestDTO dto = new EkycRequestDTO();
        dto.setFirstName("John");
        dto.setLastName("Doe");
        dto.setEmail("john.doe@example.com");
        dto.setPhoneNumber("+1234567890");
        return dto;
    }

    private String createEkycRequest(EkycRequestDTO request) throws Exception {
        String response = mockMvc.perform(post("/api/ekyc/request")
            .contentType(MediaType.APPLICATION_JSON)
            .content(asJsonString(request)))
            .andReturn()
            .getResponse()
            .getContentAsString();
        
        // Extract reference number from response
        return extractReferenceNumber(response);
    }

    private String asJsonString(Object obj) {
        try {
            return new com.fasterxml.jackson.databind.ObjectMapper()
                .writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private String extractReferenceNumber(String response) {
        // Implement JSON parsing to extract reference number
        return response;
    }
}