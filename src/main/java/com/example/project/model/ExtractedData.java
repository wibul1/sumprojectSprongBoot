package com.example.project.model;

import lombok.Data;

@Data
public class ExtractedData {
    private String extractedText;
    private TransactionModel parsedData;

    public ExtractedData(String extractedText, TransactionModel parsedData) {
        this.extractedText = extractedText;
        this.parsedData = parsedData;
    }
}