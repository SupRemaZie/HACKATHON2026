package com.vdef.hackathon.jpa;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "users")
public class UserJPA
{

    @Id
    @GeneratedValue
    @Column(nullable = false, updatable = false)
    private UUID id;

    @Column(nullable = false)
    private String email;

    @Column(name = "password_hash", nullable = false, length = 255)
    private String passwordHash;

    @Column(nullable = false, length = 255)
    private String full_name;

    @Column(length = 50)
    private String role;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime created_at;

    // Getters/Setters
    public UUID getId() {
        return id;
    }
    public void setId(UUID id) {
        this.id = id;
    }

    public String getEmail() {
        return this.email;
    }
    public void setEmail(String email) {
        this.email = email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getFull_name() {
        return this.full_name;
    }
    public void setFull_name(String full_name) {this.full_name = full_name;}

    public String getRole() {
        return this.role;
    }
    public void setRole(String role) { this.role = role;}

    public LocalDateTime getCreated_at() {return this.created_at;}
    public void getCreated_at(LocalDateTime created_at) {this.created_at = created_at;}

}
