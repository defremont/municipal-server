package com.municipal.service;

import com.municipal.exception.BusinessException;
import com.municipal.exception.ResourceNotFoundException;
import com.municipal.model.Secretaria;
import com.municipal.model.Servidor;
import com.municipal.repository.ServidorRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class ServidorService {
    
    private static final Logger logger = LoggerFactory.getLogger(ServidorService.class);
    
    private final ServidorRepository servidorRepository;
    private final SecretariaService secretariaService;
    
    @Autowired
    public ServidorService(ServidorRepository servidorRepository, SecretariaService secretariaService) {
        this.servidorRepository = servidorRepository;
        this.secretariaService = secretariaService;
    }
    
    /**
     * Find all servidores
     */
    @Transactional(readOnly = true)
    public List<Servidor> findAll() {
        logger.debug("Buscando todos os servidores");
        return servidorRepository.findAll();
    }
    
    /**
     * Find servidor by ID (used internally)
     */
    @Transactional(readOnly = true)
    public Servidor findById(String id) {
        logger.debug("Buscando servidor com ID: {}", id);
        return servidorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Servidor", "id", id));
    }
    
    /**
     * Create new servidor
     */
    public Servidor create(Servidor servidor) {
        logger.debug("Criando novo servidor: {}", servidor);
        
        validateServidorForCreate(servidor);
        
        // Validate and set secretaria
        Secretaria secretaria = secretariaService.findById(servidor.getSecretaria().getId());
        servidor.setSecretaria(secretaria);
        
        try {
            Servidor savedServidor = servidorRepository.save(servidor);
            logger.info("Servidor criado com sucesso - ID: {}, Email: {}", 
                       savedServidor.getId(), savedServidor.getEmail());
            return savedServidor;
        } catch (DuplicateKeyException e) {
            logger.error("Erro ao criar servidor - email já existe: {}", servidor.getEmail());
            throw new BusinessException("Já existe um servidor com o email: " + servidor.getEmail());
        }
    }
    
    /**
     * Update existing servidor
     */
    public Servidor update(String id, Servidor servidor) {
        logger.debug("Atualizando servidor - ID: {}", id);
        
        Servidor existingServidor = findById(id);
        validateServidorForUpdate(id, servidor);
        
        // Validate and set secretaria if changed
        if (!existingServidor.getSecretaria().getId().equals(servidor.getSecretaria().getId())) {
            Secretaria secretaria = secretariaService.findById(servidor.getSecretaria().getId());
            existingServidor.setSecretaria(secretaria);
        }
        
        // Update fields
        existingServidor.setNome(servidor.getNome());
        existingServidor.setEmail(servidor.getEmail());
        existingServidor.setDataNascimento(servidor.getDataNascimento());
        existingServidor.setUpdatedAt(LocalDateTime.now());
        
        try {
            Servidor updatedServidor = servidorRepository.save(existingServidor);
            logger.info("Servidor atualizado com sucesso - ID: {}, Email: {}", 
                       updatedServidor.getId(), updatedServidor.getEmail());
            return updatedServidor;
        } catch (DuplicateKeyException e) {
            logger.error("Erro ao atualizar servidor - email já existe: {}", servidor.getEmail());
            throw new BusinessException("Já existe um servidor com o email: " + servidor.getEmail());
        }
    }
    
    /**
     * Delete servidor by ID
     */
    public void delete(String id) {
        logger.debug("Deletando servidor - ID: {}", id);
        
        Servidor servidor = findById(id);
        servidorRepository.deleteById(id);
        
        logger.info("Servidor deletado com sucesso - ID: {}, Email: {}", id, servidor.getEmail());
    }
    
    /**
     * Validate servidor for creation
     */
    private void validateServidorForCreate(Servidor servidor) {
        if (servidor == null) {
            throw new BusinessException("Servidor não pode ser nulo");
        }
        
        // Check email uniqueness
        if (servidorRepository.existsByEmailIgnoreCase(servidor.getEmail())) {
            throw new BusinessException("Já existe um servidor com o email: " + servidor.getEmail());
        }
        
        // Validate age
        if (!servidor.isIdadeValida()) {
            throw new BusinessException(
                String.format("Idade inválida: %d anos. Deve estar entre 18 e 75 anos", 
                             servidor.getIdade()));
        }
        
        // Validate secretaria
        if (servidor.getSecretaria() == null || servidor.getSecretaria().getId() == null) {
            throw new BusinessException("Secretaria é obrigatória");
        }
    }
    
    /**
     * Validate servidor for update
     */
    private void validateServidorForUpdate(String id, Servidor servidor) {
        if (servidor == null) {
            throw new BusinessException("Servidor não pode ser nulo");
        }
        
        // Check email uniqueness (excluding current servidor)
        if (servidorRepository.existsByEmailIgnoreCaseAndIdNot(servidor.getEmail(), id)) {
            throw new BusinessException("Já existe um servidor com o email: " + servidor.getEmail());
        }
        
        // Validate age
        if (!servidor.isIdadeValida()) {
            throw new BusinessException(
                String.format("Idade inválida: %d anos. Deve estar entre 18 e 75 anos", 
                             servidor.getIdade()));
        }
        
        // Validate secretaria
        if (servidor.getSecretaria() == null || servidor.getSecretaria().getId() == null) {
            throw new BusinessException("Secretaria é obrigatória");
        }
    }
} 