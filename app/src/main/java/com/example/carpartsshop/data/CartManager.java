package com.example.carpartsshop.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class CartManager {
    private static CartManager instance;
    private final Map<Part, Integer> items = new HashMap<>();

    private CartManager() { }

    public static synchronized CartManager getInstance() {
        if (instance == null) {
            instance = new CartManager();
        }
        return instance;
    }

    public void addToCart(Part part) {
        int count = items.containsKey(part) ? items.get(part) : 0;
        items.put(part, count + 1);
    }

    public void removeOne(Part part) {
        if (!items.containsKey(part)) return;
        int count = items.get(part) - 1;
        if (count <= 0) items.remove(part);
        else items.put(part, count);
    }

    public void removeAll(Part part) {
        items.remove(part);
    }

    public void clearCart() {
        items.clear();
    }

    public List<CartItem> getCartItems() {
        List<CartItem> list = new ArrayList<>();
        for (Map.Entry<Part,Integer> e : items.entrySet()) {
            list.add(new CartItem(e.getKey(), e.getValue()));
        }
        return list;
    }

    public double getTotalPrice() {
        double sum = 0;
        for (Map.Entry<Part,Integer> e : items.entrySet()) {
            sum += e.getKey().getPrice() * e.getValue();
        }
        return sum;
    }

    public static class CartItem {
        public final Part part;
        public final int quantity;
        public CartItem(Part part, int quantity) {
            this.part = part;
            this.quantity = quantity;
        }
    }
}
