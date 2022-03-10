package io.gaddings.backofficeservice.domain;

import lombok.*;

import java.util.Objects;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerEntity {

    private String fullName;
    private CustomerType type;

    public static CustomerEntity fromEvent(final CustomerCreatedEvent event) {
        val fullName = (Objects.toString(event.getFirstName(), "") + " " + Objects.toString(event.getName(), "")).trim();
        val type = event.isBusinessCustomer() ? CustomerType.BUSINESS : CustomerType.PRIVATE;
        return new CustomerEntity(fullName, type);
    }
}
