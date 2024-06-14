package com.dmadev.customer.client;

import com.dmadev.customer.entity.FavoriteProduct;
import com.dmadev.customer.entity.ProductReview;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface FavoritesProductClient {

    Flux<FavoriteProduct> findFavoriteProducts();

    Mono<FavoriteProduct> findFavoriteProductByProductId(int id);

     Mono<FavoriteProduct> addProductToFavorites(int productId);

    Mono<Void> removeProductFromFavorites(int productId);

}
