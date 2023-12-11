package com.victorsaez.bookingapi.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.victorsaez.bookingapi.controllers.controllerAdvice.Violation;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private UserDetailsService userDetailsService;
    private JwtRequestFilter jwtRequestFilter;

    public SecurityConfig(UserDetailsService userDetailsService, JwtRequestFilter jwtRequestFilter) {
        this.userDetailsService = userDetailsService;
        this.jwtRequestFilter = jwtRequestFilter;
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
    }

    private Map<String, Object> formattedErrorMessage(int status, String message, String path) {
        Map<String, Object> data = new HashMap<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
        String formattedDate = ZonedDateTime.now().format(formatter);
        data.put("timestamp", formattedDate);
        data.put("status", status);
        data.put("error", "Unauthorized");
        data.put("message", message);
        data.put("path", path);
        return data;
    }
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable()
                .exceptionHandling()
                .authenticationEntryPoint((request, response, authException) -> {
                    int unauthorizedStatus = HttpStatus.UNAUTHORIZED.value();
                    response.setStatus(unauthorizedStatus);
                    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                    Map<String, Object> data = formattedErrorMessage(unauthorizedStatus,
                                                                    authException.getMessage(),
                                                                    request.getRequestURI());
                    response.getWriter().write(new ObjectMapper().writeValueAsString(data));
                })
                .accessDeniedHandler((request, response, accessDeniedException) -> {
                    int forbiddenStatus = HttpStatus.FORBIDDEN.value();
                    response.setStatus(forbiddenStatus);
                    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                    Map<String, Object> data = formattedErrorMessage(forbiddenStatus,
                                                                    accessDeniedException.getMessage(),
                                                                    request.getRequestURI());
                    response.getWriter().write(new ObjectMapper().writeValueAsString(data));
                })
                .and()
                .authorizeRequests()
                .antMatchers("/authentication/login", "/v3/api-docs/**", "/swagger-ui.html",
                        "/swagger-ui/**", "/swagger-resources/**", "/webjars/**", "/api/**")
                .permitAll()
                .anyRequest().authenticated()
                .and().sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        http.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }
}