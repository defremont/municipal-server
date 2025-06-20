package com.municipal.repository;

import com.municipal.model.Secretaria;
import com.municipal.model.Servidor;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ServidorRepository extends MongoRepository<Servidor, String> {
    
    /**
     * Find servidor by email (unique identifier)
     */
    Optional<Servidor> findByEmailIgnoreCase(String email);
    
    /**
     * Check if exists by email (for validation)
     */
    boolean existsByEmailIgnoreCase(String email);
    
    /**
     * Check if exists by email excluding current id (for updates)
     */
    boolean existsByEmailIgnoreCaseAndIdNot(String email, String id);
    
    /**
     * Find servidores by secretaria
     */
    List<Servidor> findBySecretaria(Secretaria secretaria);
    
    /**
     * Find servidores by secretaria id
     */
    List<Servidor> findBySecretariaId(String secretariaId);
    
    /**
     * Find servidores by nome containing (case insensitive)
     */
    @Query("{'nome': {$regex: ?0, $options: 'i'}}")
    List<Servidor> findByNomeContainingIgnoreCase(String nome);
    
    /**
     * Count servidores by secretaria
     */
    long countBySecretaria(Secretaria secretaria);
    
    /**
     * Find servidores with age range (calculated field)
     */
    @Query("{'dataNascimento': {$gte: ?1, $lte: ?0}}")
    List<Servidor> findByIdadeBetween(java.time.LocalDate maxDate, java.time.LocalDate minDate);
} 