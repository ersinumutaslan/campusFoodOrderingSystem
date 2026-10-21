package com.campusfood.restaurant;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import com.campusfood.common.SecurityConfig;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(RestaurantController.class)
@Import(SecurityConfig.class)
class RestaurantControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RestaurantRepository restaurantRepository;

    @MockitoBean
    private ProductRepository productRepository;

    @Test
    void returnsRestaurantsAsJson() throws Exception {
        when(restaurantRepository.findAll(any(Sort.class))).thenReturn(List.of(
                new Restaurant("Campus Grill"),
                new Restaurant("Pizza Corner")));

        mockMvc.perform(get("/restaurants"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value("Campus Grill"))
                .andExpect(jsonPath("$[1].name").value("Pizza Corner"));
    }

    @Test
    void returnsMenuOfRestaurant() throws Exception {
        when(restaurantRepository.existsById(1L)).thenReturn(true);
        when(productRepository.findByRestaurantIdAndAvailableTrueOrderByNameAsc(1L)).thenReturn(List.of(
                new Product("Ayran", new BigDecimal("20.00")),
                new Product("Chicken Wrap", new BigDecimal("85.50"))));

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
