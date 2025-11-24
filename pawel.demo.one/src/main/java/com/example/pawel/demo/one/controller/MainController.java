package com.example.pawel.demo.one.controller;

import com.example.pawel.demo.one.entity.Task;
import com.example.pawel.demo.one.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class MainController {

    TaskService taskService;

    @Autowired
    public MainController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping("/")
    public String mainHomepage(Model theModel){

        List<Task> userList = taskService.loadTaskFromUser();

        theModel.addAttribute("tasks", userList);

        return "index.html";
    }

    @GetMapping("/myLoginPage")
    public String myLoginPage(){

        return "login-page.html";
    }

    @GetMapping("/userList")
    public String userList(){

        return "user-list.html";
    }
}
