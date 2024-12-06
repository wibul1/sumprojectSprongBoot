package com.example.project.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Data
@Document(collection = "tasks")

public class TaskModel {
    @Id
    private String id;
    private String title;
    private String category;
    private String description;
    private String datetime;
    private boolean completed;
}
