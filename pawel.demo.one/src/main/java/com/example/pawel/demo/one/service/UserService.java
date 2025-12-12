package com.example.pawel.demo.one.service;

import com.example.pawel.demo.one.entity.User;

import java.util.List;

public interface UserService {

    public User findByUserName(String userName);

    void save(User user);

    List<User> loadUsers();

    User findById(int theId);

    void update(User theUser);
}
