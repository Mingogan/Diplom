package com.example.ServerSpring.model.Dishes;

import com.example.ServerSpring.model.Categorys.Categories;
import jakarta.transaction.Transactional;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Transactional
public interface DishesRepository extends CrudRepository<Dishes,Integer> {
    List<Dishes> findByCategoryId(int categoryId);
}