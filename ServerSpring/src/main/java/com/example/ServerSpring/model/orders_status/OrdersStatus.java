package com.example.ServerSpring.model.orders_status;

import jakarta.persistence.*;

@Entity
@Table(name = "orders_status", schema = "public", catalog = "Diplom")
public class OrdersStatus {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id")
    private int id;
    @Basic
    @Column(name = "status_name")
    private String statusName;

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getStatusName() {
        return statusName;
    }

    public void setStatusName(String statusName) {
        this.statusName = statusName;
    }
}
