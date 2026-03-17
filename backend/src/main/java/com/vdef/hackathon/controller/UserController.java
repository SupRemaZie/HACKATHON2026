package com.vdef.hackathon.controller;

import com.vdef.hackathon.jpa.UserJPA;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import com.vdef.hackathon.service.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
public class UserController
{
    private final ServiceUser userService;

    public UserController(ServiceUser userService) {
        this.userService = userService;
    }

    @GetMapping("/user/{uuid}")
    public Optional<UserJPA> getUserById(@PathVariable UUID uuid) {
        return userService.getUserByUUID(uuid);
    }

}
