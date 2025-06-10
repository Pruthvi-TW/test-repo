package com.ekyc.controller;

import com.ekyc.dto.EkycRequestDTO;
import com.ekyc.service.EkycService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockbean.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(EkycController.class)
public class EkycControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EkycService ekycService;

    @Autowired
    private ObjectMapper objectMapper;

    private EkycRequestDTO validRequest;

    @BeforeEach
    public void setUp() {
        validRequest = createValidEkycRequest();
    }

    @Test
    public void testCreateEkycRequest_ValidInput_Success() throws Exception {
        mockMvc.perform(post("/api/ekyc/request")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("INITIATED"));
    }

    @Test
    public void testCreateEkycRequest_InvalidInput_BadRequest() throws Exception {
        EkycRequestDTO invalidRequest = new EkycRequestDTO();

        mockMvc.perform(post("/api/ekyc/request")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").exists());
    }

    private EkycRequestDTO createValidEkycRequest() {
        EkycRequestDTO dto = new EkycRequestDTO();
        dto.setFirstName("John");
        dto.setLastName("Doe");
        dto.setEmail("john.doe@example.com");
        dto.setPhoneNumber("+1234567890");
        dto.setDateOfBirth(LocalDateTime.now().minusYears(30));
        return dto;
    }
}