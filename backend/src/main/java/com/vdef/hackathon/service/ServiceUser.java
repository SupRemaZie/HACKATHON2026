package com.vdef.hackathon.service;

import com.vdef.hackathon.jpa.UserJPA;
import org.springframework.stereotype.Service;

import com.vdef.hackathon.repository.*;

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
}
