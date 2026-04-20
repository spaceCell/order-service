package com.example.orderservice.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ShippingAddress {

    @Column(nullable = false)
    private String street;

    @Column(nullable = false)
    private String city;

    @Column(nullable = false)
    private String postalCode;

    @Column(nullable = false)
    private String country;

    public ShippingAddress(String street, String city, String postalCode, String country) {
        this.street = requireText(street, "Street must be provided");
        this.city = requireText(city, "City must be provided");
        this.postalCode = requireText(postalCode, "Postal code must be provided");
        this.country = requireText(country, "Country must be provided");
    }

    private String requireText(String value, String message) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(message);
        }
        return value.trim();
    }
}
