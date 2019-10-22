package com.example.demo.config;

import java.io.IOException;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.example.demo.observer.ProgressUpdateObserver;
import com.example.demo.service.MockUserService;
import com.example.demo.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;

@Configuration
public class UserServiceConfig {

    @Bean
    public UserService userService(ProgressUpdateObserver observer, ObjectMapper objectMapper) throws IOException {
        MockUserService userService = new MockUserService();
        userService.addObserver(observer);
        userService.setObjectMapper(objectMapper);
        userService.init("cli-users.json");
        return userService;
    }
}
