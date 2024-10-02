package com.example.ServerSpring.model.AuthData;

import com.example.ServerSpring.model.Dishes.Dishes;
import com.example.ServerSpring.model.Roles.Roles;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AuthDataDao {

    @Autowired
    private AuthDataRepository repository;

    public AuthData saveAuthData(AuthData authData){
        return repository.save(authData);
    }
    public void deleteAuthData(AuthData authData){
        repository.delete(authData);
    }

    public AuthData update(AuthData authData) {
        if(repository.existsById(authData.getId())) {
            return repository.save(authData);
        } else {
            throw new EntityNotFoundException("Dish not found");
        }
    }
    public String generateUniqueLogin(String baseLogin) {
        String login = baseLogin;
        int suffix = 1;
        System.out.println(baseLogin);
        while (repository.findByLogin(login).isPresent()) {
            login = baseLogin + suffix;
            suffix++;
        }
        System.out.println(login);
        return login;
    }

    public List<Roles> findRolesByUserId(int userId) {
        List<AuthData> authDataList = repository.findByUserId(userId);
        return authDataList.stream().map(AuthData::getRole).distinct().toList();
    }
    public Optional<AuthData> findByUserIdAndRoleId(int userId, int roleId) {
        return repository.findByUserIdAndRoleId(userId, roleId);
    }

    public boolean deleteRoleFromUser(int userId, int roleId) {
        Optional<AuthData> authDataOptional = repository.findByUserIdAndRoleId(userId, roleId);
        if (authDataOptional.isPresent()) {
            repository.delete(authDataOptional.get());
            return true;
        }
        return false;
    }

    public Optional<AuthData> findByLogin(String login){
        return repository.findByLogin(login);
    }
}
