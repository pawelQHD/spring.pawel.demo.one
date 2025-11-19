package com.example.pawel.demo.one.repository;

import com.example.pawel.demo.one.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Integer> {

    User findByUserName(String userName);
}
