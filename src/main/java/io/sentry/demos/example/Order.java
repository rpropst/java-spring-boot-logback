package io.sentry.demos.example;

import java.util.List;

import com.google.gson.Gson;

public class Order {
    private List<Item> cart;
    private String email;
    
    public Order() {}

    public Order(List<Item> cart, String email) {
        this.cart = cart;
        this.email = email;
    }

    public List<Item> getCart() {
        return cart;
    }

    public String getEmail() {
        return email;
    }

    public String toJson() {
        Gson gson = new Gson();
        return gson.toJson(cart);
    }
}
