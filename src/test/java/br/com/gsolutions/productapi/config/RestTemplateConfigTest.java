package br.com.gsolutions.productapi.config;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
public class RestTemplateConfigTest {

    @InjectMocks
    private RestTemplateConfig restClientConfig;

    @Test
    public void testRestTemplateBean() throws Exception {
        // Executa o método que queremos testar
        RestTemplate restTemplate = restClientConfig.restTemplate();

        // Verifica se o RestTemplate foi criado corretamente
        assertNotNull(restTemplate);
    }
}