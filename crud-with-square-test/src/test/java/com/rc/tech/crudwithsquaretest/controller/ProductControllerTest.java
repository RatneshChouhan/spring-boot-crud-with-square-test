package com.rc.tech.crudwithsquaretest.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rc.tech.crudwithsquaretest.entity.Product;
import com.rc.tech.crudwithsquaretest.service.ProductService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@WebMvcTest(ProductController.class)
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService mockProductService;

    @Test
    void testGetAllProducts() throws Exception {
        // Setup
        // Configure ProductService.getAllProducts(...).
        final List<Product> products = List.of(
                new Product(1L, "Product 1", 10L, "description 1", 10.0),
                new Product(2L, "Product 2", 20L, "description 2", 20.0)
        );

        // Run the test
        when(mockProductService.getAllProducts()).thenReturn(products);

        // Verify the results
        mockMvc.perform(get("/products").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].name", is("Product 1")))
                .andExpect(jsonPath("$[0].quantity", is(10)))
                .andExpect(jsonPath("$[0].description", is("description 1")))
                .andExpect(jsonPath("$[0].price", is(10.0)))
                .andExpect(jsonPath("$[1].id", is(2)))
                .andExpect(jsonPath("$[1].name", is("Product 2")))
                .andExpect(jsonPath("$[1].quantity", is(20)))
                .andExpect(jsonPath("$[1].description", is("description 2")))
                .andExpect(jsonPath("$[1].price", is(20.0)));
    }

    @Test
    void testGetAllProducts_ProductServiceReturnsNoItems() throws Exception {
        // Setup
        when(mockProductService.getAllProducts()).thenReturn(Collections.emptyList());

        // Run the test
        final MockHttpServletResponse response = mockMvc.perform(get("/products")
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // Verify the results
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getContentAsString()).isEqualTo("[]");
    }

    @Test
    void testGetProductById() throws Exception {
        // Setup
        // Configure ProductService.getProductById(...).
        final Product product = new Product(1L, "Product-1", 10L, "Product-1-description", 10.0);

        // Run the test
        when(mockProductService.getProductById(1L)).thenReturn(product);

        // Verify the results
        mockMvc.perform(get("/products/{id}", 1).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Product-1")))
                .andExpect(jsonPath("$.description", is("Product-1-description")))
                .andExpect(jsonPath("$.quantity", is(10)))
                .andExpect(jsonPath("$.price", is(10.0)));
    }

    @Test
    void testCreateProduct() throws Exception {
        // Setup
        // Configure ProductService.createProduct(...).
        final Product product = new Product(1L, "Test Product", 10L, "Test Description", 10.0);
        when(mockProductService.createProduct(new Product(1L, "Test Product", 10L, "Test Description", 10.0)))
                .thenReturn(product);

        String productJson = new ObjectMapper().writeValueAsString(product);

        // Run the test
        mockMvc.perform(post("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(productJson))
                //                .content("{\"id\": \"1\",\"name\": \"Test Product\",\"quantity\": \"10\",\"description\": \"Test Description\",\"price\": \"10.0\"}"))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Test Product")))
                .andExpect(jsonPath("$.quantity", is(10)))
                .andExpect(jsonPath("$.description", is("Test Description")))
                .andExpect(jsonPath("$.price", is(10.0)));
    }

    @Test
    void testUpdateProduct() throws Exception {
        // Setup
        // Configure ProductService.updateProduct(...).
        final Product product = new Product(1L, "Test Product", 10L, "Test Description", 10.0);

        String productJson = new ObjectMapper().writeValueAsString(product);

        // Run the test
        when(mockProductService.updateProduct(1L, product)).thenReturn(product);

        // verify the result
        mockMvc.perform(put("/products/{id}", 1).contentType(MediaType.APPLICATION_JSON).content(productJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Test Product")))
                .andExpect(jsonPath("$.quantity", is(10)))
                .andExpect(jsonPath("$.description", is("Test Description")))
                .andExpect(jsonPath("$.price", is(10.0)));
    }

    @Test
    void testDeleteProduct() throws Exception {
        // Setup the mock service's response
        when(mockProductService.deleteProduct(anyLong())).thenReturn(true);

        // Perform the DELETE request and verify the response
        mockMvc.perform(delete("/products/{id}", 1L))
                .andExpect(status().isOk());

        // Verify that the mock service was called with the correct argument
        ArgumentCaptor<Long> idCaptor = ArgumentCaptor.forClass(Long.class);
        verify(mockProductService).deleteProduct(idCaptor.capture());
        assertThat(idCaptor.getValue()).isEqualTo(1L);
    }
}
