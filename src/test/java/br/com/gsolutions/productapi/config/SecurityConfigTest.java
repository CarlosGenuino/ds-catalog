package br.com.gsolutions.productapi.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class SecurityConfigTest {

    @Autowired
    private MockMvc mockMvc;

    private static final String categoryJson = """
        {
            "id": 1,
            "name": "Eletrônicos"
        }
        """;

    public static final String productJson = """
            {
                "id": 1,
                "name": "Smartphone",
                "description": "Um smartphone avançado",
                "price": 999.99,
                "imgUrl": "https://example.com/smartphone.jpg",
                "date": "2023-10-05T12:34:56.789Z"
            }
            """;


    @Test
    public void testPublicEndpoints() throws Exception {
        mockMvc.perform(get("/actuator"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/categories"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/products"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = {"OPERATOR"})
    public void testOperatorAccess() throws Exception {

        mockMvc.perform(post("/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(categoryJson))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(productJson))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    public void testAdminAccess() throws Exception {
        mockMvc.perform(post("/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(categoryJson))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(productJson))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/clients"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser()
    public void testUnauthorizedAccess() throws Exception {
        mockMvc.perform(post("/categories").content(categoryJson))
                .andExpect(status().isForbidden());

        mockMvc.perform(post("/products"))
                .andExpect(status().isForbidden());

        mockMvc.perform(get("/clients"))
                .andExpect(status().isForbidden());

        mockMvc.perform(get("/users"))
                .andExpect(status().isForbidden());
    }

    @Test
    public void testDenyAll() throws Exception {
        mockMvc.perform(get("/unknown-endpoint"))
                .andExpect(status().isForbidden());
    }
}