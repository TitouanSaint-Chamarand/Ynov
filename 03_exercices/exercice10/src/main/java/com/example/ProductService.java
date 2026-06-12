package com.example;

import java.util.List;

public class ProductService {
    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public List<Product> searchByKeyword(String keyword) {
        return productRepository.searchByKeyword(keyword);
    }

    public List<Product> findByCategory(String category) {
        return productRepository.findByCategory(category);
    }

    public List<Product> searchByMaxPrice(double maxPrice) {
        return productRepository.findByMaxPrice(maxPrice);
    }
}
