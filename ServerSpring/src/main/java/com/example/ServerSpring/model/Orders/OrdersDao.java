package com.example.ServerSpring.model.Orders;

import com.example.ServerSpring.model.Dishes.Dishes;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Streamable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class OrdersDao {
    @Autowired
    private OrdersRepository repository;

    public Orders save(Orders orders){
        return repository.save(orders);
    }
    public <Optional> Orders findById(int id) {
        return repository.findById(id).orElse(null);
    }
    public void delete(int ordersId) {
        if(repository.existsById(ordersId)) {
            repository.deleteById(ordersId);
        } else {
            throw new EntityNotFoundException("Dish not found");
        }
    }

    public Orders update(Orders orders) {
        if(repository.existsById(orders.getId())) {
            return repository.save(orders);
        } else {
            throw new EntityNotFoundException("Dish not found");
        }
    }

    public List<Orders> getAllOrders(){
        List<Orders> ordersList = new ArrayList<>();
        Streamable.of(repository.findAll())
                .forEach(orders -> {
                    ordersList.add(orders);
                });
        return ordersList;
    }
    public List<Orders> getOrdersByWaiterIdAndTableId(int waiterId, int tableId, int statusId) {
        return repository.findByWaiterIdAndTableIdAndStatusIdNot(waiterId, tableId, statusId);
    }
    public List<Orders> getMyOrders(int waiterId){
        return  repository.findByWaiterId(waiterId);
    }

    public List<Orders> getOrdersByDateRange(Date startDate, Date endDate) {
        return repository.findOrdersByDateRange(startDate, endDate);
    }
}