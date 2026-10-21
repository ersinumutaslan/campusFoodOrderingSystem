package com.campusfood;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.campusfood.order.OrderRepository;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

/**
 * Runs the whole application (controllers, services, JPA, Flyway) against a real PostgreSQL
 * that Testcontainers starts in Docker for this test class. Needs Docker Desktop running.
 */
@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
class OrderFlowIntegrationTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer postgres = new PostgreSQLContainer("postgres:18");

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private OrderRepository orderRepository;

    @Test
    void restaurantsComeFromFlywaySampleData() throws Exception {
        mockMvc.perform(get("/restaurants"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(3))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Campus Grill"));
    }

    @Test
    void menuHidesSoldOutProducts() throws Exception {
        mockMvc.perform(get("/restaurants/1/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value("Ayran"))
                .andExpect(jsonPath("$[1].name").value("Chicken Wrap"))
                .andExpect(jsonPath("$[1].price").value(85.50));
    }

    @Test
    void placeOrderReadItBackAndMoveItThroughTheKitchen() throws Exception {
        String orderJson = """
                {
                  "customerId": 1,
                  "restaurantId": 1,
                  "items": [ { "productId": 1, "quantity": 2 }, { "productId": 2, "quantity": 1 } ]
                }
                """;

        String created = mockMvc.perform(post("/orders").contentType(MediaType.APPLICATION_JSON).content(orderJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.total").value(191.00))
                .andReturn().getResponse().getContentAsString();
        int id = JsonPath.read(created, "$.id");

        mockMvc.perform(get("/orders/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customer").value("Ayşe Yılmaz"))
                .andExpect(jsonPath("$.status").value("PLACED"))
                .andExpect(jsonPath("$.items.length()").value(2))
                .andExpect(jsonPath("$.total").value(191.00));

        mockMvc.perform(patch("/orders/" + id + "/status")
                        .contentType(MediaType.APPLICATION_JSON).content("{ \"status\": \"PREPARING\" }"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/orders/" + id))
                .andExpect(jsonPath("$.status").value("PREPARING"));
    }

    @Test
    void rejectedOrderIsNotSaved() throws Exception {
        long ordersBefore = orderRepository.count();
        String soldOutSoup = """
                { "customerId": 1, "restaurantId": 1, "items": [ { "productId": 3, "quantity": 1 } ] }
                """;

        mockMvc.perform(post("/orders").contentType(MediaType.APPLICATION_JSON).content(soldOutSoup))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").value("Lentil Soup is not available"));

        assertEquals(ordersBefore, orderRepository.count());
    }

    @Test
    void productFromAnotherRestaurantIsRejected() throws Exception {
        String pizzaAtGrill = """
                { "customerId": 1, "restaurantId": 1, "items": [ { "productId": 4, "quantity": 1 } ] }
                """;

        mockMvc.perform(post("/orders").contentType(MediaType.APPLICATION_JSON).content(pizzaAtGrill))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").value("Margherita is not on the menu of Campus Grill"));
    }
}
