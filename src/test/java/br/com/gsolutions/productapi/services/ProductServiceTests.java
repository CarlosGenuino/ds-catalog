package br.com.gsolutions.productapi.services;

import br.com.gsolutions.productapi.dto.ProductDTO;
import br.com.gsolutions.productapi.entities.Product;
import br.com.gsolutions.productapi.factory.ProductFactory;
import br.com.gsolutions.productapi.repositories.ProductRepository;
import br.com.gsolutions.productapi.services.exceptions.DatabaseException;
import br.com.gsolutions.productapi.services.exceptions.ResourceNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;

@ExtendWith(SpringExtension.class)
public class ProductServiceTests {

    @InjectMocks
    private ProductService service;

    @Mock
    private ProductRepository repository;

    private Long existingId;
    private Long nonExistId;
    private Long dependantId;
    private Product product;

    @BeforeEach
    void setUp(){
        existingId = 1L;
        nonExistId = 2L;
        dependantId = 3L;
        product = ProductFactory.createNewProduct();
        PageImpl<Product> page = new PageImpl<>(List.of(product));

        // FindAll
        Mockito.when(repository.findAll((Pageable) ArgumentMatchers.any())).thenReturn(page);

        // FindOne
        Mockito.when(repository.getOne(existingId)).thenReturn(product);
        Mockito.when(repository.getOne(nonExistId)).thenThrow(EntityNotFoundException.class);

        // FindById
        Mockito.when(repository.findById(existingId)).thenReturn(Optional.of(product));
        Mockito.when(repository.findById(nonExistId)).thenThrow(ResourceNotFoundException.class);

        // Save
        Mockito.when(repository.save(product)).thenReturn(product);

        // Update
        Mockito.when(repository.save(product)).thenReturn(product);

        // Delete
        Mockito.doNothing().when(repository).deleteById(existingId);
        Mockito.doThrow(EmptyResultDataAccessException.class).when(repository).deleteById(nonExistId);
        Mockito.doThrow(DataIntegrityViolationException.class).when(repository).deleteById(dependantId);
    }

    @Test
    void whenSave(){
        Product product2 = repository.save(product);
        Assertions.assertEquals(product, product2);
    }

    @Test
    void whenCreate(){
        ProductDTO dto = ProductFactory.createProductDTO();
        ProductDTO retorno = service.create(dto);
        Assertions.assertEquals(retorno.getName(), dto.getName());
    }

    @Test
    void whenUpdate(){
        ProductDTO dto = service.findById(existingId);
        String novoNome = "Carré Suíno";
        dto.setName(novoNome);
        dto = service.update(existingId, dto);
        Assertions.assertEquals(novoNome, dto.getName());
        Mockito.verify(repository, Mockito.times(1)).getOne(existingId);
        Mockito.verify(repository, Mockito.times(1)).save(product);
    }

    @Test
    void whenUpdateTrowsResourceNotFoundException(){
        ProductDTO dto = ProductFactory.createProductDTO();
        Assertions.assertThrows(ResourceNotFoundException.class, ()-> service.update(nonExistId, dto));
    }

    @Test
    void whenFindAllReturnPage(){
        Pageable pageable = PageRequest.of(0, 10);
        Page<ProductDTO> result =  service.list(pageable);
        Assertions.assertNotNull(result);
        Mockito.verify(repository, Mockito.times(1)).findAll(pageable);
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
