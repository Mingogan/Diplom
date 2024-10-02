package com.example.ServerSpring.config;

import com.example.ServerSpring.controllers.UsersController;
import com.example.ServerSpring.model.AuthData.AuthData;
import com.example.ServerSpring.model.AuthData.AuthDataDao;
import com.example.ServerSpring.model.AuthData.AuthDataRepository;

import com.example.ServerSpring.model.OrderDetailStatus.OrderDetailsStatus;
import com.example.ServerSpring.model.OrderDetailStatus.OrdersDetailsStatusRepository;
import com.example.ServerSpring.model.Roles.Roles;
import com.example.ServerSpring.model.Roles.RolesRepository;
import com.example.ServerSpring.model.Table_Status.*;
import com.example.ServerSpring.model.Users.Users;
import com.example.ServerSpring.model.Users.UsersRepository;
import com.example.ServerSpring.model.orders_status.OrdersStatus;
import com.example.ServerSpring.model.orders_status.OrdersStatusRepository;
import com.example.ServerSpring.service.EmailService;
import de.mkammerer.argon2.Argon2;
import de.mkammerer.argon2.Argon2Factory;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Configuration
public class DataInitializer {

    @Autowired
    EmailService emailService;

    @Autowired
    private TablesStatusRepository tablesStatusRepository;
    @Autowired
    private RolesRepository rolesRepository;
    @Autowired
    private UsersRepository usersRepository;
    @Autowired
    private AuthDataRepository authDataRepository;
    @Autowired
    private OrdersStatusRepository ordersStatusRepository;
    @Autowired
    private OrdersDetailsStatusRepository ordersDetailsStatusRepository;

    @Autowired
    AuthDataDao authDataDao;

    @PostConstruct
    public void init() {
        if (tablesStatusRepository.count() == 0) {
            TablesStatus free = new TablesStatus();
            free.setStatusName("Свободен");

            TablesStatus occupied = new TablesStatus();
            occupied.setStatusName("Занят");

            tablesStatusRepository.saveAll(Arrays.asList(free, occupied));
        }
        if (ordersStatusRepository.count() == 0) {

            OrdersStatus free = new OrdersStatus();
            free.setStatusName("Готовится");

            OrdersStatus awaiting = new OrdersStatus();
            awaiting.setStatusName("Ожидает выдачи");

            OrdersStatus delivered = new OrdersStatus();
            delivered.setStatusName("Доставлен");

            OrdersStatus paid = new OrdersStatus();
            paid.setStatusName("Оплачен");

            OrdersStatus end = new OrdersStatus();
            end.setStatusName("Завершен");

            ordersStatusRepository.saveAll(Arrays.asList( free, awaiting,delivered, paid, end));
        }
        if (rolesRepository.count() == 0){
            Roles admin = new Roles();
            admin.setName("Администратор");

            Roles waiter = new Roles();
            waiter.setName("Официант");

            Roles cook = new Roles();
            cook.setName("Повар");

            Roles barmen = new Roles();
            barmen.setName("Бармен");

            rolesRepository.saveAll(Arrays.asList(admin,waiter,cook,barmen));
        }
        if (ordersDetailsStatusRepository.count() == 0){
            OrderDetailsStatus free = new OrderDetailsStatus();
            free.setStatusName("В очереди");

            OrderDetailsStatus awaiting = new OrderDetailsStatus();
            awaiting.setStatusName("Готовится");

            OrderDetailsStatus delivered = new OrderDetailsStatus();
            delivered.setStatusName("Ожидает доставки");

            OrderDetailsStatus paid = new OrderDetailsStatus();
            paid.setStatusName("Доставлен");

            ordersDetailsStatusRepository.saveAll(Arrays.asList( free, awaiting,delivered, paid));
        }



        if (usersRepository.count() == 2 && authDataRepository.count() == 1) {
            Users user = new Users();
            user.setFirstName("admin");
            user.setLastName("i");
            user.setPatronymic("");
            user.setEmail("jowe123@mail.ru");
            user.setPhoneNumber("89834230451");

            Roles role = new Roles();
            role.setId(1);
            role.setName("Администратор");
            usersRepository.save(user);

            AuthData authData = new AuthData();
            authData.setLogin("admin");
            String password = "admin";
            Argon2 argon2 = Argon2Factory.create();
            String hashedPassword = argon2.hash(2, 65536, 1, password);
            authData.setPasswordHash(hashedPassword);
            authData.setRole(role);
            authData.setUser(user);

            authDataRepository.save(authData);
            /*Users user = usersRepository.findById(1).orElse(null);

            Roles role = new Roles();
            role.setId(2);
            role.setName("Официант");
            AuthData authData = new AuthData();
            authData.setLogin("waiter");
            String password = "waiter";
            Argon2 argon2 = Argon2Factory.create();
            String hashedPassword = argon2.hash(2, 65536, 1, password);
            authData.setPasswordHash(hashedPassword);
            authData.setRole(role);
            authData.setUser(user);
            authData.setRefreshToken("21");
            System.out.println(3);
            authDataRepository.save(authData);*/

        }

        /*usersRepository.save(user);


        emailService.sendEmail(
                "jowe123@mail.ru",
                "Registration Information",
                "Your login: " + "маму" + "\nYour password: " + "люблю"
        );*/

    }
}