package br.com.gsolutions.productapi.services;

import br.com.gsolutions.productapi.dto.CategoryDTO;
import br.com.gsolutions.productapi.entities.Category;
import br.com.gsolutions.productapi.repositories.CategoryRepository;
import br.com.gsolutions.productapi.services.exceptions.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class CategoryService {

    private final CategoryRepository repository;

    @Transactional(readOnly = true)
    public List<CategoryDTO> list(){
        List<Category> list = repository.findAll();
        return list.stream().map(CategoryDTO::new).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Category findByName(String name){
        return repository.findByName(name);
    }

    public Category create(Category category){
        return repository.save(category);
    }

    @Transactional(readOnly = true)
    public CategoryDTO findById(Long id){
        Optional<Category> optional = repository.findById(id);
        return optional.map(CategoryDTO::new).orElseThrow(() -> new EntityNotFoundException("Entity Not Found"));
    }

    public Category update(Long id, CategoryDTO category){
        CategoryDTO savedCategory = this.findById(id);
        savedCategory.setDescription(category.getDescription());
        return repository.save(new Category(savedCategory));
    }

    public void delete(Long id){
        CategoryDTO savedCategory = this.findById(id);
        repository.delete(new Category(savedCategory));
    }
}
