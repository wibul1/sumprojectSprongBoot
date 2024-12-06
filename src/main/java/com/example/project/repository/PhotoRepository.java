package com.example.project.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.example.project.model.PhotoModel;

@Repository
public interface PhotoRepository extends MongoRepository<PhotoModel, String> {
    
}
