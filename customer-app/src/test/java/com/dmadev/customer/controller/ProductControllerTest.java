package com.dmadev.customer.controller;

import com.dmadev.customer.client.FavoritesProductClient;
import com.dmadev.customer.client.ProductReviewsClient;
import com.dmadev.customer.client.ProductsClient;
import com.dmadev.customer.entity.Product;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.mock.http.server.reactive.MockServerHttpResponse;
import org.springframework.ui.ConcurrentModel;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@ExtendWith(MockitoExtension.class)
@DisplayName("ProductController unit tests")
class ProductControllerTest {

    @Mock
    ProductsClient productsClient;

    @Mock
    FavoritesProductClient favoritesProductClient;

    @Mock
    ProductReviewsClient productReviewsClient;

    @InjectMocks
    ProductController controller;


    @Test
    void loadProduct_ProductExists_ReturnsNotEmptyMono() {
        //given
        int id = 1;
        String title = "Товар №1";
        String details = "Описание товара №1";
        var product = new Product(id, title, details);
        Mockito.doReturn(Mono.just(product)).when(this.productsClient).findProduct(id);

        //when
        StepVerifier.create(this.controller.loadProduct(id))
                //then
                .expectNext(product)
                .expectComplete()
                .verify();
        //verify вызывать когда вносятся изменения в систему
        verify(this.productsClient).findProduct(id);
        verifyNoMoreInteractions(this.productsClient);
        verifyNoInteractions(this.favoritesProductClient, this.productReviewsClient);
    }

    @Test
    void loadProduct_ProductDoesNotExist_ReturnsMonoWithNoSuchElementException() {
        //given
        int id = 1;
        Mockito.doReturn(Mono.empty()).when(this.productsClient).findProduct(id);

        //when
        StepVerifier.create(this.controller.loadProduct(id))
                //then
                .expectErrorMatches(exception ->  exception instanceof NoSuchElementException e &&
                            e.getMessage().equals("customer.products.error.not_found"))
                .verify();
        //verify вызывать когда вносятся изменения в систему
        verify(this.productsClient).findProduct(id);
        verifyNoMoreInteractions(this.productsClient);
        verifyNoInteractions(this.favoritesProductClient, this.productReviewsClient);
    }

    @Test
    @DisplayName("Exception NoSuchElementException must be translated in errors/404 page ")
    void handleNoSuchElementException_ReturnsErrors404() {
        //given
        String productNotFound = "Товар не найден";
        var exception = new NoSuchElementException(productNotFound);
        var concurrentModel = new ConcurrentModel();
        var response=new MockServerHttpResponse();
        //when
        var result = this.controller.handleNoSuchElementException(exception, concurrentModel,response);

        //then
        assertEquals("errors/404", result);
        assertEquals(productNotFound, concurrentModel.getAttribute("error"));
        assertEquals(HttpStatus.NOT_FOUND,response.getStatusCode());
    }


}