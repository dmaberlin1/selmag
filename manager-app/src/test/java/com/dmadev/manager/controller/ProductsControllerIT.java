package com.dmadev.manager.controller;

import com.dmadev.manager.BaseIntegrationTest;
import com.dmadev.manager.entity.Product;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.matching.UrlPathPattern;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

public class ProductsControllerIT extends BaseIntegrationTest {


    @Test
    void getProductList_ReturnsProductsListPage() throws Exception {
        //given
        String role_manager = "MANAGER";
        String user = "s.stalone";
        var requestBuilder = MockMvcRequestBuilders.get("/catalogue/products/list")
                .queryParam("filter", "товар")
                .with(user(user).roles(role_manager));

        String body = """
                {"id":1,"title":"Товар 1","details":"details товара 1"},
                {"id":2,"title":"Товар 2","details":"details товара 2"}
                """;
        UrlPathPattern urlPattern = WireMock.urlPathMatching("/catalogue-api/products");
        WireMock.stubFor(WireMock.get(urlPattern)
                .withQueryParam("filter", WireMock.equalTo("товар"))
                .willReturn(WireMock.ok(body).withHeader(HttpHeaders.CONTENT_TYPE,
                        MediaType.APPLICATION_JSON_VALUE))
        );
        //when
        this.mockMvc.perform(requestBuilder)
        //then
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        view().name("catalogue/products/list"),
                        model().attribute("filter", "товар"),
                        model().attribute("products", List.of(
                                new Product(1, "Товар 1", "details товара 1"),
                                new Product(2, "Товар 2", "details товара 2")
                        ))
                );
        WireMock.verify(WireMock.getRequestedFor(urlPattern).withQueryParam("filter",WireMock.equalTo("товар")));

    }

    @Test
//    @WithMockUser(username = "s.stalone",roles = "MANAGER")
    void getNewProductPage_ReturnsProductPage() throws Exception {
        //given
        String role_manager = "MANAGER";
        String username = "s.stalone";
        var requestBuilder = MockMvcRequestBuilders.get("/catalogue/products/create")
                .with(user(username).roles(role_manager));
        //when
        this.mockMvc.perform(requestBuilder)

                //then
//                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        view().name("catalogue/products/new_product")
                );

    }
}
