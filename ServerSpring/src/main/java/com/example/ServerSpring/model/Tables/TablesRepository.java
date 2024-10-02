package com.example.ServerSpring.model.Tables;

import com.example.ServerSpring.model.Roles.Roles;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TablesRepository extends CrudRepository<Tables,Integer> {
}