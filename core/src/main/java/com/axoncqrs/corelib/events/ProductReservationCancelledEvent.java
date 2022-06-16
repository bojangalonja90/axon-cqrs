package com.axoncqrs.corelib.events;


import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProductReservationCancelledEvent {

    private final String productId;

    private final Integer quantity;
    private final String orderId;
    private final String userId;
    private final String reason;

}
