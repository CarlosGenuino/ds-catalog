package br.com.gsolutions.productapi.dto;

import br.com.gsolutions.productapi.entities.Category;
import br.com.gsolutions.productapi.entities.Product;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductDTO {

    private Long id;

    private String name;

    private String description;

    private Double price;

    private String imgUrl;

    private Instant date;

    private Set<Category> categories = new HashSet<>();

    public ProductDTO(Product entity) {
        this.id = entity.getId();
        this.name = entity.getName();
        this.description = entity.getDescription();
        this.date = entity.getDate();
        this.imgUrl = entity.getImgUrl();
        this.categories = entity.getCategories();
    }
}
