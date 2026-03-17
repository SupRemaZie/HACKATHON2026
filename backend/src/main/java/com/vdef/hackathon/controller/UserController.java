package com.vdef.hackathon.controller;

import java.util.Optional;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.vdef.hackathon.jpa.UserJPA;
import com.vdef.hackathon.service.ServiceUser;

@RestController
public class UserController
{
    private final ServiceUser userService;

    public UserController(ServiceUser userService) {
        this.userService = userService;
    }

    @GetMapping("/user/{id}")
    public Optional<UserJPA> getUserById(@PathVariable Long id) {
        return userService.getUserById(id);
    }

}
