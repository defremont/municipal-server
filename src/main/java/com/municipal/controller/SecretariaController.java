package com.municipal.controller;

import com.municipal.model.Secretaria;
import com.municipal.service.SecretariaService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/secretarias")
@CrossOrigin(origins = "*")
public class SecretariaController {
    
    private static final Logger logger = LoggerFactory.getLogger(SecretariaController.class);
    
    private final SecretariaService secretariaService;
    
    @Autowired
    public SecretariaController(SecretariaService secretariaService) {
        this.secretariaService = secretariaService;
    }
    
    /**
     * GET /secretarias - Lista todas as secretarias
     */
    @GetMapping
    public ResponseEntity<List<Secretaria>> getAllSecretarias() {
        logger.info("GET /api/secretarias - Listando todas as secretarias");
        
        List<Secretaria> secretarias = secretariaService.findAll();
        
        logger.info("GET /api/secretarias - {} secretarias encontradas", secretarias.size());
        return ResponseEntity.ok(secretarias);
    }
    
    /**
     * GET /secretarias/{id} - Busca uma secretaria por ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<Secretaria> getSecretariaById(@PathVariable String id) {
        logger.info("GET /api/secretarias/{} - Buscando secretaria por ID", id);
        
        Secretaria secretaria = secretariaService.findById(id);
        
        logger.info("GET /api/secretarias/{} - Secretaria encontrada: {}", id, secretaria.getSigla());
        return ResponseEntity.ok(secretaria);
    }
    
    /**
     * POST /secretarias - Cria uma nova secretaria
     */
    @PostMapping
    public ResponseEntity<Secretaria> createSecretaria(@Valid @RequestBody Secretaria secretaria) {
        logger.info("POST /api/secretarias - Criando nova secretaria: {}", secretaria.getSigla());
        
        Secretaria createdSecretaria = secretariaService.create(secretaria);
        
        logger.info("POST /api/secretarias - Secretaria criada com sucesso: ID={}, Sigla={}", 
                   createdSecretaria.getId(), createdSecretaria.getSigla());
        return ResponseEntity.status(HttpStatus.CREATED).body(createdSecretaria);
    }
    
    /**
     * PUT /secretarias/{id} - Atualiza uma secretaria existente
     */
    @PutMapping("/{id}")
    public ResponseEntity<Secretaria> updateSecretaria(
            @PathVariable String id, 
            @Valid @RequestBody Secretaria secretaria) {
        
        logger.info("PUT /api/secretarias/{} - Atualizando secretaria", id);
        
        // Ensure the ID from path is used, not from request body
        secretaria.setId(id);
        
        Secretaria updatedSecretaria = secretariaService.update(id, secretaria);
        
        logger.info("PUT /api/secretarias/{} - Secretaria atualizada com sucesso: Sigla={}", 
                   id, updatedSecretaria.getSigla());
        return ResponseEntity.ok(updatedSecretaria);
    }
    
    /**
     * DELETE /secretarias/{id} - Remove uma secretaria
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSecretaria(@PathVariable String id) {
        logger.info("DELETE /api/secretarias/{} - Deletando secretaria", id);
        
        secretariaService.delete(id);
        
        logger.info("DELETE /api/secretarias/{} - Secretaria deletada com sucesso", id);
        return ResponseEntity.noContent().build();
    }
} 