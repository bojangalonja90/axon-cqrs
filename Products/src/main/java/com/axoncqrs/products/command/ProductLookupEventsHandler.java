package com.axoncqrs.products.command;

import com.axoncqrs.products.core.data.ProductLookupEntity;
import com.axoncqrs.products.core.data.ProductLookupRepository;
import com.axoncqrs.products.core.events.ProductCreatedEvent;
import org.axonframework.config.ProcessingGroup;
import org.axonframework.eventhandling.EventHandler;
import org.springframework.stereotype.Component;

@Component
@ProcessingGroup("product-group")
public class ProductLookupEventsHandler {

    private final ProductLookupRepository productLookupRepository;

    public ProductLookupEventsHandler(ProductLookupRepository productLookupRepository) {
        this.productLookupRepository = productLookupRepository;
    }

    @EventHandler
    public void on(ProductCreatedEvent event) {
        ProductLookupEntity productLookupEntity = new ProductLookupEntity(event.getId(), event.getTitle());

        productLookupRepository.save(productLookupEntity);
    }

}
