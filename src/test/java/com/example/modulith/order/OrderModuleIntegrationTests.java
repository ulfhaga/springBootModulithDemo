package com.example.modulith.order;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.modulith.test.ApplicationModuleTest;
import org.springframework.modulith.test.Scenario;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@ApplicationModuleTest
class OrderModuleIntegrationTests {

    @Autowired
    private OrderService orderService;

    @Test
    void orderPlacedEventIsPublished(Scenario scenario) {
        scenario.stimulate(() ->
                        orderService.placeOrder(
                                "John Doe",
                                "Laptop",
                                2,
                                new BigDecimal("2000.00")
                        )
                )
                .andWaitForEventOfType(OrderPlacedEvent.class)
                .toArriveAndVerify(event -> {
                    assertThat(event.customerName()).isEqualTo("John Doe");
                    assertThat(event.productName()).isEqualTo("Laptop");
                    assertThat(event.quantity()).isEqualTo(2);
                });
    }
}

