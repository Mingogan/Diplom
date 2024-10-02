package com.example.ServerSpring.model.OrderDetails;

import com.example.ServerSpring.model.Dishes.Dishes;

import com.example.ServerSpring.model.OrderDetailStatus.OrderDetailsStatus;
import com.example.ServerSpring.model.Orders.Orders;
import com.example.ServerSpring.model.Table_Status.TablesStatus;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Streamable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class OrderDetailsDao {
    @Autowired
    private OrderDetailsRepository orderDetailsRepository;

    public List<OrderDetails> getAllTablesStatus(){
        List<OrderDetails> tablesList = new ArrayList<>();
        Streamable.of(orderDetailsRepository.findAll())
                .forEach(tablesStatus -> {
                    tablesList.add(tablesStatus);
                });
        return tablesList;
    }

    public void deleteById(int dishId) {
        if(orderDetailsRepository.existsById(dishId)) {
            orderDetailsRepository.deleteById(dishId);
        } else {
            throw new EntityNotFoundException("Dish not found");
        }
    }

    public OrderDetails update(OrderDetails orderDetails) {
        if(orderDetailsRepository.existsById(orderDetails.getId())) {
            return orderDetailsRepository.save(orderDetails);
        } else {
            throw new EntityNotFoundException("Dish not found");
        }
    }

    public List<OrderDetails> findByOrderId(int orderId){
        return orderDetailsRepository.getOrderDetailsByOrderId(orderId);
    }

    public OrderDetails save(OrderDetails orderDetails){
        return orderDetailsRepository.save(orderDetails);
    }

    public OrderDetails findByOrderAndDishAndStatus(Orders order, Dishes dish, OrderDetailsStatus statusDish){
        return orderDetailsRepository.findByOrderAndDishAndStroka(order,dish,statusDish);
    }

    public OrderDetails findById(int id) {
        return orderDetailsRepository.findById(id).orElse(null);
    }
}