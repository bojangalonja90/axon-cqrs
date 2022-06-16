package com.axoncqrs.products;

import com.axoncqrs.products.command.interceptors.CreateProductCommandInterceptor;
import com.axoncqrs.products.core.errorhandling.ProductServiceEventsErrorHandler;
import org.axonframework.commandhandling.CommandBus;
import org.axonframework.config.EventProcessingConfigurer;
import org.checkerframework.checker.units.qual.A;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.ApplicationContext;

@EnableEurekaClient
@SpringBootApplication
public class ProductsApplication {

	public static void main(String[] args) {
		SpringApplication.run(ProductsApplication.class, args);
	}

	Logger LOG = LoggerFactory.getLogger(ProductsApplication.class);

	@Autowired
	public void registerCreateProductCommandInterceptor(ApplicationContext context, CommandBus commandBus) {
		LOG.info("Registering CreateProductCommandInterceptor");
		commandBus.registerDispatchInterceptor(context.getBean(CreateProductCommandInterceptor.class));

	}

	@Autowired
	public void configure(EventProcessingConfigurer config) {
		config.registerListenerInvocationErrorHandler("product-group", conf -> new ProductServiceEventsErrorHandler());
	}

}
