package com.example;

import java.util.Optional;

public class OrderService {
    private final ProductRepository productRepository;
    private final CustomerRepository customerRepository;

    public OrderService(ProductRepository productRepository, CustomerRepository customerRepository) {
        this.productRepository = productRepository;
        this.customerRepository = customerRepository;
    }

    public OrderResult placeOrder(String customerEmail, String productReference, int quantity) {
        Optional<Product> productOptional = productRepository.findByReference(productReference);

        if (productOptional.isEmpty()) {
            return new OrderResult.Rejected("Unknown product");
        }

        Product product = productOptional.get();

        if (quantity > product.stock()) {
            return new OrderResult.Rejected("Insufficient stock");
        }

        ClientProfile profile = customerRepository.getProfileByEmail(customerEmail);
        double subtotal = product.unitPrice() * quantity;
        double discountRate = profile.getDiscountPercent() / 100.0;
        double totalAmount = subtotal * (1 - discountRate);

        OrderReceipt receipt = new OrderReceipt(
                product.reference(),
                quantity,
                totalAmount,
                "Order confirmed"
        );

        return new OrderResult.Accepted(receipt);
    }
}
