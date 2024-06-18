package com.dmadev.feedback.controller;

import com.dmadev.feedback.entity.ProductReview;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.mockJwt;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;

@SpringBootTest
@AutoConfigureWebTestClient
@DisplayName("Integration tests for ProductReviewsRestController")
@Slf4j
class ProductReviewsRestControllerIT {

    @Autowired
    WebTestClient webTestClient;

    @Autowired
    ReactiveMongoTemplate reactiveMongoTemplate;

    @BeforeEach
    void setUp() {
        UUID uuidUser1 = UUID.fromString("e01297aa-2ce9-11ef-bb42-ef7a8532f130");
        UUID uuidUser2 = UUID.fromString("e0f23982-2ce9-11ef-808a-a776da761969");
        UUID uuidUser3 = UUID.fromString("e14694aa-2ce9-11ef-b64b-f798a628235e");
        List<ProductReview> productReviews = List.of(

                new ProductReview(uuidUser1, 1, 1, "Отзыв номер №1", "user-1"),
                new ProductReview(uuidUser2, 2, 2, "Отзыв номер №2", "user-2"),
                new ProductReview(uuidUser3, 3, 3, "Отзыв номер №3", "user-3")
        );
        this.reactiveMongoTemplate.insertAll(productReviews).blockLast();

    }

    @AfterEach
    void tearDown() {
        this.reactiveMongoTemplate.remove(ProductReview.class)
                .all()
                .block();
    }

    @Test
    @DisplayName("findProductReviewsByProductId must Returns Reviews")
    void findProductReviewsByProductId_ReturnsReviews() {
        //given
        String uri = "/feedback-api/product-reviews/by-product-id/1";
        //when
        this.webTestClient
                .get()
                .uri(uri)
        // then
                .exchange()
                .expectStatus().isUnauthorized();
    }

 @Test
    @DisplayName("findProductReviewsByProductId must Returns Not Authorized")
    void findProductReviewsByProductId_UserIsNotAuthenticated_ReturnsNotAuthorized() {
        //given


        String expectedJson = """
                [
                {"id":"e01297aa-2ce9-11ef-bb42-ef7a8532f130",
                "productId":1,
                "rating":1,
                "review":"Отзыв номер №1",
                "userId":"user-1"},\s
                {"id":"e0f23982-2ce9-11ef-808a-a776da761969",
                "productId":2,
                "rating":2,
                "review":"Отзыв номер №2",
                "userId":"user-2"}, \s
                {"id":"e14694aa-2ce9-11ef-b64b-f798a628235e",
                "productId":3,
                "rating":3,
                "review":"Отзыв номер №3",
                "userId":"user-3"},
                ]""";

        //when
        // then

        this.webTestClient.mutateWith(mockJwt())
                .mutate().filter(ExchangeFilterFunction.ofRequestProcessor(clientRequest -> {
                    log.info("==== REQUEST====");
                    log.info("{} {}", clientRequest.method(), clientRequest.url());
                    clientRequest.headers().forEach((header, value) ->
                            log.info("{}: {}", header, value));
                    log.info("==== END OF REQUEST====");
                    return Mono.just(clientRequest);
                }))
                .build()
                .get()
                .uri("/feedback-api/product-reviews/by-product-id/1")
                .exchange()
                .expectHeader().contentTypeCompatibleWith(MediaType.APPLICATION_JSON)
                .expectBody()
                .json(expectedJson);
    }

    @Test
    void createProductReview_RequestIsValid_ReturnsCreatedProductReview(){
        //given
        String bodyValue = """
                                  {
                                  "productId": 1,
                                  "rating": 5,
                                  "review": "На пятерочку!"
                                  }
                """;
        String bodyValueWithUserId = """
                                  {
                                  "productId": 1,
                                  "rating": 5,
                                  "review": "На пятерочку!",
                                  "userId": "user-tester"
                                  }
                """;
        //when

        this.webTestClient
                .mutateWith(mockJwt().jwt(builder -> builder.subject("user-tester")))
                .post()
                .uri("/feedback-api/product-reviews")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(bodyValue)
        //then
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().exists(HttpHeaders.LOCATION)
                .expectHeader().contentTypeCompatibleWith(MediaType.APPLICATION_JSON)
                .expectBody().json(bodyValueWithUserId).jsonPath("$.id").exists();
    }

    @Test
    void createProductReview_RequestIsValid_ReturnsBadRequest(){
        //given
        String bodyValue = """
                                  {
                                  "productId": null,
                                  "rating": -1,
                                  "review": "На пятерочку!"
                                  }
                """;
        String bodyValueResponse = """
                                     {
                             "errors":[
                "Товар не указан",
                "Оценка меньше 1",
                "Отзыв не должен превышать 2000 символов"          
                                  ] 
                                  }
                                """;
        //when

        this.webTestClient
                .mutateWith(mockJwt().jwt(builder -> builder.subject("user-tester")))
                .post()
                .uri("/feedback-api/product-reviews")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(bodyValue)
        //then
                .exchange()
                .expectStatus().isBadRequest()
                .expectHeader().doesNotExist(HttpHeaders.LOCATION)
                .expectHeader().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON)
                .expectBody()
                .json(bodyValueResponse);
    }


    //eof
}