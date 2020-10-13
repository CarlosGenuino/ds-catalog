package br.com.gsolutions.productapi.services;

import br.com.gsolutions.productapi.dto.ProductDTO;
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
        Product savedProduct =  repository.save(new Product(dto));
        return new ProductDTO(savedProduct);
    }

    @Transactional(readOnly = true)
    public ProductDTO findById(Long id){
        Optional<Product> optional = repository.findById(id);
        return optional.map(ProductDTO::new).orElseThrow(() -> new ResourceNotFoundException("Entity Not Found"));
    }

    @Transactional
    public ProductDTO update(Long id, ProductDTO dto){
        try{
            Product entity = repository.getOne(id);
            entity.setName(dto.getName());
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

    public void pagedList(PageRequest pageRequest) {

    }
}
