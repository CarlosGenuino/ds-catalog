package br.com.gsolutions.productapi.resources;

import br.com.gsolutions.productapi.dto.CategoryDTO;
import br.com.gsolutions.productapi.entities.Category;
import br.com.gsolutions.productapi.services.CategoryService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "/categories")
@AllArgsConstructor
public class CategoryResource {

    private final CategoryService service;

    @GetMapping
    public ResponseEntity<List<CategoryDTO>> findAll(){
        return ResponseEntity.ok(service.list());
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<CategoryDTO> findById(@PathVariable("id") Long id){
        return ResponseEntity.ok(service.findById(id));
    }
}
