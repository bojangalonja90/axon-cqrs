package com.axoncqrs.orders.orders.command.rest;

import lombok.Data;

@Data
public class CreateOrderCommandDTO {

    private String productId;
    private Integer quantity;
    private String addressId;

}
