package com.example.ServerSpring.model.Categorys;

import com.example.ServerSpring.model.Dishes.Dishes;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Streamable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CategoriesDao {
    @Autowired
    private CategoriesRepository repository;

    public Categories save(Categories category){
        return repository.save(category);
    }

    public void delete(int categoryId) {
        if(repository.existsById(categoryId)) {
            repository.deleteById(categoryId);
        } else {
            throw new EntityNotFoundException("Category not found");
        }
    }
    public Categories findById(int id) {
        return repository.findById(id).orElse(null);
    }
    public Categories update(Categories category) {
        if(repository.existsById(category.getId())) {
            return repository.save(category);
        } else {
            throw new EntityNotFoundException("Category not found");
        }
    }

    public List<Categories> getAllCategorys(){
        List<Categories> categoryList = new ArrayList<>();
        Streamable.of(repository.findAll())
                .forEach(category -> {
                    categoryList.add(category);
                });
        return categoryList;
    }
}
