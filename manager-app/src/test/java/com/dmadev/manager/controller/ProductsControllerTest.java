package com.dmadev.manager.controller;

import com.dmadev.manager.BaseUnitTest;
import com.dmadev.manager.client.BadRequestException;
import com.dmadev.manager.client.ProductsRestClient;
import com.dmadev.manager.controller.payload.NewProductPayload;
import com.dmadev.manager.entity.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.ui.ConcurrentModel;

import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;


@DisplayName("Unit test ProductsController")
class ProductsControllerTest extends BaseUnitTest {

    @Mock
    ProductsRestClient productsRestClient= Mockito.mock(ProductsRestClient.class);

    @InjectMocks
    ProductsController controller;


    /** название состоит из
     *  Имя метода_условие при котором тестируется метод_ожидаемый результат
     */
    @Test
    @DisplayName("createProduct to create new product  and redirect to product page")
    void createProduct_RequestIsValid_ReturnsRedirectionToProductPage() {
        //todo добавить в сервис проверку на пробелы перед и после текста- complete

        //given
        String title = "Новый товар";
        String details = "Описание нового товара";
        var newProductPayload = new NewProductPayload(title, details);
        var concurrentModel = new ConcurrentModel();

        doReturn(new Product(1,title,details))
                .when(this.productsRestClient)
                .createProduct(eq(title),eq(details));
            //        .createProduct(eq(title),eq(details));
            //        .createProduct(notNull(),any());

        //when
        var result = this.controller.createProduct(newProductPayload, concurrentModel);

        //then
        assertEquals("redirect:/catalogue/products/1",result);
        verify(this.productsRestClient).createProduct(title,details);
        verifyNoMoreInteractions(this.productsRestClient);
    }

    @Test
    @DisplayName("createProduct return page with errors, if request not valid ")
    void createProduct_RequestIsInvalid_ReturnsProductsFormWithError() {
        //given
        String title = "";
        String details ="" ;
        var newProductPayload = new NewProductPayload(title, details);
        var concurrentModel = new ConcurrentModel();
        String error1 = "Error 1";
        String error2 = "Error 2";
        doThrow(new BadRequestException(List.of(error1, error2))).when(this.productsRestClient)
                .createProduct(title,details);

        //when
        var result = this.controller.createProduct(newProductPayload, concurrentModel);

        //then
        assertEquals("catalogue/products/new_product",result);
        assertEquals(newProductPayload,concurrentModel.getAttribute("payload"));
        assertEquals(List.of(error1,error2),concurrentModel.getAttribute("errors"));

        verify(this.productsRestClient).createProduct(title,details);
        verifyNoMoreInteractions(this.productsRestClient);
    }
}