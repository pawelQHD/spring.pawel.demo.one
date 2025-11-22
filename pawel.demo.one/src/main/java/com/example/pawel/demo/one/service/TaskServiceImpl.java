package com.example.pawel.demo.one.service;

import com.example.pawel.demo.one.entity.Task;
import com.example.pawel.demo.one.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class TaskServiceImpl implements TaskService{

    TaskRepository taskRepository;
    UserService userService;

    @Autowired
    public TaskServiceImpl(TaskRepository taskRepository,
                           UserService userService) {
        this.taskRepository = taskRepository;
        this.userService = userService;
    }

    @Override
    public void save(Task theTask) {

        Task task = new Task();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        task.setTitle(theTask.getTitle());
        task.setDescription(theTask.getDescription());
        task.setPriority(theTask.getPriority());
        task.setCategory(theTask.getCategory());
        task.setDueDate(theTask.getDueDate());
        task.setCreatedAt(LocalDateTime.now());
        task.setUpdatedAt(LocalDateTime.now());

        task.setUser(userService.findByUserName(authentication.getName()));

        taskRepository.save(task);
    }
}
