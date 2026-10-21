package com.campusfood.order;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.campusfood.restaurant.Product;
import com.campusfood.restaurant.ProductRepository;
import com.campusfood.restaurant.Restaurant;
import com.campusfood.restaurant.RestaurantRepository;
import com.campusfood.user.Role;
import com.campusfood.user.User;
import com.campusfood.user.UserRepository;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock private OrderRepository orderRepository;
    @Mock private UserRepository userRepository;
    @Mock private RestaurantRepository restaurantRepository;
    @Mock private ProductRepository productRepository;

    private OrderService orderService;
    private User student;
    private Restaurant grill;
    private Product wrap;
    private Product ayran;

    @BeforeEach
    void setUp() {
        orderService = new OrderService(orderRepository, userRepository, restaurantRepository, productRepository);
        student = new User("Ayşe Yılmaz", "ayse@campus.edu", Role.STUDENT);
        grill = new Restaurant("Campus Grill");
        wrap = new Product("Chicken Wrap", new BigDecimal("85.50"));
        ayran = new Product("Ayran", new BigDecimal("20.00"));
        grill.addProduct(wrap);
        grill.addProduct(ayran);
    }

    @Test
    void placesOrderAndReturnsTotal() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(student));
        when(restaurantRepository.findById(1L)).thenReturn(Optional.of(grill));
        when(productRepository.findById(1L)).thenReturn(Optional.of(wrap));
        when(productRepository.findById(2L)).thenReturn(Optional.of(ayran));
        when(orderRepository.save(any(Order.class))).thenAnswer(call -> call.getArgument(0));

        OrderResponse response = orderService.placeOrder(new OrderRequest(1L, 1L, List.of(
                new OrderItemRequest(1L, 2),
                new OrderItemRequest(2L, 1))));

        assertEquals(new BigDecimal("191.00"), response.total());
        assertEquals(OrderStatus.PLACED, response.status());
        assertEquals("Ayşe Yılmaz", response.customer());
        assertEquals(2, response.items().size());
    }

    @Test
    void productFromAnotherRestaurantIsRejectedWith400() {
        Product pizza = new Product("Margherita", new BigDecimal("150.00"));
        when(userRepository.findById(1L)).thenReturn(Optional.of(student));
        when(restaurantRepository.findById(1L)).thenReturn(Optional.of(grill));
        when(productRepository.findById(4L)).thenReturn(Optional.of(pizza));

        ResponseStatusException e = assertThrows(ResponseStatusException.class, () ->
                orderService.placeOrder(new OrderRequest(1L, 1L, List.of(new OrderItemRequest(4L, 1)))));

        assertEquals(HttpStatus.BAD_REQUEST, e.getStatusCode());
        verify(orderRepository, never()).save(any());
    }

    @Test
    void unknownCustomerIsRejectedWith404() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        ResponseStatusException e = assertThrows(ResponseStatusException.class, () ->
                orderService.placeOrder(new OrderRequest(99L, 1L, List.of(new OrderItemRequest(1L, 1)))));

        assertEquals(HttpStatus.NOT_FOUND, e.getStatusCode());
    }

    @Test
    void getOrderReturnsTheOrder() {
        Order order = new Order(student, grill);
        order.addItem(new OrderItem(wrap, 2));
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        OrderResponse response = orderService.getOrder(1L);

        assertEquals("Campus Grill", response.restaurant());
        assertEquals(new BigDecimal("171.00"), response.total());
    }

    @Test
    void getUnknownOrderIsRejectedWith404() {
        when(orderRepository.findById(99L)).thenReturn(Optional.empty());

        ResponseStatusException e = assertThrows(ResponseStatusException.class, () -> orderService.getOrder(99L));

        assertEquals(HttpStatus.NOT_FOUND, e.getStatusCode());
    }

    @Test
    void emptyOrderIsRejectedWith400() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(student));
        when(restaurantRepository.findById(1L)).thenReturn(Optional.of(grill));

        ResponseStatusException e = assertThrows(ResponseStatusException.class, () ->
                orderService.placeOrder(new OrderRequest(1L, 1L, List.of())));

        assertEquals(HttpStatus.BAD_REQUEST, e.getStatusCode());
    }
}
