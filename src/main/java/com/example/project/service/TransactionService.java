package com.example.project.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.project.model.TransactionModel;
import com.example.project.repository.TransactionRepository;

@Service
public class TransactionService {
    
    @Autowired
    private TransactionRepository transactionRepository;
    
    public List<TransactionModel> getTransactions(String month, String year) {
        if (month == null || month.trim().isEmpty()) {
            return transactionRepository.findByYear(year);
        }
        return transactionRepository.findByMonthAndYear(month, year);
    }
}
