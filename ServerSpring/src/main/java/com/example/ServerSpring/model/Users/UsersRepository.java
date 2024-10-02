package com.example.ServerSpring.model.Users;

import com.example.ServerSpring.model.AuthData.AuthData;
import com.example.ServerSpring.model.Tables.Tables;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsersRepository extends CrudRepository<Users,Integer> {

}
