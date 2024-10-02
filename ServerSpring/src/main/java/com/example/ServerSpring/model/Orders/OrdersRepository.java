package com.example.ServerSpring.model.Orders;

import com.example.ServerSpring.model.Tables.Tables;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

@Repository
public interface OrdersRepository extends CrudRepository<Orders,Integer> {
    List<Orders> findByWaiterIdAndTableIdAndStatusIdNot(int waiterId, int tableId, int statusId);

    @Query("SELECT o FROM Orders o WHERE o.table.id = :tableId AND o.status.id != 5")
    List<Orders> findOrdersByTableIdAndStatusNot(@Param("tableId") int tableId);

    List<Orders> findByWaiterId(int waiterID);
    @Query("SELECT o FROM Orders o WHERE o.status.id != 5")
    List<Orders> findOrdersByStatusNot(int statusId);

    @Query("SELECT o FROM Orders o WHERE o.dateOfCreation BETWEEN :startDate AND :endDate")
    List<Orders> findOrdersByDateRange(@Param("startDate") Date startDate, @Param("endDate") Date endDate);

}
