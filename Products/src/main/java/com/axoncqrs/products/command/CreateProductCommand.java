package com.axoncqrs.products.command;


import lombok.Builder;
import lombok.Data;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

import java.math.BigDecimal;

@Data
@Builder
public class CreateProductCommand {

    @TargetAggregateIdentifier
    private final String id;
    private final String title;
    private final BigDecimal price;
    private final Integer quantity;
}
