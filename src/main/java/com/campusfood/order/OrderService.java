package com.campusfood.order;

import com.campusfood.restaurant.Product;
import com.campusfood.restaurant.ProductRepository;
import com.campusfood.restaurant.Restaurant;
import com.campusfood.restaurant.RestaurantRepository;
import com.campusfood.user.User;
import com.campusfood.user.UserRepository;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

/** Places orders: loads the data, applies the domain rules and saves everything in one transaction. */
@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final RestaurantRepository restaurantRepository;
    private final ProductRepository productRepository;

    public OrderService(OrderRepository orderRepository, UserRepository userRepository,
                        RestaurantRepository restaurantRepository, ProductRepository productRepository) {
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
        this.restaurantRepository = restaurantRepository;
        this.productRepository = productRepository;
    }

    /** Places an order for the logged-in user (identified by their login email). */
    @Transactional
    public OrderResponse placeOrder(String customerEmail, OrderRequest request) {
        User customer = userRepository.findByEmail(customerEmail)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User " + customerEmail + " not found"));
        Restaurant restaurant = restaurantRepository.findById(request.restaurantId())
                .orElseThrow(() -> notFound("Restaurant", request.restaurantId()));

        if (request.items() == null || request.items().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Order must contain at least one item");
        }

        Order order = new Order(customer, restaurant);
        for (OrderItemRequest itemRequest : request.items()) {
            Product product = productRepository.findById(itemRequest.productId())
                    .orElseThrow(() -> notFound("Product", itemRequest.productId()));
            try {
                order.addItem(new OrderItem(product, itemRequest.quantity()));
            } catch (IllegalArgumentException e) {
                // A domain rule was broken (not on the menu, sold out, quantity < 1)
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
            }
        }

        Order saved = orderRepository.save(order);
        return OrderResponse.from(saved);
    }

    /** One order. Students may only see their own; staff and admins may see any (canSeeAllOrders). */
    @Transactional(readOnly = true)
    public OrderResponse getOrder(long id, String viewerEmail, boolean canSeeAllOrders) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> notFound("Order", id));
        if (!canSeeAllOrders && !order.getCustomer().getEmail().equals(viewerEmail)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only see your own orders");
        }
        return OrderResponse.from(order);
    }

    /** All orders of the logged-in user, newest first. */
    @Transactional(readOnly = true)
    public List<OrderResponse> getMyOrders(String customerEmail) {
        return orderRepository.findByCustomerEmailOrderByCreatedAtDesc(customerEmail).stream()
                .map(OrderResponse::from)
                .toList();
    }

    @Transactional
    public OrderResponse updateStatus(long id, OrderStatus newStatus) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> notFound("Order", id));
        try {
            order.changeStatus(newStatus);
        } catch (IllegalStateException e) {
            // The change is not allowed from the order's current status
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        }
        // No save() needed: JPA writes changes to loaded entities when the transaction commits
        return OrderResponse.from(order);
    }

    private static ResponseStatusException notFound(String what, Long id) {
        return new ResponseStatusException(HttpStatus.NOT_FOUND, what + " " + id + " not found");
    }
}
