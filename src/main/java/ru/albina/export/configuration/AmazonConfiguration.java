package ru.albina.export.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.albina.export.configuration.properties.S3Properties;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;

@Configuration
public class AmazonConfiguration {

    @Bean
    public S3Client s3Client(S3Properties properties) {
        final var cred = AwsBasicCredentials.builder()
                .accessKeyId(properties.getAccessKey())
                .secretAccessKey(properties.getSecretKey())
                .build();
        return S3Client.builder()
                .region(Region.of(properties.getRegion()))
                .credentialsProvider(
                        StaticCredentialsProvider.create(cred)
                )
                .endpointOverride(properties.getEndpoint())
                .serviceConfiguration(S3Configuration.builder()
                        .pathStyleAccessEnabled(true) // Для MinIO или других совместимых сервисов
                        .build())
                .build();
    }
}
