package com.municipal.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.municipal.model.Secretaria;
import com.municipal.repository.SecretariaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureWebMvc
@ActiveProfiles("test")
class SecretariaControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private SecretariaRepository secretariaRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        secretariaRepository.deleteAll();
    }

    @Test
    void createSecretaria_Success() throws Exception {
        Secretaria secretaria = new Secretaria("Secretaria de Educação", "SMED");

        mockMvc.perform(post("/api/secretarias")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(secretaria)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.nome").value("Secretaria de Educação"))
                .andExpect(jsonPath("$.sigla").value("SMED"))
                .andExpect(jsonPath("$.createdAt").exists())
                .andExpect(jsonPath("$.updatedAt").exists());
    }

    @Test
    void getAllSecretarias_Success() throws Exception {
        // Create test data
        secretariaRepository.save(new Secretaria("Secretaria de Educação", "SMED"));
        secretariaRepository.save(new Secretaria("Secretaria de Saúde", "SMS"));

        mockMvc.perform(get("/api/secretarias"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[*].sigla", containsInAnyOrder("SMED", "SMS")));
    }

    @Test
    void getSecretariaById_Success() throws Exception {
        Secretaria saved = secretariaRepository.save(new Secretaria("Secretaria de Educação", "SMED"));

        mockMvc.perform(get("/api/secretarias/{id}", saved.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(saved.getId()))
                .andExpect(jsonPath("$.nome").value("Secretaria de Educação"))
                .andExpect(jsonPath("$.sigla").value("SMED"));
    }

    @Test
    void updateSecretaria_Success() throws Exception {
        // Create initial secretaria
        Secretaria original = secretariaRepository.save(new Secretaria("Secretaria de Educação", "SMED"));
        
        // Updated data
        Secretaria updated = new Secretaria("Secretaria Municipal de Educação", "SME");

        mockMvc.perform(put("/api/secretarias/{id}", original.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updated)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(original.getId()))
                .andExpect(jsonPath("$.nome").value("Secretaria Municipal de Educação"))
                .andExpect(jsonPath("$.sigla").value("SME"))
                .andExpect(jsonPath("$.updatedAt").exists());
    }

    @Test
    void updateSecretaria_NotFound() throws Exception {
        Secretaria secretaria = new Secretaria("Secretaria de Educação", "SMED");

        mockMvc.perform(put("/api/secretarias/{id}", "nonexistentid")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(secretaria)))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateSecretaria_DuplicateSigla() throws Exception {
        // Create two secretarias
        Secretaria first = secretariaRepository.save(new Secretaria("Secretaria de Educação", "SMED"));
        Secretaria second = secretariaRepository.save(new Secretaria("Secretaria de Saúde", "SMS"));
        
        // Try to update second with first's sigla
        Secretaria updated = new Secretaria("Secretaria de Saúde Updated", "SMED");

        mockMvc.perform(put("/api/secretarias/{id}", second.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updated)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("Já existe uma secretaria com a sigla: SMED")));
    }

    @Test
    void deleteSecretaria_Success() throws Exception {
        Secretaria saved = secretariaRepository.save(new Secretaria("Secretaria de Educação", "SMED"));

        mockMvc.perform(delete("/api/secretarias/{id}", saved.getId()))
                .andExpect(status().isNoContent());
    }

    @Test
    void createSecretaria_ValidationError() throws Exception {
        Secretaria invalid = new Secretaria("", "A"); // Invalid nome and sigla too short

        mockMvc.perform(post("/api/secretarias")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest());
    }
} 