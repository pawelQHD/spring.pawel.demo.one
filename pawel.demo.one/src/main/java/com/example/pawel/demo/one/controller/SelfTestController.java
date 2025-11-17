package com.example.pawel.demo.one.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SelfTestController {

    @GetMapping("/")
    public String selfTestHomepage(){

        return "index.html";
    }
}
