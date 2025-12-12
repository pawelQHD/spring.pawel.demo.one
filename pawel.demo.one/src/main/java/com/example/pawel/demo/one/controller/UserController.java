package com.example.pawel.demo.one.controller;

import com.example.pawel.demo.one.entity.User;
import com.example.pawel.demo.one.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/user")
public class UserController {

    UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/userList")
    public String userList(Model theModel){

        List<User> listOfUsers = userService.loadUsers();

        theModel.addAttribute("users", listOfUsers);

        return "user/user-list";
    }

    @GetMapping("/disableEnableUser")
    public String enableDisableUser(@RequestParam("userId") int theId, Model theModel){

        User theUser = userService.findById(theId);

        if(theUser.isEnabled()){
            theUser.setEnabled(false);
        } else {
            theUser.setEnabled(true);
        }

        userService.update(theUser);

        return "redirect:/user/userList";
    }
}
