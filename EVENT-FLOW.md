# Event Flow Diagram

## 📊 Complete Event Publishing Flow

```
┌─────────────────────────────────────────────────────────────────┐
│                        USER REQUEST                              │
│  POST /api/orders                                               │
│  { customerName: "John", productName: "Laptop", quantity: 2 }   │
└───────────────────────────┬─────────────────────────────────────┘
                            │
                            ▼
┌─────────────────────────────────────────────────────────────────┐
│                   ORDER MODULE                                   │
│  ┌────────────────────────────────────────────────────────┐    │
│  │ OrderController.createOrder()                          │    │
│  └──────────────────────┬─────────────────────────────────┘    │
│                         │                                       │
│                         ▼                                       │
│  ┌────────────────────────────────────────────────────────┐    │
│  │ OrderService.placeOrder()                              │    │
│  │  1. Create Order entity                                │    │
│  │  2. Save to database (TRANSACTION BEGINS)              │    │
│  │  3. Create OrderPlacedEvent                            │    │
│  │  4. eventPublisher.publishEvent(event)                 │    │
│  │  5. Return Order (TRANSACTION COMMITS)                 │    │
│  └──────────────────────┬─────────────────────────────────┘    │
└─────────────────────────┼─────────────────────────────────────┘
                          │
                          │ Event Published AFTER
                          │ Transaction Commit ✓
                          │
          ┌───────────────┴───────────────┐
          │                               │
          ▼                               ▼
┌──────────────────────┐      ┌──────────────────────┐
│  INVENTORY MODULE    │      │ NOTIFICATION MODULE  │
│                      │      │                      │
│  @ApplicationModule  │      │  @ApplicationModule  │
│  Listener            │      │  Listener            │
│                      │      │                      │
│  onOrderPlaced()     │      │  onOrderPlaced()     │
│  │                   │      │  │                   │
│  ├─ Find Product     │      │  ├─ Create Message  │
│  ├─ Reserve Stock    │      │  ├─ Send Email      │
│  └─ Save Inventory   │      │  └─ Log Success     │
│                      │      │                      │
│  NEW TRANSACTION ✓   │      │  NEW TRANSACTION ✓  │
│  ASYNC ✓             │      │  ASYNC ✓            │
└──────────────────────┘      └──────────────────────┘
```

## 🔄 Step-by-Step Event Flow

### 1. Order Creation (Synchronous)

```
Client → OrderController → OrderService
         |
         ├─ [TX START]
         ├─ Create Order object
         ├─ orderRepository.save(order)
         ├─ Create OrderPlacedEvent
         ├─ eventPublisher.publishEvent(event)
         └─ [TX COMMIT] ← Event is held until now!
```

**Key Point**: The event is **NOT** published until the transaction commits successfully!

### 2. Event Distribution (Asynchronous)

```
OrderPlacedEvent (published)
    │
    ├─────────────────────┬─────────────────────┐
    │                     │                     │
    ▼                     ▼                     ▼
InventoryService    NotificationService   (Future Listeners)
  (Async)               (Async)              (Async)
```

### 3. Parallel Processing

Both listeners process **independently** and **asynchronously**:

**InventoryService:**
```
[NEW TX START]
  ├─ Receive OrderPlacedEvent
  ├─ Find Inventory by productName
  ├─ Reserve quantity
  ├─ Save updated inventory
[TX COMMIT]
```

**NotificationService:**
```
[NEW TX START]
  ├─ Receive OrderPlacedEvent
  ├─ Build notification message
  ├─ Send to customer
  ├─ Log success
[TX COMMIT]
```

## ⚡ Key Benefits

### 1. **Transaction Safety**
- Order is saved FIRST
- Event published ONLY if save succeeds
- No events for failed orders

### 2. **Async Processing**
- OrderService returns immediately
- Inventory and Notifications process in background
- Better response time for users

### 3. **Independent Execution**
- If Notification fails, Inventory still succeeds
- Each listener has its own transaction
- Failures are isolated

### 4. **Loose Coupling**
- Order module doesn't know about Inventory or Notification
- Easy to add new listeners without changing Order module
- True separation of concerns

## 🎯 Event Flow Timeline

```
Time 0ms:   Client sends POST /api/orders
Time 5ms:   OrderService creates order
Time 10ms:  Order saved to database
Time 12ms:  OrderPlacedEvent published
Time 15ms:  Response returned to client ← USER SEES SUCCESS
Time 20ms:  InventoryService receives event (async)
Time 25ms:  NotificationService receives event (async)
Time 30ms:  Inventory updated
Time 35ms:  Notification sent
```

**Total user wait time**: ~15ms
**Total processing time**: ~35ms

## 🔐 Transaction Boundaries

```
┌─────────────────────────────────────────┐
│  OrderService Transaction                │
│  ┌────────────────────────────────────┐ │
│  │ 1. Save Order                       │ │
│  │ 2. Publish Event (staged)           │ │
│  └────────────────────────────────────┘ │
│         ↓ Commit                         │
└─────────────────────────────────────────┘
              ↓ Event Released
┌─────────────────────────────────────────┐
│  InventoryService Transaction            │
│  ┌────────────────────────────────────┐ │
│  │ 1. Receive Event                    │ │
│  │ 2. Reserve Inventory                │ │
│  │ 3. Save                             │ │
│  └────────────────────────────────────┘ │
│         ↓ Commit                         │
└─────────────────────────────────────────┘

┌─────────────────────────────────────────┐
│  NotificationService Transaction         │
│  ┌────────────────────────────────────┐ │
│  │ 1. Receive Event                    │ │
│  │ 2. Send Notification                │ │
│  └────────────────────────────────────┘ │
│         ↓ Commit                         │
└─────────────────────────────────────────┘
```

## 🧩 Module Dependencies

```
┌─────────────┐
│   Order     │ ← No dependencies
│   Module    │
└──────┬──────┘
       │ publishes
       │ OrderPlacedEvent
       │
       ├─────────────────┬─────────────────┐
       │                 │                 │
       ▼                 ▼                 ▼
┌─────────────┐   ┌─────────────┐   ┌─────────────┐
│  Inventory  │   │Notification │   │   Future    │
│   Module    │   │   Module    │   │   Modules   │
└─────────────┘   └─────────────┘   └─────────────┘
     ↓                  ↓                  ↓
  depends on        depends on        depends on
  order (events)    order (events)    order (events)
```

## 📝 Summary

1. **User makes request** → OrderController
2. **Order created** → OrderService (in transaction)
3. **Event published** → Only after transaction commits
4. **Event distributed** → To all @ApplicationModuleListener methods
5. **Async processing** → Each listener in its own transaction
6. **Complete** → All modules updated independently

This is the power of **Event-Driven Architecture** with **Spring Modulith**! 🚀
