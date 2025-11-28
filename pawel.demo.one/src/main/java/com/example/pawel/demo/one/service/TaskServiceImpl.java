package com.example.pawel.demo.one.service;

import com.example.pawel.demo.one.entity.Task;
import com.example.pawel.demo.one.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

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

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (theTask.getCreatedAt() == null) {
            theTask.setCreatedAt(LocalDateTime.now());
        }

        theTask.setUpdatedAt(LocalDateTime.now());

        if(theTask.getUser() == null){
            theTask.setUser(userService.findByUserName(authentication.getName()));
        }

        taskRepository.save(theTask);
    }

    @Override
    public List<Task> loadActiveTasksFromUser() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        List<Task> userList = taskRepository.findByUserUserNameAndCompletedFalseOrderByPriorityDesc(authentication.getName());

        return userList;
    }

    @Override
    public List<Task> loadCompletedTasksFromUser() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        List<Task> userList = taskRepository.findByUserUserNameAndCompletedTrueOrderByUpdatedAtDesc(authentication.getName());

        return userList;
    }

    @Override
    public Task findTask(int theId) {

        Optional<Task> result = taskRepository.findById(theId);

        Task theTask = null;

        if(result.isPresent()){
            theTask = result.get();
        } else {
            throw new RuntimeException("Did not find employee id - " + theId);
        }

        return theTask;
    }


}
