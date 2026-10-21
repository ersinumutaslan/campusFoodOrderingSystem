package com.campusfood.order;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/** REST endpoints for orders. */
@RestController
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/orders")
    @ResponseStatus(HttpStatus.CREATED)
    public OrderResponse placeOrder(@Valid @RequestBody OrderRequest request) {
        return orderService.placeOrder(request);
    }
}
