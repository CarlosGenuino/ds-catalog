package br.com.gsolutions.productapi.services;

import br.com.gsolutions.productapi.repositories.ProductRepository;
import br.com.gsolutions.productapi.services.exceptions.DatabaseException;
import br.com.gsolutions.productapi.services.exceptions.ResourceNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
public class ProductServiceTests {

    @InjectMocks
    private ProductService service;

    @Mock
    private ProductRepository repository;

    private Long existingId;
    private Long nonExistId;
    private Long dependantId;

    @BeforeEach
    void setUp(){
        existingId = 1L;
        nonExistId = 1000L;
        dependantId = 4L;
        Mockito.doNothing().when(repository).deleteById(existingId);
        Mockito.doThrow(EmptyResultDataAccessException.class).when(repository).deleteById(nonExistId);
        Mockito.doThrow(DataIntegrityViolationException.class).when(repository).deleteById(dependantId);
    }

    @Test
    public void sholdDoNothingWhenIdExist(){
        Assertions.assertDoesNotThrow(() -> service.delete(existingId));
        Mockito.verify(repository, Mockito.times(1)).deleteById(existingId);
    }

    @Test
    public void sholdThowsExceptionWhenIdDoesExist(){
        Assertions.assertThrows(ResourceNotFoundException.class, () -> service.delete(nonExistId));

        Mockito.verify(repository, Mockito.times(1)).deleteById(nonExistId);
    }

    @Test
    public void sholdThowsDataIntegrityViolationExceptionWhenIdDoesExist(){
        Assertions.assertThrows(DatabaseException.class, () -> service.delete(dependantId));

        Mockito.verify(repository, Mockito.times(1)).deleteById(dependantId);
    }
}
