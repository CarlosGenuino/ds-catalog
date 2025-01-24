package br.com.gsolutions.productapi.config;

import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactory;
import org.apache.hc.client5.http.ssl.TrustAllStrategy;
import org.apache.hc.core5.ssl.SSLContextBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.SSLContext;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;

@Configuration
public class RestTemplateConfig {

    @Bean
    private static RestTemplate createRestTemplate() throws KeyStoreException, NoSuchAlgorithmException, KeyManagementException {
        // Configura o SSLContext para confiar em todos os certificados (não recomendado para produção)
        SSLContext sslContext = SSLContextBuilder
                .create()
                .loadTrustMaterial(new TrustAllStrategy())
                .build();

        // Configura o PoolingHttpClientConnectionManager com o SSLContext
        PoolingHttpClientConnectionManager connectionManager = PoolingHttpClientConnectionManagerBuilder.create()
                .setSSLSocketFactory(new SSLConnectionSocketFactory(sslContext))
                .build();

        // Configura o HttpClient com o PoolingHttpClientConnectionManager
        CloseableHttpClient httpClient = HttpClients.custom()
                .setConnectionManager(connectionManager)
                .build();

        // Configura o RestTemplate com o HttpClient
        HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory(httpClient);
        return new RestTemplate(requestFactory);
    }
}
