package com.vdef.hackathon.service;

import java.util.List;
import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.vdef.hackathon.dto.auth.UserSummaryDTO;
import com.vdef.hackathon.jpa.UserJPA;
import com.vdef.hackathon.repository.UserRepository;

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
        return repositoryUsers.findByEmailIgnoreCase(email);
    }

    public boolean isValidCredentials(String email, String rawPassword, PasswordEncoder passwordEncoder)
    {
        return repositoryUsers.findByEmailIgnoreCase(email)
            .map(user -> passwordEncoder.matches(rawPassword, user.getPasswordHash()))
            .orElse(false);
    }

    public List<UserSummaryDTO> getAllUsersSummary()
    {
        return repositoryUsers.findAll().stream()
            .map(user -> new UserSummaryDTO(
                user.getId(),
                user.getEmail(),
                user.getFullName(),
                user.getRole()
            ))
            .toList();
    }
}
