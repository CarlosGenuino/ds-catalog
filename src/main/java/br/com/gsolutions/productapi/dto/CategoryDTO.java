package br.com.gsolutions.productapi.dto;

import br.com.gsolutions.productapi.entities.Category;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CategoryDTO {

    private Long id;
    private String name;

    public CategoryDTO(Category category){
        this.id = category.getId();
        this.name = category.getName();
    }

    public Category getObjectFromDTO(CategoryDTO dto){
       return Category.builder()
                .id(dto.getId())
                .name(dto.getName())
                .build();
    }
}
