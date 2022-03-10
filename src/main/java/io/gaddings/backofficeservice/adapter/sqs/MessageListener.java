package io.gaddings.backofficeservice.adapter.sqs;

import io.awspring.cloud.messaging.listener.annotation.SqsListener;
import io.gaddings.backofficeservice.application.CustomerPersister;
import io.gaddings.backofficeservice.domain.CustomerCreatedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class MessageListener {

    private final CustomerPersister customerPersister;

    public MessageListener(final CustomerPersister customerPersister) {
        this.customerPersister = customerPersister;
    }

    @SqsListener("${sqs.queue}")
    public void processMessage(@Payload final CustomerCreatedEvent customerCreatedEvent) {
        log.info("Received CustomerCreatedEvent: {}", customerCreatedEvent.getId());
        customerPersister.createPersistentCustomer(customerCreatedEvent);
    }
}
