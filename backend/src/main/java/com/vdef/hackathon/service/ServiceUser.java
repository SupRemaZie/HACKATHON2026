package com.vdef.hackathon.service;

import com.vdef.hackathon.dto.auth.UserSummaryDTO;
import com.vdef.hackathon.jpa.UserJPA;
import org.springframework.stereotype.Service;

import com.vdef.hackathon.repository.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

@Service
public class ServiceUser
{
    private final UserRepository repositoryUsers;

    public ServiceUser(UserRepository repositoryUsers)
    {
        this.repositoryUsers = repositoryUsers;
    }

    public Optional<UserJPA> getUserById(Long id)
    {
        return repositoryUsers.findById(id);
    }

    public Optional<UserJPA> getUserByEmail(String email)
    {
        return repositoryUsers.findByEmail(email);
    }

    public boolean isValidCredentials(String email, String rawPassword, PasswordEncoder passwordEncoder)
    {
        return repositoryUsers.findByEmail(email)
            .map(user -> passwordEncoder.matches(rawPassword, user.getPasswordHash()))
            .orElse(false);
    }

    public List<UserSummaryDTO> getAllUsersSummary()
    {
        return repositoryUsers.findAll().stream()
            .map(user -> new UserSummaryDTO(
                user.getId(),
                user.getEmail(),
                user.getFull_name(),
                user.getRole()
            ))
            .toList();
    }
}
