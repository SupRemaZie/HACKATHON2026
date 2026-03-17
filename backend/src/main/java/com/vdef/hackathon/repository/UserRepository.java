package com.vdef.hackathon.repository;

import com.vdef.hackathon.jpa.UserJPA;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserJPA, Long> {
    Optional<UserJPA> findByEmail(String email);
    boolean existsByEmail(String email);
}
