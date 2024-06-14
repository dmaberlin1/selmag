package com.dmadev.customer.client;

import com.dmadev.customer.client.exception.ClientBadRequestException;
import com.dmadev.customer.client.payload.NewFavoriteProductPayload;
import com.dmadev.customer.entity.FavoriteProduct;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ProblemDetail;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@RequiredArgsConstructor
public class WebClientFavoritesProductClient implements FavoritesProductClient {
    private final WebClient webClient;

    @Override
    public Flux<FavoriteProduct> findFavoriteProducts() {
        return this.webClient.get()
                .uri("/feedback-api/favorite-products")
                .retrieve()
                .bodyToFlux(FavoriteProduct.class);
    }

    @Override
    public Mono<FavoriteProduct> findFavoriteProductByProductId(int productId) {
        return this.webClient.get()
                .uri("/feedback-api/favorite-products")
                .retrieve()
                .bodyToMono(FavoriteProduct.class)
                .onErrorComplete(WebClientResponseException.NotFound.class);
    }

    @Override
    public Mono<FavoriteProduct> addProductToFavorites(int productId) {
        return this.webClient.post()
                .uri("/feedback-api/favorite-products")
                .bodyValue(new NewFavoriteProductPayload(productId))
                .retrieve()
                .bodyToMono(FavoriteProduct.class)
                .onErrorComplete(WebClientResponseException.NotFound.class)
                .onErrorMap(WebClientResponseException.BadRequest.class,
                        exception ->new ClientBadRequestException(
                                exception,
                                ((List<String>)exception.getResponseBodyAs(ProblemDetail.class)
                                        .getProperties().get("errors")))
                );

    }

    @Override
    public Mono<Void> removeProductFromFavorites(int productId) {
        return this.webClient.delete()
                .uri("/feedback-api/favorite-products/by-product-id/{productId}",productId)
                .retrieve()
                .toBodilessEntity()
                .then();
    }

    //eof
}
