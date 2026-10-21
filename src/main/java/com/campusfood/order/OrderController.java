package com.campusfood.order;

import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/** REST endpoints for orders. "authentication" is the logged-in user, filled in by Spring Security. */
@RestController
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/orders")
    @ResponseStatus(HttpStatus.CREATED)
    public OrderResponse placeOrder(@Valid @RequestBody OrderRequest request, Authentication authentication) {
        return orderService.placeOrder(authentication.getName(), request);
    }

    @GetMapping("/orders/mine")
    public List<OrderResponse> getMyOrders(Authentication authentication) {
        return orderService.getMyOrders(authentication.getName());
    }

    @GetMapping("/orders/{id}")
    public OrderResponse getOrder(@PathVariable long id, Authentication authentication) {
        return orderService.getOrder(id, authentication.getName(), canSeeAllOrders(authentication));
    }

    @PatchMapping("/orders/{id}/status")
    public OrderResponse updateStatus(@PathVariable long id, @Valid @RequestBody StatusUpdateRequest request) {
        return orderService.updateStatus(id, request.status());
    }

    /** Staff and admins may look at any order; students only at their own. */
    private static boolean canSeeAllOrders(Authentication authentication) {
        return authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_STAFF") || a.getAuthority().equals("ROLE_ADMIN"));
    }
}
