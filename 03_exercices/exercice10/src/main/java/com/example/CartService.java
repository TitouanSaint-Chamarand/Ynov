package com.example;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CartService {
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;

    public CartService(OrderRepository orderRepository, ProductRepository productRepository) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
    }

    public CartOperationResult addProduct(String orderId, String productId) {
        Optional<Order> orderOptional = orderRepository.findById(orderId);
        if (orderOptional.isEmpty()) {
            return new CartOperationResult.Failure("Order not found");
        }

        if (productRepository.findById(productId).isEmpty()) {
            return new CartOperationResult.Failure("Product not found");
        }

        Order order = orderOptional.get();
        List<OrderItem> items = new ArrayList<>(order.items());

        Optional<OrderItem> existingItem = items.stream()
                .filter(item -> item.productId().equals(productId))
                .findFirst();

        if (existingItem.isPresent()) {
            OrderItem item = existingItem.get();
            items.set(items.indexOf(item), new OrderItem(productId, item.quantity() + 1));
        } else {
            items.add(new OrderItem(productId, 1));
        }

        orderRepository.save(new Order(orderId, List.copyOf(items)));
        return new CartOperationResult.Success("Product added to order");
    }

    public CartOperationResult removeProduct(String orderId, String productId) {
        Optional<Order> orderOptional = orderRepository.findById(orderId);
        if (orderOptional.isEmpty()) {
            return new CartOperationResult.Failure("Order not found");
        }

        Order order = orderOptional.get();
        List<OrderItem> items = new ArrayList<>(order.items());

        Optional<OrderItem> existingItem = items.stream()
                .filter(item -> item.productId().equals(productId))
                .findFirst();

        if (existingItem.isEmpty()) {
            return new CartOperationResult.Failure("Product not in order");
        }

        OrderItem item = existingItem.get();
        if (item.quantity() > 1) {
            items.set(items.indexOf(item), new OrderItem(productId, item.quantity() - 1));
        } else {
            items.remove(item);
        }

        orderRepository.save(new Order(orderId, List.copyOf(items)));
        return new CartOperationResult.Success("Product removed from order");
    }

    public CheckoutResult checkout(String orderId) {
        Optional<Order> orderOptional = orderRepository.findById(orderId);
        if (orderOptional.isEmpty()) {
            return new CheckoutResult.Failure("Order not found");
        }

        return new CheckoutResult.Success("Order confirmed");
    }

    public Optional<Integer> getProductQuantity(String orderId, String productId) {
        return orderRepository.findById(orderId)
                .flatMap(order -> order.items().stream()
                        .filter(item -> item.productId().equals(productId))
                        .findFirst()
                        .map(OrderItem::quantity));
    }
}
