package com.campusfood.order;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import com.campusfood.common.SecurityConfig;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;

@WebMvcTest(OrderController.class)
@Import(SecurityConfig.class)
class OrderControllerTest {

    private static final String ORDER_JSON = """
            {
              "customerId": 1,
              "restaurantId": 1,
              "items": [ { "productId": 1, "quantity": 2 } ]
            }
            """;

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private OrderService orderService;

    @Test
    @WithMockUser(roles = "STUDENT")
    void placingAnOrderReturns201WithTheOrder() throws Exception {
        when(orderService.placeOrder(any())).thenReturn(new OrderResponse(
                4L, "Ayşe Yılmaz", "Campus Grill", OrderStatus.PLACED,
                List.of(new OrderItemResponse("Chicken Wrap", 2, new BigDecimal("85.50"), new BigDecimal("171.00"))),
                new BigDecimal("171.00"), Instant.now()));

        mockMvc.perform(post("/orders").contentType(MediaType.APPLICATION_JSON).content(ORDER_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(4))
                .andExpect(jsonPath("$.status").value("PLACED"))
                .andExpect(jsonPath("$.items[0].product").value("Chicken Wrap"))
                .andExpect(jsonPath("$.total").value(171.00));
    }

    @Test
    @WithMockUser(roles = "STUDENT")
    void missingCustomerIdReturns400() throws Exception {
        String json = """
                { "restaurantId": 1, "items": [ { "productId": 1, "quantity": 2 } ] }
                """;

        mockMvc.perform(post("/orders").contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").value("Validation failed"))
                .andExpect(jsonPath("$.errors.customerId").value("must not be null"));
        verify(orderService, never()).placeOrder(any());
    }

    @Test
    @WithMockUser(roles = "STUDENT")
    void zeroQuantityReturns400() throws Exception {
        String json = """
                { "customerId": 1, "restaurantId": 1, "items": [ { "productId": 1, "quantity": 0 } ] }
                """;

        mockMvc.perform(post("/orders").contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors['items[0].quantity']").value("must be at least 1"));
        verify(orderService, never()).placeOrder(any());
    }

    @Test
    @WithMockUser(roles = "STUDENT")
    void getOrderReturns200WithTheOrder() throws Exception {
        when(orderService.getOrder(1L)).thenReturn(new OrderResponse(
                1L, "Ayşe Yılmaz", "Campus Grill", OrderStatus.READY,
                List.of(new OrderItemResponse("Chicken Wrap", 2, new BigDecimal("85.50"), new BigDecimal("171.00"))),
                new BigDecimal("171.00"), Instant.now()));

        mockMvc.perform(get("/orders/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.status").value("READY"));
    }

    @Test
    @WithMockUser(roles = "STUDENT")
    void getUnknownOrderReturns404() throws Exception {
        when(orderService.getOrder(99L))
                .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Order 99 not found"));

        mockMvc.perform(get("/orders/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "STAFF")
    void patchStatusReturns200WithTheUpdatedOrder() throws Exception {
        when(orderService.updateStatus(1L, OrderStatus.PREPARING)).thenReturn(new OrderResponse(
                1L, "Ayşe Yılmaz", "Campus Grill", OrderStatus.PREPARING,
                List.of(), BigDecimal.ZERO, Instant.now()));

        mockMvc.perform(patch("/orders/1/status").contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"status\": \"PREPARING\" }"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("PREPARING"));
    }

    @Test
    @WithMockUser(roles = "STAFF")
    void unknownStatusValueReturns400() throws Exception {
        mockMvc.perform(patch("/orders/1/status").contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"status\": \"DELIVERED\" }"))
                .andExpect(status().isBadRequest());
        verify(orderService, never()).updateStatus(anyLong(), any());
    }

    @Test
    @WithMockUser(roles = "STAFF")
    void notAllowedStatusChangeReturns409() throws Exception {
        when(orderService.updateStatus(1L, OrderStatus.COMPLETED)).thenThrow(new ResponseStatusException(
                HttpStatus.CONFLICT, "Cannot change order status from PLACED to COMPLETED"));

        mockMvc.perform(patch("/orders/1/status").contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"status\": \"COMPLETED\" }"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.title").value("Conflict"))
                .andExpect(jsonPath("$.detail").value("Cannot change order status from PLACED to COMPLETED"));
    }

    @Test
    @WithMockUser(roles = "STUDENT")
    void unknownRestaurantReturns404() throws Exception {
        when(orderService.placeOrder(any()))
                .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Restaurant 1 not found"));

        mockMvc.perform(post("/orders").contentType(MediaType.APPLICATION_JSON).content(ORDER_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void placingAnOrderWithoutLoginReturns401() throws Exception {
        mockMvc.perform(post("/orders").contentType(MediaType.APPLICATION_JSON).content(ORDER_JSON))
                .andExpect(status().isUnauthorized());
        verify(orderService, never()).placeOrder(any());
    }

    @Test
    @WithMockUser(roles = "STAFF")
    void staffCannotPlaceOrders() throws Exception {
        mockMvc.perform(post("/orders").contentType(MediaType.APPLICATION_JSON).content(ORDER_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "STUDENT")
    void studentCannotChangeOrderStatus() throws Exception {
        mockMvc.perform(patch("/orders/1/status").contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"status\": \"COMPLETED\" }"))
                .andExpect(status().isForbidden());
        verify(orderService, never()).updateStatus(anyLong(), any());
    }
}
