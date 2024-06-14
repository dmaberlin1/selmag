package com.dmadev.feedback.service;


import com.dmadev.feedback.entity.FavoriteProduct;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


public interface FavoriteProductService {

    Mono<FavoriteProduct> addProductToFavorites(int productId);

    Mono<Void> removeProductFromFavorites(int productId);

    Mono<FavoriteProduct> findFavoriteProductByProductId(int productId);

    Flux<FavoriteProduct> findFavoriteProducts();
}
