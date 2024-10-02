package com.example.ServerSpring.model.OrderDetailStatus;

import com.example.ServerSpring.model.orders_status.OrdersStatus;
import com.example.ServerSpring.model.orders_status.OrdersStatusRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Streamable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class OrderDetailsStatusDao {

    @Autowired
    private OrdersDetailsStatusRepository repository;

    public OrderDetailsStatus findById(int id) {
        return repository.findById(id).orElse(null);
    }

    public OrderDetailsStatus findByStatusName(String StatusName) {
        return repository.findByStatusName(StatusName).orElse(null);
    }
    public List<OrderDetailsStatus> getAllOrderStatus(){
        List<OrderDetailsStatus> ordersList = new ArrayList<>();
        Streamable.of(repository.findAll())
                .forEach(tablesStatus -> {
                    ordersList.add(tablesStatus);
                });
        return ordersList;
    }
}
