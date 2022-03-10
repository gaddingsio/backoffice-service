package io.gaddings.backofficeservice.application;

import io.gaddings.backofficeservice.application.ports.PersistencePort;
import io.gaddings.backofficeservice.domain.CustomerCreatedEvent;
import io.gaddings.backofficeservice.domain.CustomerEntity;
import lombok.SneakyThrows;
import lombok.val;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerPersisterTest {

    @Mock
    private PersistencePort persistencePort;
    @InjectMocks
    private CustomerPersister underTest;

    @Nested
    class WhenCreatingPersistentCustomer {

        @SneakyThrows
        @Test
        void test() {
            val customerCreatedEvent = CustomerCreatedEvent.builder().id("id").build();

            assertThatCode(() -> underTest.createPersistentCustomer(customerCreatedEvent)).doesNotThrowAnyException();

            verify(persistencePort).save(eq("id.json"), any(CustomerEntity.class));
        }
    }
}
