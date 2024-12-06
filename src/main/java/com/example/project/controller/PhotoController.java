package com.example.project.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import  org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.project.model.PhotoModel;
import com.example.project.repository.PhotoRepository;


@RestController
@RequestMapping("/api/photo")
public class PhotoController {
    @Autowired
    private PhotoRepository photoRepository;

    
    @GetMapping
    public List<PhotoModel> getAllPhoto() {
        return photoRepository.findAll();
    }

    @PostMapping
    public PhotoModel createPhoto(@RequestBody PhotoModel photo) {
        return photoRepository.save(photo);
    }
    
}
