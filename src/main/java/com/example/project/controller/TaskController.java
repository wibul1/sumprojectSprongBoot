package com.example.project.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.project.model.TaskModel;
import com.example.project.repository.TaskRepository;



@RestController
@RequestMapping("/api/task")
public class TaskController {
    
    @Autowired
    private TaskRepository taskRepository;

    @GetMapping
    public List<TaskModel> getAllTask() {
        return taskRepository.findByCompleted(true);
    }
    
    @PostMapping
    public TaskModel createTask(@RequestBody TaskModel task) {
        return taskRepository.save(task);
    }

    @GetMapping("/{id}")
    public TaskModel getTaskId(@PathVariable String id) {
        return taskRepository.findById(id).orElseThrow(() -> new RuntimeException("Task not found"));
    }
    
    // @PostMapping("/changeTask")
    // public Task changeTask(@PathVariable String id) {
    //     Task task = taskRepository.findById(id).orElseThrow(() -> new RuntimeException("Task not found"));
    //     task.setCompleted(!task.isCompleted());
        
    //     return taskRepository.save(task);
    // }
    @PostMapping("/changeTask")
    public TaskModel changeTask(@RequestBody Map<String, String> payload) {
        String id = payload.get("id");
        TaskModel task = taskRepository.findById(id).orElseThrow(() -> new RuntimeException("Task not found"));
        task.setCompleted(!task.isCompleted());
        
        return taskRepository.save(task);
    }


}
