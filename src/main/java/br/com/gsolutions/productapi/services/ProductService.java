package br.com.gsolutions.productapi.services;

import br.com.gsolutions.productapi.dto.CategoryDTO;
import br.com.gsolutions.productapi.dto.ProductDTO;
import br.com.gsolutions.productapi.entities.Category;
import br.com.gsolutions.productapi.entities.Product;
import br.com.gsolutions.productapi.repositories.ProductRepository;
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
import java.util.Optional;

@Service
@AllArgsConstructor
public class ProductService {

    private final ProductRepository repository;

    @Transactional(readOnly = true)
    public Page<ProductDTO> list(PageRequest pageRequest){
        Page<Product> list = repository.findAll(pageRequest);
        return list.map(ProductDTO::new);
    }

    @Transactional(readOnly = true)
    public Product findByName(String name){
        return repository.findByName(name);
    }

    @Transactional
    public ProductDTO create(ProductDTO dto){
        Product savedProduct = new Product();
        copyDataFromDTO(dto, savedProduct);
        savedProduct =  repository.save(savedProduct);
        return new ProductDTO(savedProduct);
    }

    @Transactional(readOnly = true)
    public ProductDTO findById(Long id){
        Optional<Product> optional = repository.findById(id);
        return optional.map(product -> new ProductDTO(product, product.getCategories()))
                .orElseThrow(() -> new ResourceNotFoundException("Entity Not Found"));
    }

    @Transactional
    public ProductDTO update(Long id, ProductDTO dto){
        try{
            Product entity = repository.getOne(id);
            copyDataFromDTO(dto, entity);
            entity = repository.save(entity);
            return new ProductDTO(entity);
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

    private void copyDataFromDTO(ProductDTO dto, Product entity) {
        entity.setName(dto.getName());
        entity.setDate(dto.getDate());
        entity.setDescription(dto.getDescription());
        entity.setPrice(dto.getPrice());
        entity.setImgUrl(dto.getImgUrl());
        entity.getCategories().clear();
        dto.getCategories().forEach(categoryDTO -> entity.getCategories().add(new Category(categoryDTO)));
    }
}
