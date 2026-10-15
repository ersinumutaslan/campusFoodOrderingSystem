package com.campusfood.restaurant;

/** The JSON shape of a restaurant returned by the API. */
public record RestaurantResponse(Long id, String name) {

    static RestaurantResponse from(Restaurant restaurant) {
        return new RestaurantResponse(restaurant.getId(), restaurant.getName());
    }
}
