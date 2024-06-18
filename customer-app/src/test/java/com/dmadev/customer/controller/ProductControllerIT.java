package com.dmadev.customer.controller;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import static com.github.tomakehurst.wiremock.client.WireMock.created;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.equalToJson;
import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.mockUser;


@SpringBootTest
@AutoConfigureWebTestClient
@WireMockTest(httpPort = 54321)
@DisplayName("ProductController Integration Tests")
class ProductControllerIT {

    @Autowired
    WebTestClient webTestClient;

    @Test
    @DisplayName("addProductToFavorites with valid request must return redirection to ProductPage")
    void addProductToFavorites_RequestIsValid_ReturnsRedirectionToProductPage() {
        //given
        String uriCustomer = "/customer/products/1/add-to-favorites";
        String urlCatalogue = "/catalogue-api/products/1";
        String urlFeedback = "/feedback-api/favorite-products";
        WireMock.stubFor(WireMock.get(urlCatalogue)
                .willReturn(WireMock.okJson("""
                        {
                        "id": 1,
                        "title": "Название товара №1",
                        "details": "Описание товара №1"
                        }
                        """).withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)));


        String valueFeedbackProductId = """
                {
                "productId": 1
                }
                """;
        WireMock.stubFor(WireMock.post(urlFeedback)
                .withRequestBody(WireMock.equalToJson(valueFeedbackProductId))
                .withHeader(HttpHeaders.CONTENT_TYPE, equalTo(MediaType.APPLICATION_JSON_VALUE))
                .willReturn(created()
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .withBody("""
                                {
                                "id" : "a9937b9c-2d64-11ef-bbc1-63e9e36397ef"
                                "productId" : 1
                                }
                                """)));


        //when
        this.webTestClient
                .mutateWith(mockUser())
                .mutateWith(csrf())
                .post()
                .uri(uriCustomer)
                //then
                .exchange()

                .expectStatus().is3xxRedirection()
                .expectHeader().location("/customer/products/1");


        WireMock.verify(getRequestedFor(urlPathMatching(urlCatalogue)));
        WireMock.verify(postRequestedFor(urlPathMatching(urlFeedback))
                .withRequestBody(equalToJson(valueFeedbackProductId)));

    }

    @Test
    @DisplayName("addProductToFavorites  with not exist product must return NotFoundPage")
    void addProductToFavorites_ProductDoesNotExist_ReturnsNotFoundPage() {
        //given
         String uriCustomer = "/customer/products/1/add-to-favorites";
                String urlCatalogue = "/catalogue-api/products/1";
                String urlFeedback = "/feedback-api/favorite-products";


        //when
        this.webTestClient
                .mutateWith(mockUser())
                .mutateWith(csrf())
                .post()
                .uri(uriCustomer)
                //then
                .exchange()
                .expectStatus().isNotFound();


        WireMock.verify(getRequestedFor(urlPathMatching(urlCatalogue)));


    }
}