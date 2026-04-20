package ru.iprody.orderservice.domain.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "orders")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Order {

    private static final String DEFAULT_CURRENCY = "RUB";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long customerId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private OrderStatus status;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Embedded
    private ShippingAddress shippingAddress;

    @Embedded
    private Money totalAmount;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "order_id", nullable = false)
    private List<OrderItem> items = new ArrayList<>();

    public Order(Long customerId, OrderStatus status, ShippingAddress shippingAddress, List<OrderItem> items) {
        changeCustomer(customerId);
        changeStatus(status);
        changeShippingAddress(shippingAddress);
        replaceItems(items);
    }

    public void update(Long customerId, OrderStatus status, ShippingAddress shippingAddress, List<OrderItem> items) {
        changeCustomer(customerId);
        changeStatus(status);
        changeShippingAddress(shippingAddress);
        replaceItems(items);
    }

    public void changeCustomer(Long customerId) {
        if (customerId == null || customerId <= 0) {
            throw new IllegalArgumentException("Customer id must be greater than zero");
        }
        this.customerId = customerId;
    }

    public void changeStatus(OrderStatus status) {
        this.status = status == null ? OrderStatus.NEW : status;
    }

    public void changeShippingAddress(ShippingAddress shippingAddress) {
        if (shippingAddress == null) {
            throw new IllegalArgumentException("Shipping address must be provided");
        }
        this.shippingAddress = shippingAddress;
    }

    public void replaceItems(List<OrderItem> newItems) {
        items.clear();
        if (newItems != null) {
            items.addAll(newItems);
        }
        recalculateTotal();
    }

    @PrePersist
    public void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (status == null) {
            status = OrderStatus.NEW;
        }
        if (totalAmount == null) {
            recalculateTotal();
        }
    }

    @PreUpdate
    public void onUpdate() {
        recalculateTotal();
    }

    private void recalculateTotal() {
        if (items.isEmpty()) {
            totalAmount = Money.zero(DEFAULT_CURRENCY);
            return;
        }

        Money sum = Money.zero(items.get(0).getPrice().getCurrency());
        for (OrderItem item : items) {
            sum = sum.add(item.getPrice().multiply(item.getQuantity()));
        }
        totalAmount = sum;
    }
}
