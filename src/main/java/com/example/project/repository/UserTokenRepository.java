package com.example.project.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.example.project.model.UserTokenModel;

@Repository
public interface UserTokenRepository extends MongoRepository<UserTokenModel, String> {
    Optional<UserTokenModel> findByToken(String token);
    void deleteByUserId(String userId);
}