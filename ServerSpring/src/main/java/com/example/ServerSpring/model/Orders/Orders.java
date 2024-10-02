package com.example.ServerSpring.model.Orders;

import com.example.ServerSpring.model.Tables.Tables;
import com.example.ServerSpring.model.Users.Users;
import com.example.ServerSpring.model.orders_status.OrdersStatus;
import jakarta.persistence.*;

import java.sql.Timestamp;
import java.util.Date;

@Entity
@Table(name = "orders", schema = "public", catalog = "Diplom")
public class Orders {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id")
    private int id;

    @Basic
    @Column(name = "date_of_creation")

    private Date dateOfCreation;

    @ManyToOne
    @JoinColumn(name = "status_id", referencedColumnName = "id")
    private OrdersStatus status;

    @ManyToOne
    @JoinColumn(name = "waiter_id", referencedColumnName = "id")
    private Users waiter;

    @ManyToOne
    @JoinColumn(name = "table_id", referencedColumnName = "id")
    private Tables table;

    // Getters and Setters

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Date getDateOfCreation() {
        return dateOfCreation;
    }

    public void setDateOfCreation(Date dateOfCreation) {
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

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + dateOfCreation.hashCode();
        result = 31 * result + status.hashCode();
        result = 31 * result + waiter.hashCode();
        result = 31 * result + table.hashCode();
        return result;
    }

}
