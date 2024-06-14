package com.dmadev.feedback.controller;

import com.dmadev.feedback.controller.payload.NewFavoriteProductPayload;
import com.dmadev.feedback.entity.FavoriteProduct;
import com.dmadev.feedback.service.FavoriteProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("feedback-api/favorite-products")
@RequiredArgsConstructor
public class FavoriteProductsRestController {
    private final FavoriteProductService favoriteProductService;

    @GetMapping
    public Flux<FavoriteProduct> findFavoriteProducts() {

        return this.favoriteProductService.findFavoriteProducts();
    }

    @GetMapping("by-product-id/{productId:\\d+}")
    public Mono<FavoriteProduct> favoriteProductByProductId(@PathVariable("productId") int productId) {
        return this.favoriteProductService.findFavoriteProductByProductId(productId);
    }

    @PostMapping()
    public Mono<ResponseEntity<FavoriteProduct>> addFavoriteProduct(
            @Valid @RequestBody Mono<NewFavoriteProductPayload> payloadMono,
            UriComponentsBuilder uriComponentsBuilder
    ) {


        return payloadMono
                .flatMap(payload -> this.favoriteProductService.addProductToFavorites(payload.productId()))
                .map(favoriteProduct -> ResponseEntity
                        .created(uriComponentsBuilder.replacePath("feedback-api/favorite-products/{id}")
                                .build(favoriteProduct.getId()))
                        .body(favoriteProduct));
    }


    @DeleteMapping("by-product-id/{productId:\\d+}")
    public Mono<ResponseEntity<Void>> removeProductFromFavorites(@PathVariable("productId") int productId) {


        return this.favoriteProductService.removeProductFromFavorites(productId)
                .then(Mono.just(ResponseEntity
                        .noContent()
                        .build()));
    }

    //eof
}
