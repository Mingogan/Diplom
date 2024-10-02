package com.example.ServerSpring.model.Roles;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Streamable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class RolesDao {
    @Autowired
    private RolesRepository repository;

    public void save(Roles role){
        repository.save(role);
    }
    public void delete(Roles role){
        repository.delete(role);
    }

    public List<Roles> getAllRoles(){
        List<Roles> rolesList = new ArrayList<>();
        Streamable.of(repository.findAll())
                .forEach(role -> {
                    rolesList.add(role);
                });
        return rolesList;
    }
}
