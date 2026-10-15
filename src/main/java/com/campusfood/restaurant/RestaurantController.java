package com.campusfood.restaurant;

import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

/** REST endpoints for restaurants. */
@RestController
public class RestaurantController {

    private final RestaurantRepository restaurantRepository;
    private final ProductRepository productRepository;

    public RestaurantController(RestaurantRepository restaurantRepository, ProductRepository productRepository) {
        this.restaurantRepository = restaurantRepository;
        this.productRepository = productRepository;
    }

    @GetMapping("/restaurants")
    public List<RestaurantResponse> getRestaurants() {
        return restaurantRepository.findAll(Sort.by("id")).stream()
                .map(RestaurantResponse::from)
                .toList();
    }

    @GetMapping("/restaurants/{id}/products")
    public List<ProductResponse> getMenu(@PathVariable long id) {
        if (!restaurantRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Restaurant " + id + " not found");
        }
        return productRepository.findByRestaurantIdAndAvailableTrueOrderByNameAsc(id).stream()
                .map(ProductResponse::from)
                .toList();
    }
}
