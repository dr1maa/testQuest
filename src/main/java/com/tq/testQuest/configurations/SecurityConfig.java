package com.tq.testQuest.configurations;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                .antMatchers("/api/users/register").permitAll() // Разрешить доступ к регистрации без авторизации
                .antMatchers("/api/movies/**").authenticated() // Требовать авторизацию для остальных запросов в /api/movies
                .and()
                .httpBasic(); // Использовать базовую аутентификацию
        http.csrf().disable();
    }
}
