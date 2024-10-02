package com.example.springclient.model;

import java.util.Objects;

public class AuthData {
    private int id;
    private String login;
    private String passwordHash;
    private Roles role;
    private Users employee;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public Roles getRole() {
        return role;
    }

    public void setRole(Roles role) {
        this.role = role;
    }

    public Users getEmployee() {
        return employee;
    }

    public void setEmployee(Users employee) {
        this.employee = employee;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AuthData authData = (AuthData) o;
        return id == authData.id && login.equals(authData.login) && passwordHash.equals(authData.passwordHash) && role.equals(authData.role) && employee.equals(authData.employee);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, login, passwordHash, role, employee);
    }
}
