package com.pocket.wallet.controllers;

import java.util.List;

import org.springframework.boot.webmvc.autoconfigure.WebMvcProperties.Apiversion.Use;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.pocket.wallet.entities.User;
import com.pocket.wallet.services.UserService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/user")
public class UserController {
    
    private final UserService userService;

    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody User user){
        User newUser = userService.createUser(user);

        return ResponseEntity.status(HttpStatus.CREATED).body(newUser);
    }

    @GetMapping
    public ResponseEntity<List<User>> getUsersByName(@RequestParam String name){
        List<User> users = userService.getUserByName(name);

        return ResponseEntity.status(HttpStatus.ACCEPTED).body(users);
    }
}
