package com.example.springclient.model;

import java.sql.Timestamp;
import java.util.Date;

public class Orders {
    private int id;
    private Date dateOfCreation;
    private OrdersStatus status;
    private Users waiter;
    private Tables table;

    private double totalCost;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


    public Date getDateOfCreation() {
        return dateOfCreation;
    }

    public void setDateOfCreation(Timestamp dateOfCreation) {
        this.dateOfCreation = dateOfCreation;
    }

    public OrdersStatus getStatus() {
        return status;
    }

    public void setStatus(OrdersStatus status) {
        this.status = status;
    }

    public Users getWaiter() {
        return waiter;
    }

    public void setWaiter(Users waiter) {
        this.waiter = waiter;
    }

    public Tables getTable() {
        return table;
    }

    public void setTable(Tables table) {
        this.table = table;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Orders that = (Orders) o;

        if (id != that.id) return false;

        if (!dateOfCreation.equals(that.dateOfCreation)) return false;
        if (!status.equals(that.status)) return false;
        if (!waiter.equals(that.waiter)) return false;
        return table.equals(that.table);
    }

    public double getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(double totalCost) {
        this.totalCost = totalCost;
    }
}
