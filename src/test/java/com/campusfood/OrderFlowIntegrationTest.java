package com.campusfood;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.everyItem;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
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
import org.springframework.test.web.servlet.request.RequestPostProcessor;
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

    /** Sample users from the Flyway migrations (V6 + V7), logging in for real with email + password. */
    private static final RequestPostProcessor STUDENT = httpBasic("ayse@campus.edu", "campus123");
    private static final RequestPostProcessor STAFF = httpBasic("mehmet@campus.edu", "campus123");
    private static final RequestPostProcessor ALI = httpBasic("ali@campus.edu", "campus123");

    /** Ali's order from V8 (V6 created orders 1-3). */
    private static final long ALIS_ORDER_ID = 4;

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
                  "restaurantId": 1,
                  "items": [ { "productId": 1, "quantity": 2 }, { "productId": 2, "quantity": 1 } ]
                }
                """;

        String created = mockMvc.perform(post("/orders").with(STUDENT).contentType(MediaType.APPLICATION_JSON).content(orderJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.total").value(191.00))
                .andReturn().getResponse().getContentAsString();
        int id = JsonPath.read(created, "$.id");

        mockMvc.perform(get("/orders/" + id).with(STUDENT))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customer").value("Ayşe Yılmaz"))
                .andExpect(jsonPath("$.status").value("PLACED"))
                .andExpect(jsonPath("$.items.length()").value(2))
                .andExpect(jsonPath("$.total").value(191.00));

        mockMvc.perform(patch("/orders/" + id + "/status").with(STAFF)
                        .contentType(MediaType.APPLICATION_JSON).content("{ \"status\": \"PREPARING\" }"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/orders/" + id).with(STUDENT))
                .andExpect(jsonPath("$.status").value("PREPARING"));
    }

    @Test
    void rejectedOrderIsNotSaved() throws Exception {
        long ordersBefore = orderRepository.count();
        String soldOutSoup = """
                { "restaurantId": 1, "items": [ { "productId": 3, "quantity": 1 } ] }
                """;

        mockMvc.perform(post("/orders").with(STUDENT).contentType(MediaType.APPLICATION_JSON).content(soldOutSoup))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").value("Lentil Soup is not available"));

        assertEquals(ordersBefore, orderRepository.count());
    }

    @Test
    void productFromAnotherRestaurantIsRejected() throws Exception {
        String pizzaAtGrill = """
                { "restaurantId": 1, "items": [ { "productId": 4, "quantity": 1 } ] }
                """;

        mockMvc.perform(post("/orders").with(STUDENT).contentType(MediaType.APPLICATION_JSON).content(pizzaAtGrill))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").value("Margherita is not on the menu of Campus Grill"));
    }

    @Test
    void wrongPasswordIsRejected() throws Exception {
        mockMvc.perform(get("/orders/1").with(httpBasic("ayse@campus.edu", "wrong-password")))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void studentCannotMoveOrdersThroughTheKitchen() throws Exception {
        mockMvc.perform(patch("/orders/1/status").with(STUDENT)
                        .contentType(MediaType.APPLICATION_JSON).content("{ \"status\": \"PREPARING\" }"))
                .andExpect(status().isForbidden());
    }

    @Test
    void studentCannotOpenSomeoneElsesOrder() throws Exception {
        mockMvc.perform(get("/orders/" + ALIS_ORDER_ID).with(STUDENT))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.detail").value("You can only see your own orders"));
    }

    @Test
    void staffCanOpenAnyOrder() throws Exception {
        mockMvc.perform(get("/orders/" + ALIS_ORDER_ID).with(STAFF))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customer").value("Ali Kaya"));
    }

    @Test
    void myOrdersShowsOnlyTheLoggedInStudentsOrders() throws Exception {
        mockMvc.perform(get("/orders/mine").with(ALI))
                .andExpect(status().isOk())
                // Other tests may add orders for Ali too, so check that every order is his, not an exact count
                .andExpect(jsonPath("$.length()").value(greaterThanOrEqualTo(1)))
                .andExpect(jsonPath("$[*].customer").value(everyItem(equalTo("Ali Kaya"))));
    }

    @Test
    void orderIsPlacedForTheLoggedInStudent() throws Exception {
        String json = """
                { "restaurantId": 2, "items": [ { "productId": 4, "quantity": 1 } ] }
                """;

        mockMvc.perform(post("/orders").with(ALI).contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.customer").value("Ali Kaya"));
    }
}
