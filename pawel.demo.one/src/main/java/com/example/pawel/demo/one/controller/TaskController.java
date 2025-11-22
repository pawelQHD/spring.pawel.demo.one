package com.example.pawel.demo.one.controller;

import com.example.pawel.demo.one.entity.Task;
import com.example.pawel.demo.one.service.TaskService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/task")
public class TaskController {

    private TaskService taskService;

    @Autowired
    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping("/addTask")
    public String addTask(Model theModel){

        theModel.addAttribute("task", new Task());

        return "task/add-task.html";
    }
    @PostMapping("/processNewTask")
    public String processNewTask(
            @Valid @ModelAttribute("task") Task theTask,
            BindingResult theBindingResult,
            HttpSession session,
            Model theModel
    ){

        if(theBindingResult.hasErrors()){
            return "task/add-task";
        }

        taskService.save(theTask);
        session.setAttribute("task", theTask);

        return "task/task-added-confirmation";
    }
}
