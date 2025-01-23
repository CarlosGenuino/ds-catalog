package br.com.gsolutions.productapi.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@WebMvcTest // Configura o contexto do Spring para testes web
@Import(SecurityConfig.class) // Importa a configuração de segurança
public class SecurityConfigTest {

    @Autowired
    private MockMvc mockMvc; // Simula requisições HTTP

    @MockBean
    private JwtAuthenticationFilter jwtAuthFilter; // Mock do filtro JWT

    @MockBean
    private AuthenticationProvider authenticationProvider;

    @Test
    public void testPublicEndpoints() throws Exception {
        // Testa acesso público ao endpoint de autenticação
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/auth/login"))
                .andExpect(MockMvcResultMatchers.status().isOk());

        // Testa acesso público ao Actuator
        mockMvc.perform(MockMvcRequestBuilders.get("/actuator"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void testUnauthenticatedAccessToProtectedEndpoints() throws Exception {
        // Testa acesso não autenticado a um endpoint protegido
        mockMvc.perform(MockMvcRequestBuilders.post("/categories"))
                .andExpect(MockMvcResultMatchers.status().isForbidden()); // POST /categories exige autenticação
    }

    @Test
    @WithMockUser(roles = "OPERATOR") // Simula um usuário autenticado com a role OPERATOR
    public void testAuthenticatedAccessWithRoleOperator() throws Exception {
        // Testa acesso autenticado com a role OPERATOR
        mockMvc.perform(MockMvcRequestBuilders.post("/categories"))
                .andExpect(MockMvcResultMatchers.status().isOk());

        mockMvc.perform(MockMvcRequestBuilders.delete("/categories/1"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    @WithMockUser(roles = "USER") // Simula um usuário autenticado com a role USER
    public void testAuthenticatedAccessWithRoleUser() throws Exception {
        // Testa acesso autenticado com a role USER (que não tem permissão)
        mockMvc.perform(MockMvcRequestBuilders.post("/categories"))
                .andExpect(MockMvcResultMatchers.status().isForbidden());

        mockMvc.perform(MockMvcRequestBuilders.delete("/categories/1"))
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    @WithMockUser // Simula um usuário autenticado sem roles específicas
    public void testAuthenticatedAccessWithoutRoles() throws Exception {
        // Testa acesso autenticado sem roles específicas
        mockMvc.perform(MockMvcRequestBuilders.get("/categories"))
                .andExpect(MockMvcResultMatchers.status().isOk()); // GET /categories é permitido

        mockMvc.perform(MockMvcRequestBuilders.post("/categories"))
                .andExpect(MockMvcResultMatchers.status().isForbidden()); // POST /categories exige roles específicas
    }

    @Test
    public void testCsrfDisabled() throws Exception {
        // Testa se o CSRF está desabilitado
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/auth/login"))
                .andExpect(MockMvcResultMatchers.status().isOk()); // POST sem CSRF deve ser permitido
    }

    @Test
    public void testSessionIsStateless() throws Exception {
        // Testa se a sessão é stateless
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/auth/login"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.header().doesNotExist("Set-Cookie")); // Não deve haver cookie de sessão
    }
}