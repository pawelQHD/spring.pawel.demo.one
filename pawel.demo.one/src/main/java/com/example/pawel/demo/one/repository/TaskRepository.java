package com.example.pawel.demo.one.repository;

import com.example.pawel.demo.one.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Integer> {

    List<Task> findByUserUserName(String userName);

    List<Task> findByUserUserNameAndCompletedTrueOrderByUpdatedAtDesc(String userName);

    List<Task> findByUserUserNameAndCompletedFalseOrderByPriorityDesc(String userName);
}
