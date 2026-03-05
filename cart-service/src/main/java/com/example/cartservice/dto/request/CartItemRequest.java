package com.example.cartservice.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartItemRequest {

    @NotBlank(message = "productId không được để trống")
    private String productId;

    @Min(value = 1, message = "Số lượng phải >= 1")
    private int quantity;
}