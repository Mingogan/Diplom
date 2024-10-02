package com.example.ServerSpring.model.Users;

import com.example.ServerSpring.model.AuthData.AuthData;
import jakarta.persistence.*;

import java.sql.Date;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "users", schema = "public", catalog = "Diplom")
public class Users {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id")
    private int id;

    @Basic
    @Column(name = "first_name")
    private String firstName;

    @Basic
    @Column(name = "last_name")
    private String lastName;

    @Basic
    @Column(name = "patronymic")
    private String patronymic;

    @Basic
    @Column(name = "phone_number")
    private String phoneNumber;

    @Basic
    @Column(name = "email")
    private String email;
    @Basic
    @Column(name = "active", nullable = false)
    private boolean active = true; // По умолчанию новый сотрудник активен

    // Getters and setters

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPatronymic() {
        return patronymic;
    }

    public void setPatronymic(String patronymic) {
        this.patronymic = patronymic;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Users users = (Users) o;
        return id == users.id && Objects.equals(firstName, users.firstName) && Objects.equals(lastName, users.lastName) && Objects.equals(patronymic, users.patronymic) && Objects.equals(phoneNumber, users.phoneNumber) && Objects.equals(email, users.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, firstName, lastName, patronymic, phoneNumber, email);
    }


}
