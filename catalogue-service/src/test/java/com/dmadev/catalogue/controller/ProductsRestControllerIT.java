package com.dmadev.catalogue.controller;

import com.dmadev.catalogue.BaseIntegrationTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Locale;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ProductsRestControllerIT extends BaseIntegrationTest {


    @Test
    @DisplayName("findProducts must return ProductList")
    @Sql(value = "/sql/products.sql")
    void findProducts_ReturnsProductList() throws Exception {
        //given
        var requestBuilder = MockMvcRequestBuilders.get("/catalogue-api/products")
                .param("filter", "товар")
                .with(jwt().jwt(builder -> builder.claim("scope", "view_catalogue")));
        //when
        String jsonContent = """
                [
                  {"id": 1,"title": "Товар №1","details": "Описание товара №1"},
                  {"id": 3,"title": "Товар №3","details": "Описание товара №3"}
                ]""";
        this.mockMvc.perform(requestBuilder)
                //then
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON),
                        content().json(jsonContent)
                );
    }

    @Test
    @DisplayName("createProducts must return new product")
    void createProduct_ReturnsNewProduct() throws Exception {
        //given
        String jsonContentWithoutId = """
                {
                "title":"Еще один новый товар",
                 "details": "Описание того самого товара"}""";
        String jsonContentWithId = """
                               {"id":1,
                "title":"Еще один новый товар",
                "details": "Описание того самого товара"}""";
        var requestBuilder = MockMvcRequestBuilders.post("/catalogue-api/products/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonContentWithoutId)
                .with(jwt().jwt(builder -> builder.claim("scope", "edit_catalogue")));

        //when
        this.mockMvc.perform(requestBuilder)

                //then
                .andDo(print())
                .andExpectAll(
                        status().isCreated(),
                        header().string(HttpHeaders.LOCATION, "http://localhost/catalogue-api/products/1"),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON),
                        content().json(jsonContentWithId)
                );
    }

    @Test
    void createProduct_RequestIsInvalid_ReturnsProblemDetail() throws Exception {
        //given
        String jsonContentWithoutId = """
                {
                "title":"  ",
                 "details": ""}""";
        String jsonContentErrors = """
                                               {"errors":[
                               "Заголовок товара должен быть от 3 до 50 символов"
                               ]
                }""";
        var requestBuilder = MockMvcRequestBuilders.post("/catalogue-api/products/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonContentWithoutId)
                .locale(new Locale("ru", "RU"))
                .with(jwt().jwt(builder -> builder.claim("scope", "edit_catalogue")));

        //when
        this.mockMvc.perform(requestBuilder)

                //then
                .andDo(print())
                .andExpectAll(
                        status().isBadRequest(),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON),
                        content().json(jsonContentErrors, false)
                );
    }

    @Test
    void createProduct_UserIsNotAuthorized_ReturnsForbidden() throws Exception {
        //given
        String jsonContentWithoutId = """
                  {
                  "title":"mockTitle",
                   "details": "mockDetails"}
                """;

        var requestBuilder = MockMvcRequestBuilders.post("/catalogue-api/products/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonContentWithoutId)
                .locale(new Locale("ru", "RU"))
                .with(jwt().jwt(builder -> builder.claim("scope", "view_catalogue")));

        //when
        this.mockMvc.perform(requestBuilder)

                //then
                .andDo(print())
                .andExpectAll(
                        status().isForbidden()
                );
    }

}