package io.gaddings.backofficeservice.integration;

import com.amazonaws.services.s3.AmazonS3;
import io.awspring.cloud.messaging.core.QueueMessagingTemplate;
import io.gaddings.backofficeservice.BackofficeServiceApplication;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;
import static org.testcontainers.containers.localstack.LocalStackContainer.Service.S3;
import static org.testcontainers.containers.localstack.LocalStackContainer.Service.SQS;

@Testcontainers
@SpringBootTest(classes = BackofficeServiceApplication.class)
class BackofficeServiceIT {

    @Container
    public final static LocalStackContainer LOCAL_STACK_CONTAINER = new LocalStackContainer(
        DockerImageName.parse("localstack/localstack:0.11.2")).withServices(S3, SQS);

    private static final String QUEUE_NAME = "customer-test-queue";
    private static final String BUCKET_NAME = "customer-test-bucket";

    @Autowired
    private AmazonS3 amazonS3;

    @Autowired
    private QueueMessagingTemplate queueMessagingTemplate;

    @SneakyThrows
    @BeforeAll
    static void setUp() {
        LOCAL_STACK_CONTAINER.execInContainer("awslocal", "sqs", "create-queue", "--queue-name", QUEUE_NAME);
        LOCAL_STACK_CONTAINER.execInContainer("awslocal", "s3", "mb", "s3://" + BUCKET_NAME);
    }

    @DynamicPropertySource
    static void overrideConfiguration(DynamicPropertyRegistry registry) {
        registry.add("cloud.aws.sqs.endpoint", () -> LOCAL_STACK_CONTAINER.getEndpointOverride(SQS));
        registry.add("cloud.aws.s3.endpoint", () -> LOCAL_STACK_CONTAINER.getEndpointOverride(S3));
        registry.add("cloud.aws.credentials.access-key", LOCAL_STACK_CONTAINER::getAccessKey);
        registry.add("cloud.aws.credentials.secret-key", LOCAL_STACK_CONTAINER::getSecretKey);
        registry.add("sqs.queue", () -> QUEUE_NAME);
        registry.add("s3.bucket", () -> BUCKET_NAME);
    }

    @SneakyThrows
    @Test
    void integrationTest() {
        queueMessagingTemplate.send(QUEUE_NAME, new GenericMessage<>("""
            {
              "id": "efb40e2a-edab-4770-9614-cf85438f98a8",
              "name": "Bacon",
              "firstName": "Chris P.",
              "businessCustomer": false
            }
            """, Map.of("contentType", "application/json")));

        await().atMost(30, TimeUnit.SECONDS)
            .until(() -> amazonS3.doesObjectExist(BUCKET_NAME, "efb40e2a-edab-4770-9614-cf85438f98a8.json"));
    }
}
