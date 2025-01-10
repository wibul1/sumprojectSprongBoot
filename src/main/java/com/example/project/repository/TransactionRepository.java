package com.example.project.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.example.project.model.TransactionModel;


@Repository
public interface TransactionRepository extends MongoRepository<TransactionModel, String> {
    // ค้นหาตามปี
    List<TransactionModel> findByYear(String year);
    
    // ค้นหาตามเดือนและปี
    List<TransactionModel> findByMonthAndYear(String month, String year);
}
