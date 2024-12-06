package com.example.project.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.example.project.model.TaskModel;

@Repository
public interface TaskRepository extends MongoRepository<TaskModel, String> {
    List<TaskModel> findByCompleted(boolean completed);
}
