package com.tq.testQuest.configurations;

import com.tq.testQuest.services.CustomAuthenticationProvider;
import com.tq.testQuest.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;

@Configuration
public class AuthConfig {
    @Autowired
    public void configAuthentication(AuthenticationManagerBuilder auth, UserService userService) throws Exception {
        auth.authenticationProvider(new CustomAuthenticationProvider(userService));
    }
}
