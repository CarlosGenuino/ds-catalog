package br.com.gsolutions.productapi.resources;

import br.com.gsolutions.productapi.config.JwtService;
import br.com.gsolutions.productapi.dto.ClientDTO;
import br.com.gsolutions.productapi.factory.ClientFactory;
import br.com.gsolutions.productapi.services.ClientService;
import br.com.gsolutions.productapi.services.exceptions.DatabaseException;
import br.com.gsolutions.productapi.services.exceptions.ResourceNotFoundException;
import br.com.gsolutions.productapi.token.TokenRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.List;


@WebMvcTest(ClientResource.class)
class ClientResourceTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ClientService service;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private TokenRepository tokenRepository;

    @MockBean
    private UserDetailsService userDetailsService;

    @Autowired
    private ObjectMapper mapper;

    private ClientDTO dto;
    private Long existingId;
    private Long nonExistingId;
    private Long dependentId;
    private static String baseUrl;

    @BeforeEach
    void setUp() {
        existingId = 1L;
        nonExistingId = 2L;
        dependentId = 3L;
        baseUrl = "/clients";
        dto = ClientFactory.createNewClientDTO();

        PageImpl<ClientDTO> page = new PageImpl<>(List.of(dto));

        Mockito.when(service.list(ArgumentMatchers.any())).thenReturn(page);

        Mockito.when(service.findById(existingId)).thenReturn(dto);
        Mockito.when(service.findById(nonExistingId)).thenThrow(ResourceNotFoundException.class);

        Mockito.when(service.create(ArgumentMatchers.any())).thenReturn(dto);

        Mockito.when(service.update(ArgumentMatchers.eq(existingId), ArgumentMatchers.any())).thenReturn(dto);
        Mockito.when(service.update(ArgumentMatchers.eq(nonExistingId), ArgumentMatchers.any())).thenThrow(ResourceNotFoundException.class);

        Mockito.doNothing().when(service).delete(existingId);
        Mockito.doThrow(ResourceNotFoundException.class).when(service).delete(nonExistingId);
        Mockito.doThrow(DatabaseException.class).when(service).delete(dependentId);
    }

    @Test
    void postShouldReturnCreated() throws Exception{
        String bodyJson = mapper.writeValueAsString(dto);

        ResultActions result = mockMvc.perform(
                MockMvcRequestBuilders.post(baseUrl)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(SecurityMockMvcRequestPostProcessors.user("maria@gmail.com")
                                .roles("OPERATOR", "ADMIN"))
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .content(bodyJson)
        );

        result.andExpect(MockMvcResultMatchers.status().isCreated());
        result.andExpect(MockMvcResultMatchers.jsonPath("$.id").exists());
        result.andExpect(MockMvcResultMatchers.jsonPath("$.name").exists());
    }

    @Test
    void deleteShouldReturnNoContent() throws Exception {
        ResultActions actions = mockMvc.perform(
                MockMvcRequestBuilders.delete(baseUrl.concat("/{id}"), existingId)
                        .with(SecurityMockMvcRequestPostProcessors.user("maria@gmail.com")
                                .roles("OPERATOR", "ADMIN"))
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .contentType(MediaType.APPLICATION_JSON)
        );
        actions.andExpect(MockMvcResultMatchers.status().isNoContent());
    }

    @Test
    void deleteShouldReturnNotFound() throws Exception {
        ResultActions actions = mockMvc.perform(
                MockMvcRequestBuilders.delete(baseUrl.concat("/{id}"), nonExistingId)
                        .with(SecurityMockMvcRequestPostProcessors.user("maria@gmail.com")
                                .roles("OPERATOR", "ADMIN"))
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .contentType(MediaType.APPLICATION_JSON)
        );
        actions.andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    void deleteShouldReturnBadRequest() throws Exception {
        ResultActions actions = mockMvc.perform(
                MockMvcRequestBuilders.delete(baseUrl.concat("/{id}"), dependentId)
                        .with(SecurityMockMvcRequestPostProcessors.user("maria@gmail.com")
                                .roles("OPERATOR", "ADMIN"))
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .contentType(MediaType.APPLICATION_JSON)
        );
        actions.andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    void updateShouldReturnObjectDTO() throws Exception {
        String body = mapper.writeValueAsString(dto);
        ResultActions result = mockMvc.perform(
                MockMvcRequestBuilders.put(baseUrl.concat("/{id}"), existingId)
                        .with(SecurityMockMvcRequestPostProcessors.user("maria@gmail.com")
                                .roles("OPERATOR", "ADMIN"))
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)
                        .accept(MediaType.APPLICATION_JSON)
        );

        result.andExpect(MockMvcResultMatchers.status().isOk());
        result.andExpect(MockMvcResultMatchers.jsonPath("$.id").exists());
        result.andExpect(MockMvcResultMatchers.jsonPath("$.name").exists());

    }

    @Test
    void updateShouldThrowsNotFoundException() throws Exception {
        String body = mapper.writeValueAsString(dto);
        ResultActions result = mockMvc.perform(
                MockMvcRequestBuilders.put(baseUrl.concat("/{id}"), nonExistingId)
                        .with(SecurityMockMvcRequestPostProcessors.user("maria@gmail.com")
                                .roles("OPERATOR", "ADMIN"))
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)
                        .accept(MediaType.APPLICATION_JSON)
        );

        result.andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    void getShouldReturnPage() throws Exception {
        ResultActions result = mockMvc.perform(
                MockMvcRequestBuilders.get(baseUrl)
                        .with(SecurityMockMvcRequestPostProcessors.user("maria@gmail.com")
                                .roles("OPERATOR", "ADMIN"))
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .accept(MediaType.APPLICATION_JSON)
        );

        result.andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void shouldReturnObjectDto() throws Exception {
        ResultActions result = mockMvc.perform(
                MockMvcRequestBuilders.get(baseUrl.concat("/{id}"), existingId)
                        .with(SecurityMockMvcRequestPostProcessors.user("maria@gmail.com")
                                .roles("OPERATOR", "ADMIN"))
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .accept(MediaType.APPLICATION_JSON)
        );
        result.andExpect(MockMvcResultMatchers.status().isOk());
        result.andExpect(MockMvcResultMatchers.jsonPath("$.id").exists());
        result.andExpect(MockMvcResultMatchers.jsonPath("$.name").exists());
    }

    @Test
    void shouldThrowsBadRequest() throws Exception {
        ResultActions result = mockMvc.perform(
                MockMvcRequestBuilders.get(baseUrl.concat("/{id}"), nonExistingId)
                        .with(SecurityMockMvcRequestPostProcessors.user("maria@gmail.com")
                                .roles("OPERATOR", "ADMIN"))
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .accept(MediaType.APPLICATION_JSON)
        );
        result.andExpect(MockMvcResultMatchers.status().isNotFound());
    }
}