package com.example.ServerSpring.model.Roles;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RolesRepository extends CrudRepository<Roles,Integer> {
}
