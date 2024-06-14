package com.dmadev.customer.client;

import com.dmadev.customer.entity.Product;
import lombok.RequiredArgsConstructor;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class WebClientProductsClient implements ProductsClient {

    private final WebClient webClient;
    final String  baseURI = "/catalogue-api/products";

    @Override
    public Flux<Product> findAllProducts(String filter) {
        return this.webClient.get()
                .uri(baseURI+"?filter={filter}",filter)
                .retrieve()
                .bodyToFlux(Product.class);
    }

    @Override
    public Mono<Product> findProduct(int id) {
        return this.webClient.get()
                .uri(baseURI + "/{productId}",id)
                .retrieve()
                .bodyToMono(Product.class)
                .onErrorComplete(WebClientResponseException.NotFound.class);
    }
}
