package br.com.gsolutions.productapi.resources;

import br.com.gsolutions.productapi.dto.ProductDTO;
import br.com.gsolutions.productapi.factory.ProductFactory;
import br.com.gsolutions.productapi.services.ProductService;
import br.com.gsolutions.productapi.services.exceptions.ResourceNotFoundException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.platform.engine.TestExecutionResult;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.List;

@WebMvcTest(ProductResource.class)
public class ProductResourceTests {


    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService service;

    @Autowired
    private ObjectMapper mapper;

    private PageImpl<ProductDTO> page;
    private ProductDTO productDTO;
    private Long existingId;
    private Long nonExistingId;

    @BeforeEach
    void setUp() throws Exception {
        existingId = 1L;
        nonExistingId = 2L;
        productDTO = ProductFactory.createProductDTO();
        page = new PageImpl<>(List.of(productDTO));
        Mockito.when(service.list(ArgumentMatchers.any())).thenReturn(page);
        Mockito.when(service.findById(existingId)).thenReturn(productDTO);
        Mockito.when(service.findById(nonExistingId)).thenThrow(ResourceNotFoundException.class);
        Mockito.when(service.update(ArgumentMatchers.eq(existingId), ArgumentMatchers.any())).thenReturn(productDTO);
        Mockito.when(service.update(ArgumentMatchers.eq(nonExistingId), ArgumentMatchers.any())).thenThrow(ResourceNotFoundException.class);
    }

    @Test
    void updateSholdRetornObjectDTO() throws Exception {
        String body = mapper.writeValueAsString(productDTO);
        ResultActions result =  mockMvc.perform(
                MockMvcRequestBuilders.put("/products/{id}", existingId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)
                        .accept(MediaType.APPLICATION_JSON)
        );

        result.andExpect(MockMvcResultMatchers.status().isOk());
        result.andExpect(MockMvcResultMatchers.jsonPath("$.id").exists());
        result.andExpect(MockMvcResultMatchers.jsonPath("$.name").exists());
        result.andExpect(MockMvcResultMatchers.jsonPath("$.description").exists());

    }

    @Test
    void updateSholdThrowsNotFoundException() throws Exception {
        String body = mapper.writeValueAsString(productDTO);
        ResultActions result =  mockMvc.perform(
                MockMvcRequestBuilders.put("/products/{id}", nonExistingId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)
                        .accept(MediaType.APPLICATION_JSON)
        );

        result.andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    void sholdRetornPage() throws Exception {
       ResultActions result =  mockMvc.perform(
           MockMvcRequestBuilders.get("/products")
           .accept(MediaType.APPLICATION_JSON)
       );

        result.andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void sholdReturnObjectDto() throws Exception {
        ResultActions result = mockMvc.perform(
                MockMvcRequestBuilders.get("/products/{id}", existingId)
                .accept(MediaType.APPLICATION_JSON)
        );
        result.andExpect(MockMvcResultMatchers.status().isOk());
        result.andExpect(MockMvcResultMatchers.jsonPath("$.id").exists());
        result.andExpect(MockMvcResultMatchers.jsonPath("$.name").exists());
        result.andExpect(MockMvcResultMatchers.jsonPath("$.description").exists());
    }

    @Test
    void sholdThrowsBadRequest() throws Exception {
        ResultActions result = mockMvc.perform(
                MockMvcRequestBuilders.get("/products/{id}", nonExistingId)
                        .accept(MediaType.APPLICATION_JSON)
        );
        result.andExpect(MockMvcResultMatchers.status().isNotFound());
        //result.andExpect(MockMvcResultMatchers.jsonPath("$.id").doesNotExist());
    }
}
