package com.example.ServerSpring.model.orders_status;

import com.example.ServerSpring.model.Table_Status.TablesStatus;
import com.example.ServerSpring.model.Users.Users;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Streamable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class OrdersStatusDao {
    @Autowired
    private OrdersStatusRepository repository;

    public OrdersStatus findById(int id) {
        return repository.findById(id).orElse(null);
    }

    public OrdersStatus findByStatusName(String StatusName) {
        return repository.findByStatusName(StatusName).orElse(null);
    }
    public List<OrdersStatus> getAllOrderStatus(){
        List<OrdersStatus> ordersList = new ArrayList<>();
        Streamable.of(repository.findAll())
                .forEach(tablesStatus -> {
                    ordersList.add(tablesStatus);
                });
        return ordersList;
    }
}
