package com.vdef.hackathon.repository;

import com.vdef.hackathon.jpa.UserJPA;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<UserJPA, Long> {
    Optional<UserJPA> findByEmail(String email);
    boolean existsByEmail(String email);
}
