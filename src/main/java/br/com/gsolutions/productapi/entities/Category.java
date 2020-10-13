package br.com.gsolutions.productapi.entities;

import br.com.gsolutions.productapi.dto.CategoryDTO;
import lombok.*;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.io.Serializable;


@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Category implements Serializable {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    public Category(CategoryDTO dto) {
        this.id = dto.getId();
        this.name = dto.getName();
    }
}
