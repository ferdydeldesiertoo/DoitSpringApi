package org.api.doit.security;

import org.api.doit.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username).map((user) -> new CustomUserDetails(user.getId(), user.getUsername(), user.getPassword()))
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    public UserDetails loadUserById(UUID id) {
        return userRepository.findById(id).map((user) -> new CustomUserDetails(user.getId(), user.getUsername(), user.getPassword()))
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }
}
