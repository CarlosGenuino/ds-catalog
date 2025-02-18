package br.com.gsolutions.productapi.repositories;

import br.com.gsolutions.productapi.entities.Product;
import br.com.gsolutions.productapi.factory.ProductFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

@DataJpaTest
public class ProductRepositoryTests {

    @Autowired
    private ProductRepository repository;

    private long existingId;
    private long notExistingId;

    private String productName;

    @BeforeEach
    public void setUp(){
        existingId = 1L;
        notExistingId = 1000L;
        productName = "Genius";
    }

    @Test
    public void shouldUpdateAndSaveAnObject(){
        Optional<Product> optional = repository.findById(existingId);
        Assertions.assertTrue(optional.isPresent());
        Product product = optional.get();
        product.setName(productName);
        product = repository.save(product);
        Assertions.assertEquals(productName, product.getName());
    }

    @Test
    public void shouldFindAnObjectFromDatabase(){
        Optional<Product> optional = repository.findById(existingId);
        Assertions.assertTrue(optional.isPresent());
    }

    @Test
    public void shouldNotFindAnObjectFromDatabase(){
        Optional<Product> optional = repository.findById(notExistingId);
        Assertions.assertFalse(optional.isPresent());
    }

    @Test
    public void shouldSaveANewObjectWhenIdIsNull(){
        Product product = ProductFactory.createNewProduct();
        product.setId(null);
        product = repository.save(product);
        Assertions.assertNotNull(product.getId());
    }

    @Test
    public void deleteShouldDeleteObjectWhenIdExists(){
        repository.deleteById(existingId);
        Optional<Product> result = repository.findById(existingId);
        Assertions.assertFalse(result.isPresent());
    }

//    @Test
//    public void deleteShouldDeleteObjectWhenIdDoesNotExists(){
//        Assertions.assertThrows(EmptyResultDataAccessException.class, () -> {
//            repository.deleteById(notExistingId);
//        });
//    }


}
