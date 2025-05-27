package dev.brkovic.fridge.tracker.service.impl;

import dev.brkovic.fridge.tracker.entity.UserEntity;
import dev.brkovic.fridge.tracker.enums.ExceptionSeverityLevel;
import dev.brkovic.fridge.tracker.exception.InternalException;
import dev.brkovic.fridge.tracker.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class BaseService {

    @Autowired
    protected UserRepository userRepository;

    protected UserEntity getLoggedInUser(){
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        UserEntity user = userRepository.findByUsername(username).orElse(null);

        if (user == null){
            log.warn("Unable to find user with username: {}", username);
            throw new InternalException("Unable to find user", ExceptionSeverityLevel.ERROR);
        }

        return user;
    }
}
