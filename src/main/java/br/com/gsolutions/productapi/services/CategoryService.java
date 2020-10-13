package br.com.gsolutions.productapi.services;

import br.com.gsolutions.productapi.dto.CategoryDTO;
import br.com.gsolutions.productapi.entities.Category;
import br.com.gsolutions.productapi.repositories.CategoryRepository;
import br.com.gsolutions.productapi.services.exceptions.DatabaseException;
import br.com.gsolutions.productapi.services.exceptions.ResourceNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class CategoryService {

    private final CategoryRepository repository;

    @Transactional(readOnly = true)
    public Page<CategoryDTO> list(PageRequest pageRequest){
        Page<Category> list = repository.findAll(pageRequest);
        return list.map(CategoryDTO::new);
    }

    @Transactional(readOnly = true)
    public Category findByName(String name){
        return repository.findByName(name);
    }

    @Transactional
    public CategoryDTO create(CategoryDTO category){
        Category savedCategory =  repository.save(new Category(category));
        return new CategoryDTO(savedCategory);
    }

    @Transactional(readOnly = true)
    public CategoryDTO findById(Long id){
        Optional<Category> optional = repository.findById(id);
        return optional.map(CategoryDTO::new).orElseThrow(() -> new ResourceNotFoundException("Entity Not Found"));
    }

    @Transactional
    public CategoryDTO update(Long id, CategoryDTO dto){
        try{
            Category entity = repository.getOne(id);
            entity.setName(dto.getName());
            entity = repository.save(entity);
            return new CategoryDTO(entity);
        }catch (EntityNotFoundException e){
            throw new ResourceNotFoundException("Entity not Found by id: "+ id);
        }
    }

    public void delete(Long id){
        try{
            repository.deleteById(id);

        }catch (EmptyResultDataAccessException e){
            throw new ResourceNotFoundException("Entity not Found: " + id);
        }
        catch (DataIntegrityViolationException e){
            throw new DatabaseException("Integrity violation");
        }
    }

    public void pagedList(PageRequest pageRequest) {

    }
}
