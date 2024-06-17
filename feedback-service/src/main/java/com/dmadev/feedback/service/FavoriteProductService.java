package com.dmadev.feedback.service;


import com.dmadev.feedback.entity.FavoriteProduct;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


public interface FavoriteProductService {

    Mono<FavoriteProduct> addProductToFavorites(int productId,String userId);

    Mono<Void> removeProductFromFavorites(int productId,String userId);

    Mono<FavoriteProduct> findFavoriteProductByProductId(int productId, String userId);

    Flux<FavoriteProduct> findFavoriteProducts(String userId);
}
