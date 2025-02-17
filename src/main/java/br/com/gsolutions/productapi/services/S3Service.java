package br.com.gsolutions.productapi.services;

import lombok.Setter;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

import java.time.Duration;

@Setter
@Service
public class S3Service {

    private S3Client s3Client;
    private S3Presigner s3Presigner;

    public S3Service() {
        this.s3Presigner = S3Presigner.builder().build();
        this.s3Client = S3Client.builder().region(Region.US_EAST_1).build();
    }

    public String uploadFile(String bucketName, String key, byte[] file) {
        try {
            // Faz o upload do arquivo para o S3
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .build();

            s3Client.putObject(putObjectRequest, RequestBody.fromBytes(file));
            System.out.println("File uploaded successfully to S3!");

            // Gera a URL pré-assinada
            return generatePreSignedUrl(bucketName, key);
        } catch (S3Exception e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            throw new RuntimeException("Failed to upload file to S3", e);
        }
    }

    protected String generatePreSignedUrl(String bucketName, String key) {
        // Configura a duração da URL pré-assinada (ex: 7 dias)
        Duration expiration = Duration.ofMinutes(1);

        // Cria a requisição para gerar a URL pré-assinada
        GetObjectPresignRequest getObjectPresignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(expiration)
                .getObjectRequest(b -> b.bucket(bucketName).key(key))
                .build();

        // Gera a URL pré-assinada
        PresignedGetObjectRequest presignedGetObjectRequest = s3Presigner.presignGetObject(getObjectPresignRequest);
        return presignedGetObjectRequest.url().toString();
    }
}
