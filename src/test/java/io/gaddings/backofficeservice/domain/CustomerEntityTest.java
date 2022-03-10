package io.gaddings.backofficeservice.domain;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class CustomerEntityTest {

    @ParameterizedTest
    @MethodSource("provideVariations")
    void creatingFromEvent(final CustomerCreatedEvent input, final CustomerEntity expected) {
        assertThat(CustomerEntity.fromEvent(input)).isEqualTo(expected);
    }

    private static Stream<Arguments> provideVariations() {
        return Stream.of(
            Arguments.of(
                CustomerCreatedEvent.builder().name("Bacon").firstName("Chris P.").businessCustomer(false).build(),
                new CustomerEntity("Chris P. Bacon", CustomerType.PRIVATE)),
            Arguments.of(
                CustomerCreatedEvent.builder().name("Bacon").businessCustomer(false).build(),
                new CustomerEntity("Bacon", CustomerType.PRIVATE)),
            Arguments.of(
                CustomerCreatedEvent.builder().businessCustomer(false).build(),
                new CustomerEntity("", CustomerType.PRIVATE)),
            Arguments.of(
                CustomerCreatedEvent.builder().build(),
                new CustomerEntity("", CustomerType.PRIVATE)),
            Arguments.of(
                CustomerCreatedEvent.builder().firstName("Chris P.").businessCustomer(false).build(),
                new CustomerEntity("Chris P.", CustomerType.PRIVATE)),
            Arguments.of(
                CustomerCreatedEvent.builder().name("Bacon").firstName("Chris P.").businessCustomer(true).build(),
                new CustomerEntity("Chris P. Bacon", CustomerType.BUSINESS))
        );
    }
}
