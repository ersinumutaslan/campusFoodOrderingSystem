package com.campusfood;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(RestaurantController.class)
class RestaurantControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RestaurantRepository restaurantRepository;

    @MockitoBean
    private ProductRepository productRepository;

    @Test
    void returnsRestaurantsAsJson() throws Exception {
        when(restaurantRepository.findAll()).thenReturn(List.of(
                new RestaurantResponse(1L, "Campus Grill"),
                new RestaurantResponse(2L, "Pizza Corner")));

        mockMvc.perform(get("/restaurants"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Campus Grill"))
                .andExpect(jsonPath("$[1].name").value("Pizza Corner"));
    }

    @Test
    void returnsMenuOfRestaurant() throws Exception {
        when(restaurantRepository.existsById(1L)).thenReturn(true);
        when(productRepository.findAvailableByRestaurantId(1L)).thenReturn(List.of(
                new ProductResponse(2L, "Ayran", new BigDecimal("20.00")),
                new ProductResponse(1L, "Chicken Wrap", new BigDecimal("85.50"))));

        mockMvc.perform(get("/restaurants/1/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[1].name").value("Chicken Wrap"))
                .andExpect(jsonPath("$[1].price").value(85.50));
    }

    @Test
    void unknownRestaurantReturns404() throws Exception {
        when(restaurantRepository.existsById(99L)).thenReturn(false);

        mockMvc.perform(get("/restaurants/99/products"))
                .andExpect(status().isNotFound());
    }
}
