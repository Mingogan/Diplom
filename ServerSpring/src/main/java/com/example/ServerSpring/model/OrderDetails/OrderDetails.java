package com.example.ServerSpring.model.OrderDetails;

import com.example.ServerSpring.model.Dishes.Dishes;
import com.example.ServerSpring.model.OrderDetailStatus.OrderDetailsStatus;
import com.example.ServerSpring.model.Orders.Orders;
import jakarta.persistence.*;

@Entity
@Table(name = "order_details", schema = "public", catalog = "Diplom")
public class OrderDetails {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id")
    private int id;
    @ManyToOne
    @JoinColumn(name = "status_id", referencedColumnName = "id", nullable = false)
    private OrderDetailsStatus stroka;
    @Basic
    @Column(name = "quantity", nullable = false)
    private int quantity;
    @ManyToOne
    @JoinColumn(name = "order_id", referencedColumnName = "id", nullable = false)
    private Orders order;

    @ManyToOne
    @JoinColumn(name = "dish_id", referencedColumnName = "id", nullable = false)
    private Dishes dish;




    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Orders getOrder() {
        return order;
    }

    public void setOrder(Orders order) {
        this.order = order;
    }

    public Dishes getDish() {
        return dish;
    }

    public void setDish(Dishes dish) {
        this.dish = dish;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public OrderDetailsStatus getStroka() {
        return stroka;
    }

    public void setStoka(OrderDetailsStatus status) {
        this.stroka = status;
    }


}
