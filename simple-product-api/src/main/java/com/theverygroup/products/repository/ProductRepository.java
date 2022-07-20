package com.theverygroup.products.repository;

import com.theverygroup.products.dto.Product;
import com.theverygroup.products.exception.ResourceExistsException;
import com.theverygroup.products.exception.ResourceNotFoundException;
import com.theverygroup.products.util.ProductDataUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class ProductRepository {

    private List<Product> products;

    @Autowired
    public ProductRepository() {
        products = ProductDataUtils.loadAll();
    }

    public List<Product> findAll() {
        return products;
    }

    public Product save(Product product) {
        if(findProductById(product.getId()).isPresent()) {
            throw new ResourceExistsException("Product with id " + product.getId() + " already exists");
        }
        products.add(product);
        return product;
    }

    public Optional<Product> findProductById(String id) {
        return products.stream().filter(p -> p.getId().equals(id)).findFirst();
    }

    public List<Product> findProductByType(String type) {
        return products.stream().filter(p -> p.getType().toString().equalsIgnoreCase(type)).collect(Collectors.toList());
    }

    public void delete(String id) {
        Product product = findProductById(id).orElseThrow(() -> new ResourceNotFoundException("Product with id " + id + " not found!"));
        products.remove(product);
    }
}