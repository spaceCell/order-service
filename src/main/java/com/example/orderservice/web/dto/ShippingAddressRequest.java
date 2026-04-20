package com.example.orderservice.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Адрес доставки в запросе")
public class ShippingAddressRequest {

    private String street;
    private String city;
    private String postalCode;
    private String country;
}
