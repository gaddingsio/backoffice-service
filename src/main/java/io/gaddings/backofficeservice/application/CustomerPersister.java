package io.gaddings.backofficeservice.application;

import io.gaddings.backofficeservice.application.ports.PersistencePort;
import io.gaddings.backofficeservice.domain.CustomerCreatedEvent;
import io.gaddings.backofficeservice.domain.CustomerEntity;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class CustomerPersister {

    private final PersistencePort persistencePort;

    public CustomerPersister(final PersistencePort persistencePort) {
        this.persistencePort = persistencePort;
    }

    public void createPersistentCustomer(final CustomerCreatedEvent customerCreatedEvent) {
        val key = customerCreatedEvent.getId();
        log.info("Persisting customer with id={}", key);
        val customerEntity = CustomerEntity.fromEvent(customerCreatedEvent);
        persistencePort.save(key + ".json", customerEntity);
    }
}
