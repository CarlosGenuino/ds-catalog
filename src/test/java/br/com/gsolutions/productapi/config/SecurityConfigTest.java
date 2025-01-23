package br.com.gsolutions.productapi.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@WebMvcTest // Configura o contexto do Spring para testes web
@Import(SecurityConfig.class) // Importa a configuração de segurança
public class SecurityConfigTest {

    @Autowired
    private MockMvc mockMvc; // Simula requisições HTTP

    @Test
    public void testPublicEndpoints() throws Exception {
        // Testa acesso público ao endpoint /api/v1/**
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/some-endpoint"))
                .andExpect(MockMvcResultMatchers.status().isOk());

        // Testa acesso público ao Actuator
        mockMvc.perform(MockMvcRequestBuilders.get("/actuator"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void testUnauthenticatedAccessToProtectedEndpoints() throws Exception {
        // Testa acesso não autenticado a um endpoint protegido
        mockMvc.perform(MockMvcRequestBuilders.get("/protected-endpoint"))
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    @WithMockUser // Simula um usuário autenticado
    public void testAuthenticatedAccessToProtectedEndpoints() throws Exception {
        // Testa acesso autenticado a um endpoint protegido
        mockMvc.perform(MockMvcRequestBuilders.get("/protected-endpoint"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void testCsrfDisabled() throws Exception {
        // Testa se o CSRF está desabilitado
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/some-endpoint"))
                .andExpect(MockMvcResultMatchers.status().isOk()); // POST sem CSRF deve ser permitido
    }

    @Test
    public void testSessionIsStateless() throws Exception {
        // Testa se a sessão é stateless
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/some-endpoint"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.header().doesNotExist("Set-Cookie")); // Não deve haver cookie de sessão
    }
}