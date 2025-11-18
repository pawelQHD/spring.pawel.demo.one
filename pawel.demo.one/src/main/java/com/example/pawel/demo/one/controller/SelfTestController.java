package com.example.pawel.demo.one.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SelfTestController {

    @GetMapping("/")
    public String selfTestHomepage(){

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

    @GetMapping("/addTask")
    public String addTask(){

        return "add-task.html";
    }
}
