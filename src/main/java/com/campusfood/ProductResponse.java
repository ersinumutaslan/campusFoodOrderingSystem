package com.campusfood;

import java.math.BigDecimal;

/** The JSON shape of a menu item returned by the API. */
public record ProductResponse(Long id, String name, BigDecimal price) {
}
