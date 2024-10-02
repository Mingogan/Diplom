package com.example.ServerSpring.controllers;

import com.example.ServerSpring.model.Dishes.Dishes;
import com.example.ServerSpring.model.Dishes.DishesDao;

import com.example.ServerSpring.model.OrderDetailStatus.OrderDetailsStatus;
import com.example.ServerSpring.model.OrderDetailStatus.OrderDetailsStatusDao;
import com.example.ServerSpring.model.OrderDetails.OrderDetails;
import com.example.ServerSpring.model.OrderDetails.OrderDetailsDao;
import com.example.ServerSpring.model.Orders.Orders;
import com.example.ServerSpring.model.Orders.OrdersDao;
import com.example.ServerSpring.webSocket.GenericWebSocketHandler;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class OrdersDelailsController {

    @Autowired
    private OrderDetailsStatusDao orderDetailsStatusDao;
    @Autowired
    private OrderDetailsDao orderDetailsDao;
    @Autowired
    private DishesDao dishesDao;
    @Autowired
    private OrdersDao ordersDao;
    @Autowired
    private GenericWebSocketHandler webSocketHandler;


    @GetMapping("/ordersDetails/{orderId}")
    public List<OrderDetails> getOrderDetails(@PathVariable int orderId) {
        List<OrderDetails> orderDetailsList = orderDetailsDao.findByOrderId(orderId);
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String jsonResponse = gson.toJson(orderDetailsList);

        //System.out.println("OrderDetails for orderId " + orderId + ": " + jsonResponse);

        for (OrderDetails orderDetail : orderDetailsList) {
            int orderName = orderDetail.getId();// Предполагая, что есть метод getName() в Orders
            String dishName = orderDetail.getDish().getName();    // Метод getName() в Dishes
            String statusDish = orderDetail.getStroka().getStatusName();      // Метод getStatusDish() в OrderDetails

            System.out.println("Order Name: " + orderName + ", Dish Name: " + dishName + ", Status Dish: " + statusDish);
        }
        return orderDetailsList;
    }

    @GetMapping("dishes/{categoryId}")
    public List<Dishes> getDishesByCategoryId(@PathVariable int categoryId) {
        return dishesDao.getDishesByCategoryId(categoryId);
    }

    @PostMapping("/ordersDetails")
    public OrderDetails addOrderDetail(@RequestParam int orderId, @RequestParam int dishId, @RequestParam int quantity, @RequestParam String status) {
        Orders order = ordersDao.findById(orderId);
        Dishes dish = dishesDao.findById(dishId);
        OrderDetailsStatus orderDetailsStatus = orderDetailsStatusDao.findByStatusName(status);

        OrderDetails existingOrderDetail = orderDetailsDao.findByOrderAndDishAndStatus(order, dish, orderDetailsStatus);
        Gson gson = new Gson();
        if (existingOrderDetail != null) {
            existingOrderDetail.setQuantity(existingOrderDetail.getQuantity() + 1);
            existingOrderDetail = orderDetailsDao.save(existingOrderDetail);
            String existingOrderDetailJson = gson.toJson(existingOrderDetail);
            webSocketHandler.broadcastMessage("ORDER_DETAILS_UPDATED:" + existingOrderDetailJson);
            return existingOrderDetail;
        } else {
            OrderDetails orderDetails = new OrderDetails();
            orderDetails.setOrder(order);
            orderDetails.setDish(dish);
            orderDetails.setQuantity(quantity);
            orderDetails.setStoka(orderDetailsStatus);
            orderDetails = orderDetailsDao.save(orderDetails);
            String orderDetailsJson = gson.toJson(orderDetails);
            webSocketHandler.broadcastMessage("ORDER_DETAILS_ADDED:" + orderDetailsJson);
            return orderDetails;
        }

    }

    @PutMapping("/order/orderdetails/update")
    public OrderDetails updateOrderDetails(@RequestBody OrderDetails orderDetails) {
        OrderDetails updateOrderDetails = orderDetailsDao.save(orderDetails);
        Gson gson = new Gson();
        String orderDetailsJson = gson.toJson(orderDetails);
        webSocketHandler.broadcastMessage("ORDER_DETAILS_UPDATED:" + orderDetailsJson);
        return updateOrderDetails;
    }

    @DeleteMapping("/order/orderdetails/delete/{id}")
    public void deleteOrderDetails(@PathVariable int id) {
        OrderDetails deleteOrderDetails = orderDetailsDao.findById(id);
        Gson gson = new Gson();
        String dishJson = gson.toJson(deleteOrderDetails);
        orderDetailsDao.deleteById(id);
        webSocketHandler.broadcastMessage("DISH_DELETED:" + dishJson);
    }
}
