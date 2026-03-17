package com.vdef.hackathon.controller;

import com.vdef.hackathon.dto.user.CreateUserRequest;
import com.vdef.hackathon.jpa.UserJPA;
import com.vdef.hackathon.repository.UserRepository;
import io.jsonwebtoken.security.Password;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import com.vdef.hackathon.service.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/user")
public class UserController
{
    private final ServiceUser userService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;


    public UserController(PasswordEncoder passwordEncoder, ServiceUser userService, UserRepository userRepository) {
        this.userService = userService;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/create")
    public String createUser(@RequestBody CreateUserRequest request)
    {
        UserJPA newUser = new UserJPA();
        newUser.setEmail(request.email());
        newUser.setFullName(request.fullName());
        newUser.setPasswordHash(passwordEncoder.encode(request.passwordHash()));
        newUser.setRole("USER");

        userRepository.save(newUser);

        return "ok";
    }


    @GetMapping("/{id}")
    public Optional<UserJPA> getUserById(@PathVariable Long id) {
        return userService.getUserById(id);
    }

}
