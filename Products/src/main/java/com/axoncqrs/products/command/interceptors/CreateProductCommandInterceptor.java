package com.axoncqrs.products.command.interceptors;

import com.axoncqrs.products.command.CreateProductCommand;
import com.axoncqrs.products.core.data.ProductLookupEntity;
import com.axoncqrs.products.core.data.ProductLookupRepository;
import org.axonframework.commandhandling.CommandMessage;
import org.axonframework.messaging.MessageDispatchInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.function.BiFunction;

@Component
public class CreateProductCommandInterceptor implements MessageDispatchInterceptor<CommandMessage<?>> {

    public static final Logger LOG = LoggerFactory.getLogger(CreateProductCommandInterceptor.class);

    private final ProductLookupRepository productLookupRepository;

    public CreateProductCommandInterceptor(ProductLookupRepository productLookupRepository) {
        this.productLookupRepository = productLookupRepository;
    }

    @Override
    public BiFunction<Integer, CommandMessage<?>, CommandMessage<?>> handle(List<? extends CommandMessage<?>> messages) {

        return (index, command) -> {
            LOG.info("*** Invoked CreateProductCommandInterceptor for: ", command.getPayloadType());
            if (CreateProductCommand.class.equals(command.getPayloadType())) {
                CreateProductCommand createProductCommand = (CreateProductCommand) command.getPayload();

                //Validate
                if(createProductCommand.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
                    throw new IllegalArgumentException("Price cannot be less than zero");
                }

                if(createProductCommand.getTitle() == null) {
                    throw new IllegalArgumentException("Title cannot be nukll");
                }

                ProductLookupEntity productLookupEntity = productLookupRepository
                        .findByIdOrTitle(createProductCommand.getId(), createProductCommand.getTitle());

                if (productLookupEntity != null) {
                    throw new IllegalStateException(String.format("Product with id %s or title %s already exista",
                            createProductCommand.getId(), createProductCommand.getTitle()));
                }
            }

            return command;
        };
    }
}
