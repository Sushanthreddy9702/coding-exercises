package com.theverygroup.products.controller;

import com.theverygroup.products.dto.Price;
import com.theverygroup.products.dto.Product;
import com.theverygroup.products.dto.Type;
import com.theverygroup.products.exception.ResourceExistsException;
import com.theverygroup.products.exception.ResourceNotFoundException;
import com.theverygroup.products.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class ProductControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductRepository productRepository;

    Price price;
    Product product;

    @BeforeEach
    public void setUp() {
        price = Price.builder()
                .value(new BigDecimal("18.99"))
                .currency("GBP")
                .build();
        product = Product.builder()
                .id("CLN-CDE-BOOK")
                .name("Clean Code")
                .description("Clean Code: A Handbook of Agile Software Craftsmanship (Robert C. Martin)")
                .price(price)
                .type(Type.BOOKS)
                .department("Books and Stationery")
                .weight("220g")
                .build();

        when(productRepository.findAll()).thenReturn(Arrays.asList(product));
    }

    @Test
    public void testFindAll() throws Exception {
        String expected = "[{\"id\":\"CLN-CDE-BOOK\"," +
                "\"name\":\"Clean Code\"," +
                "\"description\":\"Clean Code: A Handbook of Agile Software Craftsmanship (Robert C. Martin)\"," +
                "\"price\":{\"value\":18.99,\"currency\":\"GBP\"}," +
                "\"type\":\"Book\"," +
                "\"department\":\"Books and Stationery\"," +
                "\"weight\":\"220g\"}]";

        mockMvc.perform(get("/products"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(expected));
    }

    /**
     * Test method to test /products?byType endpoint which returns products based on type
     * */
    @Test
    public void testFindByType() throws Exception {
        when(productRepository.findProductByType(anyString())).thenReturn(Arrays.asList(product));
        mockMvc.perform(get("/products").queryParam("byType", "Book"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").exists())
                .andExpect(jsonPath("$[0].type").exists())
                .andExpect(jsonPath("$[0].type").value("Book"));
    }

    /**
     * Test method to test /save endpoint which saves product
     * */
    @Test
    public void testSave() throws Exception {
        String request = "{\"id\":\"CLN-CDE-BOOK\"," +
                "\"name\":\"Clean Code\"," +
                "\"description\":\"Clean Code: A Handbook of Agile Software Craftsmanship (Robert C. Martin)\"," +
                "\"price\":{\"value\":18.99,\"currency\":\"GBP\"}," +
                "\"type\":\"Book\"," +
                "\"department\":\"Books and Stationery\"," +
                "\"weight\":\"220g\"}";

        when(productRepository.save(any(Product.class))).thenReturn(product);

        mockMvc.perform(post("/products").accept(MediaType.APPLICATION_JSON).content(request).contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
    }

    /**
     * Test method to test /save endpoint when it does not save product
     * since the product with the same id already exists
     * */
    @Test
    public void testSave_BadRequest() throws Exception {
        String request = "{\"id\":\"CLN-CDE-BOOK\"," +
                "\"name\":\"Clean Code\"," +
                "\"description\":\"Clean Code: A Handbook of Agile Software Craftsmanship (Robert C. Martin)\"," +
                "\"price\":{\"value\":18.99,\"currency\":\"GBP\"}," +
                "\"type\":\"Book\"," +
                "\"department\":\"Books and Stationery\"," +
                "\"weight\":\"220g\"}";

        doThrow(ResourceExistsException.class).when(productRepository).save(any(Product.class));

        mockMvc.perform(post("/products").accept(MediaType.APPLICATION_JSON).content(request).contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    /**
     * Test method to test /products/{id} endpoint which return product by id
     * */
    @Test
    public void testFindProductById() throws Exception {
        when(productRepository.findProductById(anyString())).thenReturn(Optional.ofNullable(product));
        mockMvc.perform(get("/products/" + "CLN-CDE-BOOK"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.id").value("CLN-CDE-BOOK"));
    }

    /**
     * Test method to test /products/{id} endpoint which does not return any product
     * since there is no product available by the id provided in request
     * */
    @Test
    public void testFindProductById_ProductNotFound() throws Exception {
        doThrow(ResourceNotFoundException.class).when(productRepository).findProductById(anyString());
        mockMvc.perform(get("/products/" + "CLN-CDE-BOOK"))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    /**
     * Test method to test /products endpoint which delete product by id
     * */
    @Test
    public void testDelete() throws Exception {
        doNothing().when(productRepository).delete(anyString());
        mockMvc.perform(delete("/products/" + "CLN-CDE-BOOK"))
                .andDo(print())
                .andExpect(status().isOk());
    }

    /**
     * Test method to test /products/{id} endpoint which does not delete any product
     * since there is no product available by the id provided in request
     * */
    @Test
    public void testDelete_ProductNotFound() throws Exception {
        doThrow(ResourceNotFoundException.class).when(productRepository).delete(anyString());
        mockMvc.perform(delete("/products/" + "CLN-CDE-BOOK"))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

}