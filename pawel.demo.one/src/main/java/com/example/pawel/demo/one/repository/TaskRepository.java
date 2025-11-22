package com.example.pawel.demo.one.repository;

import com.example.pawel.demo.one.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskRepository extends JpaRepository<Task, Integer> {
}
