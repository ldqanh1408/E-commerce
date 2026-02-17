package com.example.productservice.coreapi.queries;

import lombok.Data;
import lombok.Value;

@Value
@Data
public class GetProductDetailQuery {
    String productId;
}
