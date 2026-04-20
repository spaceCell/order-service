package com.example.orderservice.domain.model;

import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "order_items")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @jakarta.persistence.Column(nullable = false)
    private String productName;

    @jakarta.persistence.Column(nullable = false)
    private Integer quantity;

    @Embedded
    private Money price;

    public OrderItem(String productName, Integer quantity, Money price) {
        changeProductName(productName);
        changeQuantity(quantity);
        changePrice(price);
    }

    public void changeProductName(String productName) {
        if (productName == null || productName.isBlank()) {
            throw new IllegalArgumentException("Product name must be provided");
        }
        this.productName = productName.trim();
    }

    public void changeQuantity(Integer quantity) {
        if (quantity == null || quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than zero");
        }
        this.quantity = quantity;
    }

    public void changePrice(Money price) {
        if (price == null) {
            throw new IllegalArgumentException("Price must be provided");
        }
        this.price = price;
    }
}
