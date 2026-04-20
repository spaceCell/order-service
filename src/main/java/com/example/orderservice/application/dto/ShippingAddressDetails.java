package ru.iprody.orderservice.application.dto;

public record ShippingAddressDetails(
        String street,
        String city,
        String postalCode,
        String country
) {
}
