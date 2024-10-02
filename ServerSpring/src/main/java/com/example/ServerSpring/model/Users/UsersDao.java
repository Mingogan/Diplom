package com.example.ServerSpring.model.Users;

import com.example.ServerSpring.model.Tables.Tables;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Streamable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


@Service
public class UsersDao {
    @Autowired
    private UsersRepository userRepository;

    public Users userSave(Users tables){
        return userRepository.save(tables);
    }

    public Users findById(int id) {
        return userRepository.findById(id).orElse(null);
    }

    public void userDelete(int userId) {
        if(userRepository.existsById(userId)) {
            userRepository.deleteById(userId);
        } else {
            throw new EntityNotFoundException("User not found");
        }
    }
    public List<Users> getAllUsers(){
        List<Users> tablesList = new ArrayList<>();
        Streamable.of(userRepository.findAll())
                .forEach(tables -> {
                    tablesList.add(tables);
                });
        return tablesList;
    }

    public Users userUpdate(Users user) {
        if(userRepository.existsById(user.getId())) {
            return userRepository.save(user);
        } else {
            throw new EntityNotFoundException("User not found");
        }
    }
}