package com.example.springclient.model;

public class OrderDetails {

    private int id;
    private OrderDetailsStatus stroka;
    private Dishes dish;
    private Orders order;
    private int quantity;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public OrderDetailsStatus getStroka() {
        return stroka;
    }

    public void setStroka(OrderDetailsStatus stroka) {
        this.stroka = stroka;
    }

    public Dishes getDish() {
        return dish;
    }

    public void setDish(Dishes dish) {
        this.dish = dish;
    }

    public Orders getOrder() {
        return order;
    }

    public void setOrder(Orders order) {
        this.order = order;
    }

    public int getAmount() {
        return quantity;
    }

    public void setAmount(int amount) {
        this.quantity = amount;
    }
}
