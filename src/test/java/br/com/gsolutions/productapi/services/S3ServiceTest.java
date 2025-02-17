package br.com.gsolutions.productapi.services;

import org.checkerframework.checker.units.qual.A;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.awscore.exception.AwsErrorDetails;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

import java.net.URL;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class S3ServiceTest {

    @Mock
    private S3Client s3Client;

    @Mock
    private S3Presigner s3Presigner;

    @InjectMocks
    private S3Service s3Service;

    private final String bucketName = "test-bucket";
    private final String key = "test-key";
    private final byte[] fileContent = "test file content".getBytes();
    private final String preSignedUrl = "https://test-bucket.s3.amazonaws.com/test-key";

    @BeforeEach
    public void setUp() {
        // Inicializa o S3Service com os mocks
        s3Service = new S3Service();
        s3Service.setS3Client(s3Client); // Adicionar um setter para S3Client na classe S3Service
        s3Service.setS3Presigner(s3Presigner); // Adicionar um setter para S3Presigner na classe S3Service
    }

    @Test
    public void testUploadFileSuccess() throws Exception {
        // Arrange
        when(s3Client.putObject(any(PutObjectRequest.class), any(RequestBody.class)))
                .thenReturn(PutObjectResponse.builder().build());

        // Simula a geração de uma URL pré-assinada
        PresignedGetObjectRequest presignedRequest = mock(PresignedGetObjectRequest.class);
        when(presignedRequest.url()).thenReturn(new URL(preSignedUrl));
        when(s3Presigner.presignGetObject(any(GetObjectPresignRequest.class)))
                .thenReturn(presignedRequest);

        // Act
        String result = s3Service.uploadFile(bucketName, key, fileContent);

        // Assert
        assertEquals(preSignedUrl, result, "A URL pré-assinada deve ser retornada após o upload");
        verify(s3Client, times(1)).putObject(any(PutObjectRequest.class), any(RequestBody.class));
        verify(s3Presigner, times(1)).presignGetObject(any(GetObjectPresignRequest.class));
    }

    @Test
    public void testUploadFileThrowsS3Exception() {
        // Arrange
        when(s3Client.putObject(any(PutObjectRequest.class), any(RequestBody.class)))
                .thenThrow(S3Exception.builder().message("S3 Error")
                        .awsErrorDetails(AwsErrorDetails.builder().build()).build());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            s3Service.uploadFile(bucketName, key, fileContent);
        });

        assertEquals("Failed to upload file to S3", exception.getMessage(), "A exceção deve conter a mensagem correta");
        verify(s3Client, times(1)).putObject(any(PutObjectRequest.class), any(RequestBody.class));
        verify(s3Presigner, never()).presignGetObject(any(GetObjectPresignRequest.class));
    }

    @Test
    public void testGeneratePreSignedUrl() throws Exception {
        // Arrange
        // Simula a geração de uma URL pré-assinada
        PresignedGetObjectRequest presignedRequest = mock(PresignedGetObjectRequest.class);
        when(presignedRequest.url()).thenReturn(new URL(preSignedUrl));
        when(s3Presigner.presignGetObject(any(GetObjectPresignRequest.class)))
                .thenReturn(presignedRequest);

        // Act
        String result = s3Service.generatePreSignedUrl(bucketName, key);

        // Assert
        assertEquals(preSignedUrl, result, "A URL pré-assinada deve ser gerada corretamente");
        verify(s3Presigner, times(1)).presignGetObject(any(GetObjectPresignRequest.class));
    }
}