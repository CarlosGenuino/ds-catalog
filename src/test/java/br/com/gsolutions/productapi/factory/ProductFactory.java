package br.com.gsolutions.productapi.factory;

import br.com.gsolutions.productapi.dto.ProductDTO;
import br.com.gsolutions.productapi.entities.Category;
import br.com.gsolutions.productapi.entities.Product;

import java.time.Instant;

public class ProductFactory {
    public static Product createNewProduct() {

        Category category = Category.builder()
                .id(2L).name("eletronics")
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();

        Product product = Product.builder()
                .id(1L)
                .name("Chinese Phone")
                .description("a chinese phone")
                .imgUrl("http://aws-carlos-genuino/aloromora/imagem")
                .date(Instant.now())
                .build();
        product.addCategory(category);
        return product;
    }

    public static ProductDTO createProductDTO(){
        Product product = createNewProduct();
        return new ProductDTO(product, product.getCategories());
    }
}
