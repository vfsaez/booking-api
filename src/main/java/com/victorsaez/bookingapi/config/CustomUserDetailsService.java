package com.victorsaez.bookingapi.config;

import com.victorsaez.bookingapi.config.CustomUserDetails;
import com.victorsaez.bookingapi.entities.User;
import com.victorsaez.bookingapi.repositories.UserRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.Arrays.stream;

@Service("UserDetailsService")
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> userOptional = userRepository.findByUsername(username);
        if (userOptional.isEmpty()) {
            throw new UsernameNotFoundException("User not found");
        }
        User user = userOptional.get();

        List<GrantedAuthority> authorities = Arrays.stream(user.getRoles().split(","))
                    .map(role -> new SimpleGrantedAuthority("ROLE_" + role.toUpperCase()))
                    .collect(Collectors.toList());
        CustomUserDetails customUserDetails = new CustomUserDetails(user.getUsername(), user.getPassword(), authorities);
        customUserDetails.setId(user.getId());
        customUserDetails.setUser(user);
        return customUserDetails;
    }


}
