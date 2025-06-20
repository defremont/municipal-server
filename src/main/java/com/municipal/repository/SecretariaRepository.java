package com.municipal.repository;

import com.municipal.model.Secretaria;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SecretariaRepository extends MongoRepository<Secretaria, String> {
    
    /**
     * Find secretaria by sigla (unique identifier)
     */
    Optional<Secretaria> findBySiglaIgnoreCase(String sigla);
    
    /**
     * Check if exists by sigla (for validation)
     */
    boolean existsBySiglaIgnoreCase(String sigla);
    
    /**
     * Find secretarias by nome containing (case insensitive)
     */
    @Query("{'nome': {$regex: ?0, $options: 'i'}}")
    java.util.List<Secretaria> findByNomeContainingIgnoreCase(String nome);
    
    /**
     * Check if exists by sigla excluding current id (for updates)
     */
    boolean existsBySiglaIgnoreCaseAndIdNot(String sigla, String id);
} 