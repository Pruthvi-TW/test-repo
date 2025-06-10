package com.ekyc.controller;

import com.ekyc.dto.EkycRequestDTO;
import com.ekyc.service.EkycService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockbean.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(EkycController.class)
public class EkycControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EkycService ekycService;

    private EkycRequestDTO validRequestDTO;

    @BeforeEach
    public void setUp() {
        validRequestDTO = createValidEkycRequestDTO();
    }

    @Test
    public void testCreateEkycRequest_ValidInput_ShouldReturnCreated() throws Exception {
        // Arrange
        String jsonRequest = convertToJson(validRequestDTO);

        // Act & Assert
        mockMvc.perform(post("/api/ekyc/request")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.referenceNumber").exists());
    }

    @Test
    public void testGetEkycRequest_ExistingRequest_ShouldReturnRequest() throws Exception {
        // Arrange
        String referenceNumber = "REF-12345";

        // Act & Assert
        mockMvc.perform(get("/api/ekyc/request/{referenceNumber}", referenceNumber)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.referenceNumber").value(referenceNumber));
    }

    @Test
    public void testCreateEkycRequest_InvalidInput_ShouldReturnBadRequest() throws Exception {
        // Arrange
        EkycRequestDTO invalidDTO = new EkycRequestDTO();
        String jsonRequest = convertToJson(invalidDTO);

        // Act & Assert
        mockMvc.perform(post("/api/ekyc/request")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest))
                .andExpect(status().isBadRequest());
    }

    private EkycRequestDTO createValidEkycRequestDTO() {
        EkycRequestDTO dto = new EkycRequestDTO();
        dto.setFirstName("John");
        dto.setLastName("Doe");
        dto.setEmail("john.doe@example.com");
        dto.setPhoneNumber("+1234567890");
        dto.setDateOfBirth(LocalDateTime.now().minusYears(25));
        return dto;
    }

    private String convertToJson(Object object) {
        // Implement JSON conversion logic
        return "{}"; // Placeholder
    }
}