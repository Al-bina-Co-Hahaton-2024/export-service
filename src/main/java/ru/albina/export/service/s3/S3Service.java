package ru.albina.export.service.s3;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.albina.export.configuration.properties.S3Properties;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.File;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class S3Service {

    private final S3Properties s3Properties;
    private final S3Client s3Client;

    public String uploadFile(File file, String extension) {
        final var key = UUID.randomUUID() + "." + extension;
        this.s3Client.putObject(
                PutObjectRequest.builder()
                        .bucket(s3Properties.getBucket())
                        .key(key)
                        .build(),
                RequestBody.fromFile(file)
        );

        return this.s3Properties.getEndpoint() + "/" + s3Properties.getBucket() + "/" + key;
    }
}
