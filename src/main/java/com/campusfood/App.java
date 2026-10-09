package com.campusfood;

public class App {
    public static void main(String[] args) throws Exception {
        OrderStatus status = OrderStatus.READY;

        System.out.println("Status: " + status);
        System.out.println("Is ready? " + (status == OrderStatus.READY));
        System.out.println("From text: " + OrderStatus.valueOf("PREPARING"));

        System.out.println("All statuses:");
        for (OrderStatus s : OrderStatus.values()) {
            System.out.println("  " + s);
        }
    }
}
