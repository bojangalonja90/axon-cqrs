package com.axoncqrs.products.command.rest;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CreateProductModelDTO {

    private final String title;
    private final BigDecimal price;
    private final Integer quantity;

}
