package com.theverygroup.products.controller;

import com.theverygroup.products.dto.Product;
import com.theverygroup.products.exception.ApiMsg;
import com.theverygroup.products.exception.ResourceNotFoundException;
import com.theverygroup.products.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/products")
public class ProductController {

    private final ProductRepository productRepository;

    @Autowired
    public ProductController(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    /**
     * @param type
     * @return All Products or products by type
     */
    @GetMapping
    public List<Product> findAll(@RequestParam(name = "byType", required = false) String type) {
        List<Product> products;
        if (StringUtils.hasLength(type)) {
            products = productRepository.findProductByType(type);
        } else {
            products = productRepository.findAll();
        }
        return products;
    }

    /**
     * @param product
     * @return save the product
     */
    @PostMapping
    public Product save(@RequestBody Product product) {
        return productRepository.save(product);
    }

    /**
     * @param id
     * @return return the product by id
     */
    @GetMapping("/{id}")
    public Product findProductById(@PathVariable(name = "id") String id) {
        return productRepository.findProductById(id).orElseThrow(() -> new ResourceNotFoundException("Product with id " + id + " not found!"));
    }

    /**
     * @param id
     * @return delete the product by id
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiMsg> deleteProductById(@PathVariable("id") String id) {
        productRepository.delete(id);
        return ResponseEntity.ok(new ApiMsg(HttpStatus.OK, "Product with id " + id + " has been deleted!"));
    }

}
