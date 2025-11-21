package com.example.pawel.demo.one.service;

import com.example.pawel.demo.one.entity.User;

public interface UserService {

    public User findByUserName(String userName);

    void save(User user);
}
