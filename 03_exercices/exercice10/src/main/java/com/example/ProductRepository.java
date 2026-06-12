package com.example;

import java.util.List;
import java.util.Optional;

public interface ProductRepository {
    Optional<Product> findById(String id);

    List<Product> searchByKeyword(String keyword);

    List<Product> findByCategory(String category);

    List<Product> findByMaxPrice(double maxPrice);
}
