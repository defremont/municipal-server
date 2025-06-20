package com.municipal.service;

import com.municipal.exception.BusinessException;
import com.municipal.exception.ResourceNotFoundException;
import com.municipal.model.Secretaria;
import com.municipal.repository.SecretariaRepository;
import com.municipal.repository.ServidorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SecretariaServiceTest {

    @Mock
    private SecretariaRepository secretariaRepository;

    @Mock
    private ServidorRepository servidorRepository;

    @InjectMocks
    private SecretariaService secretariaService;

    private Secretaria secretaria;

    @BeforeEach
    void setUp() {
        secretaria = new Secretaria("Secretaria de Educação", "SEDUC");
        secretaria.setId("1");
    }

    @Test
    void findAll_ShouldReturnAllSecretarias() {
        // Given
        List<Secretaria> secretarias = Arrays.asList(secretaria);
        when(secretariaRepository.findAll()).thenReturn(secretarias);

        // When
        List<Secretaria> result = secretariaService.findAll();

        // Then
        assertEquals(1, result.size());
        assertEquals("SEDUC", result.get(0).getSigla());
        verify(secretariaRepository).findAll();
    }

    @Test
    void findById_WhenExists_ShouldReturnSecretaria() {
        // Given
        when(secretariaRepository.findById("1")).thenReturn(Optional.of(secretaria));

        // When
        Secretaria result = secretariaService.findById("1");

        // Then
        assertEquals("SEDUC", result.getSigla());
        assertEquals("Secretaria de Educação", result.getNome());
        verify(secretariaRepository).findById("1");
    }

    @Test
    void findById_WhenNotExists_ShouldThrowResourceNotFoundException() {
        // Given
        when(secretariaRepository.findById("1")).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> secretariaService.findById("1"));
        verify(secretariaRepository).findById("1");
    }

    @Test
    void create_WhenValid_ShouldReturnCreatedSecretaria() {
        // Given
        when(secretariaRepository.existsBySiglaIgnoreCase("SEDUC")).thenReturn(false);
        when(secretariaRepository.save(any(Secretaria.class))).thenReturn(secretaria);

        // When
        Secretaria result = secretariaService.create(secretaria);

        // Then
        assertEquals("SEDUC", result.getSigla());
        verify(secretariaRepository).existsBySiglaIgnoreCase("SEDUC");
        verify(secretariaRepository).save(secretaria);
    }

    @Test
    void create_WhenSiglaAlreadyExists_ShouldThrowBusinessException() {
        // Given
        when(secretariaRepository.existsBySiglaIgnoreCase("SEDUC")).thenReturn(true);

        // When & Then
        assertThrows(BusinessException.class, () -> secretariaService.create(secretaria));
        verify(secretariaRepository).existsBySiglaIgnoreCase("SEDUC");
        verify(secretariaRepository, never()).save(any());
    }

    @Test
    void create_WhenSecretariaIsNull_ShouldThrowBusinessException() {
        // When & Then
        assertThrows(BusinessException.class, () -> secretariaService.create(null));
        verify(secretariaRepository, never()).save(any());
    }

    @Test
    void update_WhenValid_ShouldReturnUpdatedSecretaria() {
        // Given
        Secretaria updatedSecretaria = new Secretaria("Secretaria de Educação Atualizada", "SEDUC");
        when(secretariaRepository.findById("1")).thenReturn(Optional.of(secretaria));
        when(secretariaRepository.existsBySiglaIgnoreCaseAndIdNot("SEDUC", "1")).thenReturn(false);
        when(secretariaRepository.save(any(Secretaria.class))).thenReturn(secretaria);

        // When
        Secretaria result = secretariaService.update("1", updatedSecretaria);

        // Then
        assertNotNull(result);
        verify(secretariaRepository).findById("1");
        verify(secretariaRepository).existsBySiglaIgnoreCaseAndIdNot("SEDUC", "1");
        verify(secretariaRepository).save(any(Secretaria.class));
    }

    @Test
    void delete_WhenHasNoServidores_ShouldDeleteSuccessfully() {
        // Given
        when(secretariaRepository.findById("1")).thenReturn(Optional.of(secretaria));
        when(servidorRepository.countBySecretaria(secretaria)).thenReturn(0L);

        // When
        secretariaService.delete("1");

        // Then
        verify(secretariaRepository).findById("1");
        verify(servidorRepository).countBySecretaria(secretaria);
        verify(secretariaRepository).deleteById("1");
    }

    @Test
    void delete_WhenHasServidores_ShouldThrowBusinessException() {
        // Given
        when(secretariaRepository.findById("1")).thenReturn(Optional.of(secretaria));
        when(servidorRepository.countBySecretaria(secretaria)).thenReturn(5L);

        // When & Then
        assertThrows(BusinessException.class, () -> secretariaService.delete("1"));
        verify(secretariaRepository).findById("1");
        verify(servidorRepository).countBySecretaria(secretaria);
        verify(secretariaRepository, never()).deleteById(anyString());
    }


} 