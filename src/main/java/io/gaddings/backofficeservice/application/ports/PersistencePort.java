package io.gaddings.backofficeservice.application.ports;

import io.gaddings.backofficeservice.domain.CustomerEntity;

public interface PersistencePort {

    void save(String key, CustomerEntity customerEntity);
}
