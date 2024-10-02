package com.example.ServerSpring.model.Dishes;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Streamable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Service
public class DishesDao {
    @Autowired
    private DishesRepository dishesRepository;

    @Transactional
    public List<Dishes> getAllDishes() {
        return Streamable.of(dishesRepository.findAll()).toList();
    }

    @Transactional
    public Dishes save(Dishes dish){
        return dishesRepository.save(dish);
    }


    public Dishes findById(int id) {
        return dishesRepository.findById(id).orElse(null);
    }
    public void delete(int dishId) {
        if(dishesRepository.existsById(dishId)) {
            dishesRepository.deleteById(dishId);
        } else {
            throw new EntityNotFoundException("Dish not found");
        }
    }


    public Dishes update(Dishes dish) {
        if(dishesRepository.existsById(dish.getId())) {
            return dishesRepository.save(dish);
        } else {
            throw new EntityNotFoundException("Dish not found");
        }
    }

    public List<Dishes> getDishesByCategoryId(int categoryId) {
        return dishesRepository.findByCategoryId(categoryId);
    }
}
