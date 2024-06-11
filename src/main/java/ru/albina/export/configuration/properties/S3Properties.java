package ru.albina.export.configuration.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.net.URI;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "s3")
public class S3Properties {

    private String bucket;

    private String accessKey;

    private String secretKey;

    private String region;

    private URI endpoint;
}
