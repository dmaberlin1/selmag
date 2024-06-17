package com.dmadev.customer.controller;


import com.dmadev.customer.client.FavoritesProductClient;
import com.dmadev.customer.client.ProductReviewsClient;
import com.dmadev.customer.client.ProductsClient;
import com.dmadev.customer.client.exception.ClientBadRequestException;
import com.dmadev.customer.controller.payload.NewProductReviewPayload;
import com.dmadev.customer.entity.Product;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.web.server.csrf.CsrfToken;
import org.springframework.security.web.reactive.result.view.CsrfRequestDataValueProcessor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.NoSuchElementException;

@Controller
@RequiredArgsConstructor
@RequestMapping("customer/products/{productId:\\d+}")
@Slf4j
public class ProductController {

    private final ProductsClient productsClient;

    private final FavoritesProductClient favoritesProductClient;

    private final ProductReviewsClient productReviewsClient;

    @ModelAttribute(name = "product", binding = false)
    public Mono<Product> loadProduct(@PathVariable("productId") int id) {
        return this.productsClient.findProduct(id)
                .switchIfEmpty(Mono.defer(
                        ()->Mono.error(new NoSuchElementException("customer.products.error.not_found"))
                ));
    }

    @GetMapping
    public Mono<String> getProductPage(@PathVariable("productId") int id, Model model) {
        model.addAttribute("inFavorite", false);
        return this.productReviewsClient.findProductReviewsByProductId(id)
                .collectList()
                .doOnNext(productReviews -> model.addAttribute("reviews", productReviews))
                .then(
                        this.favoritesProductClient
                                .findFavoriteProductByProductId(id)
                                .doOnNext(favoriteProduct ->
                                        model.addAttribute("inFavorite", true))
                )
                .thenReturn("customer/products/product");
    }


    @PostMapping("add-to-favorites")
    public Mono<String> addProductToFavorites(@ModelAttribute("product") Mono<Product> productMono) {
        return productMono
                .map(Product::id)
                .flatMap(productId -> this.favoritesProductClient.addProductToFavorites(productId)
                        .thenReturn("redirect:/customer/products/%d".formatted(productId))
                        .onErrorResume(exception -> {
                            log.error(exception.getMessage(), exception);
                            return Mono.just("redirect:/customer/products/%d".formatted(productId));
                        })
                );
    }

    @PostMapping("remove-from-favorites")
    public Mono<String> removeProductFromFavourites(@ModelAttribute("product") Mono<Product> productMono) {
        return productMono
                .map(Product::id)
                .flatMap(productId -> this.favoritesProductClient.removeProductFromFavorites(productId)
                        .thenReturn("redirect:/customer/products/%d".formatted(productId)));
    }

    @PostMapping("create-review")
    public Mono<String> createReview(@PathVariable("productId") int id,
                                     NewProductReviewPayload payload,
                                     Model model) {
        return this.productReviewsClient.createProductReview(id, payload.rating(), payload.review())
                .thenReturn("redirect:/customer/products/%d".formatted(id))
                .onErrorResume(ClientBadRequestException.class,
                        exception -> {
                            model.addAttribute("inFavorite", false);
                            model.addAttribute("payload", payload);
                            model.addAttribute("errors", exception.getErrors());
                            return this.favoritesProductClient.findFavoriteProductByProductId(id)
                                    .doOnNext(favoriteProduct ->
                                            model.addAttribute("inFavorite", true))
                                    .thenReturn("customer/products/product");
                        });
    }

    @ExceptionHandler(NoSuchElementException.class)
    public String handleNoSuchElementException(NoSuchElementException exception, Model model) {
        model.addAttribute("error", exception.getMessage());
        return "errors/404";
    }

    @ModelAttribute
    public Mono<CsrfToken> loadCsrfToken(ServerWebExchange exchange) {
        return exchange.<Mono<CsrfToken>>getAttribute(CsrfToken.class.getName())
                .doOnSuccess(token -> exchange.getAttributes()
                        .put(CsrfRequestDataValueProcessor.DEFAULT_CSRF_ATTR_NAME,
                                token));
    }

    //eof
}


