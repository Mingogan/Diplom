package com.example.ServerSpring.controllers;

import com.example.ServerSpring.model.Orders.Orders;
import com.example.ServerSpring.model.Orders.OrdersDao;
import com.example.ServerSpring.model.Orders.OrdersRepository;
import com.example.ServerSpring.model.Table_Status.TablesStatusDao;
import com.example.ServerSpring.model.Tables.Tables;
import com.example.ServerSpring.model.Tables.TablesDao;
import com.example.ServerSpring.model.Users.Users;
import com.example.ServerSpring.model.Users.UsersDao;
import com.example.ServerSpring.model.orders_status.OrdersStatus;
import com.example.ServerSpring.model.orders_status.OrdersStatusDao;
import com.example.ServerSpring.utils.DateUtil;
import com.example.ServerSpring.webSocket.GenericWebSocketHandler;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@RestController
public class OrdersController {
    @Autowired
    private OrdersDao ordersDao;

    @Autowired
    private GenericWebSocketHandler webSocketHandler;

    @Autowired
    private OrdersStatusDao ordersStatusDao;

    @Autowired
    private TablesDao tablesDao;

    @Autowired
    private UsersDao usersDao;
    @Autowired
    private OrdersRepository ordersRepository;
    @Autowired
    private TablesStatusDao tablesStatusDao;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");


    @GetMapping("/orders/get-all")
    public List<Orders> getAllOrders(){
        System.out.println(ordersDao.getAllOrders().size() + "Вот такой размер");
        return ordersRepository.findOrdersByStatusNot(5);
    }

    @GetMapping("/orders/byDateRange")
    public List<Orders> getOrdersByDateRange(
            @RequestParam String startDate,
            @RequestParam String endDate) {
        try {
            Date start = dateFormat.parse(startDate);
            Date end = dateFormat.parse(endDate);
            return ordersDao.getOrdersByDateRange(start, end);
        } catch (ParseException e) {
            throw new RuntimeException("Invalid date format", e);
        }
    }

    @GetMapping("/orders/{userId}")
    public List<Orders> getOrdersForUser(@PathVariable int userId) {
        return ordersDao.getMyOrders(userId);
    }

    @GetMapping("/orders/status/get-all")
    public List<OrdersStatus> getAllTablesStatus(){
        return ordersStatusDao.getAllOrderStatus();
    }

    @GetMapping("/orders/{userId}/{tableId}")
    public ResponseEntity<List<Orders>> getOrders(@PathVariable int userId, @PathVariable int tableId) {
        List<Orders> orders = ordersDao.getOrdersByWaiterIdAndTableId(userId, tableId, 5);
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String jsonResponse = gson.toJson(orders);
        // Вывод в консоль
        System.out.println("OrderDetails for orderId " + ": " + jsonResponse);
        return ResponseEntity.ok(orders);
    }

    @PostMapping("/orders")
    public ResponseEntity<Orders> saveOrder(@RequestParam int tableId, @RequestParam int waiterId) {
        Users waiter = usersDao.findById(waiterId);
        Tables table = tablesDao.findById(tableId);
        if (table.getStatus().getId() == 1){
            table.setStatus(tablesStatusDao.findById(2));
            table = tablesDao.tableUpdate(table);
        }
        OrdersStatus status = ordersStatusDao.findById(1);
        Orders order = new Orders();
        order.setWaiter(waiter);
        order.setTable(table);
        order.setStatus(status);
        order.setDateOfCreation(DateUtil.trimMilliseconds(new Date()));
        Orders saveOrder = ordersDao.save(order);
        Gson gson = new Gson();
        String saveOrderJsom = gson.toJson(saveOrder);
        webSocketHandler.broadcastMessage("ORDER_ADDED:" + saveOrderJsom);
        return ResponseEntity.ok(saveOrder);
    }

    @PutMapping("/orders/{id}")
    public ResponseEntity<Orders> updateTable(@PathVariable("id") int orderId, @RequestBody Orders orders) {

        orders.setId(orderId);
        Orders updatedTable = ordersDao.update(orders);
        Gson gson = new Gson();
        String tableJson = gson.toJson(updatedTable);

        if (updatedTable != null) {
            webSocketHandler.broadcastMessage("ORDER_UPDATED:" + tableJson);
            return ResponseEntity.ok(updatedTable);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/user/{tableId}")
    public ResponseEntity<Users> getWaiterByTableId(@PathVariable int tableId) {
        System.out.println("я тут ");
        List<Orders> orders = ordersRepository.findOrdersByTableIdAndStatusNot(tableId);
        Orders order = orders.get(0);
        if (order != null) {
            System.out.println(order.getWaiter().getId() + " " + order.getWaiter().getEmail());
            return ResponseEntity.ok(order.getWaiter());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/orders/status/{orderId}/{statusId}")
    public ResponseEntity<Void> updateOrderStatus(@PathVariable int orderId, @PathVariable int statusId) {
        Orders order = ordersDao.findById(orderId);
        if (order != null) {
            order.setStatus(ordersStatusDao.findById(statusId));
            order = ordersDao.save(order);
            Gson gson = new Gson();
            String orderJson = gson.toJson(order);
            webSocketHandler.broadcastMessage("DELETE_ORDER:" + orderJson);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/orders/{orderId}")
    public ResponseEntity<Void> deleteOrder(@PathVariable int orderId) {
        Orders order = ordersDao.findById(orderId);
        if (order != null) {
            ordersDao.delete(order.getId());
            Gson gson = new Gson();
            String orderJson = gson.toJson(order);
            webSocketHandler.broadcastMessage("DELETE_ORDER:" + orderJson);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

    private Date trimMilliseconds(Date timestamp) {
        long time = timestamp.getTime();
        return new Timestamp((time / 1000) * 1000);
    }


}
