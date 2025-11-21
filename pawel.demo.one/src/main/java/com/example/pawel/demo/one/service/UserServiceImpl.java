package com.example.pawel.demo.one.service;

import com.example.pawel.demo.one.entity.Authority;
import com.example.pawel.demo.one.repository.AuthorityRepository;
import com.example.pawel.demo.one.repository.UserRepository;
import com.example.pawel.demo.one.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class UserServiceImpl implements UserService{

    private UserRepository userRepository;
    private AuthorityRepository authorityRepository;
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    public UserServiceImpl(UserRepository userRepository,
                           AuthorityRepository authorityRepository,
                           BCryptPasswordEncoder passwordEncoder){
        this.userRepository = userRepository;
        this.authorityRepository = authorityRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public User findByUserName(String userName) {
        return userRepository.findByUserName(userName);
    }

    @Override
    public void save(User theUser) {

        User user = new User();
        Authority authority = new Authority();

        user.setUserName(theUser.getUserName());
        user.setPassword(passwordEncoder.encode(theUser.getPassword()));
        user.setEmail(theUser.getEmail());
        user.setCreatedAt(LocalDateTime.now());

        authority.setUserName(theUser.getUserName());
        authority.setAuthority("ROLE_USER");

        userRepository.save(user);
        authorityRepository.save(authority);
    }
}
