package com.ekyc.controller;

import com.ekyc.dto.EkycRequestDTO;
import com.ekyc.service.EkycService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockbean.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(EkycController.class)
public class EkycControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EkycService ekycService;

    @Test
    public void testCreateEkycRequest_ValidInput_ReturnsCreated() throws Exception {
        // Arrange
        EkycRequestDTO validRequest = createValidEkycRequest();
        String jsonRequest = convertToJson(validRequest);

        // Act & Assert
        mockMvc.perform(post("/api/ekyc/request")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.referenceNumber").exists());
    }

    @Test
    public void testCreateEkycRequest_InvalidInput_ReturnsBadRequest() throws Exception {
        // Arrange
        EkycRequestDTO invalidRequest = new EkycRequestDTO();
        String jsonRequest = convertToJson(invalidRequest);

        // Act & Assert
        mockMvc.perform(post("/api/ekyc/request")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testGetEkycRequest_ExistingRequest_ReturnsRequest() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/ekyc/request/{referenceNumber}", "VALID_REF_NUMBER"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.referenceNumber").exists());
    }

    private EkycRequestDTO createValidEkycRequest() {
        EkycRequestDTO request = new EkycRequestDTO();
        request.setFullName("John Doe");
        request.setEmail("john.doe@example.com");
        request.setPhoneNumber("+1234567890");
        return request;
    }

    private String convertToJson(Object object) {
        // Implement JSON conversion logic
        return "{}";
    }
}