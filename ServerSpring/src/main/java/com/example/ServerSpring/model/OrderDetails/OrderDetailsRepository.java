package com.example.ServerSpring.model.OrderDetails;

import com.example.ServerSpring.model.Dishes.Dishes;
import com.example.ServerSpring.model.OrderDetailStatus.OrderDetailsStatus;
import com.example.ServerSpring.model.Orders.Orders;
import com.example.ServerSpring.model.Table_Status.TablesStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository

public interface OrderDetailsRepository extends JpaRepository<OrderDetails, Integer> {
    List<OrderDetails> getOrderDetailsByOrderId(int orderId);
    OrderDetails findByOrderAndDishAndStroka(Orders order, Dishes dish, OrderDetailsStatus statusDish);
}