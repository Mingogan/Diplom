package com.example.ServerSpring.model.AuthData;


import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AuthDataRepository extends CrudRepository<AuthData,Integer> {
    Optional<AuthData> findByLogin(String login);
    Optional<AuthData> findByUserIdAndRoleId(int userId, int roleId);
    List<AuthData> findByUserId(int userId);
}
