package com.axoncqrs.products.query.rest;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductDTO {

    private String id;
    private String title;
    private BigDecimal price;
    private Integer quantity;

}
