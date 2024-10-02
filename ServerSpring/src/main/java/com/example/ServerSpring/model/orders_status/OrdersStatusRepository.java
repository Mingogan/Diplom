package com.example.ServerSpring.model.orders_status;

import com.example.ServerSpring.model.AuthData.AuthData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrdersStatusRepository extends JpaRepository<OrdersStatus, Integer> {
    Optional<OrdersStatus> findByStatusName(String statusName);
}

