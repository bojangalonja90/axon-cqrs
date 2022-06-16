package com.axoncqrs.products.core.events;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductCreatedEvent {

    private String id;
    private String title;
    private BigDecimal price;
    private Integer quantity;

}
