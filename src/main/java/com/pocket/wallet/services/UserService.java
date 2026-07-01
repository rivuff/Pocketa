package com.pocket.wallet.services;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.pocket.wallet.entities.User;
import com.pocket.wallet.repositories.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public User createUser(User user){
        log.info("creating user: {}", user.getEmail());
        User newUser = userRepository.save(user);
        log.info("User created with id: {} in database shardwallet {}", newUser.getId(), newUser.getId()%2+1);

        return newUser;
    }

    public User getUserById(Long id){
        Optional<User> user = userRepository.findById(id);

        return user.get();
    }

    public List<User> getUserByName(String user){
        List<User> users = userRepository.findByNameContainingIgnoreCase(user);

        return users;
    }
    
}
