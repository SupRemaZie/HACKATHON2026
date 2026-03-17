package com.vdef.hackathon.service;

import com.vdef.hackathon.jpa.SiteJPA;
import com.vdef.hackathon.jpa.UserJPA;
import org.springframework.stereotype.Service;

import com.vdef.hackathon.repository.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ServiceUser
{
    private final UserRepository repositoryUsers;

    public ServiceUser(UserRepository repositoryUsers)
    {
        this.repositoryUsers = repositoryUsers;
    }

    public Optional<UserJPA> getUserByUUID(UUID uuid)
    {
        return repositoryUsers.findById(uuid);
    }
}
