package br.com.gsolutions.productapi.repositories;

import br.com.gsolutions.productapi.entities.Category;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.Instant;
import java.util.Optional;

@DataJpaTest
public class CategoryRepositoryTest {

    @Autowired
    CategoryRepository repository;

    private long existingId;
    private long notExistingId;

    private String categoryName;

    @BeforeEach
    public void setUp(){
        existingId = 1L;
        notExistingId = 1000L;
        categoryName = "Toys";
    }

    @Test
    public void shouldReturnCategory() {
        Optional<Category> result = repository.findById(existingId);
        Assertions.assertTrue(result.isPresent());
    }

    @Test
    public void shouldNotReturnCategory() {
        Optional<Category> result = repository.findById(notExistingId);
        Assertions.assertFalse(result.isPresent());
    }

    @Test
    public void shouldUpdateCategory() {
        Optional<Category> result = repository.findById(existingId);
        Assertions.assertTrue(result.isPresent());
        Category category = result.get();
        category.setName(categoryName);
        repository.save(category);
        result = repository.findById(existingId);
        Assertions.assertTrue(result.isPresent());
        Assertions.assertEquals(categoryName, result.get().getName());
    }

    @Test
    public void shouldSaveNewCategory(){
        Category cat = Category.builder()
                .name(categoryName)
                .createdAt(Instant.now())
                .build();
        cat.setId(null);
        repository.save(cat);
        Assertions.assertNotNull(cat.getId());
    }

    @Test
    public void shouldDeleteCategory(){
        repository.deleteById(existingId);
        Optional<Category> result = repository.findById(existingId);
        Assertions.assertFalse(result.isPresent());
    }

//    @Test
//    public void shouldThrowEmptyResultDataAccessException(){
//        Assertions.assertThrows(EmptyResultDataAccessException.class, () -> repository.deleteById(notExistingId));
//    }

}
