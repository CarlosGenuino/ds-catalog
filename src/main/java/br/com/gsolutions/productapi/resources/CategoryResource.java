package br.com.gsolutions.productapi.resources;

import br.com.gsolutions.productapi.dto.CategoryDTO;
import br.com.gsolutions.productapi.entities.Category;
import br.com.gsolutions.productapi.services.CategoryService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
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

    @PostMapping
    public ResponseEntity<CategoryDTO> insert(@RequestBody CategoryDTO dto){
        dto = service.create(dto);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(dto.getId()).toUri();
        return ResponseEntity.created(uri).body(dto);
    }

    @PutMapping(value = "/{id}")
    public ResponseEntity<CategoryDTO> update(@PathVariable("id") Long id, @RequestBody CategoryDTO dto){
        dto = service.update(id, dto);
        return ResponseEntity.ok(dto);
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<CategoryDTO> delete(@PathVariable("id") Long id){
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
