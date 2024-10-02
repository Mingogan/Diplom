package com.example.ServerSpring.service;

import com.example.ServerSpring.model.Users.Users;
import org.springframework.stereotype.Service;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

@Service
public class LoginPasswordGenerator {

    public String generateLogin(Users user) {
        return user.getFirstName().toLowerCase() + "." + user.getLastName().toLowerCase();
    }

    public String generatePassword() {
        int length = 8;
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();
        StringBuilder password = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            password.append(chars.charAt(random.nextInt(chars.length())));
        }
        return password.toString();
    }

    public String hashPassword(String password) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] hash = md.digest(password.getBytes());
        StringBuilder hexString = new StringBuilder();
        for (byte b : hash) {
            hexString.append(Integer.toHexString(0xFF & b));
        }
        return hexString.toString();
    }
}
