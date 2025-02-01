package com.dal.skillswap.service;

import com.dal.skillswap.enums.UserRole;
import com.dal.skillswap.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.security.core.userdetails.User;

import java.util.Optional;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String userEmail) throws UsernameNotFoundException {
        Optional<com.dal.skillswap.entities.User> user = userRepository.findByEmail(userEmail);

        if (!user.isPresent()) {
            throw new UsernameNotFoundException("User not found");
        }
        String email = user.get().getEmail();
        String password = user.get().getPassword();
        UserRole role = user.get().getRole();
        return User.builder().username(email).password(password).roles(String.valueOf(role)).build();
    }
}
