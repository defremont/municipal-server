package com.municipal.controller;

import com.municipal.model.Servidor;
import com.municipal.service.ServidorService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/servidores")
@CrossOrigin(origins = "*")
public class ServidorController {
    
    private static final Logger logger = LoggerFactory.getLogger(ServidorController.class);
    
    private final ServidorService servidorService;
    
    @Autowired
    public ServidorController(ServidorService servidorService) {
        this.servidorService = servidorService;
    }
    
    /**
     * GET /servidores - Lista todos os servidores
     */
    @GetMapping
    public ResponseEntity<List<Servidor>> getAllServidores() {
        logger.info("GET /api/servidores - Listando todos os servidores");
        
        List<Servidor> servidores = servidorService.findAll();
        
        logger.info("GET /api/servidores - {} servidores encontrados", servidores.size());
        return ResponseEntity.ok(servidores);
    }
    
    /**
     * GET /servidores/{id} - Busca um servidor por ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<Servidor> getServidorById(@PathVariable String id) {
        logger.info("GET /api/servidores/{} - Buscando servidor por ID", id);
        
        Servidor servidor = servidorService.findById(id);
        
        logger.info("GET /api/servidores/{} - Servidor encontrado: {}", id, servidor.getEmail());
        return ResponseEntity.ok(servidor);
    }
    
    /**
     * POST /servidores - Cria um novo servidor
     */
    @PostMapping
    public ResponseEntity<Servidor> createServidor(@Valid @RequestBody Servidor servidor) {
        logger.info("POST /api/servidores - Criando novo servidor: {}", servidor.getEmail());
        
        Servidor createdServidor = servidorService.create(servidor);
        
        logger.info("POST /api/servidores - Servidor criado com sucesso: ID={}, Email={}", 
                   createdServidor.getId(), createdServidor.getEmail());
        return ResponseEntity.status(HttpStatus.CREATED).body(createdServidor);
    }
    
    /**
     * PUT /servidores/{id} - Atualiza um servidor existente
     */
    @PutMapping("/{id}")
    public ResponseEntity<Servidor> updateServidor(
            @PathVariable String id, 
            @Valid @RequestBody Servidor servidor) {
        
        logger.info("PUT /api/servidores/{} - Atualizando servidor", id);
        
        // Ensure the ID from path is used, not from request body
        servidor.setId(id);
        
        Servidor updatedServidor = servidorService.update(id, servidor);
        
        logger.info("PUT /api/servidores/{} - Servidor atualizado com sucesso: Email={}", 
                   id, updatedServidor.getEmail());
        return ResponseEntity.ok(updatedServidor);
    }
    
    /**
     * DELETE /servidores/{id} - Remove um servidor
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteServidor(@PathVariable String id) {
        logger.info("DELETE /api/servidores/{} - Deletando servidor", id);
        
        servidorService.delete(id);
        
        logger.info("DELETE /api/servidores/{} - Servidor deletado com sucesso", id);
        return ResponseEntity.noContent().build();
    }
} 