package com.example.ServerSpring.controllers;

import com.example.ServerSpring.model.Categorys.Categories;
import com.example.ServerSpring.model.Dishes.Dishes;
import com.example.ServerSpring.model.Dishes.DishesDao;
import com.example.ServerSpring.webSocket.GenericWebSocketHandler;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@RestController
public class DishesController {

    @Autowired
    private DishesDao dishesDao;
    @Autowired
    private GenericWebSocketHandler webSocketHandler;

    @GetMapping("/dishes/get-all")
    public List<Dishes> getAllDishes(){
        return dishesDao.getAllDishes();
    }

    @PostMapping("/dishes")
    public Dishes save(@RequestBody Dishes dish) {
        Dishes savedDish = dishesDao.save(dish);
        Gson gson = new Gson();
        String dishJson = gson.toJson(savedDish);
        webSocketHandler.broadcastMessage("DISH_ADDED:" + dishJson);
        return savedDish;
    }

    @DeleteMapping("/dishes/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable("id") int dishId) {
        try {
            Dishes dish = dishesDao.findById(dishId);
            System.out.println();
            Gson gson = new Gson();
            String dishJson = gson.toJson(dish);
            dishesDao.delete(dishId);
            System.out.println(dishJson);
            webSocketHandler.broadcastMessage("DISH_DELETED:" + dishJson);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/dishes/{id}")
    public ResponseEntity<Dishes> updateCategory(@PathVariable("id") int dishId, @RequestBody Dishes dish) {

        dish.setId(dishId);

        Dishes updatedDish = dishesDao.update(dish);
        Gson gson = new Gson();
        String dishJson = gson.toJson(updatedDish);

        if (updatedDish != null) {
            webSocketHandler.broadcastMessage("DISH_UPDATED:" + dishJson);
            return ResponseEntity.ok(updatedDish);
        } else {
            System.out.println("no");
            return ResponseEntity.notFound().build();
        }
    }
}
