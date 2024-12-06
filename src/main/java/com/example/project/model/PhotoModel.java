package com.example.project.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Data
@Document(collection = "photo")
public class PhotoModel {
    @Id
    private String id;
    private String image;
}
