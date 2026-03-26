package com.smartsure.claimsservice.controller;

import com.smartsure.claimsservice.service.ClaimService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
class ClaimControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ClaimService claimService;

    @Test
    void testGetAllClaims() throws Exception {

        when(claimService.getAllClaims())
                .thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/admin/claims"))
                .andExpect(status().isOk());
    }
}