package com.example.pawel.demo.one.service;

import com.example.pawel.demo.one.entity.Task;

import java.util.List;

public interface TaskService {

    void save(Task task);

    List<Task> loadTaskFromUser();

    Task findTask(int theId);
}
