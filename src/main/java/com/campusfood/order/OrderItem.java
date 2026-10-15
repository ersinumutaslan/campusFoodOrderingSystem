package com.campusfood.order;

import java.math.BigDecimal;

import com.campusfood.restaurant.Product;

/** One line of an order: a product and how many of it. */
public class OrderItem {

    private final Product product;
    private final int quantity;
    private final BigDecimal unitPrice; // price when ordered; later price changes don't affect it

    public OrderItem(Product product, int quantity) {
        if (product == null) {
            throw new IllegalArgumentException("Product must not be null");
        }
        if (quantity < 1) {
            throw new IllegalArgumentException("Quantity must be at least 1");
        }
        this.product = product;
        this.quantity = quantity;
        this.unitPrice = product.getPrice();
    }

    public Product getProduct() {
        return product;
    }

    public int getQuantity() {
        return quantity;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public BigDecimal getLineTotal() {
        return unitPrice.multiply(BigDecimal.valueOf(quantity));
    }

    @Override
    public String toString() {
        return quantity + " x " + product.getName() + " = " + getLineTotal() + " TL";
    }
}
