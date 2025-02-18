package br.com.gsolutions.productapi.entities;

import br.com.gsolutions.productapi.dto.ProductDTO;
import jakarta.persistence.*;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Product implements Serializable {

	@Serial
    private static final long serialVersionUID = 5935703171790054007L;

	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Size(min = 5, max = 255, message = "O tamanho do nome do produto deve ser entre 5 e 255")
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Positive
    private Double price;

    @Column(length = 600)
    private String imgUrl;

    @Column(columnDefinition = "TIMESTAMP WITHOUT TIME ZONE")
    private Instant date;

    @Getter
    @JoinTable(name = "product_category",
    joinColumns = @JoinColumn(name = "product_id"),
    inverseJoinColumns = @JoinColumn(name = "category_id"))
    @ManyToMany
    @Builder.Default
    private Set<Category> categories = new HashSet<>();

    public Product(ProductDTO dto) {
        this.id = dto.getId();
        this.name = dto.getName();
        this.description = dto.getDescription();
        this.date = dto.getDate();
        this.imgUrl = dto.getImgUrl();
    }

    public void addCategory(Category category){
        if (getCategories() == null) {
            setCategories(new HashSet<>());
        }
        getCategories().add(category);
    }
}
