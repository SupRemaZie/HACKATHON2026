package com.vdef.hackathon.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.vdef.hackathon.jpa.UserJPA;

@Repository
public interface UserRepository extends JpaRepository<UserJPA, Long> {
    Optional<UserJPA> findByEmailIgnoreCase(String email);
    Optional<UserJPA> findByEmail(String email);
    boolean existsByEmail(String email);
}
