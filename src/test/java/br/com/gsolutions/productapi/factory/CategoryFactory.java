package br.com.gsolutions.productapi.factory;

import br.com.gsolutions.productapi.dto.CategoryDTO;
import br.com.gsolutions.productapi.entities.Category;

import java.time.Instant;

public class CategoryFactory {

    public static Category createNewCategory(){
        return Category.builder()
                .id(2L).name("eletronics")
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();
    }

    public static CategoryDTO createNewCategoryDTO(){
        return new CategoryDTO(createNewCategory());
    }

}
