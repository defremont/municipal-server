package com.municipal.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.municipal.model.Secretaria;
import com.municipal.model.Servidor;
import com.municipal.repository.SecretariaRepository;
import com.municipal.repository.ServidorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDate;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureWebMvc
@ActiveProfiles("test")
class ServidorControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private ServidorRepository servidorRepository;

    @Autowired
    private SecretariaRepository secretariaRepository;

    private ObjectMapper objectMapper;
    private MockMvc mockMvc;
    private Secretaria testSecretaria;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        
        // Clean up
        servidorRepository.deleteAll();
        secretariaRepository.deleteAll();
        
        // Create test secretaria
        testSecretaria = secretariaRepository.save(new Secretaria("Secretaria de Educação", "SMED"));
    }

    @Test
    void createServidor_Success() throws Exception {
        Servidor servidor = new Servidor(
            "João Silva", 
            "joao.silva@email.com", 
            LocalDate.of(1990, 5, 15), 
            testSecretaria
        );

        mockMvc.perform(post("/api/servidores")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(servidor)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.nome").value("João Silva"))
                .andExpect(jsonPath("$.email").value("joao.silva@email.com"))
                .andExpect(jsonPath("$.dataNascimento").value("1990-05-15"))
                .andExpect(jsonPath("$.secretaria.id").value(testSecretaria.getId()))
                .andExpect(jsonPath("$.createdAt").exists())
                .andExpect(jsonPath("$.updatedAt").exists());
    }

    @Test
    void getAllServidores_Success() throws Exception {
        // Create test data
        servidorRepository.save(new Servidor("João Silva", "joao@email.com", LocalDate.of(1990, 5, 15), testSecretaria));
        servidorRepository.save(new Servidor("Maria Santos", "maria@email.com", LocalDate.of(1985, 3, 20), testSecretaria));

        mockMvc.perform(get("/api/servidores"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[*].email", containsInAnyOrder("joao@email.com", "maria@email.com")));
    }

    @Test
    void getServidorById_Success() throws Exception {
        Servidor saved = servidorRepository.save(
            new Servidor("João Silva", "joao@email.com", LocalDate.of(1990, 5, 15), testSecretaria)
        );

        mockMvc.perform(get("/api/servidores/{id}", saved.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(saved.getId()))
                .andExpect(jsonPath("$.nome").value("João Silva"))
                .andExpect(jsonPath("$.email").value("joao@email.com"));
    }

    @Test
    void updateServidor_Success() throws Exception {
        // Create initial servidor
        Servidor original = servidorRepository.save(
            new Servidor("João Silva", "joao@email.com", LocalDate.of(1990, 5, 15), testSecretaria)
        );
        
        // Updated data
        Servidor updated = new Servidor(
            "João Silva Santos", 
            "joao.santos@email.com", 
            LocalDate.of(1990, 5, 15), 
            testSecretaria
        );

        mockMvc.perform(put("/api/servidores/{id}", original.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updated)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(original.getId()))
                .andExpect(jsonPath("$.nome").value("João Silva Santos"))
                .andExpect(jsonPath("$.email").value("joao.santos@email.com"))
                .andExpect(jsonPath("$.updatedAt").exists());
    }

    @Test
    void updateServidor_NotFound() throws Exception {
        Servidor servidor = new Servidor("João Silva", "joao@email.com", LocalDate.of(1990, 5, 15), testSecretaria);

        mockMvc.perform(put("/api/servidores/{id}", "nonexistentid")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(servidor)))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateServidor_DuplicateEmail() throws Exception {
        // Create two servidores
        Servidor first = servidorRepository.save(
            new Servidor("João Silva", "joao@email.com", LocalDate.of(1990, 5, 15), testSecretaria)
        );
        Servidor second = servidorRepository.save(
            new Servidor("Maria Santos", "maria@email.com", LocalDate.of(1985, 3, 20), testSecretaria)
        );
        
        // Try to update second with first's email
        Servidor updated = new Servidor("Maria Santos", "joao@email.com", LocalDate.of(1985, 3, 20), testSecretaria);

        mockMvc.perform(put("/api/servidores/{id}", second.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updated)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("Já existe um servidor com o email: joao@email.com")));
    }

    @Test
    void updateServidor_InvalidAge() throws Exception {
        Servidor original = servidorRepository.save(
            new Servidor("João Silva", "joao@email.com", LocalDate.of(1990, 5, 15), testSecretaria)
        );
        
        // Try to update with invalid age (too young)
        Servidor updated = new Servidor(
            "João Silva", 
            "joao@email.com", 
            LocalDate.now().minusYears(16), // 16 years old
            testSecretaria
        );

        mockMvc.perform(put("/api/servidores/{id}", original.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updated)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("Idade inválida")));
    }

    @Test
    void deleteServidor_Success() throws Exception {
        Servidor saved = servidorRepository.save(
            new Servidor("João Silva", "joao@email.com", LocalDate.of(1990, 5, 15), testSecretaria)
        );

        mockMvc.perform(delete("/api/servidores/{id}", saved.getId()))
                .andExpect(status().isNoContent());
    }

    @Test
    void createServidor_ValidationError() throws Exception {
        Servidor invalid = new Servidor("", "invalid-email", LocalDate.now().plusDays(1), testSecretaria);

        mockMvc.perform(post("/api/servidores")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest());
    }
} 