package com.example.ServerSpring.controllers;

import com.example.ServerSpring.model.AuthData.AuthData;
import com.example.ServerSpring.model.AuthData.AuthDataDao;
import com.example.ServerSpring.model.Roles.Roles;
import com.example.ServerSpring.model.Roles.RolesDao;
import com.example.ServerSpring.model.Users.Users;
import com.example.ServerSpring.model.Users.UsersDao;
import com.example.ServerSpring.secutiry.*;
import com.example.ServerSpring.service.EmailService;
import com.example.ServerSpring.utils.TransliterationUtils;
import com.example.ServerSpring.webSocket.GenericWebSocketHandler;
import com.google.gson.Gson;
import de.mkammerer.argon2.Argon2;
import de.mkammerer.argon2.Argon2Factory;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.userdetails.User;
import java.util.*;

@RestController
public class UsersController {
    @Autowired
    private UsersDao userDao;

    @Autowired
    private RolesDao rolesDao;

    @Autowired
    private AuthDataDao authDataDao;

    @Autowired
    private GenericWebSocketHandler webSocketHandler;

    @Autowired
    EmailService emailService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserDetailsService userDetailsService;

    @GetMapping("/user/get-all")
    public List<Users> getAllUsers(){
        return userDao.getAllUsers();
    }

    @GetMapping("/roles/get-all")
    public List<Roles> getAllRoles(){
        return rolesDao.getAllRoles();
    }

    @PostMapping("/user")
    public Users save(@RequestBody Map<String, Object> userRoleMap) {
        Gson gson = new Gson();

        Users user = gson.fromJson(gson.toJson(userRoleMap.get("user")), Users.class);
        Roles role = gson.fromJson(gson.toJson(userRoleMap.get("role")), Roles.class);

        Users userSave = userDao.userSave(user);

        String baseLogin = TransliterationUtils.transliterate(user.getLastName().toLowerCase()) + "." + TransliterationUtils.transliterate(user.getFirstName().toLowerCase());

        String uniqueLogin = authDataDao.generateUniqueLogin(baseLogin);

        String randomPassword = RandomStringUtils.randomAlphanumeric(3);

        Argon2 argon2 = Argon2Factory.create();
        String hashedPassword = argon2.hash(2, 65536, 1, randomPassword);

        AuthData authData = new AuthData();
        authData.setLogin(uniqueLogin);
        authData.setPasswordHash(hashedPassword);
        authData.setRole(role);
        authData.setUser(userSave);

        authDataDao.saveAuthData(authData);
        String userJson = gson.toJson(userSave);
        webSocketHandler.broadcastMessage("USER_ADDED:" + userJson);

        emailService.sendEmail(
                "jowe123@mail.ru",
                "Поздравяем с успешной регистрацией",
                "Привет" + userSave.getFirstName() + " "+ userSave.getLastName()+ "\nДанные для роли: "+ authData.getRole().getName() + " Логин:" + uniqueLogin + "\nПароль: " + randomPassword
        );

        return userSave;
    }

    @DeleteMapping("/user/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable("id") int tableId) {
        try {
            Users table = userDao.findById(tableId);
            Gson gson = new Gson();
            String userJson = gson.toJson(table);
            userDao.userDelete(tableId);
            webSocketHandler.broadcastMessage("USER_DELETED:" + userJson);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/user/{id}")
    public ResponseEntity<Users> updateUser(@RequestBody Map<String, Object> userRoleMap) {

        Gson gson = new Gson();

        Users user = gson.fromJson(gson.toJson(userRoleMap.get("user")), Users.class);
        Roles role = gson.fromJson(gson.toJson(userRoleMap.get("role")), Roles.class);

        Users updatedUser = userDao.userUpdate(user);
        String userJson = gson.toJson(updatedUser);

        if (updatedUser != null) {
            webSocketHandler.broadcastMessage("USER_UPDATED:" + userJson);
            return ResponseEntity.ok(updatedUser);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/roles/{userId}")
    public List<Roles> getRolesByUserId(@PathVariable int userId) {
        List<Roles> role = authDataDao.findRolesByUserId(userId);
        return role;
    }


    @PostMapping("/auth")
    public ResponseEntity<AuthData> addRoleToUser(@RequestBody Map<String, Object> userRoleMap) {
        Gson gson = new Gson();

        Users user = gson.fromJson(gson.toJson(userRoleMap.get("user")), Users.class);
        Roles role = gson.fromJson(gson.toJson(userRoleMap.get("role")), Roles.class);

        if (user != null) {
            System.out.println(user);
        } else {
            System.out.println("null user");
        }
        System.out.println(role);

        Optional<AuthData> existingAuthData = authDataDao.findByUserIdAndRoleId(user.getId(), role.getId());
        if (existingAuthData.isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(null);
        }

        String baseLogin = TransliterationUtils.transliterate(user.getLastName().toLowerCase()) + "." + TransliterationUtils.transliterate(user.getFirstName().toLowerCase());

        String uniqueLogin = authDataDao.generateUniqueLogin(baseLogin);

        String randomPassword = RandomStringUtils.randomAlphanumeric(3);

        Argon2 argon2 = Argon2Factory.create();
        String hashedPassword = argon2.hash(2, 65536, 1, randomPassword);

        AuthData authData = new AuthData();
        authData.setLogin(uniqueLogin);
        authData.setPasswordHash(hashedPassword);
        authData.setRole(role);
        authData.setUser(user);
        System.out.println(authData);

        emailService.sendEmail(
                "jowe123@mail.ru",
                "Поздравяем с успешной регистрацией",
                "Привет" + user.getFirstName() + " "+ user.getLastName()+ "\nДанные для роли: "+ authData.getRole().getName() + " Логин:" + uniqueLogin + "\nПароль: " + randomPassword
        );

        AuthData savedAuthData = authDataDao.saveAuthData(authData);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedAuthData);
    }
    @DeleteMapping("/auth/delete-role")
    public ResponseEntity<Void> deleteRoleFromUser(@RequestParam int userId, @RequestParam int roleId) {
        boolean isDeleted = authDataDao.deleteRoleFromUser(userId, roleId);
        if (isDeleted) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @PostMapping("/auth/login")
    public ResponseEntity<Map<String, String>> login(@RequestParam String username, @RequestParam String password) {
        Optional<AuthData> authDataOptional = authDataDao.findByLogin(username);
        if (authDataOptional.isPresent()) {
            Argon2 argon2 = Argon2Factory.create();
            AuthData authData = authDataOptional.get();
            if (argon2.verify(authData.getPasswordHash(), password) && authData.getUser().isActive()) {
                String accessToken = jwtUtil.generateToken(authData.getLogin());
                String refreshToken = jwtUtil.generateRefreshToken(authData.getLogin());
                Map<String, String> response = new HashMap<>();
                response.put("accessToken", accessToken);
                response.put("refreshToken", refreshToken);
                authData.setRefreshToken(refreshToken);
                authDataDao.update(authData);
                response.put("role", authData.getRole().getName());
                Gson gson = new Gson();
                response.put("user", gson.toJson(authData.getUser()));
                return ResponseEntity.ok(response);
            }
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @PostMapping("/auth/refresh")
    public ResponseEntity<Map<String, String>> refreshToken(@RequestParam String refreshToken) {
        try {
            if (jwtUtil.validateToken(refreshToken, jwtUtil.getRefreshSecret())) {
                String username = jwtUtil.extractUsername(refreshToken, jwtUtil.getRefreshSecret());
                AuthData authData = authDataDao.findByLogin(username)
                        .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
                if (authData.getRefreshToken().equals(refreshToken)) {
                    String newAccessToken = jwtUtil.generateToken(authData.getLogin());
                    String newRefreshToken = jwtUtil.generateRefreshToken(authData.getLogin());
                    authData.setRefreshToken(newRefreshToken);
                    authDataDao.saveAuthData(authData);
                    Map<String, String> tokens = new HashMap<>();
                    tokens.put("accessToken", newAccessToken);
                    tokens.put("refreshToken", newRefreshToken);
                    return ResponseEntity.ok(tokens);
                } else {
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
                }
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

}
