package com.example.ServerSpring.model.OrderDetailStatus;

import com.example.ServerSpring.model.orders_status.OrdersStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrdersDetailsStatusRepository extends JpaRepository<OrderDetailsStatus, Integer> {
    Optional<OrderDetailsStatus> findByStatusName(String statusName);
}
