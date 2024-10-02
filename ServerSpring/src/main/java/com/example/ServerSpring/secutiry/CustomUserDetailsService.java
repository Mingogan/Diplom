package com.example.ServerSpring.secutiry;

import com.example.ServerSpring.model.AuthData.AuthData;
import com.example.ServerSpring.model.AuthData.AuthDataDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private AuthDataDao authDataDao;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        AuthData authData = authDataDao.findByLogin(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

        GrantedAuthority authority = new SimpleGrantedAuthority(authData.getRole().getName());
        System.out.println("User loaded: " + authData.getLogin() + ", Role: " + authData.getRole().getName());

        return new org.springframework.security.core.userdetails.User(
                authData.getLogin(),
                authData.getPasswordHash(),
                Collections.singletonList(authority)
        );
    }
}