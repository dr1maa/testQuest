package com.tq.testQuest.configurations;

import com.tq.testQuest.repositories.UserRepository;
import com.tq.testQuest.services.CustomAuthenticationProvider;
import com.tq.testQuest.services.CustomUserDetailService;
import com.tq.testQuest.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;


@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    @Autowired
    private  CustomUserDetailService customUserDetailService;

    @Autowired
    public SecurityConfig(CustomUserDetailService CustomUserDetailService) {
        this.customUserDetailService = CustomUserDetailService;
    }



    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService UserDetailsService(UserRepository userRep) {
        return new CustomUserDetailService(userRep);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .userDetailsService(customUserDetailService)
                .authorizeRequests()
                .antMatchers("/api/users/register").permitAll()
                .antMatchers("/api/movies/**").authenticated()
                .and()
                .httpBasic();
        http.csrf().disable();
    }
}
