package br.com.gsolutions.productapi.resources;

import br.com.gsolutions.productapi.dto.ProductDTO;
import br.com.gsolutions.productapi.services.ProductService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.net.URI;

@RestController
@RequestMapping(value = "/products")
@AllArgsConstructor
public class ProductResource {

    private final ProductService service;

    @GetMapping
    public ResponseEntity<Page<ProductDTO>> findAll(@PageableDefault(size = 20) final Pageable pageable){
        return ResponseEntity.ok(service.list(pageable));
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<ProductDTO> findById(@PathVariable("id") Long id){
        return ResponseEntity.ok(service.findById(id));
    }

    @PostMapping
    public ResponseEntity<ProductDTO> insert(@RequestBody ProductDTO dto){
        dto = service.create(dto);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(dto.getId()).toUri();
        return ResponseEntity.created(uri).body(dto);
    }

    @PutMapping(value = "/{id}")
    public ResponseEntity<ProductDTO> update(@PathVariable("id") Long id, @RequestBody ProductDTO dto){
        dto = service.update(id, dto);
        return ResponseEntity.ok(dto);
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<ProductDTO> delete(@PathVariable("id") Long id){
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @CrossOrigin("*")
    @PostMapping(value = "/{id}/picture")
    public ResponseEntity<ProductDTO> uploadFile(@PathVariable Long id, @RequestParam("file") MultipartFile file) throws IOException {
        String bucketName = "genuebooks";
        String key = file.getOriginalFilename();
        ProductDTO entity = service.uploadFile(id, bucketName, key, file.getBytes());
        return ResponseEntity.ok(entity);
    }
}
