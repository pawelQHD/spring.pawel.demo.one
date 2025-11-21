package com.example.pawel.demo.one.controller;

import com.example.pawel.demo.one.entity.User;
import com.example.pawel.demo.one.service.UserService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/register")
public class RegistrationController {

    private UserService userService;

    @Autowired
    public RegistrationController(UserService userService){
        this.userService = userService;
    }

    @InitBinder
    public void initBinder(WebDataBinder dataBinder){

        StringTrimmerEditor stringTrimmerEditor = new StringTrimmerEditor(true);
        dataBinder.registerCustomEditor(String.class, stringTrimmerEditor);
    }

    @GetMapping("/showRegistrationForm")
    public String showMyLoginPage(Model theModel){

        theModel.addAttribute("user", new User());

        return "register/registration-form";
    }

    @PostMapping("/processRegistrationForm")
    public String processRegistrationForm(
            @Valid @ModelAttribute("user") User theUser,
            BindingResult theBindingResult,
            HttpSession session,
            Model theModel
    ){

        String userName = theUser.getUserName();

        if(theBindingResult.hasErrors()){
            return "register/registration-form";
        }

        User existing = userService.findByUserName(userName);
        if(existing != null){
            theModel.addAttribute("user", new User());
            theModel.addAttribute("registrationError",
                    "User name already exists.");
            return "register/registration-form";
        }

        userService.save(theUser);
        session.setAttribute("user", theUser);

        return "register/registration-confirmation";
    }
}
