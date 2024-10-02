package com.example.ServerSpring.controllers;

import com.example.ServerSpring.model.Categorys.Categories;
import com.example.ServerSpring.model.Categorys.CategoriesDao;
import com.example.ServerSpring.model.Dishes.Dishes;
import com.example.ServerSpring.model.Roles.Roles;
import com.example.ServerSpring.webSocket.GenericWebSocketHandler;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class CategoriesController {
    @Autowired
    private CategoriesDao categoriesDao;

    @Autowired
    private GenericWebSocketHandler webSocketHandler;

    @GetMapping("/categories/get-all")
    public List<Categories> getAllCategorys(){
        return categoriesDao.getAllCategorys();
    }

    @PostMapping("/categories")
    public Categories save (@RequestBody Categories category){
        Categories categoriesSave = categoriesDao.save(category);
        Gson gson = new Gson();
        String categoryJson = gson.toJson(categoriesSave);
        webSocketHandler.broadcastMessage("CATEGORY_ADDED:" + categoryJson);
        return categoriesSave;
    }

    @DeleteMapping("/categories/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable("id") int categoryId) {
        try {
            Categories category = categoriesDao.findById(categoryId);
            Gson gson = new Gson();
            String categoryJson = gson.toJson(category);
            categoriesDao.delete(categoryId);
            System.out.println("delete");
            webSocketHandler.broadcastMessage("CATEGORY_DELETED:" + categoryJson);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    @PutMapping("/categories/{id}")
    public ResponseEntity<Categories> updateCategory(@PathVariable("id") int categoryId, @RequestBody Categories category) {
        category.setId(categoryId);
        Categories updatedCategory = categoriesDao.update(category);
        Gson gson = new Gson();
        String categoryJson = gson.toJson(updatedCategory);

        if (updatedCategory != null) {
            webSocketHandler.broadcastMessage("CATEGORY_UPDATED:" + categoryJson);
            return ResponseEntity.ok(updatedCategory);
        } else {
            System.out.println("no");
            return ResponseEntity.notFound().build();
        }
    }
}
