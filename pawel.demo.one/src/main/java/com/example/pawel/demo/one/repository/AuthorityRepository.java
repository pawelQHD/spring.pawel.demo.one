package com.example.pawel.demo.one.repository;

import com.example.pawel.demo.one.entity.Authority;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AuthorityRepository extends JpaRepository<Authority, Integer> {
    List<Authority> findByUserName(String username);
}
