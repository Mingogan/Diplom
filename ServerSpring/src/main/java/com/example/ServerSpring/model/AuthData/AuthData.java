package com.example.ServerSpring.model.AuthData;

import com.example.ServerSpring.model.Users.Users;
import com.example.ServerSpring.model.Roles.Roles;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "auth_data", schema = "public", catalog = "Diplom")
public class AuthData {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id")
    private int id;

    @Basic
    @Column(name = "login", unique = true, nullable = false)
    private String login;

    @Basic
    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @ManyToOne
    @JoinColumn(name = "role_id", nullable = false)
    private Roles role;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private Users user;

    @Basic
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Basic
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Basic
    @Column(name = "refreshToken")
    private String refreshToken;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Getters and setters

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

    public Users getUser() {
        return user;
    }

    public void setUser(Users employee) {
        this.user = employee;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AuthData authData = (AuthData) o;
        return id == authData.id && login.equals(authData.login) && passwordHash.equals(authData.passwordHash) && role.equals(authData.role) && user.equals(authData.user) && createdAt.equals(authData.createdAt) && updatedAt.equals(authData.updatedAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, login, passwordHash, role, user, createdAt, updatedAt);
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
}
