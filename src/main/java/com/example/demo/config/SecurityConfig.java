package com.example.demo.config;

import com.example.demo.jwt.JwtAuthenticationFilter;
import com.example.demo.repository.AccountRepository;
import com.example.demo.service.UserInfoDetailService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final AccountRepository accountRepository;

    @Bean
    // authentication
    public UserDetailsService userDetailsService() {
        return new UserInfoDetailService(accountRepository);
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }


    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter();
    }


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf().disable().cors().and()
                .authorizeHttpRequests()
                .requestMatchers("/api/account/load").permitAll()
                .requestMatchers("/api/order/load").permitAll()
                .requestMatchers("/api/access-denied").permitAll()// với endpoint /hello thì sẽ được cho qua
                .and()
                .authorizeHttpRequests().requestMatchers("/api/login").permitAll()
                .and().authorizeHttpRequests().requestMatchers("/api/account/detail/*").hasRole("ADMIN")
                .and().authorizeHttpRequests().requestMatchers("/api/customers/load").hasRole("ADMIN")
                .and().authorizeHttpRequests().requestMatchers("/api/floor/**").hasRole("ADMIN")
                .and().authorizeHttpRequests().requestMatchers("/api/room/**").hasRole("ADMIN")
                .and().authorizeHttpRequests().requestMatchers("/api/service-type/**").hasRole("ADMIN")
                .anyRequest().authenticated()
                .and().exceptionHandling().accessDeniedPage("/api/access-denied")
                .and().formLogin() // trả về page login nếu chưa authenticate
                .and().logout().permitAll().and()
                .addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userDetailsService());
        authenticationProvider.setPasswordEncoder(passwordEncoder());
        return authenticationProvider;
    }
}
