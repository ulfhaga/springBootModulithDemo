# Spring Modulith Event Publishing Demo

This project demonstrates **Spring Modulith** with event-driven architecture, showing how to publish and consume domain events across different modules while maintaining clean boundaries.

## 📋 Overview

Spring Modulith is a framework that helps you build well-structured, modular Spring Boot applications. This demo showcases:

- ✅ **Modular Architecture**: Three independent modules (Order, Inventory, Notification)
- ✅ **Event Publishing**: Publishing domain events when business actions occur
- ✅ **Event Listening**: Multiple modules consuming the same event asynchronously
- ✅ **Module Boundaries**: Clear separation of concerns with enforced boundaries
- ✅ **Transactional Events**: Events are published only when transactions commit

## 🏗️ Architecture

```
┌─────────────────┐
│  Order Module   │
│                 │
│  - OrderService │──┐
│  - Order Entity │  │ publishes
│                 │  │ OrderPlacedEvent
└─────────────────┘  │
                     │
        ┌────────────┴─────────────┐
        │                          │
        ▼                          ▼
┌──────────────────┐    ┌─────────────────────┐
│ Inventory Module │    │ Notification Module │
│                  │    │                     │
│ - Reserves stock │    │ - Sends emails      │
│                  │    │                     │
└──────────────────┘    └─────────────────────┘
```

## 🎯 Key Concepts Demonstrated

### 1. **Event Publishing**

In the `OrderService`, when an order is placed:

```java
@Transactional
public Order placeOrder(...) {
    Order order = new Order(...);
    order = orderRepository.save(order);
    
    // Publish event - delivered only if transaction commits
    OrderPlacedEvent event = OrderPlacedEvent.from(order);
    eventPublisher.publishEvent(event);
    
    return order;
}
```

### 2. **Event Listening**

Multiple modules can listen to the same event:

```java
@ApplicationModuleListener
@Transactional
public void onOrderPlaced(OrderPlacedEvent event) {
    // React to the event
    // This runs asynchronously in its own transaction
}
```

### 3. **Module Boundaries**

Each module defines its boundaries in `package-info.java`:

```java
@ApplicationModule(
    displayName = "Order Management",
    allowedDependencies = {}
)
package com.example.modulith.order;
```

## 🚀 Running the Application

### Prerequisites

- Java 17 or higher
- Maven 3.6+

### Build and Run

```bash
# Build the project
mvn clean install

# Run the application
mvn spring-boot:run
```

The application starts on `http://localhost:8080`

### Testing the Event Flow

#### 1. Create Inventory First

```bash
curl -X POST http://localhost:8080/api/inventory \
  -H "Content-Type: application/json" \
  -d '{
    "productName": "Laptop",
    "quantity": 100
  }'
```

#### 2. Place an Order (This publishes the event)

```bash
curl -X POST http://localhost:8080/api/orders \
  -H "Content-Type: application/json" \
  -d '{
    "customerName": "John Doe",
    "productName": "Laptop",
    "quantity": 2,
    "totalAmount": 2000.00
  }'
```

#### 3. Check the Logs

You'll see:

```
OrderService    : Placing order for customer: John Doe, product: Laptop
OrderService    : Order placed with ID: 1 and event published

InventoryService: Received OrderPlacedEvent for order ID: 1, product: Laptop
InventoryService: Reserved 2 units of Laptop for order ID: 1

NotificationService: === NOTIFICATION SERVICE ===
NotificationService: Sending order confirmation to customer: John Doe
NotificationService: Notification sent successfully!
```

#### 4. Verify Inventory Was Reserved

```bash
curl http://localhost:8080/api/inventory/Laptop
```

Response:
```json
{
  "id": 1,
  "productName": "Laptop",
  "availableQuantity": 98,
  "reservedQuantity": 2
}
```

## 📊 Module Verification

Run the tests to verify module structure:

```bash
mvn test
```

This will:
- ✅ Verify module boundaries are not violated
- ✅ Generate module documentation in `target/modulith-docs`
- ✅ Create PlantUML diagrams of your architecture

## 🔍 Key Features

### 1. **Asynchronous Event Processing**

Events are processed asynchronously, so the order creation doesn't wait for inventory reservation or notifications.

### 2. **Transactional Guarantees**

Events are only published if the transaction commits. If order creation fails, no events are sent.

### 3. **Multiple Listeners**

The same `OrderPlacedEvent` is consumed by:
- **InventoryService**: To reserve stock
- **NotificationService**: To send customer notifications

### 4. **Loose Coupling**

Modules communicate only through events. The Order module doesn't know about Inventory or Notification modules.

### 5. **Event Externalization**

The `@Externalized` annotation on `OrderPlacedEvent` allows events to be published to external message brokers (Kafka, RabbitMQ) if configured.

## 📁 Project Structure

```
spring-modulith-demo/
├── src/
│   ├── main/
│   │   ├── java/com/example/modulith/
│   │   │   ├── ModulithApplication.java
│   │   │   ├── order/
│   │   │   │   ├── Order.java
│   │   │   │   ├── OrderService.java
│   │   │   │   ├── OrderPlacedEvent.java
│   │   │   │   ├── OrderController.java
│   │   │   │   └── package-info.java
│   │   │   ├── inventory/
│   │   │   │   ├── Inventory.java
│   │   │   │   ├── InventoryService.java
│   │   │   │   ├── InventoryController.java
│   │   │   │   └── package-info.java
│   │   │   └── notification/
│   │   │       ├── NotificationService.java
│   │   │       └── package-info.java
│   │   └── resources/
│   │       └── application.properties
│   └── test/
│       └── java/com/example/modulith/
│           └── ModulithApplicationTests.java
└── pom.xml
```

## 🎓 Learning Points

1. **Event-Driven Architecture**: Modules communicate via events, not direct calls
2. **Module Boundaries**: Spring Modulith enforces architectural boundaries
3. **Asynchronous Processing**: Event listeners run independently
4. **Transaction Safety**: Events published only on successful commits
5. **Scalability**: Easy to add new listeners without changing publishers

## 🔧 Advanced Configuration

### Event Persistence

To enable event persistence (for replay and monitoring):

```java
@Configuration
class EventPublicationConfiguration {
    @Bean
    EventExternalizationConfiguration eventExternalizationConfiguration() {
        return EventExternalizationConfiguration.externalizing()
            .select(EventExternalizationConfiguration.annotatedAsExternalized())
            .build();
    }
}
```

### Integration with Message Brokers

Spring Modulith can externalize events to Kafka, RabbitMQ, etc.:

```xml
<dependency>
    <groupId>org.springframework.modulith</groupId>
    <artifactId>spring-modulith-events-kafka</artifactId>
</dependency>
```

## 📚 Additional Resources

- [Spring Modulith Documentation](https://docs.spring.io/spring-modulith/reference/)
- [Spring Modulith GitHub](https://github.com/spring-projects/spring-modulith)
- [Event-Driven Architecture Patterns](https://martinfowler.com/articles/201701-event-driven.html)

## Kill the application hard

    kill -9 $(lsof -ti:8080)

