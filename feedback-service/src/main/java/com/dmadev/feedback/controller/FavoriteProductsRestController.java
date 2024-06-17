package com.dmadev.feedback.controller;

import com.dmadev.feedback.controller.payload.NewFavoriteProductPayload;
import com.dmadev.feedback.entity.FavoriteProduct;
import com.dmadev.feedback.service.FavoriteProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
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
    public Flux<FavoriteProduct> findFavoriteProducts(
            Mono<JwtAuthenticationToken> authenticationTokenMono) {

        return authenticationTokenMono.flatMapMany(token ->
                this.favoriteProductService
                        .findFavoriteProducts(token.getToken().getSubject()));
    }

    @GetMapping("by-product-id/{productId:\\d+}")
    public Mono<FavoriteProduct> favoriteProductByProductId(
            Mono<JwtAuthenticationToken> authenticationTokenMono,
            @PathVariable("productId") int productId
    ) {
        return authenticationTokenMono.flatMap(token ->
                this.favoriteProductService.findFavoriteProductByProductId(productId, token.getToken().getSubject()));
    }

    @PostMapping()
    public Mono<ResponseEntity<FavoriteProduct>> addFavoriteProduct(
            Mono<JwtAuthenticationToken> authenticationTokenMono,
            @Valid @RequestBody Mono<NewFavoriteProductPayload> payloadMono,
            UriComponentsBuilder uriComponentsBuilder
    ) {
        return Mono.zip(authenticationTokenMono, payloadMono)
                .flatMap(tuple -> this.favoriteProductService.addProductToFavorites(
                        tuple.getT2().productId(),
                        tuple.getT1().getToken().getSubject()))
                .map(favoriteProduct -> ResponseEntity
                        .created(uriComponentsBuilder.replacePath("feedback-api/favorite-products/{id}")
                                .build(favoriteProduct.getId()))
                        .body(favoriteProduct));
    }


    @DeleteMapping("by-product-id/{productId:\\d+}")
    public Mono<ResponseEntity<Void>> removeProductFromFavorites(
            Mono<JwtAuthenticationToken> authenticationTokenMono,
            @PathVariable("productId") int productId) {

        return authenticationTokenMono.flatMap(token ->
                this.favoriteProductService.removeProductFromFavorites(productId, token.getToken().getSubject())
                        .then(Mono.just(ResponseEntity
                                .noContent()
                                .build())));
    }

    //eof
}
