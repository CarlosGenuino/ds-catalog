package br.com.gsolutions.productapi.entities;

import br.com.gsolutions.productapi.dto.ProductDTO;
import lombok.*;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Product implements Serializable {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    private Double price;

    private String imgUrl;

    @Column(columnDefinition = "TIMESTAMP WITHOUT TIME ZONE")
    private Instant date;

    @Getter
    @JoinTable(name = "product_category",
    joinColumns = @JoinColumn(name = "product_id"),
    inverseJoinColumns = @JoinColumn(name = "category_id"))
    @ManyToMany
    private Set<Category> categories = new HashSet<>();

    public Product(ProductDTO dto) {
        this.id = dto.getId();
        this.name = dto.getName();
        this.description = dto.getDescription();
        this.date = dto.getDate();
        this.imgUrl = dto.getImgUrl();
    }
}
