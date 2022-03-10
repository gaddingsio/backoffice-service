package io.gaddings.backofficeservice.adapter.s3;

import com.amazonaws.services.s3.AmazonS3;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.gaddings.backofficeservice.application.ports.PersistencePort;
import io.gaddings.backofficeservice.domain.CustomerEntity;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class S3Facade implements PersistencePort {

    private final AmazonS3 amazonS3;
    private final String bucket;
    private final ObjectMapper objectMapper;

    public S3Facade(final AmazonS3 amazonS3, @Value("${s3.bucket}") String bucket, final ObjectMapper objectMapper) {
        this.amazonS3 = amazonS3;
        this.bucket = bucket;
        this.objectMapper = objectMapper;
    }

    @SneakyThrows
    @Override
    public void save(final String key, final CustomerEntity customerEntity) {
        amazonS3.putObject(bucket, key, objectMapper.writeValueAsString(customerEntity));
    }
}
