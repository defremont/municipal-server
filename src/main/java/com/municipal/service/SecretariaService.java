package com.municipal.service;

import com.municipal.exception.BusinessException;
import com.municipal.exception.ResourceNotFoundException;
import com.municipal.model.Secretaria;
import com.municipal.repository.SecretariaRepository;
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
public class SecretariaService {
    
    private static final Logger logger = LoggerFactory.getLogger(SecretariaService.class);
    
    private final SecretariaRepository secretariaRepository;
    private final ServidorRepository servidorRepository;
    
    @Autowired
    public SecretariaService(SecretariaRepository secretariaRepository, ServidorRepository servidorRepository) {
        this.secretariaRepository = secretariaRepository;
        this.servidorRepository = servidorRepository;
    }
    
    /**
     * Find all secretarias
     */
    @Transactional(readOnly = true)
    public List<Secretaria> findAll() {
        logger.debug("Buscando todas as secretarias");
        return secretariaRepository.findAll();
    }
    
    /**
     * Find secretaria by ID (used internally)
     */
    @Transactional(readOnly = true)
    public Secretaria findById(String id) {
        logger.debug("Buscando secretaria com ID: {}", id);
        return secretariaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Secretaria", "id", id));
    }
    
    /**
     * Create new secretaria
     */
    public Secretaria create(Secretaria secretaria) {
        logger.debug("Criando nova secretaria: {}", secretaria);
        
        validateSecretariaForCreate(secretaria);
        
        try {
            Secretaria savedSecretaria = secretariaRepository.save(secretaria);
            logger.info("Secretaria criada com sucesso - ID: {}, Sigla: {}", 
                       savedSecretaria.getId(), savedSecretaria.getSigla());
            return savedSecretaria;
        } catch (DuplicateKeyException e) {
            logger.error("Erro ao criar secretaria - sigla já existe: {}", secretaria.getSigla());
            throw new BusinessException("Já existe uma secretaria com a sigla: " + secretaria.getSigla());
        }
    }
    
    /**
     * Update existing secretaria
     */
    public Secretaria update(String id, Secretaria secretaria) {
        logger.debug("Atualizando secretaria - ID: {}", id);
        
        Secretaria existingSecretaria = findById(id);
        validateSecretariaForUpdate(id, secretaria);
        
        // Update fields
        existingSecretaria.setNome(secretaria.getNome());
        existingSecretaria.setSigla(secretaria.getSigla());
        existingSecretaria.setUpdatedAt(LocalDateTime.now());
        
        try {
            Secretaria updatedSecretaria = secretariaRepository.save(existingSecretaria);
            logger.info("Secretaria atualizada com sucesso - ID: {}, Sigla: {}", 
                       updatedSecretaria.getId(), updatedSecretaria.getSigla());
            return updatedSecretaria;
        } catch (DuplicateKeyException e) {
            logger.error("Erro ao atualizar secretaria - sigla já existe: {}", secretaria.getSigla());
            throw new BusinessException("Já existe uma secretaria com a sigla: " + secretaria.getSigla());
        }
    }
    
    /**
     * Delete secretaria by ID
     */
    public void delete(String id) {
        logger.debug("Deletando secretaria - ID: {}", id);
        
        Secretaria secretaria = findById(id);
        
        // Check if secretaria has servidores
        long servidorCount = servidorRepository.countBySecretaria(secretaria);
        if (servidorCount > 0) {
            logger.error("Tentativa de deletar secretaria com servidores vinculados - ID: {}, Servidores: {}", 
                        id, servidorCount);
            throw new BusinessException(
                String.format("Não é possível excluir a secretaria '%s' pois possui %d servidor(es) vinculado(s)", 
                             secretaria.getSigla(), servidorCount));
        }
        
        secretariaRepository.deleteById(id);
        logger.info("Secretaria deletada com sucesso - ID: {}, Sigla: {}", id, secretaria.getSigla());
    }
    
    /**
     * Validate secretaria for creation
     */
    private void validateSecretariaForCreate(Secretaria secretaria) {
        if (secretaria == null) {
            throw new BusinessException("Secretaria não pode ser nula");
        }
        
        if (secretariaRepository.existsBySiglaIgnoreCase(secretaria.getSigla())) {
            throw new BusinessException("Já existe uma secretaria com a sigla: " + secretaria.getSigla());
        }
    }
    
    /**
     * Validate secretaria for update
     */
    private void validateSecretariaForUpdate(String id, Secretaria secretaria) {
        if (secretaria == null) {
            throw new BusinessException("Secretaria não pode ser nula");
        }
        
        if (secretariaRepository.existsBySiglaIgnoreCaseAndIdNot(secretaria.getSigla(), id)) {
            throw new BusinessException("Já existe uma secretaria com a sigla: " + secretaria.getSigla());
        }
    }
} 